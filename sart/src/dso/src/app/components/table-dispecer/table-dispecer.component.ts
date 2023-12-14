import { EventListenerFocusTrapInertStrategy } from '@angular/cdk/a11y';
import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Dispecer } from 'app/models/dispecer';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import * as XLSX from 'xlsx';


export interface PeriodicElement {
  ime: string;
  grad: string;
  naselje: string;
  ulica: string;
}


@Component({
  selector: 'app-table-dispecer',
  templateUrl: './table-dispecer.component.html',
  styleUrls: ['./table-dispecer.component.scss']
})
export class TableDispecerComponent {

  constructor(private userService: UserServiceService){
    
  }
  filterObj = {
    "id": 0,
    "ime": "",
    "grad": "",
    "naselje": "",
    "ulica": "",
    "pageIndex": 1,
    "pageSize": 10

  }
  dispeceri: Dispecer[];
  displayedColumns: string[] = ['ime', 'grad', 'naselje', 'ulica'];
  ELEMENT_DATA: PeriodicElement[] = [];
  dataSource: MatTableDataSource<any>
  fileName = 'SpisakDispecera.xlsx'
  total: number
  itemsPerPage: number = 10;
  brojKojiDobijamPoStrani: number
  p: number = 1;
  start: number = 1
  last: number = 10;




  ngOnInit(){
   this.ELEMENT_DATA = []
   this.getDispecere()
   this.getDispecere2()
  }

  getDispecere(){
    this.userService.getAllDis(this.filterObj).subscribe(res=>{
      
      this.dispeceri = res
      if(res.length == 0){
        this.total = 0
        this.start = 0
        this.last = 0
      }else
      {this.total = res[0].brojKorisnika
      this.brojKojiDobijamPoStrani = this.dispeceri.length
          if(this.brojKojiDobijamPoStrani < this.filterObj.pageSize){
            this.itemsPerPage = this.brojKojiDobijamPoStrani
            this.last = this.itemsPerPage //nez da l moze ovde, mozda samo prvi put
          }
     
      for (let index = 0; index < this.dispeceri.length; index++) {
        var test: PeriodicElement = {ime: this.dispeceri[index].ime, grad : this.dispeceri[index].grad , naselje: this.dispeceri[index].naselje, ulica: this.dispeceri[index].ulica}
        this.ELEMENT_DATA.push(test)
      }
      this.dataSource = new MatTableDataSource(this.ELEMENT_DATA);}
    })

  }
  getDispecere2(){
    this.userService.getAllDis(this.filterObj).subscribe(res=>{
      
      this.dispeceri = res
      if(res.length == 0){
        this.total = 0
        this.start = 0
        this.last = 0
      }else
      {this.total = res[0].brojKorisnika
      this.brojKojiDobijamPoStrani = this.dispeceri.length
      if(this.total < 10 && this.p == 1 ){
        this.last = this.brojKojiDobijamPoStrani
      }else if(this.p == 1){
        this.last = 10
      }
      // this.last = this.p * this.brojKojiDobijamPoStrani
       
     
      for (let index = 0; index < this.dispeceri.length; index++) {
        var test: PeriodicElement = {ime: this.dispeceri[index].ime, grad : this.dispeceri[index].grad , naselje: this.dispeceri[index].naselje, ulica: this.dispeceri[index].ulica}
        this.ELEMENT_DATA.push(test)
      }
      this.dataSource = new MatTableDataSource(this.ELEMENT_DATA);}
    })

  }

  exportExcel(){
    let element = document.getElementById('excel-table')
      const ws: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element)
      const wb: XLSX.WorkBook = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
      XLSX.writeFile(wb, this.fileName)
  }

  onPrevious() {
    this.p --;
    this.filterObj.pageIndex--;
    this.getDispecere2()
  }
  onNext() {
    this.p ++;
    this.filterObj.pageIndex++;
    this.getDispecere2()
  }
  pageChangeEvent(event: number) {
    this.start = event
    this.start = this.start * 10 - 9
    this.last = event * 10

    this.p = event;
    if (this.last > this.total) {
      this.last = this.total
    }
    this.filterObj.pageIndex = this.p
    this.filterObj.pageSize = this.filterObj.pageSize
    this.getDispecere2();
  }
  obrisiFilter() {
    this.start = 1
    this.filterObj = {
      "id": 0,
      "ime": "",
      "grad": "",
      "naselje": "",
      "ulica": "",
      "pageIndex": 1,
      "pageSize": 10

    }
   
    this.getDispecere2()
  }
}
