import { Component, HostListener, OnInit, ViewEncapsulation } from '@angular/core';
import {Location} from '@angular/common';
import { UredjajiService } from 'src/app/services/uredjaji.service';
import { ActivatedRoute } from '@angular/router';
import { MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { ModalComponent } from '../modal/modal/modal.component';

@Component({
  selector: 'app-skladiste-pojedinacno',
  templateUrl: './skladiste-pojedinacno.component.html',
  styleUrls: ['./skladiste-pojedinacno.component.scss','../uredjaji-pojedinacno/bd2.css'],
  encapsulation:ViewEncapsulation.None,
})
export class SkladistePojedinacnoComponent implements OnInit{

  basicData: any;
  objectId : number = 0;
  basicOptions: any;
  isMobile?: boolean;
  screenWidth = 0;
  uredjajId: string = "0";
  uredjaj: any = {};
  skladista: any;

  modalRef: MdbModalRef<ModalComponent> | null = null;


  constructor(private _location: Location,private uredjajiService : UredjajiService, private route: ActivatedRoute, private modalService: MdbModalService) {}

  ngOnInit(){

    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500) this.isMobile = true;
    else this.isMobile = false;

    this.uredjajId = this.route.snapshot.paramMap.get('id')!!;
    this.objectId = parseInt(localStorage.getItem('objectId')!!);
    this.uredjajiService.getSkladista(this.objectId).subscribe((res) => {
      let json = JSON.stringify(res);
      this.skladista = JSON.parse(json);
      this.skladista.filter((val) => val.id == this.uredjajId);
      this.uredjaj = this.skladista[0];
    })
  }

  openModal(br:number) {
    let variable;
    if(br == 0) variable = this.uredjaj.status;
    else if(br == 1) variable = this.uredjaj.dozvolaZaPregled;
    else variable = this.uredjaj.dozvolaZaUpravljanje;

    this.modalRef = this.modalService.open(ModalComponent,{
        data: { ukljucen: variable, deviceId: this.uredjajId, situacija: br},
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event:any){
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500){
      this.isMobile = true;
    }else{
      this.isMobile = false;
    }
  }

  backClicked() {
    this._location.back();
  }


}
