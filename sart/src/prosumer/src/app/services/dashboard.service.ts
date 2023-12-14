import { Injectable } from '@angular/core';
import{HttpClient, HttpHeaders} from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environment/environment';
import { AESencriptorService } from './aesencriptor.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private baseUrl:string = environment.apiUrl;
  constructor(private http : HttpClient,private router: Router) {}
  headers = new HttpHeaders().set("Authorization", "Bearer " + AESencriptorService.decriptyString(localStorage.getItem('token')!!));


  //Skladisteno
  getSkladisteno(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosObjectSumOfStore?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }

  //Potrosnja
  getPotrosnja1d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnja1d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja7d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnja7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja31d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnja31d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja365d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPast365d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja1dPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPred2d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja7dPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPred7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja7dIstorijaPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPredPast7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja31dIstorijaPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPredPast31d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja365dIstorijaPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPredPast365d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }

  //Proizvodnja
  getProizvodnja1d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnja1d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja7d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnja7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja31d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnja31d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja365d(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPast365d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja1dPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPred2d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja7dPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPred7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja7dIstorijaPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPredPast7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja31dIstorijaPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPredPast31d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja365dIstorijaPred(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPredPast365d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }

  getPotrosnja1dProcenat(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPercent1d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja7dProcenat(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPercent7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPotrosnja31dProcenat(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosPotrosnjaPercent31d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja1dProcenat(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPercent1d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja7dProcenat(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPercent7d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getProizvodnja31dProcenat(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosProizvodnjaPercent31d?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }

  getObjekteKorisnika(idKorisnika:any){
    return this.http.post<any>(`${this.baseUrl}/api/Object/prosAllObjects?id=`+idKorisnika,{
      headers: this.headers
    });
  }
  getPodaciOKorisniku(idKorisnika:any){
    return this.http.post<any>(`${this.baseUrl}/api/User/prosUserInfo?id=`+idKorisnika,{
      headers: this.headers
    });
  }
}
