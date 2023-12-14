import { Component, OnInit } from '@angular/core';
import { podatakPoDanu } from './podatakPoDanu';

@Component({
  selector: 'app-weather-widget-main',
  templateUrl: './weather-widget-main.component.html',
  styleUrls: ['./weather-widget-main.component.scss']
})
export class WeatherWidgetMainComponent implements OnInit{

  WeatherData:any;
  lon?:number;
  lat?:number;
  listaPoDanima: podatakPoDanu[] = [];

  myApiKey:string = "0e3b7549ba09ebd6f6417d0578300c3f";

  ngOnInit(): void{
    this.getWeatherData("Kragujevac");
  }

  //na osnovu rednog broja dana vraca ime dana u nedelji
  giveDay(data:any):string{
    switch(data){
      case 0:
        return "Nedelja";
      case 1:
        return "Ponedeljak";
      case 2:
        return "Utorak";
      case 3:
        return "Sreda";
      case 4:
        return "Četvrtak";
      case 5:
        return "Petak";
      case 6:
        return "Subota";
      default:
        return "Nepoznato";
    }
  }

  //vraca prevod na srpski vremenskog tipa npr. Clouds,Rain,Sun itd...
  giveWeather(data:any):string{
    switch(data){
      case "Clouds":
        return "Oblačno";
      case "Rain":
        return "Kišovito";
      case "Thunderstorm":
        return "Grmljavina";
      case "Clear":
        return "Sunčano";
      case "Snow":
        return "Snežno";
      default:
        return "Nepoznato";
    }
  }

  //gadja Api koji vraca koordinate grada po njegovom nazivu
  getWeatherData(cityName:string){
    fetch("http://api.openweathermap.org/geo/1.0/direct?q="+cityName+",&limit=1&appid=0e3b7549ba09ebd6f6417d0578300c3f")
    .then(response => response.json())
    .then(data => this.setCoordinates(data));
  }

  //postavlja koordinate i gadja api koji po koordinatama grada povlaci podatke o vremenskoj prognozi
  setCoordinates(data:any){
    this.lon = data[0].lon;
    this.lat = data[0].lat;
    let url = "https://api.openweathermap.org/data/2.5/forecast?lat="+this.lat+"&lon="+this.lon+"&units=metric&appid=0e3b7549ba09ebd6f6417d0578300c3f";
    fetch(url)
    .then(response => response.json())
    .then(data => this.setWeatherData(data));
  }

  //posto api vraca 40 podataka, na svaka 3h u roku od 5 dana,nema potrebe prikazivati sve podatke,vec samo jedan po danu
  setWeatherData(data:any){
    this.WeatherData = data;
    let duzina = this.WeatherData.list.length;
    let i = 0;
    while(i < duzina){
      let podatak = new podatakPoDanu();
      podatak.dan = this.giveDay(new Date(this.WeatherData.list[i].dt * 1000).getUTCDay());
      podatak.temp_max = this.WeatherData.list[i].main.temp_max.toFixed(0);
      podatak.temp_min = this.WeatherData.list[i].main.temp_min.toFixed(0);
      podatak.vreme = (this.giveWeather(this.WeatherData.list[i].weather[0].main));
      this.listaPoDanima.push(podatak);
      i = i + 8;
    }
    this.listaPoDanima.pop();
  }

}
