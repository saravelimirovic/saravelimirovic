import { Component } from '@angular/core';
import { Inject} from '@angular/core';
import {MatDialog, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { DialogData } from '../upravljanje-korisnikom/DialogData';
import { ObjectService } from 'app/services/services/objectService/object.service';
import { Objekat } from 'app/models/objekat';
import { ActivatedRoute } from '@angular/router';
import { Object1 } from 'app/models/object';

@Component({
  selector: 'app-dialog-object',
  templateUrl: './dialog-object.component.html',
  styleUrls: ['./dialog-object.component.scss']
})
export class DialogObjectComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: {id: number},
                                              private objectService: ObjectService,
                                              private route: ActivatedRoute) {}
  
  objekti: Object1[]
  id: any

  ngOnInit() {
    // this.id = this.route.snapshot.params['id'];
    this.id = this.data.id;
    this.objectService.GetAllObjects(this.id).subscribe(res=>{
      this.objekti = res;
    })
  }
}
