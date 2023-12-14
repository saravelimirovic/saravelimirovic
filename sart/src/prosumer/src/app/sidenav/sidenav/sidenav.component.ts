import { Component, EventEmitter, HostListener, OnInit, Output } from '@angular/core';
import { navbarData } from './nav-data';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { filter } from 'rxjs';

interface SideNavToggle{
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})
export class SidenavComponent implements OnInit{

  @Output() onToggleSideNav: EventEmitter<SideNavToggle> = new EventEmitter();
  collapsed = false;
  screenWidth = 0;
  navData = navbarData;
  isMobile?: boolean;

  constructor(private router: Router) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
          if(this.collapsed == true) this.closeSidenav();
      }
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event:any){
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500){
      this.isMobile = true;
    }else{
      this.isMobile = false;
    }
  }

  ngOnInit(): void{
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500) this.isMobile = true;
    else this.isMobile = false;
  }

  toggleCollapse(event:any): void{
    if(!this.isMobile)
    {
      if(event.target.classList[0] == "fas" || event.target.classList[0] == "logo-text" || event.target.classList[0] == "sidenav-nav" || event.target.classList[0] == undefined)
      {
        this.collapsed = !this.collapsed;
        this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
      }
    }
    else{
      if(event.target.classList[0] == "menu-toggle")
      {
        this.collapsed = !this.collapsed;
        this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
      }
    }
  }

  closeSidenav(): void{
    this.collapsed = false;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }
}
