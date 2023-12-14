import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { HideTooltipWeekPredictService } from 'app/services/shareTooltipInfoDay/hide-tooltip-week-predict.service';
import { TooltipWeekPredictService } from 'app/services/shareTooltipInfoDay/tooltip-week-predict.service';
import { Chart } from 'chart.js';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chart-zbir-ppp',
  templateUrl: './chart-zbir-ppp.component.html',
  styleUrls: ['./chart-zbir-ppp.component.scss']
})
export class ChartZbirPPPComponent {
  public chart: any;
  datePipe = new DatePipe('en-US');
  target7days: boolean
  targetMonth: boolean = false
  datePipeM = new DatePipe('en-US');
  index?: any;
  subscription2: Subscription



  constructor(
    private recordSerice: RecordService, private sharedService: TooltipWeekPredictService, public datepipe: DatePipe, private sahredHideT: HideTooltipWeekPredictService
  ) { }
  showT() {
    this.subscription2 = this.sharedService.messageSource.subscribe((message) => {
      if (message != '100') {
        this.index = message

        this.chart.tooltip.setActiveElements([
          { datasetIndex: 0, index: this.index },
          { datasetIndex: 1, index: this.index },
        ])
        this.chart.update()
      }

    })
    //da dobija iz servisa index labele


  }
  hideT() {
    this.sahredHideT.messageSource.subscribe((mes) => {
      if (mes != '100') {
        this.chart.tooltip.setActiveElements([]);
        this.chart.setActiveElements([]);

      }
    })
  }
  ngOnInit(): void {
    if (this.targetMonth == false) {
      this.recordSerice.getPredictionP().subscribe(response => {
        var dates: string[] = []
        var usages: string[] = []
        response.forEach(x => {
          dates.push(this.datePipe.transform(x.date, 'dd.MM') as string)
          usages.push(x.usage.toString())
        })
        this.recordSerice.getPredictionProzv().subscribe(response1 => {
          var dates1: string[] = []
          var usages1: string[] = []
          response1.forEach(x1 => {
            dates1.push(x1.date)
            usages1.push(x1.usage.toString())
          })
          this.createChart(dates, usages, usages1)
          this.showT()
          this.hideT()

        })
      })
    }
    if (this.targetMonth == true) {
      this.chart.destroy()
      this.recordSerice.getPredictionPMonth().subscribe(response => {
        var datesM: string[] = []
        var usagesM: string[] = []
        response.forEach(x => {
          datesM.push(this.datePipeM.transform(x.date, 'dd.MM') as string)
          usagesM.push(x.usage.toString())

        })
        this.recordSerice.getPredictionProzvMonth().subscribe(response1 => {
          var dates1M: string[] = []
          var usages1M: string[] = []
          response1.forEach(x1 => {
            dates1M.push(x1.date)
            usages1M.push(x1.usage.toString())
          })
          this.createChart2(datesM, usagesM, usages1M)
        })
      })

    }
  }
  ngOnDestroy(){
      if(this.subscription2)
        this.subscription2.unsubscribe()
  }

  createChart(dates: string[], usage: string[], usage1: string[]) {

    this.chart = new Chart("MyChart2", {
      type: 'bar', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: "Potrošnja",
            data: usage,
            // prethodna boja #0E8388
            borderColor: '#345c84',
            backgroundColor: '#345c84',
          },
          {
            label: "Proizvodnja",
            data: usage1,
            borderColor: '#fb9b0b',
            backgroundColor: '#fb9b0b',
          }
        ]
      },
      options: {
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
          legend: { display: true, position: 'top', labels: { color: "#FFFFFF" } }
        },
        scales: {
          y: {
            title: {
              display: true,
              text: 'Električna energija[kWh]',
              color: "rgb(255,255,255)",
              font: {
                size: 15,

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
  createChart2(dates: string[], usage: string[], usages1: string[]) {
    this.chart = new Chart("MyChart2", {
      type: 'line', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: dates,
        datasets: [
          {
            label: "Potrošnja",
            data: usage,
            borderColor: '#345c84',
            backgroundColor: '#345c84',
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
          legend: { display: true, position: 'right', labels: { color: "#FFFFFF" } }
        },
        scales: {
          y: {
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
          x: {
            title: {
              display: true,
              text: 'Vreme (u danima)',
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

  getSelectedValue(event: any) {
    // console.log(event.target.value)
    if (event.target.value == 'month') {
      this.targetMonth = true
      this.ngOnInit()

    } else {

      this.targetMonth = false
      this.chart.destroy()
      this.ngOnInit()
    }


  }

}
