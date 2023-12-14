import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TabelaKorisniciComponent } from '../korisnici-tabs/tabela-korisnici/tabela-korisnici.component';

@Component({
  selector: 'app-dialog-excel',
  templateUrl: './dialog-excel.component.html',
  styleUrls: ['./dialog-excel.component.scss']
})
export class DialogExcelComponent {

  constructor(
    public dialogRef: MatDialogRef<TabelaKorisniciComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {}

  Da(){
    this.data = 'izveziF'
  }
  Da1(){
    this.data = 'izveziS'
  }


}
