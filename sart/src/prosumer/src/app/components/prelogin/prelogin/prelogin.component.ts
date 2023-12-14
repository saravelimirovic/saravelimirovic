import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-prelogin',
  templateUrl: './prelogin.component.html',
  styleUrls: ['./prelogin.component.scss']
})
export class PreloginComponent implements OnInit{

  constructor(private router: Router){

  }
  ngOnInit(): void {
    localStorage.clear();
  }
  predjiNaLogin(){
    this.router.navigate(['login'])
}

}
