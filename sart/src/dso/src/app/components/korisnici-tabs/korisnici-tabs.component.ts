import { AfterViewInit, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import { MapaComponent } from './mapa/mapa.component';


@Component({
  selector: 'app-korisnici-tabs',
  templateUrl: './korisnici-tabs.component.html',
  styleUrls: ['./korisnici-tabs.component.scss','./bd2.css'],
  encapsulation: ViewEncapsulation.None
})
export class KorisniciTabsComponent   {
  check: string;
  checkG: string;
  
  onTabClick(event: { tab: { textLabel: any; }; }){
    // console.log(event);
    // console.log(event.tab.textLabel)
    if(event.tab.textLabel=="Mapa"){
      this.check='da'
      this.checkG='ne'
    }else if(event.tab.textLabel=="Grafički prikaz"){
      this.check='ne'
      this.checkG='da'
    }
    else{
      this.check='ne'
      this.checkG='ne'
    }
    // console.log(this.check)
  }

}
