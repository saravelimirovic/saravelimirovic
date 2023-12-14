import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { MessageService, TreeNode } from 'primeng/api';
import { UredjajiService } from 'src/app/services/uredjaji.service';
import { Objekat } from 'src/helperComponents/objekat';
import { ModalComponent } from '../modal/modal/modal.component';
import { DashboardService } from 'src/app/services/dashboard.service';
import { Prostorija } from 'src/helperComponents/prostorija';

@Component({
  selector: 'app-uredjaji',
  templateUrl: './uredjaji.component.html',
  styleUrls: ['./uredjaji.component.scss','../dashboard/bd2.css'],
  encapsulation:ViewEncapsulation.None
})
export class UredjajiComponent implements OnInit{

  objectId : number = 0;
  sviUredjaji : any;
  potrosaci : any = [];
  proizvodjaci : any = [];
  skladista : any = [];
  tipoviProstorija: any = [];
  modalRef: MdbModalRef<ModalComponent> | null = null;
  objektiKorisnika?: Objekat | any;
  arrayOfArrays:any;

  constructor(private uredjajiService : UredjajiService,
            private messageService: MessageService,
            private router: Router,
            private modalService: MdbModalService,
            private dashboardService: DashboardService) {}

  ngOnInit(){
    this.objectId = parseInt(localStorage.getItem('objectId')!!);
    this.ucitajObjekteKorisnika(localStorage.getItem('userId'));
    this.uredjajiService.getSviUredjaji(this.objectId).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.sviUredjaji = JSON.parse(json) as Objekat;
      this.potrosaci = this.sviUredjaji.filter((val) => val.tipUredjaja == "Potrošač");
      this.proizvodjaci = this.sviUredjaji.filter((val) => val.tipUredjaja == "Proizvođač");
    })
    this.uredjajiService.getSkladista(this.objectId).subscribe((res) => {
      let json = JSON.stringify(res);
      this.skladista = JSON.parse(json);
    })

    this.uredjajiService.getObjectRooms(this.objectId).subscribe(res => {
      let json = JSON.stringify(res);
      this.tipoviProstorija = JSON.parse(json) as Prostorija;
      this.tipoviProstorija.forEach(element => {
        this.uredjajiService.getUredjajePoSobama(this.objectId,element.naziv).subscribe(res => {
          element.uredjaji = res;
        })
      });
    })
  }

  changeObjekat(event: any) {
    localStorage.setItem('objectId',(event.target.value).toString());
    window.location.reload();
  }

  ucitajObjekteKorisnika(idKorisnika:any){
    this.dashboardService.getObjekteKorisnika(idKorisnika).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.objektiKorisnika = JSON.parse(json) as Objekat;
      this.objektiKorisnika.forEach(element => {
        element.fullAddress = element.ulica + " " + element.adresniBroj + ", " + element.naselje + ", " + element.grad;
      });
    });
  }

  openModal(br:number) {
    let variable;
    this.modalRef = this.modalService.open(ModalComponent,{
        data: { ukljucen: variable, situacija: br},
    });
  }

  navigate(event:any,id:number)
  {
    if(event.target.classList[0] != undefined && event.target.classList[0] != "fa-power-off"){
      this.router.navigate(['uredjaj', id]);
    }
  }


}
