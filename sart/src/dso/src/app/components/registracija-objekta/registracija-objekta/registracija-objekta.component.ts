import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { DialogRegistrObjektaComponent } from 'app/components/dialog-registr-objekta/dialog-registr-objekta.component';
import { DialogRegistracijaComponent } from 'app/components/dialog-registracija/dialog-registracija.component';
import { DialogRegistracija1Component } from 'app/components/dialog-registracija1/dialog-registracija1.component';
import { KorisnikPrikaz } from 'app/models/korisnikPrikaz';
import { ObjectService } from 'app/services/services/objectService/object.service';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import { ToastrModule, ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-registracija-objekta',
  templateUrl: './registracija-objekta.component.html',
  styleUrls: ['./registracija-objekta.component.scss']
})
export class RegistracijaObjektaComponent {

  id: any
  korisnik: KorisnikPrikaz
  AddObjectForm!:FormGroup

  constructor(private route: ActivatedRoute,
    private service: UserServiceService,
    private object: ObjectService,
    private toastr: ToastrService,
    private forma: FormBuilder,
    private dialog: MatDialog){}

  showMe:boolean=false
  showHide()
  {
    this.showMe=!this.showMe;
    this.MakeForm();
  }
  getUser(){
    this.service.getUser(this.id).subscribe(response=>{
    this.korisnik=response

  })
}
  ngOnInit(){
    this.id = this.route.snapshot.params['id'];
    this.service.getUser(this.id).subscribe(response=>{
      this.korisnik=response

      this.MakeForm();
    })
  }

  cantbewhitespace = /^(?!\s).*$/;

  MakeForm()
  {
    // to znaci da se kupe podaci sa registracije
    if(this.showMe==false)
    {
    this.AddObjectForm = this.forma.group({
      korisnikId: [this.id, Validators.required],
      ulica: [this.korisnik.ulica, Validators.required],
      broj: [this.korisnik.adresniBroj, Validators.required],
      naselje: [this.korisnik.naselje, Validators.required],
      grad: [this.korisnik.grad, Validators.required],
      naziv: ['', Validators.required]
    })
    }
    // unose se novi podaci
    else{
      this.AddObjectForm = this.forma.group({
        korisnikId: [this.id, Validators.required],
        ulica: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
        broj: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
        naselje: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
        grad: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
        naziv: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])]
      })
    }
  }  

  openDialog(){
    this.dialog.open(DialogRegistracijaComponent);
  }

  openDialog1(){
    this.dialog.open(DialogRegistracija1Component);
  }

  openDialog2(){
    this.dialog.open(DialogRegistrObjektaComponent);
  }

AddNewObject()
{
  // if (this.AddObjectForm.valid) {
  //   this.object.AddObject(this.AddObjectForm.value).subscribe(res=>
  //   { 
  //     if(res.message==1){
  //       // this.toast.success({detail: "Uspešna registracija", summary: "Uspešno ste registrovali korisnika", duration: 5000})
  //       this.toastr.success("Uspešno izvršena registracija objekta!","", {
  //         timeOut: 2000
  //       });
  //     }
  //       this.AddObjectForm.reset()
  //   }
    
  //   )
  //   //u slucaju da se loguje dso koji registruje prosumere
  // } else {
  //   this.validateAllFormFileds(this.AddObjectForm);
  //   // this.toast.error({detail: "Neuspešna registracija", summary: "Unesite sve podatke", duration: 5000})
  //   // this.toastr.error("Greška pri registraciji objekta!","Greška", {
  //   //   timeOut: 2000
  //   //   });
  // }

  if(this.showMe==true) {
    this.GetAddress()
        .then(result => {
          if (result === 1) {
            if (this.AddObjectForm.valid) {
              this.object.AddObject(this.AddObjectForm.value).subscribe(res=> { 
                if(res.message==1){
                  // // this.toast.success({detail: "Uspešna registracija", summary: "Uspešno ste registrovali korisnika", duration: 5000})
                  // this.toastr.success("Uspešno izvršena registracija objekta!","", {
                  //   timeOut: 2000
                  // });
                  this.openDialog2();
                }
                  this.AddObjectForm.reset()
              })
            } 
            else {
              this.validateAllFormFileds(this.AddObjectForm);
                this.openDialog1();  
            }
          } 
          // ako ne postoji ta adresa
          else {
            this.openDialog();
          }
        })
        .catch(error => {
          // Error handling
        });
    }
    else {
      this.GetAddress()
        .then(result => {
          if (result === 1) {
            if (this.AddObjectForm.valid) {
              this.object.AddObject(this.AddObjectForm.value).subscribe(res=> { 
                if(res.message==1){
                  // // this.toast.success({detail: "Uspešna registracija", summary: "Uspešno ste registrovali korisnika", duration: 5000})
                  // this.toastr.success("Uspešno izvršena registracija objekta!","", {
                  //   timeOut: 2000
                  // });
                  this.openDialog2();
                }
                  this.AddObjectForm.reset()
              })
            } 
            else {
              this.validateAllFormFileds(this.AddObjectForm);
                this.openDialog1();  
            }
          } 
          // ako ne postoji ta adresa
          else {
            this.openDialog();
          }
        })
        .catch(error => {
          // Error handling
        });
    }
}
private validateAllFormFileds(formGroup:FormGroup){
  Object.keys(formGroup.controls).forEach(field=>{
    const control = formGroup.get(field);
    if(control instanceof FormControl){
      control.markAsDirty({onlySelf:true});
    }else if(control instanceof FormGroup){
      this.validateAllFormFileds(control);
    }
  })
}

private translate(word){
  var i;
  var answer = ""
    , a = {};

  a["E"]="Е";a["R"]="Р";a["T"]="Т";a["U"]="У";a["I"]="И";a["O"]="О";a["P"]="П";a["A"]="А";a["S"]="С";a["D"]="Д";a["F"]="Ф";a["G"]="Г";a["H"]="Х";
  a["J"]="Ј";a["K"]="К";a["L"]="Л";a["Z"]="З";a["C"]="Ц";a["V"]="В";a["B"]="Б";a["N"]="Н";a["M"]="М";a["e"]="е";a["r"]="р";a["t"]="т";a["u"]="у";
  a["i"]="и";a["o"]="о";a["p"]="п";a["a"]="а";a["s"]="с";a["d"]="д";a["f"]="ф";a["g"]="г";a["h"]="х";a["j"]="ј";a["k"]="к";
  a["l"]="л";a["z"]="з";a["c"]="ц";a["v"]="в";a["b"]="б";a["n"]="н";a["m"]="м";a["LJ"]="Љ";a["NJ"]="Њ";a["lj"]="љ";a["nj"]="њ";
  a["Ć"]="Ћ";a["ć"]="ћ";a["Č"]="Ч";a["č"]="ч";a["Š"]="Ш";a["š"]="ш";a["Đ"]="Ђ";a["đ"]="ђ";a["Ž"]="Ж";a["ž"]="ж";

  for (i in word){
    if (word.hasOwnProperty(i)) {
      if (a[word[i]] === undefined){
        answer += word[i];
      } else {
        answer += a[word[i]];
      }
    }
  }
  return answer;
}

GetAddress(): Promise<number>{
  return new Promise<number>((resolve, reject) => {
    var grad;
    var ulica;
    var broj;

    if(this.showMe === true) {
      grad = (<HTMLInputElement>document.getElementById("GRAD")).value;
      ulica = (<HTMLInputElement>document.getElementById("ULICA")).value;
      broj = (<HTMLInputElement>document.getElementById("BROJ")).value;
    }
    else {
      grad = this.korisnik.grad;
      ulica = this.korisnik.ulica;
      broj = this.korisnik.adresniBroj;
    }

    var requestOptions = {
      method: 'GET',
    };

    fetch("http://dev.virtualearth.net/REST/v1/Locations?countryRegion=RS&locality=" + this.translate(grad) + "&addressLine=" + broj + this.translate(ulica) + "&o=json&key=AnEFyy5lm4z6A0DvrM73UrBRHFAA8HVKFp93EdmF7pC3foV-WzbsBKit5Xs4B2nv", requestOptions)
      .then(response => response.json())
      .then(data => {
        const result = this.checkAddress(data);
        resolve(result);
      })
      .catch(error => {
        reject(error);
      });
  });

}

checkAddress(data:any)
{   
  var adresa=data.resourceSets[0].resources[0].address.addressLine;
  var drzava=data.resourceSets[0].resources[0].address.countryRegion;

  if(adresa!=undefined && drzava=="Serbia") //ako je adresa postojeca
      return 1;
  else // ako ne postoji
    return 0;    
  
}


}
