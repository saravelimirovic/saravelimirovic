import { Injectable } from '@angular/core';
import{HttpClient, HttpHeaders} from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environment/environment';
import { BehaviorSubject } from 'rxjs';
import { AESencriptorService } from './aesencriptor.service';

@Injectable({
  providedIn: 'root'
})
export class UredjajiService {

  private baseUrl:string = environment.apiUrl;

  public deviceStatus: BehaviorSubject<string> = new BehaviorSubject<string>("");
  public buttonStatus: BehaviorSubject<string> = new BehaviorSubject<string>("");
  public devicePermission: BehaviorSubject<string> = new BehaviorSubject<string>("");
  public deviceControl: BehaviorSubject<string> = new BehaviorSubject<string>("");
  headers = new HttpHeaders().set("Authorization", "Bearer " + AESencriptorService.decriptyString(localStorage.getItem('token')!!));

  constructor(private http : HttpClient,private router: Router) { }

  changeDeviceStatus(status: string, id?:string){
    this.deviceStatus.next(status);
    if(status) this.buttonStatus.next(id!!);
  }
  changeDevicePermission(status: string){
    this.devicePermission.next(status);
  }
  changeDeviceControl(status: string){
    this.deviceControl.next(status);
  }

  promeniStatusUredjaja(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosUpdateStatusObjectDevice?objectDeviceId=`+idUredjaja,{
      headers: this.headers
    });
  }
  promeniPermissionUredjaja(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosUpdatePermissionObjectDevice?objectDeviceId=`+idUredjaja,{
      headers: this.headers
    });
  }
  promeniControlUredjaja(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosUpdateControlObjectDevice?objectDeviceId=`+idUredjaja,{
      headers: this.headers
    });
  }
  izbrisiUredjaj(idUredjaja:any){
    return this.http.delete<any>(`${this.baseUrl}/api/Device/prosDeleteDevice?objectDeviceId=`+idUredjaja,{
      headers: this.headers
    });
  }
  izbrisiSkladiste(idUredjaja:any){
    return this.http.delete<any>(`${this.baseUrl}/api/Device/prosDeleteStore?objectStoreId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getSviUredjaji(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosObjectDevices?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getSkladista(idObjekta:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosObjectStore?objekatId=`+idObjekta,{
      headers: this.headers
    });
  }
  getPojedinacanUredjaj(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosDeviceInfo?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanDanas(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevice1d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanDanasPredikcija(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordPred2d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanNedelja(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevice7d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanNedeljaIstPred(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePredPast7d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanNedeljaPredikcija(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePred7d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanMesec(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevice31d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanMesecIstPred(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePredPast31d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanGodina(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevice365d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getPojedinacanGodinaIstPred(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordDevicePredPast365d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }

  get1dProcenat(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordPercent1d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  get7dProcenat(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordPercent7d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  get31dProcenat(idUredjaja:any){
    return this.http.post<any>(`${this.baseUrl}/api/Record/prosRecordPercent31d?objekatUredjajId=`+idUredjaja,{
      headers: this.headers
    });
  }
  getVrsteUredjaja(){
    return this.http.get<any>(`${this.baseUrl}/api/Device/prosSpeciesOfDevice`,{
      headers: this.headers
    });
  }

  dodajNoviUredjaj(objekatId:number,prostorijaId:number,nazivUredjaja:string,vrstaUredjajaId:number,tipUredjajaId:number,pPrilikomMirovanja:number,
    prosecnaPotrosnja:number,ukljucen:string,dozvola:string,kontrola:string){
    return this.http.post<any>(`${this.baseUrl}/api/Device/prosInsertObjectDevice`,{
      "objekatId": objekatId,
      "prostorijaId": prostorijaId,
      "nazivUredjaja": nazivUredjaja,
      "vrstaUredjajaId": vrstaUredjajaId,
      "tipUredjajaId": tipUredjajaId,
      "pPrilikomMirovanja": pPrilikomMirovanja,
      "prosecnaPotrosnja": prosecnaPotrosnja,
      "ukljucen": ukljucen,
      "dozvola": dozvola,
      "kontrola": kontrola
    },
    {headers: this.headers});
}

getObjectRooms(objectId:any){
  return this.http.post<any>(`${this.baseUrl}/api/Device/prosRooms?objekatId=`+objectId,{
    headers: this.headers
  });
}

getUredjajePoSobama(objectId:any,nazivSobe:any){
  return this.http.post<any>(`${this.baseUrl}/api/Device/prosObjectDevicesForRoom?objekatId=${objectId}&nazivPostorije=${nazivSobe}`,{
    headers: this.headers
  });
}

getPotrosace(){
  return this.http.get<any>(`${this.baseUrl}/api/Device/prosGetSpeciesDevicePotrosaci`,{
    headers: this.headers
  });
}

getProizvodjace(){
  return this.http.get<any>(`${this.baseUrl}/api/Device/prosGetSpeciesDeviceProizvodjaci`,{
    headers: this.headers
  });
}

getUredjajPoVrsti(vrstaUredjaja:any){
  return this.http.post<any>(`${this.baseUrl}/api/Device/prosGetDeviceBySpecies?vrstaId=${vrstaUredjaja}`,{
    headers: this.headers
  });
}

getAllProstorije(){
  return this.http.get<any>(`${this.baseUrl}/api/Device/prosGetAllProstorije`,{
    headers: this.headers
  });
}
}
