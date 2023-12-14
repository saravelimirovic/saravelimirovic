import { Component, Input, ViewChild, ViewEncapsulation } from '@angular/core';
import { Korisnik } from 'app/models/korisnik';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { Route, Router } from '@angular/router';
import * as XLSX from 'xlsx';
import { PagingDTO } from 'app/models/pagingDTO';
import { AllUsers } from 'app/models/allUsers';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogExcelComponent } from 'app/components/dialog-excel/dialog-excel.component';
import { saveAs } from 'file-saver';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'app-tabela-korisnici',
  templateUrl: './tabela-korisnici.component.html',
  styleUrls: ['./tabela-korisnici.component.scss'],

})
export class TabelaKorisniciComponent {
  korisnici: Korisnik[]
  displayedColumns: string[] = ['ime', 'grad', 'naselje', 'ulica', 'potrosnja', 'proizvodnja', 'rola', 'edit'];
  dataSource!: MatTableDataSource<any>;
  fileName = 'Korisnici.xlsx'
  maxSize = 5;
  start = 1;
  last = 10;
  maxPotr: number;
  maxProiz: number;
  max1: number;
  max2: number;
  data: any ;
  // clickedRows = new Set<Korisnik>();

  // @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  public pageSize = 5;
  public isFiltered: boolean;

  // public currentPage = 0;
  constructor(private userService: UserServiceService, private router: Router, private http: HttpClient, private recordService: RecordService,
    private dialog: MatDialog) { }

  public pagingInfo: PagingDTO = new PagingDTO();
  public countUsers: AllUsers = new AllUsers()

  pageOptions = {
    "pageIndex": 1,
    "pageSize": 10
  }

  filterObj = {
    "id": 0,
    "ime": null,
    "grad": null,
    "naselje": null,
    "ulica": null,
    "potrosnjaKorisnika": 0,
    "proizvodnjaKorisnika": 0,
    "rolaVracam": null,
    "potrosnjaOd": null,
    "potrosnjaDo": null,
    "proizvodnjaOd": null,
    "proizvodnjaDo": null,
    "rolaUzimam": 0,
    "pageIndex": 1,
    "pageSize": 10

  }

  p: number = 1;
  total: number = 0;
  itemsPerPage: number = 10;
  brojKojiDobijamPoStrani: number

  ngOnInit() {
    this.filterUsers()
  }
  // fillMax() {
  //   this.recordService.getMaxSlider().subscribe(res => {
  //     this.maxPotr = res.maximumPotr
  //     this.maxProiz = res.maximumProiz
  //     this.filterObj.potrosnjaDo = res.maximumPotr
  //     this.max1 = res.maximumPotr
  //     this.max2 = res.maximumProiz
  //     // console.log(this.filterObj.potrosnjaDo)
  //     this.filterObj.proizvodnjaDo = res.maximumProiz
  //     this.filterUsers()
  //     this.checkIfFiltered()

  //   })

  // }
  onPrevious() {
    this.p --;
    this.filterObj.pageIndex--;
    this.filterUsers()
  }
  onNext() {
    this.p ++;
    this.filterObj.pageIndex++;
    this.filterUsers()
  }
  getCount() {
    this.userService.getAllUsersCount().subscribe(res => {
      this.countUsers = res
      this.countUsers.ukupno = res.ukupno
      this.total = res.ukupno
    })
  }

  getAllUsers() {
    this.filterObj.pageIndex = this.p
    this.filterObj.pageSize = this.filterObj.pageSize
    this.userService.getAllUsers(this.pagingInfo).subscribe(response => {
      this.total = response[0].brojKorisnika
      this.korisnici = response
      this.dataSource = new MatTableDataSource(this.korisnici)
    })
  }
  filterUsers() {
    this.checkIfFiltered()
    this.filterObj.pageIndex = this.p
    this.filterObj.pageSize = this.filterObj.pageSize
    // console.log(this.filterObj)
    this.userService.getUsersFiltered(this.filterObj).subscribe(res => {
      if(res.length > 0){ 
      
      this.korisnici = res
    // console.log(this.korisnici)

      this.brojKojiDobijamPoStrani = this.korisnici.length
      this.total = res[0].brojKorisnika
      if(this.brojKojiDobijamPoStrani < 10 && this.p == 1){
        // console.log('prvi')
        this.last = this.brojKojiDobijamPoStrani
      }else if(this.p == 1){
        // console.log('drugi')
        this.last = 10
      }
      else {
        this.last = this.p * 10
      }
      if (this.last > this.total) {
        // console.log('treci')
        this.last = this.total
      }

      // console.log(this.last)
      // else if(this.last > this.total) this.last = this.total
      this.dataSource = new MatTableDataSource(this.korisnici)
    }else{
      this.p = 1
      this.start = 1
      this.filterUsers()
      // this.last = this.korisnici.length
      // this.total = this.korisnici.length
    }
    })
  }
  obrisiFilter() {
    this.filterObj = {
      "id": 0,
      "ime": null,
      "grad": null,
      "naselje": null,
      "ulica": null,
      "potrosnjaKorisnika": 0,
      "proizvodnjaKorisnika": 0,
      "rolaVracam": null,
      "potrosnjaOd": null,
      "potrosnjaDo": null,
      "proizvodnjaOd": null,
      "proizvodnjaDo": null,
      "rolaUzimam": 0,
      "pageIndex": 1,
      "pageSize": 10
    }
    // this.fillMax()
    this.p = 1
    this.start = 1
    this.filterUsers()
  }
  checkIfFiltered() {
    if (this.filterObj.ime != null || this.filterObj.grad != null || this.filterObj.naselje != null ||
      this.filterObj.ulica != null || this.filterObj.potrosnjaOd != null ||  this.filterObj.potrosnjaDo != null ||
      this.filterObj.proizvodnjaOd != null || this.filterObj.proizvodnjaDo != null || this.filterObj.rolaUzimam != 0) {
      this.isFiltered = true
      // console.log(this.filterObj)
    } else {
      this.isFiltered = false
    }
  }

  editujKorisnika() {
    this.router.navigate(['upravljanje-korisnikom'])
  }
  exportExcel() {
    if (this.isFiltered == true) {
      const dialogRef = this.dialog.open(DialogExcelComponent, {
        data: this.data,
      })
      dialogRef.afterClosed().subscribe(async res => {
        this.data = res
        // console.log(res.clicked)
        if (res.clicked == 'Ok') {
          const table = document.querySelector('table');
          const exportColumns = [0, 1, 2, 3, 4, 5, 6]; 
          const tableData = Array.from(table!.querySelectorAll('tr')).map((row: any) =>
            Array.from(row.querySelectorAll('th, td')).filter((_cell: any, index: number) =>
              exportColumns.includes(index)
            ).map((cell: any) => cell.innerText)
          );

          const workbook = XLSX.utils.book_new();
          const worksheet = XLSX.utils.aoa_to_sheet(tableData);

          XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

          const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });

          const excelBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          saveAs(excelBlob, 'Tabela_korisnika.xlsx');
        }else if(res.clicked == 'Ok1'){
          this.obrisiFilter()
          await new Promise(f => setTimeout(f, 2000));//mora da saceka da se ucitaju podaci pa tek onda sve da sacuva
          // console.log(this.korisnici)
          const table = document.querySelector('table');
          const exportColumns = [0, 1 , 2, 3, 4, 5, 6]; 
          const tableData = Array.from(table!.querySelectorAll('tr')).map((row: any) =>
            Array.from(row.querySelectorAll('th, td')).filter((_cell: any, index: number) =>
              exportColumns.includes(index)
            ).map((cell: any) => cell.innerText)
          );

          const workbook = XLSX.utils.book_new();
          const worksheet = XLSX.utils.aoa_to_sheet(tableData);

          XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

          const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });

          const excelBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          saveAs(excelBlob, 'Tabela_korisnika.xlsx');

        }
      })

    }else{
        const table = document.querySelector('table');
        const exportColumns = [0, 1,  2, 3, 4, 5, 6]; 
        const tableData = Array.from(table!.querySelectorAll('tr')).map((row: any) =>
        Array.from(row.querySelectorAll('th, td')).filter((_cell: any, index: number) =>
          exportColumns.includes(index)
        ).map((cell: any) => cell.innerText)
      );

      const workbook = XLSX.utils.book_new();
      const worksheet = XLSX.utils.aoa_to_sheet(tableData);

      XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

      const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });

      const excelBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      saveAs(excelBlob, 'Tabela_korisnika.xlsx');
    }
}
exportToPDF(): void {



    ///
    if (this.isFiltered == true) {
      const dialogRef = this.dialog.open(DialogExcelComponent, {
        data: this.data,
      })
      dialogRef.afterClosed().subscribe(async res => {
        this.data = res
        // console.log(res.clicked)
        if (res.clicked == 'Ok') {
          //izvezi filtriranu
          var columns = [{ key: 'ime', header: 'Ime' }, { key: 'grad', header: 'Grad' },
          { key: 'naselje', header: 'Naselje' }, { key: 'ulica', header: 'Ulica' },
          { key: 'rola', header: 'Rola' }, { key: 'potrosnja', header: 'Mesečna potrošnja[kWh]' }, { key: 'proizvodnja', header: 'Mesečna proizvodnja[kWh]' }
          ]
      
      
          const tableHeader = columns.map(column => column.header);
      
          const tableBody = this.korisnici.map(item => {
            const row = columns.map(column => item[column.key] || ''); // Replace undefined values with an empty string
            while (row.length < tableHeader.length) {
              row.push(''); // Pad the row with empty strings to match the length of tableHeader
            }
            return row;
          });
      
          const documentDefinition = {
            content: [
              {text: 'Korisnici', style: 'headerStyle'},
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
          pdfDocGenerator.download(`${'Korisnici'}.pdf`);

        }else if(res.clicked == 'Ok1'){
          this.obrisiFilter()
          await new Promise(f => setTimeout(f, 2000));//mora da saceka da se ucitaju podaci pa tek onda sve da sacuva
          //izvezi celu
          var columns = [{ key: 'ime', header: 'Ime' }, { key: 'grad', header: 'Grad' },
          { key: 'naselje', header: 'Naselje' }, { key: 'ulica', header: 'Ulica' },
          { key: 'rola', header: 'Rola' }, { key: 'potrosnja', header: 'Mesečna potrošnja[kWh]' }, { key: 'proizvodnja', header: 'Mesečna proizvodnja[kWh]' }
          ]
      
      
          const tableHeader = columns.map(column => column.header);
      
          const tableBody = this.korisnici.map(item => {
            const row = columns.map(column => item[column.key] || ''); // Replace undefined values with an empty string
            while (row.length < tableHeader.length) {
              row.push(''); // Pad the row with empty strings to match the length of tableHeader
            }
            return row;
          });
      
          const documentDefinition = {
            content: [
              {text: 'Korisnici', style: 'headerStyle'},
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
          pdfDocGenerator.download(`${'Korisnici'}.pdf`);

        }
      })

    }else{
      //izvezi celu kad ne filtrira

      var columns = [{ key: 'ime', header: 'Ime' }, { key: 'grad', header: 'Grad' },
      { key: 'naselje', header: 'Naselje' }, { key: 'ulica', header: 'Ulica' },
      { key: 'rola', header: 'Rola' }, { key: 'potrosnja', header: 'Mesečna potrošnja[kWh]' }, { key: 'proizvodnja', header: 'Mesečna proizvodnja[kWh]' }
      ]
  
  
      const tableHeader = columns.map(column => column.header);
  
      const tableBody = this.korisnici.map(item => {
        const row = columns.map(column => item[column.key] || ''); // Replace undefined values with an empty string
        while (row.length < tableHeader.length) {
          row.push(''); // Pad the row with empty strings to match the length of tableHeader
        }
        return row;
      });
  
      const documentDefinition = {
        content: [
          {text: 'Korisnici', style: 'headerStyle'},
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
      pdfDocGenerator.download(`${'Korisnici'}.pdf`);

    }
}

  pageChangeEvent(event: number) {
    this.start = event
    this.start = this.start * 10 - 9
    this.last = event * 10

    this.p = event;
    if (this.last > this.total) {
      this.last = this.total
    }
    this.filterUsers();
  }

}
