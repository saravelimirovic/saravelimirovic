import { Component, ElementRef, HostListener, OnInit, ViewEncapsulation } from '@angular/core';
import { MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { ModalComponent } from '../modal/modal/modal.component';
import { ActivatedRoute } from '@angular/router';
import { UredjajiService } from 'src/app/services/uredjaji.service';
import {DatePipe, Location} from '@angular/common';
import { Potrosnja1d } from 'src/helperComponents/potrosnja1d';
import { Chart } from 'chart.js';
import * as FileSaver from 'file-saver';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-uredjaji-pojedinacno',
  templateUrl: './uredjaji-pojedinacno.component.html',
  styleUrls: ['./uredjaji-pojedinacno.component.scss','./bd2.css'],
  encapsulation:ViewEncapsulation.None,
  providers: [DatePipe]
})
export class UredjajiPojedinacnoComponent implements OnInit{

  basicData: any;
  basicOptions: any;
  isMobile?: boolean;
  screenWidth = 0;
  uredjajId: string = "0";
  uredjaj: any = {};

  public chartDanas: any = null;
  public chartNedelja: any = null;
  public chartMesec: any = null;
  public chartGodina: any = null;

  exportColumnsDanas: any = [];
  exportColumnsNedelja: any = [];
  exportColumnsGodina: any = [];

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

  modalRef: MdbModalRef<ModalComponent> | null = null;
  selectedPotrosnja?: any;

  procenatDanas?: number;
  procenatNedelja?: number;
  procenatMesec?: number;
  isPotrosac: boolean = false;

  constructor(private modalService: MdbModalService,
            private route: ActivatedRoute,
            private uredjajiService: UredjajiService,
            private _location: Location,
            private elementRef: ElementRef,
            private datePipe: DatePipe) {

    this.maxPotrosnjaDanas.usage = 0;
    this.maxPotrosnjaNedelja.usage = 0;

    this.uredjajiService.deviceStatus.subscribe((val) =>{
      this.uredjaj.status = val;
    })
    this.uredjajiService.devicePermission.subscribe((val) =>{
      this.uredjaj.dozvolaZaPregled = val;
    })
    this.uredjajiService.deviceControl.subscribe((val) =>{
      this.uredjaj.dozvolaZaUpravljanje = val;
    })
  }

  tabChanged(event: any) {
    //  if (event.index === 1) {
    //   if(this.chartDanas == null) this.createChartDanas(this.potrosnja1dLabele,this.potrosnja1dPotrosnja,this.potrosnja1dPotrosnjaPred);
    // }
    if (event.index === 1) {
      if(this.chartNedelja == null) this.createChartNedelja(this.potrosnja7dLabele,this.potrosnja7dPotrosnja,this.potrosnja7dPotrosnjaPred);
    }
    if (event.index === 2) {
      if(this.chartMesec == null) this.createChartMesec(this.potrosnja31dLabele,this.potrosnja31dPotrosnja,this.potrosnja31dPotrosnjaIstPred);
    }
    if (event.index === 3) {
      if(this.chartGodina == null) this.createChartGodina(this.potrosnja365dLabele,this.potrosnja365dPotrosnja,this.potrosnja365dPotrosnjaIstPred);
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

  backClicked() {
    this._location.back();
  }

  public openPDF(type:number): void {
    const doc = new jsPDF('p','pt');
    let title = "";
    let data: any;
    let columns;
    let exportName;
    if(this.uredjaj.tipUredjaja == "Potrošač")
    {
      this.exportColumnsDanas = [
        {title: "Datum", dataKey: "date"},
        {title: "Vreme[h]", dataKey: "time"},
        {title: "Izmerena današnja potrošnja[kWh]", dataKey: "usage"},
        {title: "Predikovana današnja i predikcija sutrašnje potrošnje[kWh]", dataKey: "usage2"},
      ]
      this.exportColumnsNedelja = [
        {title: "Datum", dataKey: "date"},
        {title: "Istorija potrošnje[kWh]", dataKey: "usage"},
        {title: "Predikcija potrošnje[kWh]", dataKey: "usage2"},
      ]
      this.exportColumnsGodina = [
        {title: "Mesec", dataKey: "month"},
        {title: "Godina", dataKey: "year"},
        {title: "Istorija potrošnje[kWh]", dataKey: "usage"},
        {title: "Predikovana potrošnja[kWh]", dataKey: "usage2"},
      ]
    }else{
      this.exportColumnsDanas = [
        {title: "Datum", dataKey: "date"},
        {title: "Vreme[h]", dataKey: "time"},
        {title: "Izmerena današnja proizvodnja[kWh]", dataKey: "usage"},
        {title: "Predikovana današnja i predikcija sutrašnje proizvodnje[kWh]", dataKey: "usage2"},
      ]
      this.exportColumnsNedelja = [
        {title: "Datum", dataKey: "date"},
        {title: "Istorija proizvodnje[kWh]", dataKey: "usage"},
        {title: "Predikcija proizvodnje[kWh]", dataKey: "usage2"},
      ]
      this.exportColumnsGodina = [
        {title: "Mesec", dataKey: "month"},
        {title: "Godina", dataKey: "year"},
        {title: "Istorija proizvodnje[kWh]", dataKey: "usage"},
        {title: "Predikovana proizvodnja[kWh]", dataKey: "usage2"},
      ]
    }
    if(this.uredjaj.tipUredjaja == "Potrošač")
    {
    //ukoliko se radi o potrosnji danas
    if(type == 0)
    {
        title = "Istorija i predikcija potrošnje za današnji dan";
        data = this.potrosnja1dPredikcija;
        columns = this.exportColumnsDanas;
        exportName = "danasnja_potrosnja";
    }
    else if(type == 1)
    //potrosnja 7d
    {
        title = "Istorija i predikcija potrošnje za period od 7 dana";
        data = this.potrosnja7dIstorija;
        columns = this.exportColumnsNedelja;
        exportName = "nedeljna_proizvodnja";
    }
    else if(type == 2){
      title = "Istorija potrošnje za period od 31 dan";
      data = this.potrosnja31dIstorija;
      columns = this.exportColumnsNedelja;
      exportName = "mesecna_potrosnja";
    }
    else if(type == 3){
      title = "Istorija potrošnje za period od godinu dana";
      data = this.potrosnja365dIstorija;
      columns = this.exportColumnsGodina;
      exportName = "godisnja_potrosnja";
    }
  }
  else{
    if(type == 0)
    //proizvodnja danas
    {
        title = "Istorija i predikcija proizvodnje za današnji dan";
        data = this.potrosnja1dPredikcija;
        columns = this.exportColumnsDanas;
        exportName = "danasnja_proizvodnja";
    }
    else if(type == 1)
    //proizvodnja 7d
    {
        title = "Istorija i predikcija proizvodnje za period od 7 dana";
        data = this.potrosnja7dIstorija;
        columns = this.exportColumnsNedelja;
        exportName = "nedeljna_proizvodnja";
    }
    else if(type == 2){
      title = "Istorija proizvodnje za period od 31 dan";
      data = this.potrosnja31dIstorija;
      columns = this.exportColumnsNedelja;
      exportName = "mesecna_proizvodnja";
    }
    else if(type == 3){
      title = "Istorija proizvodnje za period od godinu dana";
      data = this.potrosnja365dIstorija;
      columns = this.exportColumnsGodina;
      exportName = "godisnja_proizvodnja";
    }
  }
    autoTable(doc, {
            columns: columns,
            body: data,
            didDrawPage: (dataArg) => {
            doc.text(title, dataArg.settings.margin.left, 10);
        }
    });
    doc.save(exportName + '.pdf');
  }

  exportExcel(type:number) {
    let data: any;
    let Heading: any;
    let title: string;
    if(this.uredjaj.tipUredjaja == "Potrošač")
    {
      //Potrosnja danas
      if(type == 0)
      {
          data = this.potrosnja1dPredikcija;
          Heading = [['Datum','Vreme[h]','Izmerena današnja potrošnja[kWh]','Predikovana današnja i predikcija sutrašnje potrošnje[kWh]']];
          title = "danasnja_potrosnja";
      }
      //Potrosnja 7d
      else if(type == 1)
      {
          data = this.potrosnja7dIstorija;
          Heading = [['Datum','Izmerena potrošnja za prethodnu nedelju[kWh]','Predikovana potrošnja za prethodnu i predikcija za sledeću nedelju[kWh]']];
          title = "nedeljna_potrosnja";
      }
      else if(type == 2)
      {
          data = this.potrosnja31dIstorija;
          Heading = [['Datum','Izmerena potrošnja za prethodnih mesec dana[kWh]','Predikovana potrošnja za prethodnih mesec dana[kWh]']];
          title = "mesecna_potrosnja";
      }
      else if(type == 3)
      {
          data = this.potrosnja365dIstorija;
          Heading = [['Mesec','Godina','Izmerena potrošnja za prethodnih godinu dana[kWh]','Predikovana potrošnja za prethodnih 365 dana[kWh]']];
          title = "godisnja_potrosnja";
      }
    }
    else
    {
      //Proizvodnja danas
      if(type == 0)
      {
          data = this.potrosnja1dPredikcija;
          Heading = [['Datum','Vreme[h]','Izmerena današnja proizvodnja[kWh]','Predikovana današnja i predikcija sutrašnje proizvodnje[kWh]']];
          title = "danasnja_proizvodnja";
      }
      //Proizvodnja 7d
      else if(type == 1)
      {
          data = this.potrosnja7dIstorija;
          Heading = [['Datum','Izmerena proizvodnja za prethodnu nedelju[kWh]','Predikovana proizvodnja za prethodnu i predikcija za sledeću nedelju[kWh]']];
          title = "nedeljna_proizvodnja";
      }
      else if(type == 2)
      {
          data = this.potrosnja31dIstorija;
          Heading = [['Datum','Izmerena proizvodnja za prethodnih mesec dana[kWh]','Predikovana proizvodnja za prethodnih mesec dana[kWh]']];
          title = "mesecna_proizvodnja";
      }
      else if(type == 3)
      {
          data = this.potrosnja365dIstorija;
          Heading = [['Mesec','Godina','Izmerena proizvodnja za prethodnih godinu dana[kWh]','Predikovana proizvodnja za prethodnih 365 dana[kWh]']];
          title = "godisnja_proizvodnja";
      }

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

  ngOnInit(): void{
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500) this.isMobile = true;
    else this.isMobile = false;

    this.uredjajId = this.route.snapshot.paramMap.get('id')!!;

    this.uredjajiService.getPojedinacanUredjaj(this.uredjajId).subscribe((res) =>{
      this.uredjaj = res;
      if(this.uredjaj.tipUredjaja == "Potrošač") this.isPotrosac = true;
      else this.isPotrosac = false;
    });

    this.ucitajUredjajPodatkeDanas();
    this.ucitajUredjajPodatkeNedelja();
    this.ucitajUredjajPodatkeMesec();
    this.ucitajUredjajPodatkeGodina();
  }

  ucitajUredjajPodatkeDanas(){

    this.uredjajiService.get1dProcenat(this.uredjajId).subscribe((res) => {
      this.procenatDanas = res;
    });

  this.uredjajiService.getPojedinacanDanas(this.uredjajId).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.potrosnja1dIstorija = JSON.parse(json) as Potrosnja1d;
      this.potrosnja1dPotrosnja = this.potrosnja1dIstorija.map((val) => val.usage);
      this.ukupnaPotrosnjaDanas = this.potrosnja1dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxPotrosnjaDanas = this.potrosnja1dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);

    this.uredjajiService.getPojedinacanDanasPredikcija(this.uredjajId).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja1dPredikcija = JSON.parse(json) as Potrosnja1d;
        this.potrosnja1dIstorija.map((val, index) => this.potrosnja1dPredikcija[index].usage2 = val.usage);
        this.potrosnja1dLabele = this.potrosnja1dPredikcija.map((val) => val.time.substring(0,5));
        this.potrosnja1dPotrosnjaPred = this.potrosnja1dPredikcija.map((val) => val.usage);
        this.potrosnja1dPredikcija = this.potrosnja1dPredikcija.map((val) => ({date: val.date,time: val.time, usage: val.usage2, usage2: val.usage}));
        this.createChartDanas(this.potrosnja1dLabele,this.potrosnja1dPotrosnja,this.potrosnja1dPotrosnjaPred);
    });
  });
}

ucitajUredjajPodatkeNedelja(){

  this.uredjajiService.get7dProcenat(this.uredjajId).subscribe((res) => {
    this.procenatNedelja = res;
  });

  this.uredjajiService.getPojedinacanNedelja(this.uredjajId).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.potrosnja7dIstorija = JSON.parse(json) as Potrosnja1d;
      this.potrosnja7dPotrosnja = this.potrosnja7dIstorija.map((val) => val.usage);
      this.ukupnaPotrosnjaNedelja = this.potrosnja7dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxPotrosnjaNedelja = this.potrosnja7dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);

      this.uredjajiService.getPojedinacanNedeljaIstPred(this.uredjajId).subscribe((res) =>{
        let json = JSON.stringify(res);
        this.potrosnja7dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
          this.potrosnja7dIstorijaPredikcije.map((val, index) => this.potrosnja7dIstorija[index].usage2 = val.usage);
          this.potrosnja7dPotrosnjaIstPred = this.potrosnja7dIstorijaPredikcije.map((val) => val.usage);

    this.uredjajiService.getPojedinacanNedeljaPredikcija(this.uredjajId).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja7dPredikcija = JSON.parse(json) as Potrosnja1d;
       this.potrosnja7dLabele = this.potrosnja7dLabele.concat(this.potrosnja7dIstorija.map((val) => val.date),this.potrosnja7dPredikcija.map((val) => val.date));
        this.potrosnja7dLabele = this.potrosnja7dLabele.map((obj) => {
          const [year, month, date] = obj.split("-");
          return `${date}/${month}/${year}`;
        });
        this.potrosnja7dPredikcija = this.potrosnja7dPredikcija.map((val) => ({date: val.date, usage2: val.usage}));
        this.potrosnja7dIstorija = [...this.potrosnja7dIstorija, ...this.potrosnja7dPredikcija];
        this.potrosnja7dPotrosnjaPred = this.potrosnja7dPotrosnjaIstPred.concat(this.potrosnja7dPredikcija.map((val) => val.usage2));
    });
    });
  });
}

ucitajUredjajPodatkeMesec(){

  this.uredjajiService.get31dProcenat(this.uredjajId).subscribe((res) => {
    this.procenatMesec = res;
  });

  this.uredjajiService.getPojedinacanMesec(this.uredjajId).subscribe((res) =>{
    let json = JSON.stringify(res);
    this.potrosnja31dIstorija = JSON.parse(json) as Potrosnja1d;
      this.potrosnja31dPotrosnja = this.potrosnja31dIstorija.map((val) => val.usage);
      this.ukupnaPotrosnjaMesec = this.potrosnja31dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
      this.maxPotrosnjaMesec = this.potrosnja31dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
      this.potrosnja31dLabele = this.potrosnja31dIstorija.map((val) => val.date.substring(5,10));
      this.potrosnja31dLabele = this.potrosnja31dLabele.map((obj) => {
        const [month, date] = obj.split("-");
        return `${date}/${month}`;
      });
    this.uredjajiService.getPojedinacanMesecIstPred(this.uredjajId).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja31dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
        this.potrosnja31dIstorijaPredikcije.map((val, index) => this.potrosnja31dIstorija[index].usage2 = val.usage);
        this.potrosnja31dPotrosnjaIstPred = this.potrosnja31dIstorijaPredikcije.map((val) => val.usage);
    });
  });
}

ucitajUredjajPodatkeGodina(){

    this.uredjajiService.getPojedinacanGodina(this.uredjajId).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja365dIstorija = JSON.parse(json) as Potrosnja1d;
        this.potrosnja365dPotrosnja = this.potrosnja365dIstorija.map((val) => val.usage);
        this.ukupnaPotrosnjaGodina = this.potrosnja365dIstorija.reduce((acc, obj) => acc = acc + obj.usage, 0);
        this.maxPotrosnjaGodina = this.potrosnja365dIstorija.reduce((prev, current) => (prev.usage > current.usage) ? prev : current);
        this.potrosnja365dLabele = this.potrosnja365dIstorija.map((val) => this.month(val.month) + " " + val.year);
      this.uredjajiService.getPojedinacanGodinaIstPred(this.uredjajId).subscribe((res) =>{
        let json = JSON.stringify(res);
        this.potrosnja365dIstorijaPredikcije = JSON.parse(json) as Potrosnja1d;
          this.potrosnja365dIstorijaPredikcije.map((val, index) => this.potrosnja365dIstorija[index].usage2 = val.usage);
          this.potrosnja365dPotrosnjaIstPred = this.potrosnja365dIstorijaPredikcije.map((val) => val.usage);
      });
    });
  }

getDate(value : string): number{
  if(value == this.datePipe.transform(new Date(), 'yyyy-MM-dd')) return 1;
  return 0;
}

showOnGraphDanas(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartDanas;
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
this.chartDanas.update();
}

showOnGraphNedelja(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartNedelja;

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
  this.chartNedelja.update();
}

showOnGraphMesec(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartMesec;
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  this.chartMesec.update();
}

showOnGraphGodina(index:number) {
  const {
    tooltip,
    chartArea
  } = this.chartGodina;
    tooltip.setActiveElements([{
      datasetIndex: 0,
      index: index
    },
    {
      datasetIndex: 1,
      index: index
    }
  ]);
  this.chartGodina.update();
}

onRowSelectDanas(event:any){
  this.showOnGraphDanas(parseInt(event.data.time.substring(0,3)));
}
onRowSelectNedelja(event:any){
  this.showOnGraphNedelja(event.index);
}
onRowSelectMesec(event:any){
  this.showOnGraphMesec(event.index);
}
onRowSelectGodina(event:any){
  this.showOnGraphGodina(event.index);
}

  openModal(br:number) {
    let variable;
    if(br == 0) variable = this.uredjaj.status;
    else if(br == 1) variable = this.uredjaj.dozvolaZaPregled;
    else variable = this.uredjaj.dozvolaZaUpravljanje;

    this.modalRef = this.modalService.open(ModalComponent,{
        data: { ukljucen: variable, deviceId: this.uredjajId, situacija: br},
    });
  }

  createChartDanas(labele: any[], potrosnja: string[], potrosnjaPred: string[]) {
    let chart = this.elementRef.nativeElement.querySelector(`#MyChart`);
    this.chartDanas = new Chart(chart, {
      type: 'line',
      data: {
        labels:  labele,
        datasets: [{
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Izmerena današnja potrošnja[kWh]' : 'Izmerena današnja proizvodnja[kWh]',
          tension: 0,
          borderColor: "rgb(68,166,245)",
          borderJoinStyle: 'bevel',
          fill: true,
          data:potrosnja,
        },
        {
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Predikovana današnja i predikcija potrošnje za sledeći dan[kWh]' : 'Predikovana današnja i predikcija proizvodnje za sledeći dan[kWh]',
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
              size: !this.isMobile ? 12 : 12
            },
            bodyFont: {
              size: !this.isMobile ? 12 : 12
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
              text: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Potrošnja struje[kWh]' : 'Proizvodnja struje[kWh]',
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
              text: 'Vreme (u časovima)',
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
      }
    });
  }

  createChartNedelja(labele: any[], potrosnja: string[], potrosnjaPred: string[]) {
    let chart = this.elementRef.nativeElement.querySelector(`#MyChart2`);
    this.chartNedelja = new Chart(chart, {
      type: 'bar',
      data: {
        labels:  !this.isMobile ? labele : labele.map((val) => val = val = val.substring(0,5)),
        datasets: [{
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Izmerena potrošnja za prethodnu nedelju[kWh]' : 'Izmerena proizvodnja za prethodnu nedelju[kWh]',
          data:potrosnja,
          backgroundColor: "rgb(68,166,245)",
        },
        {
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Predikovana potrošnja za prethodnu i predikcija za sledeću nedelju[kWh]' : 'Predikovana proizvodnja za prethodnu i predikcija za sledeću nedelju[kWh]',
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
              text: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Potrošnja struje[kWh]' : 'Proizvodnja struje[kwh]',
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

  createChartMesec(labele: any[], potrosnja: string[], potrosnjaPredIst: string[]) {
    let chart = this.elementRef.nativeElement.querySelector(`#MyChart3`);
    this.chartMesec = new Chart(chart, {
      type: 'bar',
      data: {
        labels: labele,
        datasets: [{
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Istorija potrošnje za prethodni mesec[kWh]' : 'Istorija proizvodnje za prethodni mesec[kWh]',
          data:potrosnja,
          backgroundColor: "rgb(68,166,245)",
        },
        {
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Predikovana potrošnja za prethodni mesec[kWh]' : 'Predikovana proizvodnja za prethodni mesec[kWh]',
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
              text: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Potrošnja struje[kWh]' : 'Proizvodnja struje[kWh]',
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

  createChartGodina(labele: any[], potrosnja: string[], potrosnjaPredIst: string[]) {
    let chart = this.elementRef.nativeElement.querySelector(`#MyChart4`);
    this.chartGodina = new Chart(chart, {
      type: 'bar',
      data: {
        labels: labele,
        datasets: [{
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Istorija potrošnje za prethodnu godinu[kWh]' : 'Istorija proizvodnje za prethodnu godinu[kWh]',
          data:potrosnja,
          backgroundColor: "rgb(68,166,245)",
        },
        {
          label: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Predikovana potrošnja za prethodnu godinu[kWh]' : 'Predikovana proizvodnja za prethodnu godinu[kWh]',
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
              text: this.uredjaj.tipUredjaja == 'Potrošač' ? 'Potrošnja struje[kWh]' : 'Proizvodnja struje[kWh]',
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

}
