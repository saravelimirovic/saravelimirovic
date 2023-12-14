import { Component } from '@angular/core';
import { LeafletBaseLayersDirective } from '@asymmetrik/ngx-leaflet';
import { DeviceService } from 'app/services/services/deviceService/device.service';
import { TooltipHideEndService } from 'app/services/tooltip-hide-end.service';
import { TooltipShowEndService } from 'app/services/tooltip-show-end.service';
import { Chart } from 'chart.js';
import { Util } from 'leaflet';
import { Subscription } from 'rxjs';
import { utils } from 'xlsx';
@Component({
  selector: 'app-chart-devices-proiz',
  templateUrl: './chart-devices-proiz.component.html',
  styleUrls: ['./chart-devices-proiz.component.scss']
})
export class ChartDevicesProizComponent {

  public chart: any;
  names: string[] = []
  counts: number[] = []
  colors: string[]=[]
  index?: any
  subscriptionCreateChart: Subscription
  subscriptionTooltip: Subscription
  

  constructor(private service : DeviceService,  private sharedService: TooltipShowEndService, private sharedServiceHide: TooltipHideEndService){
  }

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
ngOnDestroy(){
  this.subscriptionCreateChart.unsubscribe()
  this.subscriptionTooltip.unsubscribe()
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
    this.subscriptionCreateChart = this.service.getAllDeviceProizPieChart().subscribe(res=>{
      this.colors=[]
      res.forEach(x=>{
        this.names.push(x.nazivUredjaja),
        this.counts.push(x.procenat)
        this.getRandomColor()
      })
      this.createChart(this.names, this.counts);
      this.showT()
      this.hideT()
    }
  )}



  createChart(names: string[], counts: number[]){
  
    this.chart = new Chart("MyChart6", {
      type: 'pie', //this denotes tha type of chart
      data: {// values on X-Axis
        labels:names,
        datasets: [{
          label: ' ',
          data: counts ,
          backgroundColor: ['#BEF0CB', '#DDFFBB'],
          borderWidth: 3,
          hoverOffset: 4
        }]
      },
      options: {
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
              pointStyle: 'dashss',
              
              
              
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
          },

        },
      
        
      
      }});
  }
  
}
