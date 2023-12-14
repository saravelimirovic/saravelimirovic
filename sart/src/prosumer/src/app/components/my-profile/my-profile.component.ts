import { Component, HostListener, OnInit, ViewEncapsulation } from '@angular/core';
import { DashboardService } from 'src/app/services/dashboard.service';
import {Location} from '@angular/common';
import * as L from 'leaflet';
import { Objekat } from 'src/helperComponents/objekat';
import { LatLngExpression } from 'leaflet';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.scss'],
})
export class MyProfileComponent implements OnInit{

  private mapa: L.Map | undefined;
  lon:any;
  lat:any;
  screenWidth = 0;
  isMobile?: boolean;
  objektiKorisnika?: Objekat | any;
  objectId: number = 0;
  markers: any[] = [];
  centroid?: L.LatLngExpression;

constructor(private dashboardService : DashboardService, private _location: Location) {}

  podaciKorisnika : any = {};

  private translate(word){
    var i;
    var answer = ""
      , a = {};

   a["E"]="Е";a["R"]="Р";a["T"]="Т";a["U"]="У";a["I"]="И";a["O"]="О";a["P"]="П";a["A"]="А";a["S"]="С";a["D"]="Д";a["F"]="Ф";a["G"]="Г";a["H"]="Х";
   a["J"]="Ј";a["K"]="К";a["L"]="Л";a["Z"]="З";a["C"]="Ц";a["V"]="В";a["B"]="Б";a["N"]="Н";a["M"]="М";a["e"]="е";a["r"]="р";a["t"]="т";a["u"]="у";
   a["i"]="и";a["o"]="о";a["p"]="п";a["a"]="а";a["s"]="с";a["d"]="д";a["f"]="ф";a["g"]="г";a["h"]="х";a["j"]="ј";a["k"]="к";
   a["l"]="л";a["z"]="з";a["c"]="ц";a["v"]="в";a["b"]="б";a["n"]="н";a["m"]="м";a["LJ"]="Љ";a["NJ"]="Њ";a["lj"]="љ";a["nj"]="њ";
   a["Ć"]="Ћ";a["ć"]="ћ";a["Č"]="Ч";a["č"]="ч";a["Š"]="Ш";a["š"]="ш";a["Đ"]="Ђ";a["đ"]="ђ";a["Ž"]="Ж";a["ž"]="ж";

   for (i in word){
     if (word.hasOwnProperty(i)) {
       if (a[word[i]] === undefined){
         answer += word[i];
       } else {
         answer += a[word[i]];
       }
     }
   }
   return answer;
}

  ngOnInit(): void {

    this.objectId = parseInt(localStorage.getItem('objectId')!!);

    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500) this.isMobile = true;
    else this.isMobile = false;

    this.dashboardService.getPodaciOKorisniku(localStorage.getItem('userId')).subscribe((res) =>{
      this.podaciKorisnika = res;
    });

    this.ucitajObjekteKorisnika(localStorage.getItem('userId'));

  }

  ucitajObjekteKorisnika(idKorisnika:any){
    this.dashboardService.getObjekteKorisnika(idKorisnika).subscribe((res) =>{
      let json = JSON.stringify(res);
      this.objektiKorisnika = JSON.parse(json) as Objekat;
      this.objektiKorisnika.forEach((element,index) => {
        element.fullAddress = element.ulica + " " + element.adresniBroj + ", " + element.naselje + ", " + element.grad;
        element.inactive = true;
        element.index = index;
      });
      this.fetchAllData(this.objektiKorisnika);
    });
  }

  fetchAllData = async (addresses) => {
    for (const address of addresses) {
      await this.fetchData(address);
    }
  }

  fetchData = async (element) => {
    try {
      var requestOptions = {
        method: 'GET',
      };
      const response = await fetch("http://dev.virtualearth.net/REST/v1/Locations?countryRegion=RS&locality="+this.translate(element.grad)+"&addressLine="+this.translate(element.ulica)+"&o=json&key=AnEFyy5lm4z6A0DvrM73UrBRHFAA8HVKFp93EdmF7pC3foV-WzbsBKit5Xs4B2nv", requestOptions);
      const data = await response.json();
      this.LatLog(element.index,data,element.ulica,element.adresniBroj,element.grad,this.podaciKorisnika.rola,element.naselje);
      // Process the data for each address
    } catch (error) {
      // Handle the error
      console.error(`Error fetching data for ${element}:`, error);
    }
  };

  @HostListener('window:resize', ['$event'])
  onResize(event:any){
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 500){
      this.isMobile = true;
    }else{
      this.isMobile = false;
    }
  }

  LatLog(index:number,data:any,adresa:string,broj:string,grad:string,rola:string,naselje:string){

    this.lat=data.resourceSets[0].resources[0].point.coordinates[0];
    this.lon=data.resourceSets[0].resources[0].point.coordinates[1];
    if(index == 0)
    {
      var centroid: L.LatLngExpression = [this.lat,this.lon];
      const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        minZoom: 8,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      });
      this.mapa = L.map('map', {
        center: centroid,
        zoom: 13,
        layers:[tiles]
      });
    }

    var ikona = L.icon({
      iconUrl: '../../../assets/icons/green.png',
      iconSize: [40, 40],
    });

    var ikona1 = L.icon({
    iconUrl: '../../../assets/icons/red.png',
    iconSize: [40, 40],
    });

    var ikona2 = L.icon({
      iconUrl: '../../../assets/icons/yellow.png',
      iconSize: [40, 40],
      });

      if(rola==='prozumer'){
        this.markers.push(L.marker([this.lat, this.lon], { icon: ikona }).bindPopup(`${adresa} ${broj}, ${naselje}, ${grad}`).addTo(this.mapa!!));
      }

      if(rola==='potrošač'){
        this.markers.push(L.marker([this.lat, this.lon], { icon: ikona1 }).bindPopup(`${adresa} ${broj}, ${naselje}, ${grad}`).addTo(this.mapa!!));
      }

      if(rola==='proizvođač'){
        this.markers.push(L.marker([this.lat, this.lon], { icon: ikona2 }).bindPopup(`${adresa} ${broj}, ${naselje}, ${grad}`).addTo(this.mapa!!));
      }

      if(index == 0) this.markers[index].openPopup();

  }

  popup(event:any){
    var filter_array = this.objektiKorisnika.filter(x => x.id == event.target.value);
    this.markers[filter_array[0].index].openPopup();
    let lat = this.markers[filter_array[0].index].getLatLng().lat;
    let lng = this.markers[filter_array[0].index].getLatLng().lng;
    this.mapa!!.setView(new L.LatLng(lat, lng), 13,{
      animate: true,
      duration: 1.5
});
  }

  backClicked() {
    this._location.back();
  }

}
