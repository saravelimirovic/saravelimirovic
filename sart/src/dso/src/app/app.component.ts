import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import {PrimeNGConfig} from 'primeng/api';

interface SideNavToggle{
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = 'dso';

  isSideNavCollapsed = false;
  screenWidth = 0;
  showNavbar : boolean = true;

    constructor(private router: Router,private primengConfig:PrimeNGConfig) {
      router.events.subscribe((val) => {
        if(val instanceof NavigationEnd){
          if(val.url == '/' || val.url == '/login' || val.url.includes('reset')){
            this.showNavbar = false;
          }else{
            this.showNavbar = true;
          }
        }
      })
    }

    ngOnInit(): void{
      this.primengConfig.ripple = true;
    }

  onToggleSideNav(data: SideNavToggle): void{
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
