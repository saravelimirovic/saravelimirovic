import { Injectable } from '@angular/core';
import{HttpClient} from '@angular/common/http';
import { environment } from '../../environment/environment';
import { Router } from '@angular/router';
import {JwtHelperService} from '@auth0/angular-jwt';
import { AESencriptorService } from './aesencriptor.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

// private baseUrl:string = "https://localhost:7285/api/User/";

private baseUrl = environment.apiUrl;
private userPayload:any;
constructor(private http : HttpClient,private router: Router) {
  // this.userPayload = this.decodeToken();
}


login(loginObj : any){
  return this.http.post<any>(`${this.baseUrl}/api/User/prosumerAuthenticate`,loginObj);
}

logout(){
  localStorage.clear();
  this.router.navigate(['/login']);
}

storeToken(tokenValue : string){
  localStorage.setItem('token',AESencriptorService.encriptyString(tokenValue));
}

getToken(){
  return localStorage.getItem('token');
}

isLoggedIn() : boolean{
  return !!localStorage.getItem('token');
}

decodeToken(token:string){
  const jwtHelper = new JwtHelperService();
  return jwtHelper.decodeToken(token);
}

getFullNameFromToken(){
  if(this.userPayload)
    return this.userPayload.unique_name;
}

getRoleFromToken(){
  if(this.userPayload)
    return this.userPayload.role;
}

}
