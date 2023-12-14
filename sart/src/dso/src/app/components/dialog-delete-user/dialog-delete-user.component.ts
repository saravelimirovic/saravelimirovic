import { Component, Inject} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UpravljanjeKorisnikomComponent } from '../upravljanje-korisnikom/upravljanje-korisnikom.component';

@Component({
  selector: 'app-dialog-delete-user',
  templateUrl: './dialog-delete-user.component.html',
  styleUrls: ['./dialog-delete-user.component.scss']
})
export class DialogDeleteUserComponent {
  constructor(
    public dialogRef: MatDialogRef<UpravljanjeKorisnikomComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {}

  Da(){
    this.data = 'izveziF'
  }
}
