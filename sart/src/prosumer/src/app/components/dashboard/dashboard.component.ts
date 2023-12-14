import { Component, OnInit, ElementRef, HostListener } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AuthService } from 'src/app/services/auth.service';
import { DashboardService } from 'src/app/services/dashboard.service';
import { UserStoreService } from 'src/app/services/user-store.service';
import { Potrosnja1d } from 'src/helperComponents/potrosnja1d';
import { ViewEncapsulation } from '@angular/core';
import { Chart } from 'chart.js';
import * as FileSaver from 'file-saver';
import jsPDF, { Html2CanvasOptions } from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { Objekat } from 'src/helperComponents/objekat';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss','./bd2.css'],
  encapsulation:ViewEncapsulation.None,
  providers: [DatePipe]
})
export class DashboardComponent implements OnInit {

  //Podaci o sirini ekrana
  isMobile?: boolean;
  screenWidth = 0;

  //Podaci o tabeli i graficima
  public chartPotrosnjaDanas: any = null;
  public chartPotrosnjaNedelja: any = null;
  public chartPotrosnjaMesec: any = null;
  public chartPotrosnjaGodina: any = null;
  public chartProizvodnjaDanas: any = null;
  public chartProizvodnjaNedelja: any = null;
  public chartProizvodnjaMesec: any = null;
  public chartProizvodnjaGodina: any = null;
  exportColumnsPotrosnjaDanas: any = [];
  exportColumnsPotrosnjaNedelja: any = [];
  exportColumnsPotrosnjaGodina: any = [];
  exportColumnsProizvodnjaDanas: any = [];
  exportColumnsProizvodnjaNedelja: any = [];
  exportColumnsProizvodnjaGodina: any = [];
  selectedPotrosnja?: any;
  selectedProizvodnja?: any;

  ukupnoSkladisteno?: number;

  //Podaci o potrosnji
  ukupnaPotrosnjaDanas: number = 0;
  ukupnaPotrosnjaNedelja: number = 0;
  ukupnaPotrosnjaMesec: number = 0;
  ukupnaPotrosnjaGodina: number = 0;
  maxPotrosnjaDanas: Potrosnja1d = new Potrosnja1d();
  maxPotrosnjaNedelja: Potrosnja1d = new Potrosnja1d();
  maxPotrosnjaMesec: Potrosnja1d = new Potrosnja1d();
  maxPotrosnjaGodina: Potrosnja1d = new Potrosnja1d();
  potrosnja1dIstorija:any = [];
  potrosnja7dIstorija:any = [];
  potrosnja31dIstorija:any = [];
  potrosnja365dIstorija:any = [];
  potrosnja1dPredikcija:any;
  potrosnja7dPredikcija:any;
  potrosnja1dIstorijaPredikcije:any;
  potrosnja7dIstorijaPredikcije:any;
  potrosnja31dIstorijaPredikcije:any;
  potrosnja365dIstorijaPredikcije:any;
  potrosnja1dLabele: any[] = [];
  potrosnja7dLabele: any[] = [];
  potrosnja31dLabele: any[] = [];
  potrosnja365dLabele: any[] = [];
  potrosnja1dPotrosnja: any[] = [];
  potrosnja7dPotrosnja: any[] = [];
  potrosnja31dPotrosnja: any[] = [];
  potrosnja365dPotrosnja: any[] = [];
  potrosnja1dPotrosnjaPred: any[] = [];
  potrosnja7dPotrosnjaPred: any[] = [];
  potrosnja1dPotrosnjaIstPred: any[] = [];
  potrosnja7dPotrosnjaIstPred: any[] = [];
  potrosnja31dPotrosnjaIstPred: any[] = [];
  potrosnja365dPotrosnjaIstPred: any[] = [];

  //Podaci o proizvodnji
  ukupnaDnevnaProizvodnja: number = 0;
  ukupnaNedeljnaProizvodnja: number = 0;
  ukupnaMesecnaProizvodnja: number = 0;
  ukupnaGodisnjaProizvodnja: number = 0;
  maxProizvodnjeDanas: Potrosnja1d = new Potrosnja1d();
  maxProizvodnjeNedelja: Potrosnja1d = new Potrosnja1d();
  maxProizvodnjeMesec: Potrosnja1d = new Potrosnja1d();
  maxProizvodnjeGodina: Potrosnja1d = new Potrosnja1d();
  proizvodnja1dIstorija:any = [];
  proizvodnja7dIstorija:any = [];
  proizvodnja31dIstorija:any = [];
  proizvodnja365dIstorija:any = [];
  proizvodnja1dPredikcija:any = [];
  proizvodnja7dPredikcija:any;
  proizvodnja1dIstorijaPredikcije:any;
  proizvodnja7dIstorijaPredikcije:any;
  proizvodnja31dIstorijaPredikcije:any;
  proizvodnja365dIstorijaPredikcije:any;
  proizvodnja1dLabele: any[] = [];
  proizvodnja7dLabele: any[] = [];
  proizvodnja31dLabele: any[] = [];
  proizvodnja365dLabele: any[] = [];
  proizvodnja1dProizvodnja: any[] = [];
  proizvodnja7dProizvodnja: any[] = [];
  proizvodnja31dProizvodnja: any[] = [];
  proizvodnja365dProizvodnja: any[] = [];
  proizvodnja1dProizvodnjaPred: any[] = [];
  proizvodnja7dProizvodnjaPred: any[] = [];
  proizvodnja1dProizvodnjaIstPred: any[] = [];
  proizvodnja7dProizvodnjaIstPred: any[] = [];
  proizvodnja31dProizvodnjaIstPred: any[] = [];
  proizvodnja365dProizvodnjaIstPred: any[] = [];

  //Procenti
  procenatPotrosnjaDanas?: number;
  procenatPotrosnjaNedelja?: number;
  procenatPotrosnjaMesec?: number;

  procenatProizvodnjaDanas?: number;
  procenatProizvodnjaNedelja?: number;
  procenatProizvodnjaMesec?: number;


  //Osnovni user podaci
  role? : string;
  objektiKorisnika?: Objekat | any;
  objectId: number = 0;

  constructor(private auth : AuthService,
            private userStoreService: UserStoreService,
            private dashboardService : DashboardService,
            private elementRef: ElementRef,
            private datePipe: DatePipe) {

  this.maxPotrosnjaDanas.usage = 0;
  this.maxPotrosnjaNedelja.usage = 0;
  this.maxProizvodnjeDanas.usage = 0;
  this.maxProizvodnjeNedelja.usage = 0;


  this.exportColumnsPotrosnjaDanas = [
    {title: "Datum", dataKey: "date"},
    {title: "Vreme[h]", dataKey: "time"},
    {title: "Izmerena današnja potrošnja[kWh]", dataKey: "usage"},
    {title: "Predikovana današnja i predikcija sutrašnje potrošnje[kWh]", dataKey: "usage2"},
  ]

  this.exportColumnsPotrosnjaNedelja = [
    {title: "Datum", dataKey: "date"},
    {title: "Istorija potrošnje[kWh]", dataKey: "usage"},
    {title: "Predikcija potrošnje[kWh]", dataKey: "usage2"},
  ]

  this.exportColumnsProizvodnjaDanas = [
    {title: "Datum", dataKey: "date"},
    {title: "Vreme[h]", dataKey: "time"},
    {title: "Izmerena današnja proizvodnja[kWh]", dataKey: "usage"},
    {title: "Predikovana današnja i predikcija sutrašnje proizvodnje[kWh]", dataKey: "usage2"},
  ]

  this.exportColumnsProizvodnjaNedelja = [
    {title: "Datum", dataKey: "date"},
    {title: "Istorija proizvodnje[kWh]", dataKey: "usage"},
    {title: "Predikcija proizvodnje[kWh]", dataKey: "usage2"},
  ]

  this.exportColumnsPotrosnjaGodina = [
    {title: "Mesec", dataKey: "month"},
    {title: "Godina", dataKey: "year"},
    {title: "Istorija potrošnje[kWh]", dataKey: "usage"},
    {title: "Predikovana potrošnja[kWh]", dataKey: "usage2"},
  ]

  this.exportColumnsProizvodnjaGodina = [
    {title: "Mesec", dataKey: "month"},
    {title: "Godina", dataKey: "year"},
    {title: "Istorija proizvodnje[kWh]", dataKey: "usage"},
    {title: "Predikovana proizvodnja[kWh]", dataKey: "usage2"},
  ]
}


tabChanged(event: any) {
  if (event.index === 1) {
    if(this.chartPotrosnjaNedelja == null && this.potrosnja7dIstorija.length) this.createChartPotrosnjaNedelja(this.potrosnja7dLabele,this.potrosnja7dPotrosnja,this.potrosnja7dPotrosnjaPred);
    if(this.chartProizvodnjaNedelja == null && this.proizvodnja7dIstorija.length) this.createChartProizvodnjaNedelja(this.proizvodnja7dLabele,this.proizvodnja7dProizvodnja,this.proizvodnja7dProizvodnjaPred);
  }
  if (event.index === 2) {
    if(this.chartPotrosnjaMesec == null && this.potrosnja31dIstorija.length) this.createChartPotrosnjaMesec(this.potrosnja31dLabele,this.potrosnja31dPotrosnja,this.potrosnja31dPotrosnjaIstPred);
    if(this.chartProizvodnjaMesec == null && this.proizvodnja31dIstorija.length) this.createChartProizvodnjaMesec(this.proizvodnja31dLabele,this.proizvodnja31dProizvodnja, this.proizvodnja31dProizvodnjaIstPred);
  }
  if (event.index === 3) {
    if(this.chartPotrosnjaGodina == null && this.potrosnja365dIstorija.length) this.createChartPotrosnjaGodina(this.potrosnja365dLabele,this.potrosnja365dPotrosnja,this.potrosnja365dPotrosnjaIstPred);
    if(this.chartProizvodnjaGodina == null && this.proizvodnja365dIstorija.length) this.createChartProizvodnjaGodina(this.proizvodnja365dLabele,this.proizvodnja365dProizvodnja, this.proizvodnja365dProizvodnjaIstPred);
  }
}

  month(value:number): string{
    switch(value){
      case 1: return "Januar";
      case 2: return "Februar";
      case 3: return "Mart";
      case 4: return "April";
      case 5: return "Maj";
      case 6: return "Jun";
      case 7: return "Jul";
      case 8: return "Avgust";
      case 9: return "Septembar";
      case 10: return "Oktobar";
      case 11: return "Novembar";
      case 12: return "Decembar";
      default: return "";
    }
  }

  ngOnInit() {

  this.objectId = parseInt(localStorage.getItem('objectId')!!);

  this.screenWidth = window.innerWidth;

  if(this.screenWidth <= 500) this.isMobile = true;
  else this.isMobile = false;

  //cuvanje podataka iz tokena u Session Storage-u radi kasnijeg koriscenja za pozivanje API-ja
  if(!localStorage.getItem('role')){
    this.userStoreService.getRole().subscribe(val=>{
      localStorage.setItem('role',val);
    });
  }
  if(!localStorage.getItem('userId')){
    this.userStoreService.getUserId().subscribe(val=>{
      localStorage.setItem('userId',val);
    });
  }

  //Inicijalizacija podataka
  this.uzmiRolu();
  this.ucitajObjekteKorisnika(localStorage.getItem('userId'));
  this.ucitajGrafikPodatkeDanas();
  this.ucitajGrafikPodatkeNedelja();
  this.ucitajGrafikPodatkeMesec();
  this.ucitajGrafikPodatkeGodina();
}

  //Ako je danasnji datum vraca "Danas" u tabeli
  getDate(value : string): number{
    if(value == this.datePipe.transform(new Date(), 'yyyy-MM-dd')) return 1;
    return 0;
  }

  public openPDF(type:number): void {
    const doc = new jsPDF('p','pt');
    let title = "";
    let data: any;
    let columns;
    let exportName;
    //ukoliko se radi o potrosnji danas
    if(type == 0)
    {
        title = "Istorija i predikcija potrošnje za današnji dan";
        data = this.potrosnja1dPredikcija;
        columns = this.exportColumnsPotrosnjaDanas;
        exportName = "danasnja_potrosnja";
    }
    else if(type == 1)
    //proizvodnja danas
    {
        title = "Istorija i predikcija proizvodnje za današnji dan";
        data = this.proizvodnja1dPredikcija;
        columns = this.exportColumnsProizvodnjaDanas;
        exportName = "danasnja_proizvodnja";
    }
    else if(type == 2)
    //potrosnja 7d
    {
        title = "Istorija i predikcija potrošnje za period od 7 dana";
        data = this.potrosnja7dIstorija;
        columns = this.exportColumnsPotrosnjaNedelja;
        exportName = "nedeljna_potrosnja";
    }
    else if(type == 3)
    //proizvodnja 7d
    {
        title = "Istorija i predikcija proizvodnje za period od 7 dana";
        data = this.proizvodnja7dIstorija;
        columns = this.exportColumnsProizvodnjaNedelja;
        exportName = "nedeljna_proizvodnja";
    }
    else if(type == 4){
      title = "Istorija potrošnje za period od 31 dan";
      data = this.potrosnja31dIstorija;
      columns = this.exportColumnsPotrosnjaNedelja;
      exportName = "mesecna_potrosnja";
    }
    else if(type == 5){
      title = "Istorija proizvodnje za period od 31 dan";
      data = this.proizvodnja31dIstorija;
      columns = this.exportColumnsProizvodnjaNedelja;
      exportName = "mesecna_proizvodnja";
    }
    else if(type == 6){
      title = "Istorija potrošnje za period od godinu dana";
      data = this.potrosnja365dIstorija;
      columns = this.exportColumnsPotrosnjaGodina;
      exportName = "godisnja_potrosnja";
    }
    else if(type == 7){
      title = "Istorija proizvodnje za period od godinu dana";
      data = this.proizvodnja365dIstorija;
      columns = this.exportColumnsProizvodnjaGodina;
      exportName = "godisnja_proizvodnja";
    }
    autoTable(doc, {
            columns: columns,
            body: data,
            didDrawPage: (dataArg) => {
            doc.text(title, dataArg.settings.margin.left, 10);
            doc.text(title, dataArg.settings.margin.top, 10);
        }
    });
    doc.save(exportName+'.pdf');
  }

exportExcel(type:number) {
  let data: any;
  let Heading: any;
  let title: string;
  //Potrosnja danas
  if(type == 0)
  {
      data = this.potrosnja1dPredikcija;
      Heading = [['Datum','Vreme[h]','Izmerena današnja potrošnja[kWh]','Predikovana današnja i predikcija sutrašnje potrošnje[kWh]']];
      title = "danasnja_potrosnja";
  }
  //Proizvodnja danas
  else if(type == 1)
  {
      data = this.proizvodnja1dPredikcija;
      Heading = [['Datum','Vreme[h]','Izmerena današnja proizvodnja[kWh]','Predikovana današnja i predikcija sutrašnje proizvodnje[kWh]']];
      title = "danasnja_proizvodnja";
  }
  //Potrosnja 7d
  else if(type == 2)
  {
      data = this.potrosnja7dIstorija;
      Heading = [['Datum','Izmerena potrošnja za prethodnu nedelju[kWh]','Predikovana potrošnja za prethodnu i predikcija za sledeću nedelju[kWh]']];
      title = "nedeljna_potrosnja";
  }
  //Proizvodnja 7d
  else if(type == 3)
  {
      data = this.proizvodnja7dIstorija;
      Heading = [['Datum','Izmerena proizvodnja za prethodnu nedelju[kWh]','Predikovana proizvodnja za prethodnu i predikcija za sledeću nedelju[kWh]']];
      title = "nedeljna_proizvodnja";
  }
  else if(type == 4)
  {
      data = this.potrosnja31dIstorija;
      Heading = [['Datum','Izmerena potrošnja za prethodnih mesec dana[kWh]','Predikovana potrošnja za prethodnih mesec dana[kWh]']];
      title = "mesecna_potrosnja";
  }
  else if(type == 5)
  {
      data = this.proizvodnja31dIstorija;
      Heading = [['Datum','Izmerena proizvodnja za prethodnih mesec dana[kWh]','Predikovana proizvodnja za prethodnih mesec dana[kWh]']];
      title = "mesecna_proizvodnja";
  }
  else if(type == 6)
  {
      data = this.potrosnja365dIstorija;
      Heading = [['Mesec','Godina','Izmerena potrošnja za prethodnih godinu dana[kWh]','Predikovana potrošnja za prethodnih 365 dana[kWh]']];
      title = "godisnja_potrosnja";
  }
  else if(type == 7)
  {
      data = this.proizvodnja365dIstorija;
      Heading = [['Mesec','Godina','Izmerena proizvodnja za prethodnih godinu dana[kWh]','Predikovana proizvodnja za prethodnih 365 dana[kWh]']];
      title = "godisnja_proizvodnja";
  }

  import("xlsx").then(xlsx => {
    const worksheet = xlsx.utils.json_to_sheet(data);
    XLSX.utils.sheet_add_aoa(worksheet,Heading);
    const workbook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
    const excelBuffer: any = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
    this.saveAsExcelFile(excelBuffer, title);
  });
}

saveAsExcelFile(buffer: any, fileName: string): void {
  let EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
  let EXCEL_EXTENSION = '.xlsx';
  const data: Blob = new Blob([buffer], {
    type: EXCEL_TYPE
  });
  FileSaver.saveAs(data, fileName + '_export_' + new Date().getTime() + EXCEL_EXTENSION);
}

  @HostListener('window:resize', ['$event'])
  onResize(event:any){
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500){
      this.isMobile = true;
    }else{
      this.isMobile = false;
    }
  }

ucitajObjekteKorisnika(idKorisnika:any){
  this.dashboardService.getObjekteKorisnika(idKorisnika).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.objektiKorisnika = JSON.parse(json) as Objekat;
    this.objektiKorisnika.forEach(element => {
      element.fullAddress = element.ulica + " " + element.adresniBroj + ", " + element.naselje + ", " + element.grad;
    });
  });
}

changeObjekat(event: any) {
  localStorage.setItem('objectId',(event.target.value).toString());
  window.location.reload();
}

ucitajGrafikPodatkeDanas(){

  this.dashboardService.getSkladisteno(localStorage.getItem('objectId')).subscribe((res) => {
    this.ukupnoSkladisteno = res;
  })


  if(this.role == 'prozumer' || this.role == 'potrošač'){

    this.dashboardService.getPotrosnja1dProcenat(localStorage.getItem('objectId')).subscribe((res) => {
      this.procenatPotrosnjaDanas = res;
    });

  this.dashboardService.getPotrosnja1d(localStorage.getItem('objectId')).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.potrosnja1dIstorija = JSON.parse(json) as Potrosnja1d;
    if(this.potrosnja1dIstorija.length)
    {
      this.potrosnja1dPotrosnja = this.potrosnja1dIstorija.map((val) => val.usage);
      this.ukupnaPotrosnjaDanas = this.potrosnja1dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxPotrosnjaDanas = this.potrosnja1dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
    }

    this.dashboardService.getPotrosnja1dPred(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja1dPredikcija = JSON.parse(json) as Potrosnja1d;
      if(this.potrosnja1dPredikcija.length)
      {
        this.potrosnja1dIstorija.map((val, index) => this.potrosnja1dPredikcija[index].usage2 = val.usage);
        this.potrosnja1dLabele = this.potrosnja1dPredikcija.map((val) => val.time.substring(0,5));
        this.potrosnja1dPotrosnjaPred = this.potrosnja1dPredikcija.map((val) => val.usage);
        this.potrosnja1dPredikcija = this.potrosnja1dPredikcija.map((val) => ({date: val.date,time: val.time, usage: val.usage2, usage2: val.usage}));
        this.createChartPotrosnjaDanas(this.potrosnja1dLabele,this.potrosnja1dPotrosnja,this.potrosnja1dPotrosnjaPred);
      }
      });
    });
}

if(this.role == 'prozumer' || this.role == 'proizvođač'){

  this.dashboardService.getProizvodnja1dProcenat(localStorage.getItem('objectId')).subscribe((res) => {
    this.procenatProizvodnjaDanas = res;
  });

  this.dashboardService.getProizvodnja1d(localStorage.getItem('objectId')).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.proizvodnja1dIstorija = JSON.parse(json) as Potrosnja1d;
    if(this.proizvodnja1dIstorija.length)
    {
      this.proizvodnja1dProizvodnja = this.proizvodnja1dIstorija.map((val) => val.usage);
      this.ukupnaDnevnaProizvodnja = this.proizvodnja1dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxProizvodnjeDanas = this.proizvodnja1dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
    }

    this.dashboardService.getProizvodnja1dPred(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.proizvodnja1dPredikcija = JSON.parse(json) as Potrosnja1d;
      if(this.proizvodnja1dPredikcija.length)
      {
        this.proizvodnja1dIstorija.map((val, index) => this.proizvodnja1dPredikcija[index].usage2 = val.usage);
        this.proizvodnja1dLabele = this.proizvodnja1dPredikcija.map((val) => val.time.substring(0,5));
        this.proizvodnja1dProizvodnjaPred = this.proizvodnja1dPredikcija.map((val) => val.usage);
        this.proizvodnja1dPredikcija = this.proizvodnja1dPredikcija.map((val) => ({date: val.date,time: val.time, usage: val.usage2, usage2: val.usage}));
        this.createChartProizvodnjaDanas(this.proizvodnja1dLabele,this.proizvodnja1dProizvodnja,this.proizvodnja1dProizvodnjaPred);
      }
    });
  });
}
}

ucitajGrafikPodatkeNedelja(){
  if(this.role == 'prozumer' || this.role == 'potrošač'){

    this.dashboardService.getPotrosnja7dProcenat(localStorage.getItem('objectId')).subscribe((res) => {
      this.procenatPotrosnjaNedelja = res;
    });

  this.dashboardService.getPotrosnja7d(localStorage.getItem('objectId')).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.potrosnja7dIstorija = JSON.parse(json) as Potrosnja1d;
    if(this.potrosnja7dIstorija.length)
    {
      this.potrosnja7dPotrosnja = this.potrosnja7dIstorija.map((val) => val.usage);
      this.ukupnaPotrosnjaNedelja = this.potrosnja7dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxPotrosnjaNedelja = this.potrosnja7dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
    }

    this.dashboardService.getPotrosnja7dIstorijaPred(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja7dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
      if(this.potrosnja7dIstorijaPredikcije.length)
      {
        this.potrosnja7dIstorijaPredikcije.map((val, index) => this.potrosnja7dIstorija[index].usage2 = val.usage);
        this.potrosnja7dPotrosnjaIstPred = this.potrosnja7dIstorijaPredikcije.map((val) => val.usage);
      }
      this.dashboardService.getPotrosnja7dPred(localStorage.getItem('objectId')).subscribe((res) =>{
        let json = JSON.stringify(res);
      this.potrosnja7dPredikcija = JSON.parse(json) as Potrosnja1d;
      if(this.potrosnja7dPredikcija.length)
      {
        this.potrosnja7dLabele = this.potrosnja7dLabele.concat(this.potrosnja7dIstorija.map((val) => val.date),this.potrosnja7dPredikcija.map((val) => val.date));
        this.potrosnja7dLabele = this.potrosnja7dLabele.map((obj) => {
          const [year, month, date] = obj.split("-");
          return `${date}/${month}/${year}`;
        });
        this.potrosnja7dPredikcija = this.potrosnja7dPredikcija.map((val) => ({date: val.date, usage2: val.usage}));
        this.potrosnja7dIstorija = [...this.potrosnja7dIstorija, ...this.potrosnja7dPredikcija];
        this.potrosnja7dPotrosnjaPred = this.potrosnja7dPotrosnjaIstPred.concat(this.potrosnja7dPredikcija.map((val) => val.usage2));
      }

    });
    });
  });
}

if(this.role == 'prozumer' || this.role == 'proizvođač'){

  this.dashboardService.getProizvodnja7dProcenat(localStorage.getItem('objectId')).subscribe((res) => {
    this.procenatProizvodnjaNedelja = res;
  });

  this.dashboardService.getProizvodnja7d(localStorage.getItem('objectId')).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.proizvodnja7dIstorija = JSON.parse(json) as Potrosnja1d;
    if(this.proizvodnja7dIstorija.length)
    {
      this.proizvodnja7dProizvodnja = this.proizvodnja7dIstorija.map((val) => val.usage);
      this.ukupnaNedeljnaProizvodnja = this.proizvodnja7dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxProizvodnjeNedelja = this.proizvodnja7dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
    }


    this.dashboardService.getProizvodnja7dIstorijaPred(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.proizvodnja7dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
      if(this.proizvodnja7dIstorijaPredikcije.length)
      {
        this.proizvodnja7dIstorijaPredikcije.map((val, index) => this.proizvodnja7dIstorija[index].usage2 = val.usage);
        this.proizvodnja7dProizvodnjaIstPred = this.proizvodnja7dIstorijaPredikcije.map((val) => val.usage);
      }
    this.dashboardService.getProizvodnja7dPred(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.proizvodnja7dPredikcija = JSON.parse(json) as Potrosnja1d;
      if(this.proizvodnja7dPredikcija.length)
      {
         this.proizvodnja7dLabele = this.proizvodnja7dLabele.concat(this.proizvodnja7dIstorija.map((val) => val.date),this.proizvodnja7dPredikcija.map((val) => val.date));
          this.proizvodnja7dLabele = this.proizvodnja7dLabele.map((obj) => {
            const [year, month, date] = obj.split("-");
            return `${date}/${month}/${year}`;
          });
          this.proizvodnja7dPredikcija = this.proizvodnja7dPredikcija.map((val) => ({date: val.date, usage2: val.usage}));
          this.proizvodnja7dIstorija = [...this.proizvodnja7dIstorija, ...this.proizvodnja7dPredikcija];
          this.proizvodnja7dProizvodnjaPred = this.proizvodnja7dProizvodnjaIstPred.concat(this.proizvodnja7dPredikcija.map((val) => val.usage2));
      }
    });
    });
  });
}
}

ucitajGrafikPodatkeMesec(){
  if(this.role == 'prozumer' || this.role == 'potrošač'){

    this.dashboardService.getPotrosnja31dProcenat(localStorage.getItem('objectId')).subscribe((res) => {
      this.procenatPotrosnjaMesec = res;
    });

  this.dashboardService.getPotrosnja31d(localStorage.getItem('objectId')).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.potrosnja31dIstorija = JSON.parse(json) as Potrosnja1d;
    if(this.potrosnja31dIstorija.length)
    {
      this.potrosnja31dPotrosnja = this.potrosnja31dIstorija.map((val) => val.usage);
      this.ukupnaPotrosnjaMesec = this.potrosnja31dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxPotrosnjaMesec = this.potrosnja31dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
      this.potrosnja31dLabele = this.potrosnja31dIstorija.map((val) => val.date.substring(5,10));
      this.potrosnja31dLabele = this.potrosnja31dLabele.map((obj) => {
        const [month, date] = obj.split("-");
        return `${date}/${month}`;
      });
    }
    this.dashboardService.getPotrosnja31dIstorijaPred(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja31dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
      if(this.potrosnja31dIstorijaPredikcije.length)
      {
        this.potrosnja31dIstorijaPredikcije.map((val, index) => this.potrosnja31dIstorija[index].usage2 = val.usage);
        this.potrosnja31dPotrosnjaIstPred = this.potrosnja31dIstorijaPredikcije.map((val) => val.usage);
      }
    });

  });
}

if(this.role == 'prozumer' || this.role == 'proizvođač'){

  this.dashboardService.getProizvodnja31dProcenat(localStorage.getItem('objectId')).subscribe((res) => {
    this.procenatProizvodnjaMesec = res;
  });

  this.dashboardService.getProizvodnja31d(localStorage.getItem('objectId')).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.proizvodnja31dIstorija = JSON.parse(json) as Potrosnja1d;
    if(this.proizvodnja31dIstorija.length)
    {
      this.proizvodnja31dProizvodnja = this.proizvodnja31dIstorija.map((val) => val.usage);
      this.ukupnaMesecnaProizvodnja = this.proizvodnja31dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxProizvodnjeMesec = this.proizvodnja31dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
      this.proizvodnja31dLabele = this.proizvodnja31dIstorija.map((val) => val.date.substring(5,10));
      this.proizvodnja31dLabele = this.proizvodnja31dLabele.map((obj) => {
        const [month, date] = obj.split("-");
        return `${date}/${month}`;
      });
    }
    this.dashboardService.getProizvodnja31dIstorijaPred(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.proizvodnja31dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
      if(this.proizvodnja31dIstorijaPredikcije.length)
      {
        this.proizvodnja31dIstorijaPredikcije.map((val, index) => this.proizvodnja31dIstorija[index].usage2 = val.usage);
        this.proizvodnja31dProizvodnjaIstPred = this.proizvodnja31dIstorijaPredikcije.map((val) => val.usage);
      }
    });
  });
}
}

ucitajGrafikPodatkeGodina(){
  if(this.role == 'prozumer' || this.role == 'potrošač'){

    this.dashboardService.getPotrosnja365d(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja365dIstorija = JSON.parse(json) as Potrosnja1d;
      if(this.potrosnja365dIstorija.length)
      {
        this.potrosnja365dPotrosnja = this.potrosnja365dIstorija.map((val) => val.usage);
        this.ukupnaPotrosnjaGodina = this.potrosnja365dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
        this.maxPotrosnjaGodina = this.potrosnja365dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
        this.potrosnja365dLabele = this.potrosnja365dIstorija.map((val) => this.month(val.month) + " " + val.year);
      }
      this.dashboardService.getPotrosnja365dIstorijaPred(localStorage.getItem('objectId')).subscribe((res) =>{
        let json = JSON.stringify(res);
        this.potrosnja365dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
        if(this.potrosnja365dIstorijaPredikcije.length)
        {
          this.potrosnja365dIstorijaPredikcije.map((val, index) => this.potrosnja365dIstorija[index].usage2 = val.usage);
          this.potrosnja365dPotrosnjaIstPred = this.potrosnja365dIstorijaPredikcije.map((val) => val.usage);
        }
      });

    });
  }
  if(this.role == 'prozumer' || this.role == 'proizvođač'){

    this.dashboardService.getProizvodnja365d(localStorage.getItem('objectId')).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.proizvodnja365dIstorija = JSON.parse(json) as Potrosnja1d;
      if(this.proizvodnja365dIstorija.length)
      {
        this.proizvodnja365dProizvodnja = this.proizvodnja365dIstorija.map((val) => val.usage);
        this.ukupnaGodisnjaProizvodnja = this.proizvodnja365dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
        this.maxProizvodnjeGodina = this.proizvodnja365dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
        this.proizvodnja365dLabele = this.proizvodnja365dIstorija.map((val) => this.month(val.month) + " " + val.year);
      }
      this.dashboardService.getProizvodnja365dIstorijaPred(localStorage.getItem('objectId')).subscribe((res) =>{
        let json = JSON.stringify(res);
        this.proizvodnja365dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
        if(this.proizvodnja365dIstorijaPredikcije.length)
        {
          this.proizvodnja365dIstorijaPredikcije.map((val, index) => this.proizvodnja365dIstorija[index].usage2 = val.usage);
          this.proizvodnja365dProizvodnjaIstPred = this.proizvodnja365dIstorijaPredikcije.map((val) => val.usage);
        }
      });
    });
  }
}

showOnGraphPotrosnjaDanas(index:number) {
    const {
      tooltip,
      chartArea
    } = this.chartPotrosnjaDanas;
    if(this.potrosnja1dPredikcija[index].usage  && this.potrosnja1dPredikcija[index].usage2)
    {
      tooltip.setActiveElements([{
        datasetIndex: 0,
        index: index
      },
      {
        datasetIndex: 1,
        index: index
      }
    ]);
    }
    else{
      tooltip.setActiveElements([{
        datasetIndex: 1,
        index: index
      }
    ]);
    }
  this.chartPotrosnjaDanas.update();
}

showOnGraphPotrosnjaNedelja(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartPotrosnjaNedelja;

  if(this.potrosnja7dIstorija[index].usage  && this.potrosnja7dIstorija[index].usage2)
  {
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  }
  else{
    tooltip.setActiveElements([
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  }
  this.chartPotrosnjaNedelja.update();
}

showOnGraphPotrosnjaMesec(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartPotrosnjaMesec;
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  this.chartPotrosnjaMesec.update();
}

showOnGraphPotrosnjaGodina(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartPotrosnjaGodina;
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  this.chartPotrosnjaGodina.update();
}

showOnGraphProizvodnjaDanas(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartProizvodnjaDanas;
  if(this.proizvodnja1dPredikcija[index].usage  && this.proizvodnja1dPredikcija[index].usage2)
  {
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  }
  else{
    tooltip.setActiveElements([{
      datasetIndex: 1,
      index: index
    }
  ]);
  }
this.chartProizvodnjaDanas.update();
}

showOnGraphProizvodnjaNedelja(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartProizvodnjaNedelja;

  if(this.proizvodnja7dIstorija[index].usage  && this.proizvodnja7dIstorija[index].usage2)
  {
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  }
  else{
    tooltip.setActiveElements([
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  }
  this.chartProizvodnjaNedelja.update();
}

showOnGraphProizvodnjaMesec(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartProizvodnjaMesec;
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  this.chartProizvodnjaMesec.update();
}

showOnGraphProizvodnjaGodina(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartProizvodnjaGodina;
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  this.chartProizvodnjaGodina.update();
}

onRowSelectPotrosnjaDanas(event:any){
  this.showOnGraphPotrosnjaDanas(event.index);
}

onRowSelectPotrosnjaNedelja(event:any){
  this.showOnGraphPotrosnjaNedelja(event.index);
}

onRowSelectPotrosnjaMesec(event:any){
  this.showOnGraphPotrosnjaMesec(event.index);
}

onRowSelectPotrosnjaGodina(event:any){
  this.showOnGraphPotrosnjaGodina(event.index);
}

onRowSelectProizvodnjaDanas(event:any){
  this.showOnGraphProizvodnjaDanas(event.index);
}

onRowSelectProizvodnjaNedelja(event:any){
  this.showOnGraphProizvodnjaNedelja(event.index);
}

onRowSelectProizvodnjaMesec(event:any){
  this.showOnGraphProizvodnjaMesec(event.index);
}

onRowSelectProizvodnjaGodina(event:any){
  this.showOnGraphProizvodnjaGodina(event.index);
}

createChartPotrosnjaDanas(labele: any[], potrosnja: string[], potrosnjaPred: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart`);
  const verticalLine = {
    id: 'verticalLine',
    afterDatasetsDraw(chart, args, pluginOptions){
      const {ctx, chartArea: {top, bottom, left, right, widht, height},scales: {x,y}} = chart;
      ctx.beginPath();
      ctx.strokeStyle = 'rgba(71, 255, 102, 0.5)';
      ctx.lineWidth = 3;
      ctx.moveTo(x.getPixelForValue(24),top);
      ctx.lineTo(x.getPixelForValue(24),bottom);
      ctx.stroke();
      ctx.closePath();
    }
  }
  this.chartPotrosnjaDanas = new Chart(chart, {
    type: 'line',
    data: {
      labels:  labele,
      datasets: [{
        label: 'Izmerena današnja potrošnja[kWh]',
        tension: 0,
        borderColor: "rgb(68,166,245)",
        borderJoinStyle: 'bevel',
        fill: true,
        data:potrosnja,
      },
      {
        label: 'Predikovana današnja i predikcija potrošnje za sledeći dan[kWh]',
        tension: 0,
        borderColor: "rgb(255,167,38)",
        fill: true,
        data:potrosnjaPred,
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          titleFont: {
            size: 12
          },
          bodyFont: {
            size: 12
          }
        },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)',
                usePointStyle: true,
                pointStyle: 'line',
                font: {
                  size: !this.isMobile ? 12 : 13
                }
        }
      }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Potrošnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 14 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Vreme[h]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 14 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    },
    plugins: [verticalLine]
  });
}

createChartPotrosnjaNedelja(labele: any[], potrosnja: string[], potrosnjaPred: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart3`);
  this.chartPotrosnjaNedelja = new Chart(chart, {
    type: 'bar',
    data: {
      labels:  !this.isMobile ? labele : labele.map((val) => val = val.substring(0,5)),
      datasets: [{
        label: 'Izmerena potrošnja za prethodnu nedelju[kWh]',
        data:potrosnja,
        backgroundColor: "rgb(68,166,245)",
      },
      {
        label: 'Predikovana potrošnja za prethodnu i predikcija za sledeću nedelju[kWh]',
        data:potrosnjaPred,
        backgroundColor: "rgb(255,167,38)",
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
          tooltip: {
            titleFont: {
              size: !this.isMobile ? 12 : 10
            },
            bodyFont: {
              size: !this.isMobile ? 12 : 10
            }
          },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)',
                font: {
                  size: !this.isMobile ? 12 : 10
                }
            }
        }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Potrošnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Datum',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    }
  });
}

createChartPotrosnjaMesec(labele: any[], potrosnja: string[], potrosnjaPredIst: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart5`);
  this.chartPotrosnjaMesec = new Chart(chart, {
    type: 'bar',
    data: {
      labels: labele,
      datasets: [{
        label: 'Istorija potrošnje za prethodni mesec[kWh]',
        data:potrosnja,
        backgroundColor: "rgb(68,166,245)",
      },
      {
        label: 'Predikovana potrošnja za prethodni mesec[kWh]',
        data:potrosnjaPredIst,
        backgroundColor: "rgb(255,167,38)",
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          titleFont: {
            size: !this.isMobile ? 12 : 13
          },
          bodyFont: {
            size: !this.isMobile ? 12 : 13
          }
        },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)'
            }
        }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Potrošnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Datum',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    }
  });
}

createChartPotrosnjaGodina(labele: any[], potrosnja: string[], potrosnjaPredIst: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart7`);
  this.chartPotrosnjaGodina = new Chart(chart, {
    type: 'bar',
    data: {
      labels: labele,
      datasets: [{
        label: 'Istorija potrošnje za prethodnu godinu[kWh]',
        data:potrosnja,
        backgroundColor: "rgb(68,166,245)",
      },
      {
        label: 'Predikovana potrošnja za prethodnu godinu[kWh]',
        data:potrosnjaPredIst,
        backgroundColor: "rgb(255,167,38)",
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          titleFont: {
            size: !this.isMobile ? 12 : 12
          },
          bodyFont: {
            size: !this.isMobile ? 12 : 12
          }
        },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)'
            }
        }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Potrošnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Datum',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    }
  });
}

createChartProizvodnjaDanas(labele: any[], proizvodnja: string[], proizvodnjaPred: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart2`);
  const verticalLine = {
    id: 'verticalLine',
    afterDatasetsDraw(chart, args, pluginOptions){
      const {ctx, chartArea: {top, bottom, left, right, widht, height},scales: {x,y}} = chart;
      ctx.beginPath();
      ctx.strokeStyle = 'rgba(71, 255, 102, 0.5)';
      ctx.lineWidth = 3;
      ctx.moveTo(x.getPixelForValue(24),top);
      ctx.lineTo(x.getPixelForValue(24),bottom);
      ctx.stroke();
      ctx.closePath();
    }
  }
  this.chartProizvodnjaDanas = new Chart(chart, {
    type: 'line',
    data: {
      labels:  labele,
      datasets: [{
        label: 'Istorija proizvodnje za današnji dan[kWh]',
        tension: 0,
        borderColor: "rgb(68,166,245)",
        borderJoinStyle: 'bevel',
        fill: true,
        data:proizvodnja,
      },
      {
        label: 'Predikovana današnja i predikcija proizvodnje za sledeći dan[kWh]',
        tension: 0,
        borderColor: "rgb(255,167,38)",
        fill: true,
        data:proizvodnjaPred,
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          titleFont: {
            size: !this.isMobile ? 12 : 11
          },
          bodyFont: {
            size: !this.isMobile ? 12 : 11
          }
        },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)',
                usePointStyle: true,
                pointStyle: 'line',
                font: {
                  size: !this.isMobile ? 12 : 12
                }
            }
        }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Proizvodnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 10 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Vreme[h]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 10 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    },
    plugins: [verticalLine]
  });
}

createChartProizvodnjaNedelja(labele: any[], proizvodnja: string[], proizvodnjaPred: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart4`);
  this.chartProizvodnjaNedelja = new Chart(chart, {
    type: 'bar',
    data: {
      labels:  !this.isMobile ? labele : labele.map((val) => val = val.substring(0,5)),
      datasets: [{
        label: 'Istorija proizvodnje za prethodnu nedelju[kWh]',
        data:proizvodnja,
        backgroundColor: "rgb(68,166,245)",
      },
      {
        label: 'Predikovana proizvodnja za prethodnu i predikcija za sledeću nedelju[kWh]',
        data:proizvodnjaPred,
        backgroundColor: "rgb(255,167,38)",
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          titleFont: {
            size: !this.isMobile ? 12 : 10
          },
          bodyFont: {
            size: !this.isMobile ? 12 : 10
          }
        },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)',
                font: {
                  size: !this.isMobile ? 12 : 10
                }
            }
        }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Proizvodnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Datum',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    }
  });
}

createChartProizvodnjaMesec(labele: any[], proizvodnja: string[], proizvodnjaPredIst: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart6`);
  this.chartProizvodnjaMesec = new Chart(chart, {
    type: 'bar',
    data: {
      labels: labele,
      datasets: [{
        label: 'Istorija proizvodnje za prethodni mesec[kWh]',
        data:proizvodnja,
        backgroundColor: "rgb(5, 187, 97)",
      },
      {
        label: 'Predikovana proizvodnja za prethodni mesec[kWh]',
        data:proizvodnjaPredIst,
        backgroundColor: "rgb(255,167,38)",
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          titleFont: {
            size: !this.isMobile ? 12 : 12
          },
          bodyFont: {
            size: !this.isMobile ? 12 : 12
          }
        },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)'
            }
        }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Proizvodnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Datum',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    }
  });
}

createChartProizvodnjaGodina(labele: any[], proizvodnja: string[], proizvodnjaPredIst: string[]) {
  let chart = this.elementRef.nativeElement.querySelector(`#MyChart8`);
  this.chartProizvodnjaGodina = new Chart(chart, {
    type: 'bar',
    data: {
      labels: labele,
      datasets: [{
        label: 'Istorija proizvodnje za prethodnu godinu[kWh]',
        data:proizvodnja,
        backgroundColor: "rgb(5, 187, 97)",
      },
      {
        label: 'Predikovana proizvodnja za prethodnu godinu[kWh]',
        data:proizvodnjaPredIst,
        backgroundColor: "rgb(255,167,38)",
      }],
    },
    options: {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          titleFont: {
            size: !this.isMobile ? 12 : 12
          },
          bodyFont: {
            size: !this.isMobile ? 12 : 12
          }
        },
        legend: {
            labels: {
                color: 'rgb(221, 201, 201)'
            }
        }
    },
      scales: {
        y: {
          title: {
            display: true,
            text: 'Proizvodnja struje[kWh]',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Datum',
            color: "rgb(255,255,255)",
            font: {
              size: this.isMobile ? 13 : 15
            }
          },
          ticks: {
            color: 'rgb(221, 201, 201)'
          },
          grid: {
            color: 'rgba(255,255,255,0.2)'
          }
        }
      }
    }
  });
}

  uzmiRolu():void{
    this.role = localStorage.getItem('role')!;
  }

  //kasnije postaviti na klik dugmeta u navbaru!
  logout(){
    this.auth.logout();
  }

}
