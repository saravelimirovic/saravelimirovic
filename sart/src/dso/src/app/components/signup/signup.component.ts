import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'app/services/services/auth.service';
import { Rola } from 'app/models/rola';
// import { NgToastService} from 'ng-angular-popup'
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DialogRegistracijaComponent } from '../dialog-registracija/dialog-registracija.component';
import { DialogRegistracija1Component } from '../dialog-registracija1/dialog-registracija1.component';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {

  type: string="password";
  isText: boolean=false;
  eyeIcon: string = "fa-eye-slash";
  signUpForm!:FormGroup;
  role : Rola[]=[]
  rolaId?:Number;
  niz :any[];
  idKor:any;

  jmbgPattern = /^(0[1-9]|1\d|2\d|3[01])(0[1-9]|1[0-2])\d{9}$/;
  cantbewhitespace = /^(?!\s).*$/;

  constructor( private forma: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private toastr: ToastrService,
    private dialog: MatDialog
    // private toast : NgToastService
    ){}
  

    rolaIdValidator(control: Number) : ValidationErrors | null {
      const value = control;
      if (value && value > new Number(5)) {
        return { rolaId: true };
      }
      return { rolaId: true };
    }


  ngOnInit(){
    this.role=[{_id:'3', naziv:'Prosumer'},{_id:'4', naziv:'Potrošač'},{_id:'5', naziv:'Proizvođač'},
               {_id:'10', naziv:'--Izaberi status--'}];
    this.rolaId=10;
    this.signUpForm = this.forma.group({
      ime: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      prezime: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      jmbg: ['', Validators.compose([Validators.required,Validators.pattern(this.jmbgPattern)])],
      ulica: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      adresniBroj: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      naselje: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      grad: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      brojTelefona: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      email: ['', Validators.compose([Validators.required,Validators.email])],
      lozinka: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      rolaId:['', Validators.compose([Validators.required, Validators.max(5)])],
    })
  }

  sakrijPrikazi(){
   this.isText=!this.isText;
   this.isText ? this.eyeIcon="fa-eye" : this.eyeIcon = "fa-eye-slash";
   this.isText ? this.type = "text" : this.type = "password";
  }

  onSignUp(){
    // if (this.signUpForm.valid) {
    //   this.signUpForm.value.ime = this.signUpForm.value.ime.trim();
    //   this.signUpForm.value.prezime = this.signUpForm.value.prezime.trim();
    //   this.signUpForm.value.brojTelefona = this.signUpForm.value.brojTelefona.trim();
    //   this.signUpForm.value.grad = this.signUpForm.value.grad.trim();
    //   this.signUpForm.value.naselje = this.signUpForm.value.naselje.trim();
    //   this.signUpForm.value.ulica = this.signUpForm.value.ulica.trim();
    //   this.signUpForm.value.adresniBroj = this.signUpForm.value.adresniBroj.trim();
    //   this.signUpForm.value.email = this.signUpForm.value.email.trim();

    //   this.auth.signUp(this.signUpForm.value).subscribe(res=>
    //   { 
    //     if(res.message==1){
    //       this.toastr.success("Uspešno izvršena registracija!","", {
    //         timeOut: 2000
    //       });

    //       this.idKor=res.idDodatogKorisnika;
    //       // console.log(this.idKor);
    //       this.signUpForm.reset()
    //       this.router.navigate(['registracija-objekta/' + this.idKor]);
    //     }
    //     else {
    //       this.openDialog();
    //     }
    //   }
      
    //   )
    //   // do ovde ne bi trebalo nikad ni da dodje
    // } else {
    //   this.validateAllFormFileds(this.signUpForm);
    //   // this.toastr.error("Greška pri registraciji!","Greška", {
    //   //   timeOut: 2000
    //   //   });
    // }



    this.GetAddress()
      .then(result => {
        if (result === 1) {
          if (this.signUpForm.valid) {
            this.signUpForm.value.ime = this.signUpForm.value.ime.trim();
            this.signUpForm.value.prezime = this.signUpForm.value.prezime.trim();
            this.signUpForm.value.brojTelefona = this.signUpForm.value.brojTelefona.trim();
            this.signUpForm.value.grad = this.signUpForm.value.grad.trim();
            this.signUpForm.value.naselje = this.signUpForm.value.naselje.trim();
            this.signUpForm.value.ulica = this.signUpForm.value.ulica.trim();
            this.signUpForm.value.adresniBroj = this.signUpForm.value.adresniBroj.trim();
            this.signUpForm.value.email = this.signUpForm.value.email.trim();
      
            this.auth.signUp(this.signUpForm.value).subscribe(res=>
            { 
              if(res.message==1){
                this.toastr.success("Uspešno izvršena registracija!","", {
                  timeOut: 2000
                });
      
                this.idKor=res.idDodatogKorisnika;
                // console.log(this.idKor);
                this.signUpForm.reset()
                this.router.navigate(['registracija-objekta/' + this.idKor]);
              }
              else {
                this.openDialog();
              }
            }
            
            )
            // do ovde ne bi trebalo nikad ni da dodje
          } else {
            this.validateAllFormFileds(this.signUpForm);
            // this.toastr.error("Greška pri registraciji!","Greška", {
            //   timeOut: 2000
            //   });
            this.openDialog1();
          }

        // ako ne postoji ta adresa
        } else {
          this.openDialog();
        }
      })
      .catch(error => {
        // Error handling
      });
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

  openDialog(){
    this.dialog.open(DialogRegistracijaComponent);
  }

  openDialog1(){
    this.dialog.open(DialogRegistracija1Component);
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
      var grad = (<HTMLInputElement>document.getElementById("GRAD")).value;
      var ulica = (<HTMLInputElement>document.getElementById("ULICA")).value;
      var broj = (<HTMLInputElement>document.getElementById("BROJ")).value;
  
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
