import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-prelogin',
  templateUrl: './prelogin.component.html',
  styleUrls: ['./prelogin.component.scss']
})
export class PreloginComponent {

  constructor(private router: Router){
    
  }

  predjiNaLogin(){
      this.router.navigate(['login'])
  }
}
