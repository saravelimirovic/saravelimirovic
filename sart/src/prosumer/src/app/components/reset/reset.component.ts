import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ConfirmPasswordValidator } from './confirm-password.validator';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { ResetPasswordService } from 'src/app/services/reset-password.service';
import { ResetPassword } from 'src/app/models/resetPassword';

@Component({
  selector: 'app-reset',
  templateUrl: './reset.component.html',
  styleUrls: ['./reset.component.scss']
})
export class ResetComponent implements OnInit {
  
  resetPasswordForm!: FormGroup;
  emailToReset!: string;
  emailToken!: string;
  resetPasswordObj = new ResetPassword();

  

  constructor(private fb: FormBuilder,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private toastr: ToastrService,
              private resetService: ResetPasswordService) { }

  ngOnInit(): void {
      this.resetPasswordForm = this.fb.group({
        password: [null, Validators.required],
        confirmPassword: [null, Validators.required]
      }, {
        validator: ConfirmPasswordValidator("password", "confirmPassword")
      });

      this.activatedRoute.queryParams.subscribe(val => {
        this.emailToReset = val['email'];
        let uriToken = val['code'];
        this.emailToken = uriToken.replace(/ /g, '+');
      })
  }

  reset() {
    if(this.resetPasswordForm.valid) {
      this.resetPasswordObj.email = this.emailToReset;
      this.resetPasswordObj.newPassword = this.resetPasswordForm.value.password;
      this.resetPasswordObj.confirmPassword = this.resetPasswordForm.value.confirmPassword;
      this.resetPasswordObj.emailToken = this.emailToken;

      this.resetService.resetPassword(this.resetPasswordObj)
      .subscribe({
        next:(res)=> {
          this.toastr.success("Uspešno izvršeno resetovanje lozinke!","", {
            timeOut: 2000
          });

          this.router.navigate(['/login'])
        },
        error:(err)=> {
          this.toastr.error("Neuspešno resetovanje lozinke! Link za resetovanje je istekao!","Greška", {
            timeOut: 2000
          });
        }
      })
    }
    else {
      this.toastr.error("Forma nije pravilno popunjena!","Greška", {
        timeOut: 2000
        });
        this.router.navigate(['/login']);
    }
  }

  // sakrijPrikazi(){
  //   this.isText=!this.isText;
  //   this.isText ? this.eyeIcon="fa-eye" : this.eyeIcon = "fa-eye-slash";
  //   this.isText ? this.type = "text" : this.type = "password";
  //  }
}
