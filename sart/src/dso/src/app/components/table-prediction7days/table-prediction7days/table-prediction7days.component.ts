import {Component, ViewChild, ViewEncapsulation} from '@angular/core';
import { Korisnik } from 'app/models/korisnik';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import { Route, Router } from '@angular/router';
import * as XLSX from 'xlsx';
import { DeviceService } from 'app/services/services/deviceService/device.service';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { DatePipe } from '@angular/common';
import { Record } from 'app/models/record';
import { TableHistory } from 'app/models/tableHistory';
import { TooltipWeekPredictService } from 'app/services/shareTooltipInfoDay/tooltip-week-predict.service';
import { HideTooltipWeekPredictService } from 'app/services/shareTooltipInfoDay/hide-tooltip-week-predict.service';
import { saveAs } from 'file-saver';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;



@Component({
  selector: 'app-table-prediction7days',
  templateUrl: './table-prediction7days.component.html',
  styleUrls: ['./table-prediction7days.component.scss']

})
export class TablePrediction7daysComponent{
  // korisnici: Korisnik[]=[];
  displayedColumns: string[] = ['date', 'potrosnja', 'proizvodnja'];
  dataSource!: MatTableDataSource<any>;
  fileName = 'Korisnici.xlsx'
  records: TableHistory[]
  // clickedRows = new Set<Korisnik>();

  // @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  datePipe = new DatePipe('en-US');
  @ViewChild(MatSort) sort!: MatSort;
  constructor( private Service: RecordService, private router: Router,private sharedService1: TooltipWeekPredictService, private sharedServiceHide: HideTooltipWeekPredictService){}
  
  
  ngOnInit(){
    this.sharedService1.messageSource.next('100')
    this.sharedServiceHide.messageSource.next('100')
    this.getAllUsers()
    // this.paginator._intl.itemsPerPageLabel="Broj prikaza po stranici";
  }
  
  getAllUsers() {
    this.Service.getPrediction7daysTable().subscribe(response=>{
      this.records=response
      for(let i=0; i< this.records.length;i++){
        this.records[i].date=(this.datePipe.transform(this.records[i].date, 'dd.MM')as string)
      }
     this.dataSource = new MatTableDataSource(this.records);
    //  this.dataSource.paginator=this.paginator
     this.dataSource.sort = this.sort
     })
  }
  showTooltips(index: any, row: any){
    //treba da salje grafiku index labele(reda)
      this.sharedService1.messageSource.next(index)
    }
  hideTooltips(){
    this.sharedServiceHide.messageSource.next('hide')
      
  }

  //za exporte
  exportToExcel() {
    var columns = [{ key: 'date', header: 'Datum' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]

    const filteredData = this.records.map(item => {
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
    saveAs(blob, `${'Nedeljni_pregled_predikcija'}.xlsx`);
  }
  exportToPDF(): void {
    var columns = [{ key: 'date', header: 'Datum' },
    { key: 'potrosnja', header: 'Potrošnja[kWh]' },
    { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
    ]


    const tableHeader = columns.map(column => column.header);
    const tableBody = this.records.map(item => columns.map(column => item[column.key]));

    const documentDefinition = {
      // header: {text: this.header, style: 'headerStyle'}, // Add the header here
      content: [
        {text: 'Predikcija potrošnje i proizvodnje za narednih 7 dana', style: 'headerStyle'},
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
        tableHeader: {
          bold: true,
          // fillColor: '#CCCCCC'
        },
        headerStyle: {
          alignment: 'center', 
          fontSize: 15, 
          bold: true, 
          margin: [0, 20, 0, 10] ,
          fillColor: '#CCCCCC'

        }
      }
    };

    const pdfDocGenerator = pdfMake.createPdf(documentDefinition);
    pdfDocGenerator.download(`${'Nedeljni_pregled_predikcija'}.pdf`);
  }



}







