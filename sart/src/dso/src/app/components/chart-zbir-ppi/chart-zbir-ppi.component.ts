import { DatePipe } from '@angular/common';
import { Component, Input, OnChanges } from '@angular/core';
import { Record } from 'app/models/record';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { HideTooltipWeekService } from 'app/services/shareTooltipInfoDay/hide-tooltip-week.service';
import { HideTooltipService } from 'app/services/shareTooltipInfoDay/hide-tooltip.service';
import { TooltipDayService } from 'app/services/shareTooltipInfoDay/tooltip-day.service';
import { TooltipWeekHistoryService } from 'app/services/shareTooltipInfoDay/tooltip-week-history.service';
import { SharedService } from 'app/services/shared.service';
import { Chart } from 'chart.js';
import 'chart.js/auto';
import { Subscription } from 'rxjs';
@Component({
  selector: 'app-chart-zbir-ppi',
  templateUrl: './chart-zbir-ppi.component.html',
  styleUrls: ['./chart-zbir-ppi.component.scss']
})

export class ChartZbirPPIComponent implements   OnChanges {
  public chart: any 
  datePipe = new DatePipe('en-US');
  target7daysBar: boolean = false
  target7daysLine: boolean = false
  targetMonth: boolean = false
  targetYear: boolean = false
  datePipeM = new DatePipe('en-US');
  mess: string
  @Input() target;
  index?: any
  subscriptionTooltip1: Subscription




  constructor(
    private recordSerice: RecordService,  private sharedService: TooltipWeekHistoryService, public datepipe: DatePipe, private sahredHideT: HideTooltipWeekService
  ) {}

  showT() {
    this.subscriptionTooltip1 = this.sharedService.messageSource.subscribe((message) => {
      if (message != '100') {
        this.index = message
       
            this.chart.tooltip.setActiveElements([
            { datasetIndex: 0, index: this.index },
            { datasetIndex: 1, index: this.index },
            { datasetIndex: 2, index: this.index },
            { datasetIndex: 3, index: this.index }
          ])
          this.chart.update()
      }

    })
    //da dobija iz servisa index labele


}
hideT(){
this.sahredHideT.messageSource.subscribe((mes)=>{
  if(mes!='100'){
    this.chart.tooltip.setActiveElements([]);
    this.chart.setActiveElements([]);

  }
})
}

  ngOnChanges(): void {

      if (this.target == '7daysBar') {
        if( this.targetMonth == true || this.targetYear== true || this.target7daysLine == true) {this.chart.destroy()}

        this.target7daysBar = true
        this.targetMonth = false
        this.targetYear = false
        this.target7daysLine = false
      }else if(this.target == '7daysLine'){
        if( this.targetMonth == true || this.targetYear== true || this.target7daysBar == true) {this.chart.destroy()}

        this.target7daysLine = true
        this.target7daysBar = false
        this.targetMonth = false
        this.targetYear = false
      }else if (this.target == 'month') {
        if( this.target7daysBar == true || this.targetYear== true || this.target7daysLine == true) {this.chart.destroy()}

        this.targetMonth = true
        this.target7daysBar = false
        this.target7daysLine = false
        this.targetYear = false
      } else {
        if( this.target7daysBar == true || this.targetMonth== true || this.target7daysLine == true) {this.chart.destroy()}

        this.targetYear = true
        this.target7daysBar = false
        this.target7daysLine = false
        this.targetMonth = false
      }
    if (this.target7daysBar == true) {
        this.recordSerice.getHistroryP().subscribe(response => {
        var dates: string[] = []
        var usages: string[] = []
        response.forEach(x => {
          dates.push(this.datePipe.transform(x.date, 'dd.MM') as string)
          usages.push(x.usage.toString())
        })
        this.recordSerice.getHistoryProzv().subscribe(response1 => {
          var dates1: string[] = []
          var usages1: string[] = []
          response1.forEach(x1 => {
            dates1.push(x1.date)
            usages1.push(x1.usage.toString())
          })
          // this.createChart(dates, usages, usages1)
          this.recordSerice.getHistroryPredP().subscribe(response => {
            var dates3: string[] = []
            var usages3: string[] = []
            response.forEach(x1 => {
              dates3.push(x1.date)
              usages3.push(x1.usage.toString())
            })
            // this.createChart(dates, usages, usages1,usages3)
            this.recordSerice.getHistoryPredProzv().subscribe(res => {
              var dates4: string[] = []
              var usages4: string[] = []
              res.forEach(x1 => {
                dates4.push(x1.date)
                usages4.push(x1.usage.toString())
              })
             
              this.createChart(dates, usages, usages1, usages3, usages4)
              this.showT()
              this.hideT()

            })

          })
        })
      })
    }
    if (this.target7daysLine == true) {
      this.recordSerice.getHistroryP().subscribe(response => {
        var dates: string[] = []
        var usages: string[] = []
        response.forEach(x => {
          dates.push(this.datePipe.transform(x.date, 'dd.MM') as string)
          usages.push(x.usage.toString())
        })
        this.recordSerice.getHistoryProzv().subscribe(response1 => {
          var dates1: string[] = []
          var usages1: string[] = []
          response1.forEach(x1 => {
            dates1.push(x1.date)
            usages1.push(x1.usage.toString())
          })
          // this.createChart(dates, usages, usages1)
          this.recordSerice.getHistroryPredP().subscribe(response => {
            var dates3: string[] = []
            var usages3: string[] = []
            response.forEach(x1 => {
              dates3.push(x1.date)
              usages3.push(x1.usage.toString())
            })
            // this.createChart(dates, usages, usages1,usages3)
            this.recordSerice.getHistoryPredProzv().subscribe(res => {
              var dates4: string[] = []
              var usages4: string[] = []
              res.forEach(x1 => {
                dates4.push(x1.date)
                usages4.push(x1.usage.toString())
              })
              this.createChart2(dates, usages, usages1, usages3, usages4)

            })

          })
        })
      })
    }

    

    if (this.targetMonth == true) {
      this.recordSerice.getHistroryPMonth().subscribe(response => {
        var datesM: string[] = []
        var usagesM: string[] = []
        response.forEach(x => {
          datesM.push(this.datePipeM.transform(x.date, 'dd.MM') as string)
          usagesM.push(x.usage.toString())
        })
        this.recordSerice.getHistoryProzvMonth().subscribe(response1 => {
          var dates1M: string[] = []
          var usages1M: string[] = []
          response1.forEach(x1 => {
            dates1M.push(x1.date)
            usages1M.push(x1.usage.toString())
          })
          this.recordSerice.getHistroryPredPMonth().subscribe(res => {
            var dates2M: string[] = []
            var usages2M: string[] = []
            res.forEach(x1 => {
              dates2M.push(x1.date)
              usages2M.push(x1.usage.toString())
            })
            this.recordSerice.getHistoryPredProzvMonth().subscribe(res => {
              var dates2M: string[] = []
              var usages3M: string[] = []
              res.forEach(x1 => {
                dates2M.push(x1.date)
                usages3M.push(x1.usage.toString())
              })
              this.createChart2(datesM, usagesM, usages1M, usages2M, usages3M)
            })
          })
          // this.createChart2(datesM, usagesM, usages1M)
        })
      })

    }
    if (this.targetYear == true) {
      // this.chart.destroy()
      this.recordSerice.getHistoryPYear().subscribe(response => {
        var datesM: string[] = []
        var usagesM: string[] = []
        const date = new Date();
        response.forEach(x => {
          date.setMonth(x.month - 1)

          datesM.push(date.toLocaleString('sr-Latn', {
            month: 'short',
          }))
          usagesM.push(x.usage.toString())
        })
        this.recordSerice.getHistoryProzvYear().subscribe(response1 => {
          var dates1M: string[] = []
          var usages1M: string[] = []
          response1.forEach(x1 => {
            dates1M.push(x1.month.toString())
            usages1M.push(x1.usage.toString())
          })
          this.recordSerice.getHistoryPredPYear().subscribe(res => {
            var dates2M: string[] = []
            var usages2M: string[] = []
            res.forEach(x1 => {
              // dates2M.push(x1.date)
              usages2M.push(x1.usage.toString())
            })
            this.recordSerice.getHistoryPredProzvYear().subscribe(res => {
              var dates2M: string[] = []
              var usages3M: string[] = []
              res.forEach(x1 => {
                // dates2M.push(x1.date)
                usages3M.push(x1.usage.toString())
              })
              this.createChart2(datesM, usagesM, usages1M, usages2M, usages3M)
            })
          })
        })
      })

    }

  }

  ngOnDestroy(){
    if(this.subscriptionTooltip1)
      this.subscriptionTooltip1.unsubscribe()
  }

  createChart(dates: string[], usage: string[], usages1: string[], usages3: string[], usages4: string[]) {
   
     this.chart = new Chart("MyChart", {
      type: 'bar', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: "Prediktivna potrošnja",
            data: usages3,
            borderColor: '#6DA9E4',
            backgroundColor: '#6DA9E4',
          },
          {
            label: "Potrošnja",
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
          },
          {
            label: "Prediktivna proizvodnja",
            data: usages4,
            borderColor: '#F7D060',
            backgroundColor: '#F7D060',
          },


          {
            label: "Proizvodnja",
            data: usages1,
            borderColor: '#fb9b0b',
            backgroundColor: '#fb9b0b',
          }

        ]
      },
      options: {
        maintainAspectRatio: false,
        plugins: {
          tooltip:{
            callbacks:{
              label:  function(context) {
                let label = context.dataset.label || '';
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

          legend: { display: true, position: 'top', labels: { color: "#FFFFFF" } }


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

  }
  createChart2(dates: string[], usage: string[], usages1: string[], usages2M: string[], usages3M: string[]) {
    this.chart = new Chart("MyChart", {
      type: 'line', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: "Potrošnja",
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
            // pointStyle: 'line'

          },
          {
            label: "Prediktivna potrošnja",
            data: usages2M,
            borderColor: '#6DA9E4',
            backgroundColor: '#6DA9E4',
            borderDash: [5, 6],
            // pointStyle: 'dash'

          },
          {
            label: "Proizvodnja",
            data: usages1,
            borderColor: '#fb9b0b',
            backgroundColor: '#fb9b0b',
            // pointStyle: 'line'

          },
          
          {
            label: "Prediktivna proizvodnja",
            data: usages3M,
            borderColor: '#F7D060',
            backgroundColor: '#F7D060',
            borderDash: [5, 6],
            // pointStyle: 'dash'

          },
         
        ]
      },
      options: {
        interaction:{
          mode: 'point'
        },
      
        maintainAspectRatio: false,
        plugins: {
          tooltip:{
            callbacks:{
              label:  function(context) {
                let label = context.dataset.label || '';
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
            // display: true, position: 'top', labels: {
            //   color: "#FFFFFF", usePointStyle: true,
            //   pointStyle: 'line'
            // }
            maxWidth: 150,display: true, position: 'top', labels: {   generateLabels: (chart: Chart) => {
              const originalLabels = Chart.defaults.plugins.legend.labels.generateLabels(chart);

              originalLabels.forEach((label, index) => {
                if (index === 1 || index === 3) {
                  // Customize the style of the legend label for the second and fourth datasets
                  label.lineDash = [4, 4];
                  // label.text += '*'; // Add a marker element
                }
              });

              return originalLabels;
            }, usePointStyle: true, pointStyle: 'line'}
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

  }
}