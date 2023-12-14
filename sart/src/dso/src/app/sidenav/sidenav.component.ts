import { Component, EventEmitter, HostListener, OnInit, Output } from '@angular/core';
import { INavbarData } from './helper';
import { navbarData } from './nav-data';
import { AuthService } from 'app/services/services/auth.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AESencriptorService } from 'app/services/aesencriptor.service';
import { navbarData2 } from './nav-data2';
import { NavigationStart, Router } from '@angular/router';

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
  helper = new JwtHelperService();

  
  email : any
  userId: any
  
  constructor( private service: AuthService, private router: Router){
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        if (this.collapsed == true) this.closeSidenav();
      }
    });
  }

  @Output() onToggleSideNav: EventEmitter<SideNavToggle> = new EventEmitter();
  collapsed = false;
  screenWidth = 0;
  navData = navbarData;
  navData2 = navbarData2;

  multiple: boolean = false;
  token: string
  dispecer: string

  @HostListener('window:resize', ['$event'])
  onResize(event:any){
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 768){
      this.collapsed = false;
      this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
    }
  }

  ngOnInit(): void{
    this.closeSidenav()
    this.screenWidth = window.innerWidth;
    if(this.service.isLoggedIn())
    {this.token = AESencriptorService.decriptyString(localStorage.getItem('token') as string)
    const decodeToken = this.helper.decodeToken(this.token)
    this.email = decodeToken.ImePrezime
    this.userId = decodeToken.UserId
    this.dispecer = decodeToken.role
  }

  }

  // za pregled korisnika da se otvori ceo nav
  open():void{

    this.collapsed = true
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});

  }

  toggleCollapse(): void{
    this.collapsed = !this.collapsed;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }

  closeSidenav(): void{
    this.collapsed = false;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }

  handleClick(item: INavbarData):void{
    if(!this.multiple){
      for(let modelItem of this.navData){
        if(item!== modelItem && modelItem.expanded){
          modelItem.expanded=false;
        }
      }

    }
    item.expanded=!item.expanded
  }
}
