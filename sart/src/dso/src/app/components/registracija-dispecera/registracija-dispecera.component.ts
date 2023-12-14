import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'app/services/services/auth.service';
import { Rola } from 'app/models/rola';
// import { NgToastService} from 'ng-angular-popup'
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { SignUp } from 'app/models/signUp';

@Component({
  selector: 'app-registracija-dispecera',
  templateUrl: './registracija-dispecera.component.html',
  styleUrls: ['./registracija-dispecera.component.scss']
})
export class RegistracijaDispeceraComponent {

  type: string="password";
  isText: boolean=false;
  eyeIcon: string = "fa-eye-slash";
  signUpForm!:FormGroup;
  role : Rola[]=[]
  rolaId?:Number;
  niz :any[];
  idKor:any;
  signUp1: SignUp


  constructor( private forma: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private toastr: ToastrService
    // private toast : NgToastService
    ){}
 
  ngOnInit(){
    this.role=[{_id:'2', naziv:'Dispečer'}];
     this.rolaId=2;
    this.signUpForm = this.forma.group({
      ime: ['', Validators.required],
      prezime: ['', Validators.required],
      jmbg: ['', Validators.compose([Validators.required,Validators.pattern('[0-9]{13}')])],
      ulica: ['', Validators.required],
      adresniBroj: ['', Validators.required],
      naselje: ['', Validators.required],
      grad: ['', Validators.required],
      brojTelefona: ['', Validators.required],
      email: ['', Validators.compose([Validators.required,Validators.email])],
      lozinka: ['', Validators.required],
      // rolaId:['', Validators.required],
    })
  }

  sakrijPrikazi(){
   this.isText=!this.isText;
   this.isText ? this.eyeIcon="fa-eye" : this.eyeIcon = "fa-eye-slash";
   this.isText ? this.type = "text" : this.type = "password";
  }

  onSignUp(){
    if (this.signUpForm.valid) {
      // console.log(this.signUpForm.value)
      this.signUp1 = this.signUpForm.value
      // console.log(this.signUp1)
      this.signUp1.rolaId = 2 + ''
      this.auth.signUp(this.signUp1).subscribe(res=>
      { 
        if(res.message==1){
          this.toastr.success("Uspešno izvršena registracija!","", {
            timeOut: 2000
          });
        }
          this.signUpForm.reset()
      }
      
      )
      //u slucaju da se loguje dso koji registruje prosumere
    } else {
      this.validateAllFormFileds(this.signUpForm);
      // this.toastr.error("Greška pri registraciji!","Greška", {
      //   timeOut: 2000
      //   });
      alert("Greška pri registraciji!")
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

}
