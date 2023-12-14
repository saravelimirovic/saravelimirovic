import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Route, Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AuthService } from 'app/services/services/auth.service';
import { ResetPasswordService } from 'app/services/services/userService/reset-password.service';
// import { ToastService } from 'app/services/toast.service';
import { latLng } from 'leaflet';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss', './login.css']
})
export class LoginComponent {

  type: string="password";
  isText: boolean=false;
  eyeIcon: string = "fa-eye-slash";
  loginForm!:FormGroup;

  // za Zaboravili ste lozinku
  public resetPasswordEmail!: string;
  public isValidEmail!: boolean;
  helper = new JwtHelperService();
  

  constructor( private forma: FormBuilder,
               private auth: AuthService,
               private router: Router,
               private toastr: ToastrService,
               private messageService : MessageService,
               private resetService: ResetPasswordService // za Zaboravili ste lozinku
               ){}

  ngOnInit():void{
    localStorage.clear()
    this.loginForm=this.forma.group({
      email:['',Validators.required],
      password:['',Validators.required],
    })
  }

  sakrijPrikazi(){
   this.isText=!this.isText;
   this.isText ? this.eyeIcon="fa-eye" : this.eyeIcon = "fa-eye-slash";
   this.isText ? this.type = "text" : this.type = "password";
  }

  onLogin(){
    if (this.loginForm.valid) {
      this.auth.login(this.loginForm.value).subscribe(
        { next:(res)=>{
          this.toastr.success("Uspešna prijava!","", {
            timeOut: 2000
          });

        var t = this.auth.getToken()
        const decodeToken = this.helper.decodeToken(t)
        if( decodeToken.role == 'admin')
          this.router.navigate(['dispeceri'])
          else
          this.router.navigate(['pocetna'])
        }, error:(err)=>{
          this.toastr.error("Pogrešna lozinka ili email adresa!","Greška", {
            timeOut: 2000
            });

        }
        })
      //u slucaju da se loguje dso koji registruje prosumere
    } else {
      this.validateAllFormFileds(this.loginForm);
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


  // za Zaboravili ste lozinku
  // da vidim dal da zapravo saljem podatak iz baze
  checkValidEmail(event: string) {
    const value = event;
    const pattern = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/; // za email regex
    this.isValidEmail = pattern.test(value);

    return this.isValidEmail;
  }

  confirmToSend() {
    if(this.checkValidEmail(this.resetPasswordEmail)) {

      
      // API calling
      this.resetService.sendResetPasswordLink(this.resetPasswordEmail)
      .subscribe({
        next:(res)=> {
          this.toastr.success("Uspešno Vam je poslat email!","", {
            timeOut: 2000
          });

          this.resetPasswordEmail = "";
          const buttonRef = document.getElementById("closeBtn");
          buttonRef?.click();
        },
        error:(err)=> {
          this.toastr.error("Došlo je do greške pri slanju email-a!","Greška", {
            timeOut: 2000
          });
        }
      })
    }
  }

}
