import { Component } from '@angular/core';
import { DeviceService } from 'app/services/services/deviceService/device.service';
import { HideTooltipStatisiticPotService } from 'app/services/shareTooltipInfoDay/hide-tooltip-statisitic-pot.service';
import { TooltipWeekStatisiticPotService } from 'app/services/shareTooltipInfoDay/tooltip-week-statisitic-pot.service';
import { Chart } from 'chart.js';
import { Util } from 'leaflet';
import { Subscription } from 'rxjs';
import { utils } from 'xlsx';

@Component({
  selector: 'app-chart-korisnici',
  templateUrl: './chart-korisnici.component.html',
  styleUrls: ['./chart-korisnici.component.scss']
})
export class ChartKorisniciComponent {
  public chart: any;
  names: string[] = []
  counts: number[] = []
  colors: string[]=[]
  index?: any
  subscriptionCreateChart: Subscription
  subscriptionTooltip: Subscription




  constructor(private service : DeviceService, private sharedService: TooltipWeekStatisiticPotService, private sharedServiceHide: HideTooltipStatisiticPotService ){}


  showT() {
    this.subscriptionTooltip = this.sharedService.messageSource.subscribe((message) => {
      if (message != '100') {
        this.index = message
       
            this.chart.tooltip.setActiveElements([
              { datasetIndex: 0, index: this.index },
            
          ])
          this.chart.update()
      }

    })
    //da dobija iz servisa index labele


}
hideT(){
this.sharedServiceHide.messageSource.subscribe((mes)=>{
  if(mes!='100'){
    this.chart.tooltip.setActiveElements([]);
    this.chart.setActiveElements([]);

  }
})
}
  getRandomColor2() {
    var length = this.names.length;
    var chars = '0123456789ABCDEF';
    var hex = '#';
    while(length--) hex += chars[(Math.random() * 16) | 0];
    return hex;
  }

  getRandomColor() {
    var color = Math.floor(0x1010000 * Math.random()).toString(16);
    this.colors.push( '#' + ('000000' + color).slice(-6))
  }


  ngOnInit(): void {
    this.subscriptionCreateChart = this.service.getAllDevicePieChart().subscribe(res=>{
      this.colors=['#9CA777','#7C9070','#87CBB9','#B9EDDD','569DAA','41644A']
      res.forEach(x=>{
        this.names.push(x.nazivUredjaja),
        this.counts.push(x.procenat)
        this.getRandomColor()
      })
      // console.log(res)
      this.createChart(this.names, this.counts);
      this.showT()
      this.hideT()
    }
  )}

  ngOnDestroy(){
    this.subscriptionCreateChart.unsubscribe()
    this.subscriptionTooltip.unsubscribe()
  }



  createChart(names: string[], counts: number[]){
  
    this.chart = new Chart("MyChart5", {
      type: 'pie', //this denotes tha type of chart
      data: {// values on X-Axis
        labels:names,
        datasets: [{
          label: ' ',
          data: counts ,
          backgroundColor: ['#9CA777','#7C9070','#87CBB9','#B9EDDD','#569DAA','#41644A'],
          //backgroundColor: ['#4a5b45','#577d4b','#6b9c5c','#7fae71','#a1c597','#EDF1D6'],
          borderWidth: 3,
          hoverOffset: 4
        }]
      },
      options: {
        interaction:{
          mode: 'nearest'
        },
        responsive: true,
        aspectRatio:2.5,
        plugins:{
          legend:{
            
            display: true,
            position: 'right',
            
            align: 'center',
            labels:{
              color: "#FFFFFF",
              padding: 10,
              usePointStyle: true,
              pointStyle: 'dashss'
              
            }
          },
          tooltip:{
            callbacks:{
              label:  function(context) {
                let label = context.dataset.label || '';

                if (label) {
                    label += ': ';
                }
                if (context.parsed !== null) {
                    label += (context.parsed)+'%'
                }
                return label;
            }
            }
          }
        },
      
      
      }
  });
  }

}
