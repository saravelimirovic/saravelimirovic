import { Component , Input, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { Router } from '@angular/router';
import { LocationService } from 'app/services/services/locationService/location.service';
import { Korisnik } from 'app/models/korisnik';
import { MatTableDataSource } from '@angular/material/table';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import { HttpClient } from '@angular/common/http';
import { RecordService } from 'app/services/services/recordService/record-service.service';
import { MatDialog } from '@angular/material/dialog';
import { PagingDTO } from 'app/models/pagingDTO';
import { AllUsers } from 'app/models/allUsers';


@Component({
  selector: 'app-mapa',
  templateUrl: './mapa.component.html',
  styleUrls: ['./mapa.component.scss']
})
export class MapaComponent implements OnInit {

  private mapa: L.Map;
  private centroid: L.LatLngExpression = [44.01793,20.90682]; //

  json: any;
  lon:any;
  lat:any;
  overlay:any;
  lokacije: any[]=[];
  prosumeri=L.layerGroup();
  potrosaci=L.layerGroup();
  proizvodjaci=L.layerGroup();
  showOverlay = false;
  addresLine:any
  drzava:any
  
  constructor( private locationService: LocationService, private router: Router,
    private userService: UserServiceService, private http: HttpClient, private recordService: RecordService,
    private dialog: MatDialog) { }

  
  ngOnInit(): void {
    this.initMap();
    this.filterUsers()
    var overlay = {
      "Prosumeri": this.prosumeri,
      "Potrošači": this.potrosaci,
      "Proizvođači":this.proizvodjaci
    };
    var layer = L.control.layers(undefined,overlay, {
      collapsed: false
    }).addTo(this.mapa);
  }

  clearMarkers() {
    for (const marker of this.markers) {
      marker.remove();
    }
    this.markers = [];
  }

  filterUsers() {
    this.clearMarkers();
    this.checkIfFiltered()
    this.locationService.getLocations(this.filterObj).subscribe(
      res=>{
      this.lokacije=res;
      for(const lokacija of this.lokacije)
      {
        this.Convert(lokacija.korisnikaId,lokacija.nazivUlice,lokacija.adrsniBroj,lokacija.nazivGrada,lokacija.rolaKorisnika,lokacija.ime);
      }
    } 
    );
  }
  

  private initMap(): void {

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      minZoom: 7,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    this.mapa = L.map('map', {
      center: this.centroid,
      zoom: 8,
      layers:[tiles,this.prosumeri,this.potrosaci,this.proizvodjaci]
    });
    
  }

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

  Convert(id:string,adresa:string,broj:string,grad:string,rola:string,ime:string){
    var requestOptions = {
      method: 'GET',
    };
    fetch("http://dev.virtualearth.net/REST/v1/Locations?countryRegion=RS&locality="+this.translate(grad)+"&addressLine="+broj+this.translate(adresa)+"&o=json&key=AnEFyy5lm4z6A0DvrM73UrBRHFAA8HVKFp93EdmF7pC3foV-WzbsBKit5Xs4B2nv", requestOptions)
      .then(response => response.json())
      .then(data => this.LatLog(data,rola,adresa,broj,grad,id,ime))

  }

  markers: L.Marker[] = [];

  LatLog(data:any,rola:string,adresa:string,broj:string,grad:string,id:string,ime:string){
    this.lat=data.resourceSets[0].resources[0].point.coordinates[0];
    this.lon=data.resourceSets[0].resources[0].point.coordinates[1];
    this.addresLine=data.resourceSets[0].resources[0].address.addressLine;
    this.drzava=data.resourceSets[0].resources[0].address.countryRegion;


    var ikona = L.icon({
      iconUrl: '../../../assets/img/green.png',
      iconSize: [40, 40],
    });

    var ikona1 = L.icon({
    iconUrl: '../../../assets/img/red.png',
    iconSize: [40, 40],
    });

    var ikona2 = L.icon({
      iconUrl: '../../../assets/img/yellow.png',
      iconSize: [40, 40],
    });

   if(this.addresLine!=undefined && this.drzava=="Serbia")
   { 
    if(rola==='prozumer')
    {
      const marker = L.marker([this.lat, this.lon], { icon: ikona })
      .bindPopup(`${ime}, ${adresa} ${broj}, ${grad}, ${rola}`);
      this.markers.push(marker);
      marker.on('mouseover',function(ev){
        marker.openPopup();
      });
        marker.addTo(this.prosumeri);
        marker.on('click', () => {
          this.showOverlay = true;
          this.getToUser(id);
      });
    }
    else if(rola==='potrošač')
    {
      const marker = L.marker([this.lat, this.lon], { icon: ikona1 })
      .bindPopup(`${ime}, ${adresa} ${broj}, ${grad}, ${rola}`);
      this.markers.push(marker);
      marker.on('mouseover',function(ev){
        marker.openPopup();
      });
      marker.addTo(this.potrosaci);
      marker.on('click', () => {
        this.showOverlay = true;
        this.getToUser(id);
      });
    }
    else if(rola==='proizvođač')
    {
      const marker = L.marker([this.lat, this.lon], { icon: ikona2 })
      .bindPopup(`${ime}, ${adresa} ${broj}, ${grad}, ${rola}`);
      this.markers.push(marker);
      marker.on('mouseover',function(ev){
        marker.openPopup();
      });
      marker.addTo(this.proizvodjaci);
      marker.on('click', () => {
        this.showOverlay = true;
        this.getToUser(id);
      });
    }
  }
  }

  getToUser(id:string)
  {
    this.router.navigate(['upravljanje-korisnikom/' + id]);
  }









    korisnici: Korisnik[]
    displayedColumns: string[] = ['ime', 'grad', 'naselje', 'ulica', 'potrosnja', 'proizvodnja', 'rola', 'edit'];
    dataSource!: MatTableDataSource<any>;
    maxPotr: number;
    maxProiz: number;
    max1: number;
    max2: number;
    data: any ;
    public isFiltered: boolean;

  filterObj = {
    "id": 0,
    "ime": null,
    "grad": null,
    "naselje": null,
    "ulica": null,
    "potrosnjaKorisnika": 0,
    "proizvodnjaKorisnika": 0,
    "rolaVracam": null,
    "potrosnjaOd": null,
    "potrosnjaDo": null,
    "proizvodnjaOd": null,
    "proizvodnjaDo": null,
    "rolaUzimam": 0,
    "pageIndex": 1,
    "pageSize": 10

  }

  obrisiFilter() {
    this.filterObj = {
      "id": 0,
      "ime": null,
      "grad": null,
      "naselje": null,
      "ulica": null,
      "potrosnjaKorisnika": 0,
      "proizvodnjaKorisnika": 0,
      "rolaVracam": null,
      "potrosnjaOd": null,
      "potrosnjaDo": null,
      "proizvodnjaOd": null,
      "proizvodnjaDo": null,
      "rolaUzimam": 0,
      "pageIndex": 1,
      "pageSize": 10
    }
    this.filterUsers()
  }
  checkIfFiltered() {
    if (this.filterObj.ime != null || this.filterObj.grad != null || this.filterObj.naselje != null ||
      this.filterObj.ulica != null || this.filterObj.potrosnjaOd != null ||  this.filterObj.potrosnjaDo != null ||
      this.filterObj.proizvodnjaOd != null || this.filterObj.proizvodnjaDo != null || this.filterObj.rolaUzimam != 0) {
      this.isFiltered = true
      // console.log(this.filterObj)
    } else {
      this.isFiltered = false
    }
  }

}

    