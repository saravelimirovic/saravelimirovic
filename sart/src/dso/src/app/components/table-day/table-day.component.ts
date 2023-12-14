import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { DatePipe } from '@angular/common';
import { TooltipDayService } from 'app/services/shareTooltipInfoDay/tooltip-day.service';
import { HideTooltipService } from 'app/services/shareTooltipInfoDay/hide-tooltip.service';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';



export interface PeriodicElement {
  time: string;
  potrosnjaPred: string;
  potrosnja: string;
  proizvodnja: string;
  proizvodnjaPred: string;

}



@Component({
  selector: 'app-table-day',
  templateUrl: './table-day.component.html',
  styleUrls: ['./table-day.component.scss']
})


export class TableDayComponent {
  
  ELEMENT_DATA: PeriodicElement[] = [];

  currentDateTime: string
  displayedColumns: string[] = ['time','potrosnjaPred','potrosnja','proizvodnjaPred', 'proizvodnja'];
  // displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];
  dataSource: MatTableDataSource<any>

  constructor( private serviceRecord: RecordService,public datepipe: DatePipe, private sharedService: TooltipDayService, private sharedServiceHide: HideTooltipService ){
   this.currentDateTime = this.datepipe.transform((new Date), 'HH') +':00'as string;
  }
  ngOnInit(){

    
    this.serviceRecord.getHistoryDayP().subscribe(response=>{  
      var dates: string[] = []
      var usages: string[] = []
      response.forEach(x => {
        dates.push(x.time.substring(0,5))
        // dates.push(x.time)
        usages.push(x.usage.toString())
      },
      )
      this.serviceRecord.getHistoryDayPPred().subscribe(response1 => {
        var dates1: string[] = []
        var usages1: string[] = []
        response1.forEach(x1 => {
          dates1.push(x1.time.substring(0,5))
          usages1.push(x1.usage.toString())
        })
        // this.table.potrosnjaPred = usages1
        this.serviceRecord.getHistoryDayProiz().subscribe(response=>{  
          var dates2: string[] = []
          var usages2: string[] = []
          response.forEach(x => {
            dates2.push(x.time.substring(0,2))
            // dates.push(x.time)
            usages2.push(x.usage.toString())
          })
          this.serviceRecord.getHistoryDayProizPred().subscribe(response1 => {
            var dates3: string[] = []
            var usages3: string[] = []
            response1.forEach(x1 => {
              dates3.push(x1.time.substring(0,2)+'')
              usages3.push(x1.usage.toString())
            })
            // this.table.proizvodnjaPred = usages3
            for (let index = 0; index < dates1.length; index++) {
              var test: PeriodicElement = {time: dates1[index], potrosnja:usages[index], proizvodnja: usages2[index], potrosnjaPred: usages1[index], proizvodnjaPred: usages3[index]}
              this.ELEMENT_DATA.push(test)
            }
            this.dataSource = new MatTableDataSource(this.ELEMENT_DATA);
          })
        })
        // this.createChart(dates1, usages, usages1)
      })
    })

  }
  showTooltips(index: any, row: any){
    //treba da salje grafiku index labele(reda)
      this.sharedService.messageSource.next(index)
    }
  hideTooltips(){
    this.sharedServiceHide.messageSource.next('hide')
      
  }

  exportToExcel() {
    var columns = [{ key: 'time', header: 'Vreme' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
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
    saveAs(blob, `${'Dnevni_pregled_istorija'}.xlsx`);
  }
  exportToPDF(): void {
    var columns = [{ key: 'time', header: 'Vreme' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]


      const tableHeader = columns.map(column => column.header);

      const tableBody = this.ELEMENT_DATA.map(item => {
        const row = columns.map(column => item[column.key] || ''); // Replace undefined values with an empty string
        while (row.length < tableHeader.length) {
          row.push(''); // Pad the row with empty strings to match the length of tableHeader
        }
        return row;
      });
  
      const documentDefinition = {
        content: [
          {text: 'Istorija i predikcija potrošnje i proizvodnje za prethodnih 24 časa', style: 'headerStyle'},
          {
            table: {
              headerRows: 1,
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
      pdfDocGenerator.download(`${'Dnevni_pregled_istorija'}.pdf`);
  }


}
 


