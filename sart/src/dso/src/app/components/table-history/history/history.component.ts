import { AfterViewInit, Component, ElementRef, EventEmitter, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { Korisnik } from 'app/models/korisnik';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { Route, Router } from '@angular/router';
import * as XLSX from 'xlsx';
import { DeviceService } from 'app/services/services/deviceService/device.service';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { DatePipe } from '@angular/common';
import { Record } from 'app/models/record';
import { TableHistory } from 'app/models/tableHistory';
import { SharedService } from 'app/services/shared.service';
import { TooltipDayService } from 'app/services/shareTooltipInfoDay/tooltip-day.service';
import { HideTooltipService } from 'app/services/shareTooltipInfoDay/hide-tooltip.service';
import { TooltipWeekHistoryService } from 'app/services/shareTooltipInfoDay/tooltip-week-history.service';
import { HideTooltipWeekService } from 'app/services/shareTooltipInfoDay/hide-tooltip-week.service';
import { saveAs } from 'file-saver';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;



@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']

})
export class HistoryComponent {
  // korisnici: Korisnik[]=[];
  displayedColumns: string[] = ['date', 'potrosnjaPred', 'potrosnja', 'proizvodnjaPred', 'proizvodnja'];
  dataSource!: MatTableDataSource<any>
  // fileName = 'Korisnici.xlsx'
  records: TableHistory[]
  // clickedRows = new Set<Korisnik>();

  // @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  datePipe = new DatePipe('en-US');
  @ViewChild(MatSort) sort!: MatSort;
  constructor(private Service: RecordService, private router: Router, private sharedService: SharedService, private sharedService1: TooltipWeekHistoryService, private sharedServiceHide: HideTooltipWeekService) { }
  target7days: boolean = false
  targetMonth: boolean = false
  targetYear: boolean = false
  vreme: string = 'Datum'
  filename: string
  header: string
  // @ViewChild('tableRef', { static: false }) tableRef: ElementRef
  // @Output() exportToExcel: EventEmitter<void> = new EventEmitter<void>();
  @Output() dataEmitted = new EventEmitter<any[]>();

  ngOnInit() {
    // const table = this.tableRef.nativeElement;
    this.sharedService1.messageSource.next('100')
    this.sharedServiceHide.messageSource.next('100')
    this.sharedService.messageSource.subscribe((message) => {
      // console.log('Message: ', message); // => Hello from child 1!
      if (message == '7daysBar' || message == '7daysLine') {
        this.vreme = 'Datum'
        this.target7days = true
        this.targetMonth = false
        this.targetYear = false
      } else if (message == 'month') {
        this.vreme = 'Datum'
        this.targetMonth = true
        this.target7days = false
        this.targetYear = false
      } else {
        this.vreme = 'Mesec'
        this.targetYear = true
        this.target7days = false
        this.targetMonth = false
      }
      if (this.target7days) {
        this.getTable7d()
        // console.log('u if 7')
      }
      else if (this.targetMonth) {
        // console.log('u if month')

        this.getTable31days()
      } else {
        this.getTableYear()
      }
    });

    // this.paginator._intl.itemsPerPageLabel="Broj prikaza po stranici";
  }
  exportToExcel() {

    if (this.target7days) {
      this.filename = 'Nedeljni_pregled_istorija'
      var columns = [{ key: 'date', header: 'Datum' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]
    } else if (this.targetMonth) {
      this.filename = 'Mesecni_pregled_istorija'
      var columns = [{ key: 'date', header: 'Datum' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]
    } else {
      this.filename = 'Godisnji_pregled_istorija'
      var columns = [{ key: 'date', header: 'Mesec' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]
    }



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
    saveAs(blob, `${this.filename}.xlsx`);
  }
  exportToPDF(): void {
    // const columnsToExport = ['date', 'potrosnjaPred', 'potrosnja' , 'proizvodnjaPred','proizvodnja'];
    if (this.target7days) {
      this.filename = 'Nedeljni_pregled_istorija'
      var columns = [{ key: 'date', header: 'Datum' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]
      this.header = 'Istorija i predikcija potrošnje i proizvodnje za prethodnih nedelju dana'
    } else if (this.targetMonth) {
      this.filename = 'Mesecni_pregled_istorija'
      var columns = [{ key: 'date', header: 'Datum' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]
      this.header = 'Istorija i predikcija potrošnje i proizvodnje za prethodnih mesec dana'

    } else {
      this.filename = 'Godisnji_pregled_istorija'
      var columns = [{ key: 'date', header: 'Mesec' }, { key: 'potrosnjaPred', header: 'Prediktivna potrošnja[kWh]' },
      { key: 'potrosnja', header: 'Potrošnja[kWh]' }, { key: 'proizvodnjaPred', header: 'Prediktivna proizvodnja[kWh]' },
      { key: 'proizvodnja', header: 'Proizvodnja[kWh]' }
      ]
      this.header = 'Istorija i predikcija potrošnje i proizvodnje za prethodnih godinu dana'

    }


    const tableHeader = columns.map(column => column.header);
    const tableBody = this.records.map(item => columns.map(column => item[column.key]));

    const documentDefinition = {
      // header: {text: this.header, style: 'headerStyle'}, // Add the header here
      content: [
        {text: this.header, style: 'headerStyle'},
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
    pdfDocGenerator.download(`${this.filename}.pdf`);
  }





  getTable7d() {
    this.Service.getHistory7daysTable().subscribe(response => {
      this.records = []
      this.records = response
      for (let i = 0; i < this.records.length; i++) {
        this.records[i].date = (this.datePipe.transform(this.records[i].date, 'dd.MM') as string)
      }
      this.dataSource = new MatTableDataSource(this.records);
      //  this.dataSource.paginator=this.paginator
      this.dataSource.sort = this.sort
      this.dataEmitted.emit(this.records);

      // console.log(this.dataSource)
    })
  }

  getTable31days() {
    this.Service.getHistoryMonthTable().subscribe(response => {
      this.records = []
      this.records = response
      for (let i = 0; i < this.records.length; i++) {
        this.records[i].date = (this.datePipe.transform(this.records[i].date, 'dd.MM') as string)
      }
      this.dataSource = new MatTableDataSource(this.records);
      //  this.dataSource.paginator=this.paginator
      this.dataSource.sort = this.sort
    })

  }
  getTableYear() {
    this.Service.getHistoryYearTable().subscribe(res => {
      this.records = []
      this.records = res
      const date = new Date();

      for (let i = 0; i < this.records.length; i++) {
        date.setMonth(this.records[i].month - 1)

        this.records[i].date = date.toLocaleString('sr-Latn', {
          month: 'long',
        })
      }
      this.dataSource = new MatTableDataSource(this.records);
      //  this.dataSource.paginator=this.paginator
      this.dataSource.sort = this.sort
    })
  }
  showTooltips(index: any, row: any) {
    //treba da salje grafiku index labele(reda)
    this.sharedService1.messageSource.next(index)
  }
  hideTooltips() {
    this.sharedServiceHide.messageSource.next('hide')

  }


}
