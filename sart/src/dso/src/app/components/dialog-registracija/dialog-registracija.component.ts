import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-registracija',
  templateUrl: './dialog-registracija.component.html',
  styleUrls: ['./dialog-registracija.component.scss']
})
export class DialogRegistracijaComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: string ) {}
}
