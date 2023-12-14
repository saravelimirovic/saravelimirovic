import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'app/environment/environment';
import { Observable } from 'rxjs';
import { Object1 } from 'app/models/object';


@Injectable({
  providedIn: 'root'
})
export class ObjectService {

  private baseUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  AddObject(AddObj: any){
    return this.http.post<any>(`${this.baseUrl}/api/Object/addObject`,AddObj);
  }

  GetAllObjects(IdKorisnika: any): Observable<Object1[]> {
    return this.http.post<Object1[]>(`${this.baseUrl}/api/Object/prosAllObjects?id=`+ IdKorisnika, IdKorisnika);
  }
}
