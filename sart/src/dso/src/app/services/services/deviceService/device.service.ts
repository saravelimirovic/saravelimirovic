import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'app/environment/environment';
import { AllDevices } from 'app/models/allDevices';
import { AllUsers } from 'app/models/allUsers';
import { DeviceInfo } from 'app/models/deviceInfo';
import { DevicePerHour } from 'app/models/devicePerHour';
import { RecordForDay } from 'app/models/recordForDay';
import { UredjajiPoObjektu } from 'app/models/uredjajiPoObjektu';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private baseUrl = environment.apiUrl;
  constructor(private http : HttpClient) { }

  getAllDevice() : Observable<AllDevices[]>{
    return this.http.get<AllDevices[]>(`${this.baseUrl}/api/Record/dsoPotrPoUredjaju`)
  }
  getAllDevicePieChart() : Observable<AllDevices[]>{
    return this.http.get<AllDevices[]>(`${this.baseUrl}/api/Record/dsoPotrPoUredjajuPieChart`)
  }
  getAllDeviceProiz() : Observable<AllDevices[]>{
    return this.http.get<AllDevices[]>(`${this.baseUrl}/api/Record/dsoProizPoUredjaju`)
  }
  getAllDeviceProizPieChart() : Observable<AllDevices[]>{
    return this.http.get<AllDevices[]>(`${this.baseUrl}/api/Record/dsoProizPoUredjajuPieChart`)
  }
  getAllDevicePP() : Observable<AllDevices[]>{
    return this.http.get<AllDevices[]>(`${this.baseUrl}/api/Record/dsoUredjajiSumPotrPro`)
  }
  getDevicesByIdUser(id: any): Observable<UredjajiPoObjektu[]>{
    return this.http.get<UredjajiPoObjektu[]>(`${this.baseUrl}/api/Record/dsoUredjajiKorisnika/`+ id)
  }
  getDevicesByIdObject(id: any): Observable<UredjajiPoObjektu[]>{
    return this.http.get<UredjajiPoObjektu[]>(`${this.baseUrl}/api/Record/dsoUredjajiObjekta/`+ id)
  }
  getUsageDevicePerHoursById(objekatUredjajId: any): Observable<DevicePerHour[]>{
    return this.http.post<DevicePerHour[]>(`${this.baseUrl}/api/Record/prosRecordDevice1d?objekatUredjajId=`+ objekatUredjajId,objekatUredjajId)
  }
  getAllPotByObject(objekatId: any): Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/api/Record/dsoPotrosnjaUredjajaObjektaVidi/`+ objekatId)
  }
  getAllProizByObject(objekatId: any): Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/api/Record/dsoProizvodnjaUredjajaObjektaVidi/`+ objekatId)
  }

  getStorageByObject(objekatId: any):Observable<Storage[]>{
    return this.http.post<Storage[]>(`${this.baseUrl}/api/Device/prosObjectStore?objekatId=`+ objekatId,objekatId)

  }
  promeniStatusUredjaja(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosUpdateStatusObjectDevice?objectDeviceId=`+idUredjaja, idUredjaja);
  }


  //za info uredjaja

  getPojedinacanUredjaj(idUredjaja:any): Observable<DeviceInfo>{
    return this.http.post<DeviceInfo>(`${this.baseUrl}/api/Device/prosDeviceInfo?objekatUredjajId=`+idUredjaja,idUredjaja);
  }


  //za pojedinacne uredjaje
  //dan
  getPojedinacanDanas(idUredjaja:any): Observable<RecordForDay[]>{
    return this.http.post<RecordForDay[]>(`${this.baseUrl}/api/Record/prosRecordDevice1d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }
  getPojedinacanDanasPredikcija(idUredjaja:any): Observable<RecordForDay[]>{
    return this.http.post<RecordForDay[]>(`${this.baseUrl}/api/Record/prosRecordPred2d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }
  //nedelja
  getPojedinacanNedelja(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevice7d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }
  getPojedinacanNedeljaIstPred(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePredPast7d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }

  getPojedinacanNedeljaPredikcija(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePred7d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }

  //mesec
  getPojedinacanMesec(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevice31d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }
  getPojedinacanMesecIstPred(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePredPast31d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }

  //godina
  getPojedinacanGodina(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevice365d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }
  getPojedinacanGodinaIstPred(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePredPast365d?objekatUredjajId=`+idUredjaja,idUredjaja);
  }








}
