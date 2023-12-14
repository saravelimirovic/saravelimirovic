import { Component } from '@angular/core';
import { Korisnik } from 'app/models/korisnik';
import { UserServiceService } from 'app/services/services/userService/user-service.service';

@Component({
  selector: 'app-statistic',
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.scss']
})
export class StatisticComponent {

  topPotrosaci: Korisnik[];
  topProizvodjaci: Korisnik[];

  constructor(private userService: UserServiceService) {}

  ngOnInit() {
    this.top3Potrosaca()
    this.top3Proizvodjaci()
  }

  top3Potrosaca() {
    this.userService.getTop3Potrosaca().subscribe(res => {
      this.topPotrosaci = res
    })
  }

  top3Proizvodjaci() {
    this.userService.getTop3Proizvodjaca().subscribe(res => {
      this.topProizvodjaci = res
    })
  }
}
