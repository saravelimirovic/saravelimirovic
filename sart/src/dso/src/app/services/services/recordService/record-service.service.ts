import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'app/environment/environment';
import { MaxSlider } from 'app/models/MaxSlider';
import { Record } from 'app/models/record';
import { RecordForDay } from 'app/models/recordForDay';
import { RecordYear } from 'app/models/recordYear';
import { TableHistory } from 'app/models/tableHistory';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecordService {

  private baseUrl = environment.apiUrl;
  
  constructor(private http: HttpClient) { }

  ///za potrosnju do ovog trenutka i predikcija potrosnje 

  getHistoryDayP():Observable<RecordForDay[]>{
    return this.http.get<RecordForDay[]>(`${this.baseUrl}/api/Record/dsoPotrosnja1d`)
  }
  getHistoryDayPPred():Observable<RecordForDay[]>{
    return this.http.get<RecordForDay[]>(`${this.baseUrl}/api/Record/dsoPotrosnjaPredTodays`)
  }

  ///za proizvodnju do ovog trenutka i predikcija proizvodnje
  
  getHistoryDayProiz():Observable<RecordForDay[]>{
    return this.http.get<RecordForDay[]>(`${this.baseUrl}/api/Record/dsoProizvodnja1d`)
  }
  getHistoryDayProizPred():Observable<RecordForDay[]>{
    return this.http.get<RecordForDay[]>(`${this.baseUrl}/api/Record/dsoProizvodnjaPredTodays`)
  }
  




  ///za prozivodnju i potrosnju u zadnjih 7 dana

  getHistroryP():Observable<Record[]> {
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoPotrosnja7d`)
  }
  getHistoryProzv():Observable<Record[]>{
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoProizvodnja7d`)
  }

  getHistroryPredP():Observable<Record[]> {
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoPotrosnjaPredPast7d`)
  }
  getHistoryPredProzv():Observable<Record[]>{
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoProizvodnjaPredPast7d`)
  }

  getHistory7daysTable():Observable<TableHistory[]>{
    return this.http.get<TableHistory[]>(`${this.baseUrl}/api/Table/dsoPotrProiz7d`)
  }
   //za mesec
  getHistroryPMonth():Observable<Record[]> {
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoPotrosnja31d`)
  }
  getHistoryProzvMonth():Observable<Record[]>{
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoProizvodnja31d`)
  }
  getHistroryPredPMonth():Observable<Record[]> {
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoPotrosnjaPredPast31d`)
  }
  getHistoryPredProzvMonth():Observable<Record[]>{
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoProizvodnjaPredPast31d`)
  }

  
  getHistoryMonthTable():Observable<TableHistory[]>{
    return this.http.get<TableHistory[]>(`${this.baseUrl}/api/Table/dsoPotrProiz31d`)

  }
  //za godinu
  getHistoryPYear():Observable<RecordYear[]>{
    return this.http.get<RecordYear[]>(`${this.baseUrl}/api/Record/dsoPotrosnjaPast365d`)
  }

  getHistoryProzvYear():Observable<RecordYear[]>{
    return this.http.get<RecordYear[]>(`${this.baseUrl}/api/Record/dsoProizvodnjaPast365d`)
  }

  getHistoryPredPYear():Observable<RecordYear[]>{
    return this.http.get<RecordYear[]>(`${this.baseUrl}/api/Record/dsoPotrosnjaPredPast365d`)
  }

  getHistoryPredProzvYear():Observable<RecordYear[]>{
    return this.http.get<RecordYear[]>(`${this.baseUrl}/api/Record/dsoProizvodnjaPredPast365d`)
  }
  getHistoryYearTable():Observable<TableHistory[]>{
    return this.http.get<TableHistory[]>(`${this.baseUrl}/api/Table/dsoPotrProiz1Y`)

  }



   ///za prozivodnju i potrosnju u narednih 7 dana
  getPredictionP(): Observable<Record[]> {
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoPotrosnjaPred7d`)
  }
  getPredictionProzv(): Observable<Record[]> {
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoProizvodnjaPred7d`)
  }
  getPrediction7daysTable():Observable<TableHistory[]>{
    return this.http.get<TableHistory[]>(`${this.baseUrl}/api/Table/dsoPotrProizPred7d`)

  }
  //za mesec
  getPredictionPMonth():Observable<Record[]> {
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoPotrosnjaPred31d`)
  }
  getPredictionProzvMonth():Observable<Record[]>{
    return this.http.get<Record[]>(`${this.baseUrl}/api/Record/dsoProizvodnjaPred31d`)
  }

  //za pocetnu stranu brojevi u cardu
  getAllProiz(): Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/api/Record/dsoProizvodnjaAll1d`)
    
  }
  getAllPotr(): Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/api/Record/dsoPotrosnjaAll1d`)
    
  }
  getAllSkladiste(): Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/api/Record/dsoSkladistaAll`)
    
  }
  //za pocetnu stranu brojevi u cardu jucerasnji dan
  getPotrosnjaYesterday():Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/api/Record/dsoPotrosnjaAllYesterday`)
  }
  getProizvodnjaYesterday():Observable<number>{
    return this.http.get<number>(`${this.baseUrl}/api/Record/dsoProizvodnjaAllYesterday`)
  }

  getMaxSlider():Observable<MaxSlider>{
    return this.http.get<MaxSlider>(`${this.baseUrl}/api/Table/minMaxSlider`)
  }


}
