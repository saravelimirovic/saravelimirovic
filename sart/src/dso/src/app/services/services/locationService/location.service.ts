import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Location } from 'app/models/location';
import { environment } from 'app/environment/environment';
import { Observable } from 'rxjs';
import { FilterDTO } from 'app/models/filterDTO';


@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private baseUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  getLocations(filterInfo: FilterDTO): Observable<Location[]>{
    return this.http.post<Location[]>(`${this.baseUrl}/api/Table/filterTabelaMapa/`, filterInfo)
  }

  // getUsersFiltered(filterInfo: FilterDTO): Observable<Korisnik[]>{
  //   return this.http.post<Korisnik[]>(`${this.baseUrl}/api/Table/filterTabela/`, filterInfo)
  // }
}
