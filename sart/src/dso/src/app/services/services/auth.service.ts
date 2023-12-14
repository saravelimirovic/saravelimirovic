import { Injectable } from '@angular/core';
import {HttpClient} from  '@angular/common/http'
import { environment } from 'app/environment/environment';
import { map, ReplaySubject } from 'rxjs';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Korisnik } from '../../models/korisnik'
import { AESencriptorService } from '../aesencriptor.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = environment.apiUrl;
  helper = new JwtHelperService();

  //da cuva trentuno ulogovanog - za sada se ne koristi
  private currentUserSource = new ReplaySubject<any>(1);
  currentUser$ = this.currentUserSource.asObservable();

  //ulogavan korisnik informacije
  //prebaciti u neki model
  id : any;
  rolaNaziv : any;
  email : any


  constructor( private http: HttpClient, private router: Router) { } 

  login(loginObj: any){
    return this.http.post<any>(`${this.baseUrl}/api/User/dsoAuthenticate`,loginObj).pipe(map(response=>{
      localStorage.setItem("token", AESencriptorService.encriptyString(response.token))
      const decodeToken = this.helper.decodeToken(response.token)
      this.currentUserSource.next(response.user)
      this.id = decodeToken.UserId
      this.email = decodeToken.unique_name
      this.rolaNaziv = decodeToken.role
    }))
  }

  getToken(){
    return AESencriptorService.decriptyString(localStorage.getItem('token')!)
  }

  isLoggedIn():boolean{

    return !!localStorage.getItem('token')
  }

  isLoggedDis():boolean{
    var dispecer = false
    const decodeToken = this.helper.decodeToken(this.getToken())
    if(decodeToken.role == "dispečer") dispecer = true

    return dispecer
  }

  isLoggedAdmin():boolean{
    var admin = false
    const decodeToken = this.helper.decodeToken(this.getToken())
    if(decodeToken.role == "admin") admin = true

    return admin
  }

  logOut(){
    localStorage.clear()
    this.router.navigate(['login'])
  }
  signUp(signUpObj: any){
    return this.http.post<any>(`${this.baseUrl}/api/User/register`,signUpObj);
  }
}
