import { ContentObserver } from '@angular/cdk/observers';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { AllUsers } from 'app/models/allUsers';
import { AuthService } from 'app/services/services/auth.service';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import Chart from 'chart.js/auto';
import { MatDialog } from '@angular/material/dialog';
import { ChartUsersComponent } from '../chart-users/chart-users.component';
import { SharedService } from 'app/services/shared.service';
import { DatePipe } from '@angular/common';
import { ModelTableDay } from 'app/models/modelTableDay';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';


@Component({
  selector: 'app-pocetna',
  templateUrl: './pocetna.component.html',
  styleUrls: ['./pocetna.component.scss','./bd2.css'],
  encapsulation: ViewEncapsulation.None
})

export class PocetnaComponent implements OnInit {
  public chart: any;
  public allProiz: number;
  public allPotr: number;
  public allProizY: number;
  public allPotrY: number;
  public allSkladiste: number;
  public allSkladisteY: number;
  public razlikaPotr: number;
  public razlikaProiz: number;
  public sign1: String;
  public sign2: String;
  public usersAll: AllUsers;
  public users: number;
  now = new Date();
  datum: String;
  count: number;
  value: string = '7days';
  value2 : string = 'bar'
  mess: string;
  datePipe = new DatePipe('en-US');
  i: number = 1
  times: string[]
  first: boolean = true
  // loader: boolean = false

  @ViewChild('dataTable') dataTable: MatTable<any>;
  showSpinner = false;




  constructor(private auth: AuthService, private router: Router, private serviceRecord: RecordService,
    private serviceUsers: UserServiceService, private dialog: MatDialog, private sharedService: SharedService) { }
  logOut() {
    this.auth.logOut()
  
  }
  RegistrujProsumera() {
    this.router.navigate(['signup'])
  }

  sendMessage(): void {
    // this.first = false
    if (this.value=='7days'){
      if(this.value2 == 'bar'){
          this.mess='7daysBar'
          this.showSpinnerFunction()
      }
      else{
        this.mess='7daysLine'
        this.showSpinnerFunction()
        this.first = false
      }
    }else if (this.value=='month'){
      this.mess='month'
      this.first = false
      this.showSpinnerFunction()
    }else{
      this.mess='year'
      this.first = false
      this.showSpinner = true;

      setTimeout(() => {
        this.hideSpinnerFunction();
      }, 7000);
      // this.showSpinnerFunction()
    }
    this.sharedService.messageSource.next(this.mess);

 
}
showSpinnerFunction() {
  this.showSpinner = true;
  
  setTimeout(() => {
    this.hideSpinnerFunction();
  }, 1500);
}

// Function to hide the spinner
hideSpinnerFunction() {
  this.showSpinner = false;
}
  duration = 1000;
  ngOnInit() {
    // this.first = true
    // console.log(ELEMENT_DATA)
    this.sendMessage()

    var dd = String(this.now.getDate()).padStart(2, '0');
    var mm = String(this.now.getMonth() + 1).padStart(2, '0'); //January is 0!
    var yyyy = this.now.getFullYear();

    this.datum = dd + '.' + mm + '.' + yyyy+ '.';

    // console.log(this.datum)
    if(this.i==1)
     
    this.i++;
    this.serviceRecord.getAllProiz().subscribe(res => {
      this.allProiz = res
      this.serviceRecord.getAllPotr().subscribe(res => {
        this.allPotr = res
        this.serviceRecord.getProizvodnjaYesterday().subscribe(res => {
          this.allProizY = res
          this.serviceRecord.getPotrosnjaYesterday().subscribe(res => {
            this.allPotrY = res
            this.serviceRecord.getAllSkladiste().subscribe(res=>{
              this.allSkladiste = res
              this.razlikaPotr = Number(((this.allPotr - this.allPotrY) / this.allPotr * 100).toFixed(3))
              this.razlikaProiz = Number(((this.allProiz - this.allProizY) / this.allProiz * 100).toFixed(3))
              if (this.razlikaPotr < 0) {
                this.sign1 = 'down'
              } else {
                this.sign1 = 'up'
              }

              if (this.razlikaProiz < 0) {
                this.sign2 = 'down'
              } else {
                this.sign2 = 'up'
              }
              this.serviceUsers.getAllUsersCount().subscribe(res => {
                this.usersAll = res
                this.users = Number(this.usersAll.potrosaci + this.usersAll.proizvodjaci + this.usersAll.prosumeri)
                // this.createChart(this.usersAll);
                this.count = Number(res.ukupno)           
              })


          })
        })

        })
      })
    })
  }

  openDialog(){
    this.dialog.open(ChartUsersComponent)
    this.first = false
  }
  
  getSelectedValue(event:any){

    if(event.target.value!='7days') {
      
      this.first = false
      // this.loader = true
    
    }
    this.first = false
    // console.log(this.first)
    this.value = event.target.value
    this.value2 = 'bar'
    this.ngOnInit()

  }

  getSelectedValue2(event: any){
    this.first = false
    this.value2 = event.target.value
    this.ngOnInit()
  }

}
