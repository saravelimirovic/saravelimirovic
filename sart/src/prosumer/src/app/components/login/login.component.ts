import { Component, OnInit } from '@angular/core';
import{ FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { UserStoreService } from 'src/app/services/user-store.service';
import { MessageService } from 'primeng/api';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { JwtHelperService } from '@auth0/angular-jwt';
import { ResetPasswordService } from 'src/app/services/reset-password.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss','./login.css']
})
export class LoginComponent implements OnInit {


  type:string = "password";
  isText: boolean = false;
  eyeIcon: string = "fa-eye-slash";
  loginForm!: FormGroup;

    // za Zaboravili ste lozinku
    public resetPasswordEmail!: string;
    public isValidEmail!: boolean;
    helper = new JwtHelperService();


  constructor(private fb : FormBuilder,
              private auth : AuthService,
              private router : Router,
              private userStoreService: UserStoreService,
              private messageService : MessageService,
              private toastr: ToastrService,
              private resetService: ResetPasswordService // za Zaboravili ste lozinku
              ) {}

  ngOnInit(): void {
    localStorage.clear();
    this.loginForm = this.fb.group({
      email: ['',Validators.required],
      password:['',Validators.required]
    });
  }

  hideShowPass(){
    this.isText = !this.isText;
    this.isText ? this.eyeIcon = "fa-eye" : this.eyeIcon = "fa-eye-slash";
    this.isText ? this.type = "text" : this.type = "password";
  }

  onLogin(){
    if(this.loginForm.valid){
      this.loginForm.value.email = this.loginForm.value.email.trim();
      this.auth.login(this.loginForm.value).subscribe({
        next:(res)=>{
          this.loginForm.reset();
          this.auth.storeToken(res.token);
          const tokenPayload = this.auth.decodeToken(res.token);
          this.userStoreService.setUserId(tokenPayload.UserId);
          this.userStoreService.setFullName(tokenPayload.unique_name);
          this.userStoreService.setRole(tokenPayload.role);
          localStorage.setItem('objectId', tokenPayload.objectId);
          this.toastr.success("Uspešna prijava!","", {
            timeOut: 2000
          });
          this.router.navigate(['pocetna-strana']);
        },
        error:(err)=>{
          this.toastr.error("Pogrešna lozinka ili email adresa!","Greška", {
            timeOut: 2000
            });
        }
      })
    }else{
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
