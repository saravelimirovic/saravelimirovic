import { Component, Input, OnInit } from '@angular/core';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { ToastrService } from 'ngx-toastr';
import { UredjajiService } from 'src/app/services/uredjaji.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { Prostorija } from 'src/helperComponents/prostorija';
import { ModelUredjaja } from 'src/helperComponents/arrow/ModelUredjaja';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss','../../dashboard/bd2.css']
})
export class ModalComponent implements OnInit{

  objectId : number = 0;
  deviceId? : string;
  ukljucen: string | null = null;
  changeOnOf: string = "";
  situacija?: number;
  sobe: any[] = [];
  vrsteUredjaja: any[] = [];
  sveVrste: any[] = [];
  proizvodjaci: any[] = [];
  potrosaci: any[] = [];
  modeliUredjaja: any = [];
  dozvolaPregled: any[] = [];
  tip: any[] = [];
  dozvolaUpravljanje: any[] = [];
  selectedVrstaPotrosaci: any = 1;
  selectedVrstaProizvodjac: any = 12;
  selectedDozvolaPregled: any;
  selectedDozvolaUpravljanje: any;
  selectedTip: any;
  selectedSoba: any;
  selectedModelUredjaja: any = 1;
  customNazivUredjaja?: string = "";
  customPotrosnjaMirovanje?: number = 0;
  customProsecnaPotrosnja?: number = 0;

  constructor(public modalRef: MdbModalRef<ModalComponent>,private uredjajiService: UredjajiService,private toastr: ToastrService, private router: Router) {

    this.tip = [
      { id:1, name: 'Potrošač'},
      { id:2, name: 'Proizvođač'},
    ];
     this.dozvolaPregled = [
        { id:1, name: 'Da'},
        { id:2, name: 'Ne'},
      ];
    this.dozvolaUpravljanje = [
        { id:1, name: 'Da'},
        { id:2, name: 'Ne'},
];
  }

  ngOnInit(): void {
    if(this.situacija == 0 || this.situacija == 3)
    {
      if(this.ukljucen?.toUpperCase() == "DA") this.changeOnOf = "isključite";
      else this.changeOnOf = "uključite";
    }
    else if(this.situacija == 1 || this.situacija == 2)
    {
      if(this.ukljucen?.toUpperCase() == "DA") this.changeOnOf = "oduzmete";
      else this.changeOnOf = "date";
    }
    else if(this.situacija == 6){
      this.objectId = parseInt(localStorage.getItem('objectId')!!);
      this.uredjajiService.getAllProstorije().subscribe(res => {
        this.sobe = res;
      })
      this.uredjajiService.getPotrosace().subscribe((res) => {
        this.potrosaci = res;
      })
      this.uredjajiService.getProizvodjace().subscribe((res) => {
        this.proizvodjaci = res;
      })
      this.uredjajiService.getUredjajPoVrsti(this.selectedVrstaPotrosaci).subscribe((res) => {
        let json = JSON.stringify(res);
        this.modeliUredjaja = JSON.parse(json) as ModelUredjaja;
        let id = this.modeliUredjaja[this.modeliUredjaja.length - 1].id;
        this.modeliUredjaja.push(new ModelUredjaja(id+1,'Drugo'));
      })
      this.uredjajiService.getPotrosace().subscribe((res) => {
        this.potrosaci = res;
      })
      this.uredjajiService.getProizvodjace().subscribe((res) => {
        this.proizvodjaci = res;
      })
    }
  }

  changeModeleUredjaja(){
    this.uredjajiService.getUredjajPoVrsti(this.selectedTip == 1 ? this.selectedVrstaPotrosaci : this.selectedVrstaProizvodjac).subscribe((res) => {
      let json = JSON.stringify(res);
      this.modeliUredjaja = JSON.parse(json) as ModelUredjaja;
      let id = this.modeliUredjaja[this.modeliUredjaja.length - 1].id;
      this.modeliUredjaja.push(new ModelUredjaja(id+1,'Drugo'));
    })
  }

  changeDozvole(event:any){
    if(event.value == 2) this.selectedDozvolaUpravljanje = 2;
  }

  findNameById(id:number){
    let uredjaj = this.modeliUredjaja.filter((uredjaj) => uredjaj.id == id);
    return uredjaj[0].naziv;
  }

  accept(){
    if(this.situacija == 0)
    {
      this.uredjajiService.promeniStatusUredjaja(this.deviceId!!).subscribe((val) => {
        if(val.message == 1)
        {
          let status;
          if(this.ukljucen?.toUpperCase() == "DA") status = "NE";
          else status = "DA";
          this.uredjajiService.changeDeviceStatus(status);
        }
      })
    }
    else if(this.situacija == 1)
    {
      this.uredjajiService.promeniPermissionUredjaja(this.deviceId!!).subscribe((val) => {
        if(val.message == 1)
        {
          let status;
          if(this.ukljucen?.toUpperCase() == "DA") {
            status = "NE";
            this.uredjajiService.promeniControlUredjaja(this.deviceId!!).subscribe((val) => {
              if(val.message == 1){
                this.uredjajiService.changeDeviceControl(status);
              }
            })
          }
          else status = "DA";
          this.uredjajiService.changeDevicePermission(status);
        }
      })
    }
    else if(this.situacija == 2)
    {
      this.uredjajiService.promeniControlUredjaja(this.deviceId!!).subscribe((val) => {
        if(val.message == 1)
        {
          let status;
          if(this.ukljucen?.toUpperCase() == "DA") status = "NE";
          else status = "DA";
          this.uredjajiService.changeDeviceControl(status);
        }
      })
    }
    else if(this.situacija == 3)
    {
      this.uredjajiService.promeniStatusUredjaja(this.deviceId!!).subscribe((val) => {
        if(val.message == 1)
        {
          let status;
          if(this.ukljucen?.toUpperCase() == "DA") status = "NE";
          else status = "DA";
          this.uredjajiService.changeDeviceStatus(status,this.deviceId!!);
        }
      })
    }
    else if(this.situacija == 4)
    {
      this.uredjajiService.izbrisiUredjaj(this.deviceId!!).subscribe((val) => {
          this.toastr.success("Uređaj je uspešno obrisan!","", {
            timeOut: 2000
          });
          this.router.navigate(['/uredjaji']);
      })
    }
    else if(this.situacija == 5)
    {
      this.uredjajiService.izbrisiSkladiste(this.deviceId!!).subscribe((val) => {
          this.toastr.success("Skladište je uspešno obrisano!","", {
            timeOut: 2000
          });
          this.router.navigate(['/uredjaji']);
      })
    }
    else if(this.situacija == 6)
    {
      this.modalRef.close();
      Swal.fire({
        title: 'Molimo sačekajte!',
        html: 'Dodavanje novog uređaja je u toku.',
        allowEscapeKey: false,
        allowOutsideClick: false,
        background: '#1f262e',
        color:'#fff',
        showConfirmButton: false,
        timerProgressBar: true,
        didOpen: () => {
          Swal.showLoading()
        }
      });
      this.uredjajiService.dodajNoviUredjaj(
        this.objectId,
        this.selectedSoba,
        this.selectedModelUredjaja == this.modeliUredjaja[this.modeliUredjaja.length - 1].id ? this.customNazivUredjaja!! : this.findNameById(this.selectedModelUredjaja),
        this.selectedTip == 1 ? this.selectedVrstaPotrosaci : this.selectedVrstaProizvodjac,
        this.selectedTip,
        this.selectedModelUredjaja == this.modeliUredjaja[this.modeliUredjaja.length - 1].id ? this.customPotrosnjaMirovanje!! : 0,
        this.selectedModelUredjaja == this.modeliUredjaja[this.modeliUredjaja.length - 1].id ? this.customProsecnaPotrosnja!! : 0,
        "Ne",
        this.dozvolaPregled[this.selectedDozvolaPregled - 1].name,
        this.dozvolaUpravljanje[this.selectedDozvolaUpravljanje - 1].name
        ).subscribe(res => {
        if(res.message == 1){
          Swal.close();
          Swal.fire({
            icon: 'success',
            title: 'Uspešno!',
            background: '#1f262e',
            color:'#fff',
            text: 'Uređaj je uspešno dodat!'
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
            }
          });
        }
      },(error) => {
          Swal.close();
          Swal.fire({
            icon: 'error',
            title: 'Greška!',
            background: '#1f262e',
            color:'#fff',
            text: 'Došlo je do greške pri dodavanju uređaja!',
          });
        }
      )

    }
    this.modalRef.close();
  }
}
