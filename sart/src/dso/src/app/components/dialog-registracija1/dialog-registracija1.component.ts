import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-registracija1',
  templateUrl: './dialog-registracija1.component.html',
  styleUrls: ['./dialog-registracija1.component.scss']
})
export class DialogRegistracija1Component {
  constructor(@Inject(MAT_DIALOG_DATA) public data: string ) {}
}
