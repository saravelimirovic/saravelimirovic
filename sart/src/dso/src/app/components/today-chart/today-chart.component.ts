import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { HideTooltipService } from 'app/services/shareTooltipInfoDay/hide-tooltip.service';
import { TooltipDayService } from 'app/services/shareTooltipInfoDay/tooltip-day.service';
import Chart from 'chart.js/auto';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-today-chart',
  templateUrl: './today-chart.component.html',
  styleUrls: ['./today-chart.component.scss']
})
export class TodayChartComponent {
  public chart: any;
  index?: any
  currentDateTime: string
  subscription: Subscription


  constructor(private serviceRecord: RecordService, private sharedService: TooltipDayService, public datepipe: DatePipe, private sahredHideT: HideTooltipService) {
    this.currentDateTime = this.datepipe.transform((new Date), 'HH') as string;

  }


  showT() {
    this.subscription = this.sharedService.messageSource.subscribe((message) => {
      if (message) {
        this.index = message

        if (Number(message) <= Number(this.currentDateTime)) {
          this.chart.tooltip.setActiveElements([
            { datasetIndex: 0, index: this.index },
            { datasetIndex: 1, index: this.index },
            { datasetIndex: 2, index: this.index },
            { datasetIndex: 3, index: this.index }
          ])
          this.chart.update()
        }
        else {
          this.chart.tooltip.setActiveElements([
            // { datasetIndex: 0, index: this.index },
            { datasetIndex: 1, index: this.index },
            // { datasetIndex: 2, index: this.index },
            { datasetIndex: 3, index: this.index }
          ])
          this.chart.update()
        }
      }
    })
  }

  hideT() {
    this.sahredHideT.messageSource.subscribe((mes) => {
      if (mes != '100') {
        this.chart.tooltip.setActiveElements([]);
        this.chart.setActiveElements([]);

      }
    })
  }

  ngOnInit() {
    this.serviceRecord.getHistoryDayP().subscribe(response => {
      var dates: string[] = []
      var usages: string[] = []
      response.forEach(x => {
        dates.push(x.time.substring(0, 2))
        // dates.push(x.time)
        usages.push(x.usage.toString())
      },
      )
      this.serviceRecord.getHistoryDayPPred().subscribe(response1 => {
        var dates1: string[] = []
        var usages1: string[] = []
        response1.forEach(x1 => {
          dates1.push(x1.time.substring(0, 5))
          usages1.push(x1.usage.toString())
        })
        this.serviceRecord.getHistoryDayProiz().subscribe(response => {
          var dates2: string[] = []
          var usages2: string[] = []
          response.forEach(x => {
            dates2.push(x.time.substring(0, 2))
            usages2.push(x.usage.toString())
          })
          this.serviceRecord.getHistoryDayProizPred().subscribe(response1 => {
            var dates3: string[] = []
            var usages3: string[] = []
            response1.forEach(x1 => {
              dates3.push(x1.time.substring(0, 2) + '')
              usages3.push(x1.usage.toString())
            })
            this.createChart(dates1, usages, usages1, usages2, usages3)
            // if(this.chart)
            this.showT()
            this.hideT()
          })
        })
        // this.createChart(dates1, usages, usages1)
      })
    })
  }

  ngOnDestroy(){
    if(this.subscription)
      this.subscription.unsubscribe()
      if(this.chart)
        this.chart.destroy()
  }

  createChart(times: string[], usage: string[], usages1: string[], usage2: string[], usages3: string[]) {
    this.chart = new Chart("MyChartDAY", {
      type: 'line', //this denotes tha type of chart
      data: {// values on X-Axis
        labels: times,
        datasets: [
          {
            label: "Potrošnja",
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
            // pointStyle: 'line'

          },
          {
            label: "Predikcija potrošnje",
            data: usages1,
            borderColor: '#6DA9E4',
            backgroundColor: '#6DA9E4',
            borderDash: [5, 6],
            pointStyle: 'dash'

          },
          {
            label: "Proizvodnja",
            data: usage2,
            borderColor: '#fb9b0b',
            backgroundColor: '#fb9b0b',
            // pointStyle: 'line'
            
          },
          {
            label: "Predikcija proizvodnje",
            data: usages3,
            borderColor: '#F7D060',
            backgroundColor: '#F7D060',
            borderDash: [5, 6],
            pointStyle: 'dash'
          }
        ]
      },
      options: {
        interaction: {
          mode: 'point'
        },
        maintainAspectRatio: false,
        plugins: {
          tooltip: {
            callbacks: {
              label: function (context) {
                let label = context.dataset.label || '';
                // console.log(context.parsed.)
                if (label) {
                  label += ': ';
                }
                if (context.parsed !== null) {
                  label += (context.parsed.y) + ' [kWh]'
                }
                return label;
              }

            }
          },

          legend: { maxWidth: 150,display: true, position: 'top', labels: {   generateLabels: (chart: Chart) => {
              const originalLabels = Chart.defaults.plugins.legend.labels.generateLabels(chart);

              originalLabels.forEach((label, index) => {
                if (index === 1 || index === 3) {
                  // Customize the style of the legend label for the second and fourth datasets
                  label.lineDash = [4, 4];
                  // label.text += '*'; // Add a marker element
                }
              });

              return originalLabels;
            }, usePointStyle: true, pointStyle: 'line'}}


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
              text: 'Vreme[h]',
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
}
