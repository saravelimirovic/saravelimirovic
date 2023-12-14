import { Component } from '@angular/core';
import { AllUsers } from 'app/models/allUsers';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import Chart from 'chart.js/auto';


@Component({
  selector: 'app-chart-users',
  templateUrl: './chart-users.component.html',
  styleUrls: ['./chart-users.component.scss']
})
export class ChartUsersComponent {
  public usersAll: AllUsers;
  public chart: any;



constructor( private serviceUsers: UserServiceService){}

ngOnInit() {

  this.serviceUsers.getAllUsersCount().subscribe(res=>{
    this.usersAll = res
    this.createChart(this.usersAll)
  })

}

  createChart(users: AllUsers) {
    this.chart = new Chart("MyChart3", {
      type: 'pie', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: ['Prosumeri', 'Potrošači', 'Proizvođači'],
        datasets: [{
          label: 'Korisnici',
          data: [users.prosumeri, users.potrosaci, users.proizvodjaci],
          backgroundColor: [
            '#44EE77',
            '#EF3340',
            '#FFCD58'
          ],
          hoverOffset: 4
        }],
      },
      options: {
        responsive: true,
        aspectRatio: 2.5,
      plugins:{
        legend: {display: true}
      }
      }

    });

  }

}
