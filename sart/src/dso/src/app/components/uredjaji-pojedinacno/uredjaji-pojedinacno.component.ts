import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { DeviceInfo } from 'app/models/deviceInfo';
import { RecordForDay } from 'app/models/recordForDay';
import { RecordForWeek } from 'app/models/recordForWeek';
import { DeviceService } from 'app/services/services/deviceService/device.service';
import { Chart } from 'chart.js';
import { Location} from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { KontrolaModalComponent } from '../modal-kontrola/kontrola-modal/kontrola-modal.component';
// import { MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
// import { ModalComponent } from '../modal/modal/modal.component';

import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { UserServiceService } from 'app/services/services/userService/user-service.service';

export interface PeriodicElement {
  // datum: string;
  vreme: string;
  istorija: string;
  predikcija: string;
}




@Component({
  selector: 'app-uredjaji-pojedinacno',
  templateUrl: './uredjaji-pojedinacno.component.html',
  styleUrls: ['./uredjaji-pojedinacno.component.scss','./bd2.css'],
  encapsulation: ViewEncapsulation.None
})
export class UredjajiPojedinacnoComponent implements OnInit{

//   tipUredjaja: string
  infoUredjaj: DeviceInfo
  idUredjaja: any;
  nazivUredjaja: any;
  vrstaUredjaja: any;
  tipUredjaja!: string;
  statusUredjaj: any;
  dozvolaZaKontrolu: any;
  chartMesec: any = null;
  public chartGodina: any = null;
  filename: string;

  open: boolean = false
  //dan
  chartD: any = null;
  potrosnjaDanasObject: any = []
  potrosnjaDanasPredObject: any = []
  potrosnjaDanas: any[] = []
  potrosnjaDanasPredikaicja: any[]=[]
  potrosnja1dLabele: any[] = [];

  //nedelja
  chartN: any = null;
  potrosnja7dIstorija:any = [];
  potrosnja31dIstorija:any = [];
  potrosnja365dIstorija:any = [];
  potrosnja1dPredikcija:any;
  potrosnja7dPredikcija:any;
  potrosnja1dIstorijaPredikcije:any;
  potrosnja7dIstorijaPredikcije:any;
  potrosnja31dIstorijaPredikcije:any;
  potrosnja365dIstorijaPredikcije:any;
  // potrosnja1dLabele: any[] = [];
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
  dataSource: MatTableDataSource<any>
  dataSource2: MatTableDataSource<any>
  dataSource3: MatTableDataSource<any>
  dataSource4: MatTableDataSource<any>
  ELEMENT_DATA: PeriodicElement[] = [
  ];
  ELEMENT_DATA2: PeriodicElement[] = [
  ];
  ELEMENT_DATA3: PeriodicElement[] = [
  ];
  ELEMENT_DATA4: PeriodicElement[] = [
  ];
  

  idKorisnika: any;
  nazivK: any;
  nazivExport: any;



  displayedColumns: string[] = ['vreme', 'istorija', 'predikcija'];
  // dataSource = ELEMENT_DATA;


  constructor(private route: ActivatedRoute, private serviceDevice: DeviceService, private userService: UserServiceService , private _location: Location,  private dialog: MatDialog){}

  // modalRef: MdbModalRef<ModalComponent> | null = null;

  // constructor(private modalService: MdbModalService) {}

  Info(){
    this.serviceDevice.getPojedinacanUredjaj(this.idUredjaja).subscribe(res=>{
        let json = JSON.stringify(res);
        // this.potrosnja1dIstorija = JSON.parse(json) as Potrosnja1d;
        this.infoUredjaj = JSON.parse(json) as DeviceInfo
        // console.log(this.infoUredjaj)
        this.nazivUredjaja = this.infoUredjaj.naziv
        this.vrstaUredjaja = this.infoUredjaj.vrstaUredjaja
        this.statusUredjaj = this.infoUredjaj.status
        this.dozvolaZaKontrolu = this.infoUredjaj.dozvolaZaUpravljanje
        this.getTip(this.infoUredjaj)

    })
  }
  getTip(info: DeviceInfo){
    this.tipUredjaja = info.tipUredjaja
  }

  ngOnInit(): void{
    this.idKorisnika = this.route.snapshot.params['idK']
    // console.log(this.idKorisnika)
    this.userService.getUser(this.idKorisnika).subscribe(res=>{
      this.nazivK = res.ime
    })
    
    this.idUredjaja = this.route.snapshot.params['id'];
    this.Info()
    this.nazivExport = this.nazivK + '_ '+ this.vrstaUredjaja + '_ '
    this.ucitajPodatkeDanas()
    this.ucitajPodatkeNedelja()
    this.ucitajUredjajPodatkeMesec()
    this.ucitajUredjajPodatkeGodina()
  
  }
  ucitajTabeluDanas(labele: string[], potrosnja: string[], proizvodnja: string[]){
    this.ELEMENT_DATA = []
    for (let index = 0; index < labele.length; index++) {
      var test: PeriodicElement = {vreme: labele[index], istorija:potrosnja[index], predikcija: proizvodnja[index] }
      this.ELEMENT_DATA.push(test)
    }
    this.dataSource = new MatTableDataSource(this.ELEMENT_DATA);
  }
  ucitajTabeluNedelja(labele: string[], potrosnja: string[], proizvodnja: string[]){
    this.ELEMENT_DATA2 = []
    for (let index = 0; index < labele.length; index++) {
      var test: PeriodicElement = {vreme: labele[index], istorija:potrosnja[index], predikcija: proizvodnja[index] }
      this.ELEMENT_DATA2.push(test)
    }
    this.dataSource2 = new MatTableDataSource(this.ELEMENT_DATA2);
  }
  ucitajTabeluMesec(labele: string[], potrosnja: string[], proizvodnja: string[]){
    this.ELEMENT_DATA3 = []

    for (let index = 0; index < labele.length; index++) {
      var test: PeriodicElement = {vreme: labele[index], istorija:potrosnja[index], predikcija: proizvodnja[index] }
      this.ELEMENT_DATA3.push(test)
    }
    this.dataSource3 = new MatTableDataSource(this.ELEMENT_DATA3);
  }
  ucitajTabeluGodina(labele: string[], potrosnja: string[], proizvodnja: string[]){
    this.ELEMENT_DATA4 = []
    for (let index = 0; index < labele.length; index++) {
      var test: PeriodicElement = {vreme: labele[index], istorija:potrosnja[index], predikcija: proizvodnja[index] }
      this.ELEMENT_DATA4.push(test)
    }
    this.dataSource4 = new MatTableDataSource(this.ELEMENT_DATA4);
  }

  ucitajPodatkeDanas(){

        this.serviceDevice.getPojedinacanDanas(this.idUredjaja).subscribe((res)=>{
            let json = JSON.stringify(res);
            // this.potrosnja1dIstorija = JSON.parse(json) as Potrosnja1d;
        
            // this.potrosnja1dPotrosnja = this.potrosnja1dIstorija.map((val) => val.usage);
            this.potrosnjaDanasObject = JSON.parse(json) as RecordForDay;
            // console.log(this.potrosnjaDanasObject)

            var time1: string[] = []
            var usage1: string[] = []
            res.forEach(x1 => {
                time1.push(x1.time.toString())
                usage1.push(x1.usage.toString())
              })
            this.potrosnjaDanas = this.potrosnjaDanasObject.map((val)=>val.usage)
            // console.log(this.potrosnjaDanas)

         
        })
        
        this.serviceDevice.getPojedinacanDanasPredikcija(this.idUredjaja).subscribe((res2)=>{
            let json = JSON.stringify(res2);
            // this.potrosnja1dIstorija = JSON.parse(json) as Potrosnja1d;
        
            // this.potrosnja1dPotrosnja = this.potrosnja1dIstorija.map((val) => val.usage);
            this.potrosnjaDanasPredObject = JSON.parse(json) as RecordForDay;
            // console.log(this.potrosnjaDanasPredObject)
            this.potrosnjaDanasPredikaicja = this.potrosnjaDanasPredObject.map((val) => val.usage)
            // console.log(this.potrosnjaDanasPredikaicja)
            this.potrosnja1dLabele = this.potrosnjaDanasPredObject.map((val) => val.time.substring(0,5));
            // console.log(this.potrosnja1dLabele)
            var time2: string[] = []
            var usage2: string[] = []
            res2.forEach(x1 => {
                time2.push(x1.time.substring(0,5).toString())
                usage2.push(x1.usage.toString())
              })
            // this.potrosnjaDanasPredikaicja = res
            // console.log(usage2)
            // this.createChart(time2, usage1 , usage2)
        })

  }

  ucitajPodatkeNedelja(){

    this.serviceDevice.getPojedinacanNedelja(this.idUredjaja).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja7dIstorija = JSON.parse(json) as RecordForWeek;
        this.potrosnja7dPotrosnja = this.potrosnja7dIstorija.map((val) => val.usage);

  
        this.serviceDevice.getPojedinacanNedeljaIstPred(this.idUredjaja).subscribe((res) =>{
          let json = JSON.stringify(res);
          this.potrosnja7dIstorijaPredikcije = JSON.parse(json) as RecordForWeek;
            this.potrosnja7dIstorijaPredikcije.map((val, index) => this.potrosnja7dIstorija[index].usage2 = val.usage);
            this.potrosnja7dPotrosnjaIstPred = this.potrosnja7dIstorijaPredikcije.map((val) => val.usage);
  
      this.serviceDevice.getPojedinacanNedeljaPredikcija(this.idUredjaja).subscribe((res) =>{
        let json = JSON.stringify(res);
        this.potrosnja7dPredikcija = JSON.parse(json) as RecordForWeek;
         this.potrosnja7dLabele = this.potrosnja7dLabele.concat(this.potrosnja7dIstorija.map((val) => val.date),this.potrosnja7dPredikcija.map((val) => val.date));
          this.potrosnja7dLabele = this.potrosnja7dLabele.map((obj) => {
            const [year, month, date] = obj.split("-");
            return `${date}.${month}.${year}.`;
          });
          this.potrosnja7dPredikcija = this.potrosnja7dPredikcija.map((val) => ({date: val.date, usage2: val.usage}));
          this.potrosnja7dIstorija = [...this.potrosnja7dIstorija, ...this.potrosnja7dPredikcija];
          this.potrosnja7dPotrosnjaPred = this.potrosnja7dPotrosnjaIstPred.concat(this.potrosnja7dPredikcija.map((val) => val.usage2));
      });
      });
    });
  }
  ucitajUredjajPodatkeMesec(){
    this.serviceDevice.getPojedinacanMesec(this.idUredjaja).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja31dIstorija = JSON.parse(json) as RecordForWeek;
        this.potrosnja31dPotrosnja = this.potrosnja31dIstorija.map((val) => val.usage);
        this.potrosnja31dLabele = this.potrosnja31dIstorija.map((val) => val.date.substring(8,10)+ '.'+ val.date.substring(5,7));
        // this.potrosnja31dLabele = this.potrosnja31dLabele.map((val)=> val.date.subscribe(0,3))
      this.serviceDevice.getPojedinacanMesecIstPred(this.idUredjaja).subscribe((res) =>{
        let json = JSON.stringify(res);
        this.potrosnja31dIstorijaPredikcije = JSON.parse(json) as RecordForWeek;
          this.potrosnja31dIstorijaPredikcije.map((val, index) => this.potrosnja31dIstorija[index].usage2 = val.usage);
          this.potrosnja31dPotrosnjaIstPred = this.potrosnja31dIstorijaPredikcije.map((val) => val.usage);
      });
    });
  }
  ucitajUredjajPodatkeGodina(){

    this.serviceDevice.getPojedinacanGodina(this.idUredjaja).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.potrosnja365dIstorija = JSON.parse(json) as RecordForWeek;
        this.potrosnja365dPotrosnja = this.potrosnja365dIstorija.map((val) => val.usage);
        this.potrosnja365dLabele = this.potrosnja365dIstorija.map((val) => this.month(val.month) + " " + val.year);
      this.serviceDevice.getPojedinacanGodinaIstPred(this.idUredjaja).subscribe((res) =>{
        let json = JSON.stringify(res);
        this.potrosnja365dIstorijaPredikcije = JSON.parse(json) as RecordForWeek;
          this.potrosnja365dIstorijaPredikcije.map((val, index) => this.potrosnja365dIstorija[index].usage2 = val.usage);
          this.potrosnja365dPotrosnjaIstPred = this.potrosnja365dIstorijaPredikcije.map((val) => val.usage);
      });
    });
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


  
  
tabChanged(event: any) {
    if (event.index === 1) { 
      this.ucitajTabeluDanas(this.potrosnja1dLabele,this.potrosnjaDanas,this.potrosnjaDanasPredikaicja)
     if(this.chartD == null){ this.createChart(this.potrosnja1dLabele,this.potrosnjaDanas,this.potrosnjaDanasPredikaicja);}
   }
   if (event.index === 2) {
      this.ucitajTabeluNedelja(this.potrosnja7dLabele,this.potrosnja7dPotrosnja,this.potrosnja7dPotrosnjaPred)
     if(this.chartN == null) this.createChartNedelja(this.potrosnja7dLabele,this.potrosnja7dPotrosnja,this.potrosnja7dPotrosnjaPred);
   }
   if (event.index === 3) {
    this.ucitajTabeluMesec(this.potrosnja31dLabele,this.potrosnja31dPotrosnja,this.potrosnja31dPotrosnjaIstPred)
     if(this.chartMesec == null) this.createChartMesec(this.potrosnja31dLabele,this.potrosnja31dPotrosnja,this.potrosnja31dPotrosnjaIstPred);
   }
   if (event.index === 4) {
    this.ucitajTabeluGodina(this.potrosnja365dLabele,this.potrosnja365dPotrosnja,this.potrosnja365dPotrosnjaIstPred)
     if(this.chartGodina == null) this.createChartGodina(this.potrosnja365dLabele,this.potrosnja365dPotrosnja,this.potrosnja365dPotrosnjaIstPred);
   }
 }


  createChart(dates: string[], usage: string[], usages1: string[]) {
    this.chartD = new Chart("MyChart", {
      type: 'line', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Izmerena današnja potrošnja' : 'Izmerena današnja proizvodnja',
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
            // pointStyle: 'line'

          },
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Predikovana današnja i predikcija potrošnje za sledeći dan' : 'Predikovana današnja i predikcija proizvodnje za sledeći dan',
            data: usages1,
            borderColor: '#FFA726',
            backgroundColor: '#FFA726',
            // pointStyle: 'line'

          },
        ]
      },
      options: {
        // animation: false,
        interaction:{
          mode: 'point'
        },
      
        maintainAspectRatio: false,
        plugins: {
          tooltip:{
            callbacks:{
              label:  function(context) {
                let label = context.dataset.label || '';
                // console.log(context.parsed.)
                if (label) {
                    label += ': ';
                }
                if (context.parsed !== null) {
                    label += (context.parsed.y)+' [kWh]'
                }
                return label;
            }
          
            }
          },
          legend: {
            display: true, position: 'top', labels: {
              color: "#FFFFFF", usePointStyle: true,
              pointStyle: 'line'
            }
          }


        },
        scales: {
          y: {
            title: {
              display: true,
              text: 'Električna energija [kWh]',
              color: "rgb(255,255,255)",
              font: {
                size: 15
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
              text: 'Vreme [h]',
              color: "rgb(255,255,255)",
              font: {
                size: 15
              }
            },
            ticks: {
              color: 'rgb(221, 201, 201)'
            },
            grid: {
              color: 'rgba(255,255,255,0.2)'
            }

          },
        }
      }
    });

  }
  createChartNedelja(dates: string[], usage: string[], usages1: string[]) {
    this.chartN = new Chart("MyChart2", {
      type: 'bar', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Izmerena potrošnja za prethodnu nedelju' : 'Izmerena proizvodnja za prethodnu nedelju',
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
          },
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Predikcija potrošnje za sledeću nedelju' : 'Predikcija proizvodnje za sledeću nedelju',
            data: usages1,
            borderColor: '#FFA726',
            backgroundColor: '#FFA726',
          },
        ]
      },
      options: {
        // animation: false,
        interaction:{
          mode: 'point'
        },
      
        maintainAspectRatio: false,
        plugins: {
          tooltip:{
            callbacks:{
              label:  function(context) {
                let label = context.dataset.label || '';
                // console.log(context.parsed.)
                if (label) {
                    label += ': ';
                }
                if (context.parsed !== null) {
                    label += (context.parsed.y)+' [kWh]'
                }
                return label;
            }
          
            }
          },
          legend: {
            display: true, position: 'top', labels: {
              color: "#FFFFFF"
            }
          }


        },
        scales: {
          y: {
            title: {
              display: true,
              text: 'Električna energija [kWh]',
              color: "rgb(255,255,255)",
              font: {
                size: 15
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
                size: 15
              }
            },
            ticks: {
              color: 'rgb(221, 201, 201)'
            },
            grid: {
              color: 'rgba(255,255,255,0.2)'
            }

          },
        }
      }
    });

  }
  createChartMesec(dates: string[], usage: string[], usages1: string[]) {
    this.chartMesec = new Chart("MyChart3", {
      type: 'bar', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Istorija potrošnje za prethodni mesec' : 'Istorija proizvodnje za prethodni mesec',
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
          },
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Predikovana potrošnja za prethodni mesec' : 'Predikovana proizvodnja za prethodni mesec',
            data: usages1,
            borderColor: '#FFA726',
            backgroundColor: '#FFA726',
          },
        ]
      },
      options: {
        // animation: false,
        interaction:{
          mode: 'point'
        },
      
        maintainAspectRatio: false,
        plugins: {
          tooltip:{
            callbacks:{
              label:  function(context) {
                let label = context.dataset.label || '';
                // console.log(context.parsed.)
                if (label) {
                    label += ': ';
                }
                if (context.parsed !== null) {
                    label += (context.parsed.y)+' [kWh]'
                }
                return label;
            }
          
            }
          },
          legend: {
            display: true, position: 'top', labels: {
              color: "#FFFFFF"
            }
          }


        },
        scales: {
          y: {
            title: {
              display: true,
              text: 'Električna energija [kWh]',
              color: "rgb(255,255,255)",
              font: {
                size: 15
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
                size: 15
              }
            },
            ticks: {
              color: 'rgb(221, 201, 201)'
            },
            grid: {
              color: 'rgba(255,255,255,0.2)'
            }

          },
        }
      }
    });

  }
  createChartGodina(dates: string[], usage: string[], usages1: string[]) {
    this.chartGodina = new Chart("MyChart4", {
      type: 'bar', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Istorija potrošnje za prethodnu godinu' : 'Istorija proizvodnje za prethodnu godinu',
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
          },
          {
            label: this.tipUredjaja == 'Potrošač' ? 'Predikovana potrošnja za prethodnu godinu' : 'Predikovana proizvodnja za prethodnu godinu',
            data: usages1,
            borderColor: '#FFA726',
            backgroundColor: '#FFA726',
          },
        ]
      },
      options: {
        // animation: false,
        interaction:{
          mode: 'point'
        },
      
        maintainAspectRatio: false,
        plugins: {
          tooltip:{
            callbacks:{
              label:  function(context) {
                let label = context.dataset.label || '';
                // console.log(context.parsed.)
                if (label) {
                    label += ': ';
                }
                if (context.parsed !== null) {
                    label += (context.parsed.y)+' [kWhh]'
                }
                return label;
            }
          
            }
          },
          legend: {
            display: true, position: 'top', labels: {
              color: "#FFFFFF"
            }
          }


        },
        scales: {
          y: {
            title: {
              display: true,
              text: 'Električna energija [kWh]',
              color: "rgb(255,255,255)",
              font: {
                size: 15
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
              text: 'Mesec',
              color: "rgb(255,255,255)",
              font: {
                size: 15
              }
            },
            ticks: {
              color: 'rgb(221, 201, 201)'
            },
            grid: {
              color: 'rgba(255,255,255,0.2)'
            }

          },
        }
      }
    });

  }


  //za tooltipove
  showTooltipsDay(index: any, row: any){
    if(row.istorija!=undefined){  
      this.chartD.tooltip.setActiveElements([
      {datasetIndex: 0, index:index },
      {datasetIndex: 1, index:index }
      ])
    }else{
      this.chartD.tooltip.setActiveElements([
        {datasetIndex: 1, index:index }
        ])

    }
    this.chartD.update()
  }
  hideTooltipsDay(){
    this.chartD.tooltip.setActiveElements([])
      
  }
  showTooltipsWeek(index: any, row: any){
    if(row.istorija!=undefined){  
      this.chartN.tooltip.setActiveElements([
      {datasetIndex: 0, index:index },
      {datasetIndex: 1, index:index }
      ])
    }else{
      this.chartN.tooltip.setActiveElements([
        {datasetIndex: 1, index:index }
        ])

    }
    this.chartN.update()
  }
  hideTooltipsWeek(){
    this.chartN.tooltip.setActiveElements([])
      
  }
  showTooltipsMonth(index: any, row: any){
      this.chartMesec.tooltip.setActiveElements([
      {datasetIndex: 0, index:index },
      {datasetIndex: 1, index:index }
      ])
    
    this.chartMesec.update()
  }
  hideTooltipsMonth(){
    this.chartMesec.tooltip.setActiveElements([])
      
  }
  showTooltipsYear(index: any, row: any){
    this.chartGodina.tooltip.setActiveElements([
    {datasetIndex: 0, index:index },
    {datasetIndex: 1, index:index }
    ])
  
  this.chartGodina.update()
}
hideTooltipsYear(){
  this.chartGodina.tooltip.setActiveElements([])
    
}
backClicked() {
  this._location.back();
}

Izmeni(){
  this.open = true
  // console.log(this.open)
}
OnOff(){
  this.serviceDevice.promeniStatusUredjaja(this.idUredjaja).subscribe(res=>{
    this.Info()
  })
}

exportToExcel() {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_dnevni_pregled'
  var columns = [
    { key: 'vreme', header: 'Vreme' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]
  const filteredData = this.ELEMENT_DATA.map(item => {
    const filteredItem = {};
    columns.forEach(column => {
      filteredItem[column.header] = item[column.key];
    });
    return filteredItem;
  });

  const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(filteredData, { header: columns.map(column => column.header) });
  const workbook: XLSX.WorkBook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
  const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
  const blob: Blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
  saveAs(blob, `${this.filename}.xlsx`);
}
exportToPDF(): void {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_dnevni_pregled'

  var columns = [
    { key: 'vreme', header: 'Vreme' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]

    if (this.tipUredjaja == 'Potrošač')
      var text = 'Istorija i predikcija potrošnje za danas'
    else
      var text = 'Istorija i predikcija proizvodnje za danas'



    const tableHeader = columns.map(column => column.header);

    const tableBody = this.ELEMENT_DATA.map(item => {
      const row = columns.map(column => {
        const value = item[column.key];
        return value === 0 ? '0' : value || ''; // Convert zero values to '0' string
      });
      return row;
    });
  
      const documentDefinition = {
        content: [
          {text: text, style: 'headerStyle'},
          {
            table: {
              headerRows: 1,
              widths: Array(columns.length).fill('*'),
              body: [tableHeader, ...tableBody]
            },
            layout: {
              fillColor: function (rowIndex: number, node: any, columnIndex: number) {
                return rowIndex == 0 ? '#CCCCCC' :'#F2F2F2'; // Set fill color for the header row
              },
              hLineWidth: function (i: number, node: any) {
                return (i === 0 || i === node.table.body.length) ? 0 : 1; // Remove borders for header and footer rows
              },
              vLineWidth: function (i: number, node: any) {
                return 0; // Remove vertical borders for all rows
              }
          }
          }
        ],
        styles: {
          tableStyle: {
            margin: [0, 5, 0, 15] // Add margin to the table
          },
          headerStyle: {
            alignment: 'center', 
            fontSize: 15, 
            bold: true, 
            margin: [0, 20, 0, 10] ,
            fillColor: '#CCCCCC'
  
          },
          tableHeader: {
            bold: true,
            fontSize: 12,
            color: 'white',
            fillColor: '#808080',
            alignment: 'center'
          },
          tableBody: {
            fontSize: 10,
            alignment: 'center'
          }
        }
      };
  
      const pdfDocGenerator = pdfMake.createPdf(documentDefinition);
      pdfDocGenerator.download(`${this.filename}.pdf`);
}
exportToExcel1() {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_nedeljni_pregled'
  var columns = [
    { key: 'vreme', header: 'Datum' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]
  const filteredData = this.ELEMENT_DATA2.map(item => {
    const filteredItem = {};
    columns.forEach(column => {
      filteredItem[column.header] = item[column.key];
    });
    return filteredItem;
  });

  const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(filteredData, { header: columns.map(column => column.header) });
  const workbook: XLSX.WorkBook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
  const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
  const blob: Blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
  saveAs(blob, `${this.filename}.xlsx`);
}
exportToPDF1(): void {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_nedeljni_pregled'

  var columns = [
    { key: 'vreme', header: 'Vreme' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]

    if (this.tipUredjaja == 'Potrošač')
      var text = 'Istorija i predikcija potrošnje za prethodnu i narednu nedelju'
    else
      var text = 'Istorija i predikcija proizvodnje za prethodnu i narednu nedelju'



    const tableHeader = columns.map(column => column.header);

    const tableBody = this.ELEMENT_DATA2.map(item => {
      const row = columns.map(column => {
        const value = item[column.key];
        return value === 0 ? '0' : value || ''; // Convert zero values to '0' string
      });
      return row;
    });
  
      const documentDefinition = {
        content: [
          {text: text, style: 'headerStyle'},
          {
            table: {
              headerRows: 1,
              widths: Array(columns.length).fill('*'),
              body: [tableHeader, ...tableBody]
            },
            layout: {
              fillColor: function (rowIndex: number, node: any, columnIndex: number) {
                return rowIndex == 0 ? '#CCCCCC' :'#F2F2F2'; // Set fill color for the header row
              },
              hLineWidth: function (i: number, node: any) {
                return (i === 0 || i === node.table.body.length) ? 0 : 1; // Remove borders for header and footer rows
              },
              vLineWidth: function (i: number, node: any) {
                return 0; // Remove vertical borders for all rows
              }
          }
          }
        ],
        styles: {
          tableStyle: {
            margin: [0, 5, 0, 15] // Add margin to the table
          },
          headerStyle: {
            alignment: 'center', 
            fontSize: 15, 
            bold: true, 
            margin: [0, 20, 0, 10] ,
            fillColor: '#CCCCCC'
  
          },
          tableHeader: {
            bold: true,
            fontSize: 12,
            color: 'white',
            fillColor: '#808080',
            alignment: 'center'
          },
          tableBody: {
            fontSize: 10,
            alignment: 'center'
          }
        }
      };
  
      const pdfDocGenerator = pdfMake.createPdf(documentDefinition);
      pdfDocGenerator.download(`${this.filename}.pdf`);
}
exportToExcel2() {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_mesecni_pregled'
  var columns = [
    { key: 'vreme', header: 'Datum' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]
  const filteredData = this.ELEMENT_DATA3.map(item => {
    const filteredItem = {};
    columns.forEach(column => {
      filteredItem[column.header] = item[column.key];
    });
    return filteredItem;
  });

  const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(filteredData, { header: columns.map(column => column.header) });
  const workbook: XLSX.WorkBook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
  const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
  const blob: Blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
  saveAs(blob, `${this.filename}.xlsx`);
}
exportToPDF2(): void {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_mesecni_pregled'

  var columns = [
    { key: 'vreme', header: 'Vreme' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]

    if (this.tipUredjaja == 'Potrošač')
      var text = 'Istorija i predikcija potrošnje za prethodnih mesec dana'
    else
      var text = 'Istorija i predikcija proizvodnje za prethodnih mesec dana'



    const tableHeader = columns.map(column => column.header);

    const tableBody = this.ELEMENT_DATA3.map(item => {
      const row = columns.map(column => {
        const value = item[column.key];
        return value === 0 ? '0' : value || ''; // Convert zero values to '0' string
      });
      return row;
    });
  
      const documentDefinition = {
        content: [
          {text: text, style: 'headerStyle'},
          {
            table: {
              headerRows: 1,
              widths: Array(columns.length).fill('*'),
              body: [tableHeader, ...tableBody]
            },
            layout: {
              fillColor: function (rowIndex: number, node: any, columnIndex: number) {
                return rowIndex == 0 ? '#CCCCCC' :'#F2F2F2'; // Set fill color for the header row
              },
              hLineWidth: function (i: number, node: any) {
                return (i === 0 || i === node.table.body.length) ? 0 : 1; // Remove borders for header and footer rows
              },
              vLineWidth: function (i: number, node: any) {
                return 0; // Remove vertical borders for all rows
              }
          }
          }
        ],
        styles: {
          tableStyle: {
            margin: [0, 5, 0, 15] // Add margin to the table
          },
          headerStyle: {
            alignment: 'center', 
            fontSize: 15, 
            bold: true, 
            margin: [0, 20, 0, 10] ,
            fillColor: '#CCCCCC'
  
          },
          tableHeader: {
            bold: true,
            fontSize: 12,
            color: 'white',
            fillColor: '#808080',
            alignment: 'center'
          },
          tableBody: {
            fontSize: 10,
            alignment: 'center'
          }
        }
      };
  
      const pdfDocGenerator = pdfMake.createPdf(documentDefinition);
      pdfDocGenerator.download(`${this.filename}.pdf`);
}
exportToExcel3() {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_godisnji_pregled'
  var columns = [
    { key: 'vreme', header: 'Mesec' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]
  const filteredData = this.ELEMENT_DATA4.map(item => {
    const filteredItem = {};
    columns.forEach(column => {
      filteredItem[column.header] = item[column.key];
    });
    return filteredItem;
  });

  const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(filteredData, { header: columns.map(column => column.header) });
  const workbook: XLSX.WorkBook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
  const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
  const blob: Blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
  saveAs(blob, `${this.filename}.xlsx`);
}
exportToPDF3(): void {
  this.filename = this.nazivK + '_' + this.vrstaUredjaja + '_godisnji_pregled'

  var columns = [
    { key: 'vreme', header: 'Mesec' },
    { key: 'istorija', header: 'Potrošnja[kWh]' }, 
    { key: 'predikcija', header: 'Proizvodnja[kWh]' }
    ]

    if (this.tipUredjaja == 'Potrošač')
      var text = 'Istorija i predikcija potrošnje za prethodnih godinu dana'
    else
      var text = 'Istorija i predikcija proizvodnje za prethodnih godinu dana'



    const tableHeader = columns.map(column => column.header);

    const tableBody = this.ELEMENT_DATA4.map(item => {
      const row = columns.map(column => {
        const value = item[column.key];
        return value === 0 ? '0' : value || ''; // Convert zero values to '0' string
      });
      return row;
    });
  
      const documentDefinition = {
        content: [
          {text: text, style: 'headerStyle'},
          {
            table: {
              headerRows: 1,
              widths: Array(columns.length).fill('*'),
              body: [tableHeader, ...tableBody]
            },
            layout: {
              fillColor: function (rowIndex: number, node: any, columnIndex: number) {
                return rowIndex == 0 ? '#CCCCCC' :'#F2F2F2'; // Set fill color for the header row
              },
              hLineWidth: function (i: number, node: any) {
                return (i === 0 || i === node.table.body.length) ? 0 : 1; // Remove borders for header and footer rows
              },
              vLineWidth: function (i: number, node: any) {
                return 0; // Remove vertical borders for all rows
              }
          }
          }
        ],
        styles: {
          tableStyle: {
            margin: [0, 5, 0, 15] // Add margin to the table
          },
          headerStyle: {
            alignment: 'center', 
            fontSize: 15, 
            bold: true, 
            margin: [0, 20, 0, 10] ,
            fillColor: '#CCCCCC'
  
          },
          tableHeader: {
            bold: true,
            fontSize: 12,
            color: 'white',
            fillColor: '#808080',
            alignment: 'center'
          },
          tableBody: {
            fontSize: 10,
            alignment: 'center'
          }
        }
      };
  
      const pdfDocGenerator = pdfMake.createPdf(documentDefinition);
      pdfDocGenerator.download(`${this.filename}.pdf`);
}
}

