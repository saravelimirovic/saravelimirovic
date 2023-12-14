import {Component, ViewChild} from '@angular/core';
import { Korisnik } from 'app/models/korisnik';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import { Route, Router } from '@angular/router';
import * as XLSX from 'xlsx';
import { DeviceService } from 'app/services/services/deviceService/device.service';
import { TooltipWeekStatisiticPotService } from 'app/services/shareTooltipInfoDay/tooltip-week-statisitic-pot.service';
import { HideTooltipStatisiticPotService } from 'app/services/shareTooltipInfoDay/hide-tooltip-statisitic-pot.service';

@Component({
  selector: 'app-table-devices',
  templateUrl: './table-devices.component.html',
  styleUrls: ['./table-devices.component.scss']
})
export class TableDevicesComponent{
  // korisnici: Korisnik[]=[];
  displayedColumns: string[] = ['nazivUredjaja', 'potrosnja'];
  dataSource!: MatTableDataSource<any>;
  fileName = 'Korisnici.xlsx'
  // clickedRows = new Set<Korisnik>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  constructor( private Service: DeviceService, private router: Router, private sharedService: TooltipWeekStatisiticPotService, private sharedServiceHide: HideTooltipStatisiticPotService ){}
  
  ngOnInit(){
    this.sharedService.messageSource.next('100')
    this.sharedServiceHide.messageSource.next('100')
    this.getAllUsers()
  }
  
  getAllUsers() {
    this.Service.getAllDevice().subscribe(response=>{
      // console.log(response)
     this.dataSource = new MatTableDataSource(response);
     this.dataSource.sort = this.sort
     this.dataSource.paginator = this.paginator
     

     })
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  editujKorisnika(){
    this.router.navigate(['upravljanje-korisnikom'])
  }
  exportExcel(){
    let element = document.getElementById('excel-table')
    const ws: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element)
    const wb : XLSX.WorkBook = XLSX.utils.book_new()

    XLSX.utils.book_append_sheet(wb,ws,'Sheet1')
    XLSX.writeFile(wb, this.fileName)

  }
  showTooltips(index: any, row: any){
    if (index>=8) index = 8
    //treba da salje grafiku index labele(reda)
      this.sharedService.messageSource.next(index)
    }
  hideTooltips(){
    this.sharedServiceHide.messageSource.next('hide')
      
  }
}
