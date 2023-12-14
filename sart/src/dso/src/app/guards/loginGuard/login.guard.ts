import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from 'app/services/services/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginGuard implements CanActivate {
  constructor(private auth : AuthService, private router: Router){

  } 
  canActivate():boolean {
    if(this.auth.isLoggedIn()){
      return true
    }else{
      this.router.navigate(['login'])
      return false
    }
  }
}
