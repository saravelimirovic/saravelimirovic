import { Component, ElementRef, ViewChild, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DevicePerHour } from 'app/models/devicePerHour';
import { KorisnikPrikaz } from 'app/models/korisnikPrikaz';
import { NizPerHour } from 'app/models/niz.PerHour';
import { Objekat } from 'app/models/objekat';
import { Rola } from 'app/models/rola';
import { UredjajiPoObjektu } from 'app/models/uredjajiPoObjektu';
import { DeviceService } from 'app/services/services/deviceService/device.service';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import { Chart } from 'chart.js';


@Component({
  selector: 'app-prikaz-uredjaja-korisnika',
  templateUrl: './prikaz-uredjaja-korisnika.component.html',
  styleUrls: ['./prikaz-uredjaja-korisnika.component.scss','./bd2.css'],
  encapsulation: ViewEncapsulation.None
})
export class PrikazUredjajaKorisnikaComponent {
  id: any
  uredjaji: UredjajiPoObjektu[] = []
  uredjajiObjekta: UredjajiPoObjektu[] = []
  korisnik: KorisnikPrikaz
  // pieces: Piece[] = [];
  objekti: Objekat[] = []
  uredjajiPot: UredjajiPoObjektu[] = []
  uredjajiProiz: UredjajiPoObjektu[] = []
  uredjajiSkladista: UredjajiPoObjektu[] = []
  skladista: Storage[] = []
  rola: string
  ids: number[] = []
  idShowedObject: string
  start: boolean = true
  public selectedValue: string
  devicePerHourPot: DevicePerHour[] = []
  public chart: any;
  showGraph: boolean = false
  public niz: Array<DevicePerHour[]>
  nizNiz: NizPerHour[] = new Array<NizPerHour>
  potrObject: number;
  proizObject: number;
  





  constructor(private route: ActivatedRoute,
    private service: DeviceService,
    private serviceUser: UserServiceService) { }

  ngOnInit() {
    this.uredjaji = []
    // this.objekti = []
    this.id = this.route.snapshot.params['id'];
    this.getUser()
    this.getUserDevices()
  }

  getUserDevices() {
    this.service.getDevicesByIdUser(this.id).subscribe(response => {
      this.uredjaji = response
      if(this.uredjaji.length > 0){
      if (this.start) {
        for (let i = 0; i < response.length; i++) {
          if (i == 0) {
            this.objekti[i] = new Objekat(response[i].idObjekta, response[i].lokacijaObjekta)
            this.idShowedObject = this.objekti[i].id + ''
            this.selectedValue = this.idShowedObject
            this.service.getAllPotByObject(this.selectedValue).subscribe(res=>{
              this.potrObject = res
              this.service.getAllProizByObject(this.selectedValue).subscribe(res=>{
                this.proizObject = res
              })
            })
          }
          else {
            this.ids = this.objekti.map((item) => item.id)
            for (let index = 0; index < this.ids.length; index++) {
              if (this.ids[index] != response[i].idObjekta) {
                this.objekti[index + 1] = new Objekat(response[i].idObjekta, response[i].lokacijaObjekta)
              }
            }

          }
        }
      }
      this.service.getStorageByObject(this.idShowedObject).subscribe(res=>{
        this.skladista = []
        this.skladista = res
        // console.log(this.skladista[0]['trenutnoStanje'])
      })
      this.service.getDevicesByIdObject(this.idShowedObject).subscribe(res => {
        this.uredjajiObjekta = []
        this.uredjajiPot = []
        this.uredjajiProiz = []
        this.uredjajiSkladista = []
        this.uredjajiObjekta = res

        for (let index = 0; index < this.uredjajiObjekta.length; index++) {
          if (this.uredjajiObjekta[index].tipUredjaja == 'Potrošač') {
            var i = -1
            this.uredjajiPot.push(this.uredjajiObjekta[index])
            this.service.getPojedinacanUredjaj(this.uredjajiObjekta[index].idObjekatUredjaj).subscribe(res=>{
              for (let j = 0; j < this.uredjajiPot.length; j++) {
                const element = this.uredjajiPot[j];
                if(this.uredjajiPot[j].idObjekatUredjaj == this.uredjajiObjekta[index].idObjekatUredjaj)
                  this.uredjajiPot[j].status = res.status
                
              }
            })
            // console.log(this.uredjajiPot)

          } else if (this.uredjajiObjekta[index].tipUredjaja == 'Proizvođač') {
            this.uredjajiProiz.push(this.uredjajiObjekta[index])
            this.service.getPojedinacanUredjaj(this.uredjajiObjekta[index].idObjekatUredjaj).subscribe(res=>{
              for (let j = 0; j < this.uredjajiProiz.length; j++) {
                const element = this.uredjajiProiz[j];
                if(this.uredjajiProiz[j].idObjekatUredjaj == this.uredjajiObjekta[index].idObjekatUredjaj)
                  this.uredjajiProiz[j].status = res.status
                
              }
            })
          } else {
            this.uredjajiSkladista.push(this.uredjajiObjekta[index])
          }
        }
        for (let index = 0; index < this.uredjajiPot.length; index++) {
          this.service.getUsageDevicePerHoursById(this.uredjajiPot[index].idObjekatUredjaj).subscribe(res => {
            var dates4: string[] = []
            var usages4: string[] = []

            var sum: number = 0
            res.forEach(x => {
              sum += x.usage
              dates4.push(x.time)
              usages4.push(x.usage.toString())
            })
            this.uredjajiPot[index].usageAll = sum

          });
        }
        for (let j = 0; j < this.uredjajiProiz.length; j++) {
          this.service.getUsageDevicePerHoursById(this.uredjajiProiz[j].idObjekatUredjaj).subscribe(res => {
            var sum: number = 0
            res.forEach(x => {
              sum += x.usage
            })
            this.uredjajiProiz[j].usageAll = sum
          })

        }

        if (this.showGraph == true) {
          this.createChart(this.uredjajiPot)
        }
        // console.log(this.uredjajiPot)
      })
    }
    })


  }

  createChart(niz: UredjajiPoObjektu[]) {
    // console.log(niz)
    for (let index = 0; index < niz.length; index++) {
      this.service.getUsageDevicePerHoursById(niz[index].idObjekatUredjaj).subscribe(res => {
        this.devicePerHourPot = res

        // console.log(this.devicePerHourPot)
        var dates2M: string[] = []
        var usages3M: string[] = []
        var labela: string
        labela = niz[index].vrstaUredjaja
        this.devicePerHourPot.forEach(element => {
          dates2M.push(element.time)
          usages3M.push(element.usage.toString())
        });
        
        // if(index==0)
          // this.create(index,dates2M, usages3M, labela)
     
      })
    }
    

  }
  create(index: number,dates2M: string[], usages3M: string[], labela:string){
     var data = {
      labels: dates2M,
      datasets: [
          {
              label: labela,
              backgroundColor: 
                'rgba(255, 99, 132, 0.2)',
                
            
            borderColor: 
                'rgba(255,99,132,1)',
              borderWidth: 1,
              data: usages3M,
          }
      ]
  };if(index!=0){
    var myNewDataset = {
      label: "My Second dataset",
      backgroundColor: "rgba(187,205,151,0.5)",
      borderColor: "rgba(187,205,151,0.8)",
      borderWidth: 1,
      data: usages3M
  }
    this.chart.data.datasets.push(myNewDataset)

  }   
     this.chart = new Chart("MyChart", {
      type: 'line', //this denotes tha type of chart

      data:data,
      options: {
        maintainAspectRatio:false,
        plugins: {

          legend: { display: true, position:'right', labels:{color: "#FFFFFF" }}
          

        },
        scales: {
          y:{
            title: {
             display: true,
             text: 'W',
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
           x:{
             title: {
              display: true,
              text: 'Vreme',
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
  // console.log(this.chart.data.datasets)
  this.chart.update()
  }
  getUser() {
    this.rola = ''
    this.serviceUser.getUser(this.id).subscribe(res => {
      this.korisnik = res
      this.rola = this.korisnik.rola
    })
  }
  onSelected(event: any) {
    this.start = false
    //promena uredjaja zavisno od objekta
    this.idShowedObject = event.target.value
    // console.log(this.idShowedObject)
    this.getUserDevices()
    this.service.getAllPotByObject(this.selectedValue).subscribe(res=>{
      this.potrObject = res
      this.service.getAllProizByObject(this.selectedValue).subscribe(res=>{
        this.proizObject = res
      })
    })

  }

  onTabClick(event: { tab: { textLabel: any; }; }) {
    if (event.tab.textLabel == "Statistika") {
      this.showGraph = true
      this.start = false
      this.getUserDevices()
    } else {
      this.showGraph = false
    }
  }
}
