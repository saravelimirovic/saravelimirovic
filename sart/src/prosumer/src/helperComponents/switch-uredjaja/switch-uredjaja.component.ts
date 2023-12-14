import { Component, Input, OnInit } from '@angular/core';
import { ModalComponent } from '../../app/components/modal/modal/modal.component';
import { MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { UredjajiService } from 'src/app/services/uredjaji.service';

@Component({
  selector: 'app-switch-uredjaja',
  templateUrl: './switch-uredjaja.component.html',
  styleUrls: ['./switch-uredjaja.component.scss']
})
export class SwitchUredjajaComponent implements OnInit{

  @Input() isOn? : string;
  klasa: string = "";
  label: string = "Isključen";
  @Input() deviceId?: number;

  constructor(private modalService: MdbModalService, private uredjajiService: UredjajiService) {
    this.uredjajiService.buttonStatus.subscribe((val) =>{
      if(val == this.deviceId?.toString()){
        if(this.isOn == "DA") this.isOn = "NE";
        else this.isOn = "DA";
        this.chooseClass(this.isOn);
      }
    })
  }

  modalRef: MdbModalRef<ModalComponent> | null = null;

  ngOnInit() : void {
    this.chooseClass(this.isOn!!);
  }

  openModal(){
     this.modalRef = this.modalService.open(ModalComponent,{
        data: { ukljucen: this.isOn, deviceId: this.deviceId, situacija: 3},
    });
  }

  changeButton(){
    this.openModal();
  }

  chooseClass(isOn: string){
    if(isOn == "DA"){
      this.klasa = "off on";
      this.label = "Uključen";
    }else{
      this.klasa = "off";
      this.label = "Isključen";
    }
  }

}
