import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'app/environment/environment';
import { AllUsers } from 'app/models/allUsers';
import { Dispecer } from 'app/models/dispecer';
import { editDTO } from 'app/models/editDTO';
import { FilterDTO } from 'app/models/filterDTO';
import { FilterDisDTO } from 'app/models/filterDisDTO';
import { Korisnik } from 'app/models/korisnik';
import { KorisnikPrikaz } from 'app/models/korisnikPrikaz';
import { PagingDTO } from 'app/models/pagingDTO';
import { AESencriptorService } from 'app/services/aesencriptor.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserServiceService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getAllUsers(pagingInfo: PagingDTO):Observable<Korisnik[]> {
    return this.http.post<Korisnik[]>(`${this.baseUrl}/api/User/tabelaUsers/`, pagingInfo)
  }

  getUsersFiltered(filterInfo: FilterDTO): Observable<Korisnik[]>{
    return this.http.post<Korisnik[]>(`${this.baseUrl}/api/Table/filterTabela/`, filterInfo)
  }

  getUser(id: any):Observable<KorisnikPrikaz>{
    return this.http.get<KorisnikPrikaz>(`${this.baseUrl}/api/User/profilUser/`+id)
  }

  //edit
  editUser(editDTO: editDTO){
    return this.http.post<any>(`${this.baseUrl}/api/User/editUser`,editDTO);
  }

  //dispeceri
  getAllDis(filterInfo: FilterDisDTO):Observable<Dispecer[]>{
    return this.http.post<Dispecer[]>(`${this.baseUrl}/api/Table/filterTabelaAdmin/`, filterInfo)
    
  }

  //sve usere
  getAllUsersCount():Observable<AllUsers>{
    return this.http.get<AllUsers>(`${this.baseUrl}/api/User/usersCount`)
  }
   //ostao deo koda za header kada se odradi autorizacija na beku
   //brisanje korisnika
   deteleUser(id : string){
    // var headers = new HttpHeaders().set("Authorization", "Bearer " + AESencriptorService.decriptyString(sessionStorage.getItem("token")!).trim())
    return this.http.delete(`${this.baseUrl}/api/User/dsoDeleteUser?userId=`+id)
  }


  getTop3Potrosaca():Observable<Korisnik[]>{
    return this.http.get<Korisnik[]>(`${this.baseUrl}/api/User/dsoTopPotrosaca`)
  }

  getTop3Proizvodjaca():Observable<Korisnik[]>{
    return this.http.get<Korisnik[]>(`${this.baseUrl}/api/User/dsoTopProizvodjaca`)
  }
}
