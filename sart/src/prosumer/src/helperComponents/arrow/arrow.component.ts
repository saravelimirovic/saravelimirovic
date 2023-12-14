import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-arrow',
  templateUrl: './arrow.component.html',
  styleUrls: ['./arrow.component.scss']
})
export class ArrowComponent implements OnInit{

  @Input() range?: number;
  klasa: string = "";
  @Input() isPotrosnja? : boolean;

  ngOnInit() : void {
    if(this.isPotrosnja)
    {
      if(this.range!! > 0){
        this.klasa = "up red";
      }else if (this.range!! < 0){
        this.klasa = "down green";
      }
      else if(this.range!! == 0){
        this.klasa = "up yellow";
      }
    }
    else{
      if(this.range!! > 0){
        this.klasa = "up green";
      }else if(this.range!! < 0){
        this.klasa = "down red";
      }
      else if(this.range!! == 0){
        this.klasa = "up yellow";
      }
    }
  }

}
