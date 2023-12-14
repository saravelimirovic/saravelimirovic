import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-registr-objekta',
  templateUrl: './dialog-registr-objekta.component.html',
  styleUrls: ['./dialog-registr-objekta.component.scss']
})
export class DialogRegistrObjektaComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: string ) {}

}
