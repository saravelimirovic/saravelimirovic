import { Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { KorisnikPrikaz } from 'app/models/korisnikPrikaz';
import { Object1 } from 'app/models/object';
import { ObjectService } from 'app/services/services/objectService/object.service';
import { UserServiceService } from 'app/services/services/userService/user-service.service';
import { MapaComponent } from '../korisnici-tabs/mapa/mapa.component';
import * as L from 'leaflet';
import { Location } from '@angular/common';
import { ToastService } from 'app/services/toast.service';
import { AuthService } from 'app/services/services/auth.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import {MatDialog, MAT_DIALOG_DATA, MatDialogConfig} from '@angular/material/dialog';
import { DialogObjectComponent } from '../dialog-object/dialog-object.component';
import { count } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Rola } from 'app/models/rola';
import { editDTO } from 'app/models/editDTO';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { DialogRegistracijaComponent } from '../dialog-registracija/dialog-registracija.component';
import { DialogEditComponent } from '../dialog-edit/dialog-edit.component';
import { DialogDeleteUserComponent } from '../dialog-delete-user/dialog-delete-user.component';



@Component({
  selector: 'app-upravljanje-korisnikom',
  templateUrl: './upravljanje-korisnikom.component.html',
  styleUrls: ['./upravljanje-korisnikom.component.scss'],

})
export class UpravljanjeKorisnikomComponent {
  private mapa: L.Map;
  lon: any;
  lat: any;
  markers: any[] = [];
  centroid?: L.LatLngExpression;
  objectId: number = 0;

  id: any
  results:any;
  objekat: Object1 | any
  korisnik: KorisnikPrikaz
  dozvolaZaBrisanje: boolean = false
  dozvolaZaRegistrovanje: boolean = false
  rolaUlogovan: string
  helper = new JwtHelperService();
  EditUserForm!:FormGroup;
  role : Rola[]=[]
  rolaId?:Number;
  ime: string;
  email: string;
  jmbg: string;
  ulica: string;
  adresniBroj: string;
  naselje: string;
  grad: string;
  brojTelefona: string;
  rola: string;
  editDTO: editDTO;
  isButtonDisabled: boolean = true;
  addresLine:any
  drzava:any
  isDivVisible: boolean = false
  dozvolaObjekti: boolean = false
  dozvolaEdit: boolean = true
  data: any ;

  //ulogovan dispecer i dispecer profil dozvolaEdit je false


  jmbgPattern = /^(0[1-9]|1\d|2\d|3[01])(0[1-9]|1[0-2])\d{9}$/;
  cantbewhitespace = /^(?!\s).*$/;

  constructor(private route: ActivatedRoute,
    private service: UserServiceService,
    private _location: Location,
    private objects: ObjectService,
    private toast: ToastService,
    private authS: AuthService,
    private dialog: MatDialog,
    private forma: FormBuilder,
    private toastr: ToastrService,) { }

  ngOnInit() {
    this.editDTO = new editDTO()
    this.role=[{_id:'3', naziv:'Prosumer'},{_id:'4', naziv:'Potrošač'},{_id:'5', naziv:'Proizvođač'},{_id:'1', naziv:'Admin'},
    {_id:'2', naziv:'Dispečer'}];
    // this.id = this.route.snapshot.params['id'];
    this.route.paramMap.subscribe(params => {
      this.id = params.get('id');
      
      // Use the 'id' parameter to update the information on the page
      // this.updatePageContent(id);
    });
    this.getUser()
    // this.getObjects()
    // this.rolaId=3;

    this.EditUserForm = this.forma.group({
      ime: ['', Validators.compose([Validators.required, Validators.pattern(this.cantbewhitespace)])],
      email: ['', Validators.compose([Validators.required,Validators.email])],
      jmbg: ['', Validators.compose([Validators.required,Validators.pattern(this.jmbgPattern)])],
      ulica: ['', Validators.required],
      adresniBroj: ['', Validators.required],
      naselje: ['', Validators.required],
      grad: ['', Validators.required],
      brojTelefona: ['', Validators.required],
      rolaId:['', Validators.compose([Validators.required, Validators.max(5)])],
    })

    this.EditUserForm.disable();
  }

  openDialog(id: number){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      id: id
    };
    this.dialog.open(DialogObjectComponent, dialogConfig);
  }
  //dozvola za brisanje admin-dispecer dispecer-korisnik
  //dozvola za registrovanje dispecer-korisnik

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

  getUser() {
    const decodeToken = this.helper.decodeToken(this.authS.getToken())
    this.rolaUlogovan = decodeToken.role
    this.service.getUser(this.id).subscribe(response => {
      this.korisnik = response
      this.ime = this.korisnik.ime
      this.email = this.korisnik.email
      this.adresniBroj = this.korisnik.adresniBroj
      this.naselje = this.korisnik.naselje
      this.grad = this.korisnik.grad
      this.ulica = this.korisnik.ulica
      this.jmbg = this.korisnik.jmbg
      this.brojTelefona = this.korisnik.brTelefona
      this.rola = this.korisnik.rola

      if(this.rola == 'prozumer') this.rolaId = 3
      else if(this.rola == 'potrošač') this.rolaId = 4
      else if(this.rola == 'proizvođač') this.rolaId = 5
      else if(this.rola == 'dispečer' ) this.rolaId = 2
      else this.rolaId = 1

      // console.log(this.korisnik)
      if(this.rolaUlogovan == 'admin' && this.korisnik.rola == 'dispečer'){
        this.dozvolaZaBrisanje = true
      }

      if(this.rolaUlogovan == 'dispečer' && (this.korisnik.rola == 'prozumer' || this.korisnik.rola == 'potrošač' || this.korisnik.rola == 'proizvođač' )){
        this.dozvolaZaBrisanje = true
        this.dozvolaZaRegistrovanje = true
      }

      if(this.rolaUlogovan == 'dispečer' && this.korisnik.rola == 'dispečer') this.dozvolaEdit = false

      if(this.rolaId==1 || this.rolaId==2){  
        this.fetchDataDisp(this.korisnik);
        this.dozvolaObjekti = false
      }else{
        this.dozvolaObjekti = true
        this.getObjects()
      }
    })
  }

  getObjects(){

    this.objects.GetAllObjects(this.id).subscribe(res => {
      this.objekat=res

      // var requestOptions = {
      //   method: 'GET',
      // };
      // for(let i=0;i<this.objekat.length;i++)
      // {
      // fetch("http://dev.virtualearth.net/REST/v1/Locations?countryRegion=RS&locality="+this.translate(this.objekat[i].grad)+"&addressLine="+this.translate(this.objekat[i].ulica)+"&o=json&key=AnEFyy5lm4z6A0DvrM73UrBRHFAA8HVKFp93EdmF7pC3foV-WzbsBKit5Xs4B2nv", requestOptions)
      // .then((response) => response.json())
      // .then(data => this.LatLog(data, this.objekat[i].ulica, this.objekat[i].adresniBroj, this.objekat[i].grad, this.korisnik.rola))
      // .then(json => {
      //   this.results= json
      //   console.log(this.results);  
      //  })
      // }
      let json = JSON.stringify(res);
      this.objekat = JSON.parse(json) as Object1[];
      this.objekat.forEach((element,index) => {
        element.fullAddress = element.ulica + " " + element.adresniBroj + ", " + element.naselje + ", " + element.grad;
        element.inactive = true;
        element.index = index;

        if(element.ulica === this.ulica && element.adresniBroj === this.adresniBroj && element.naselje === this.naselje && element.grad === this.grad) {
          this.objectId = parseInt(element.id);
        }
      });
      this.fetchAllData(this.objekat);
    })
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
      this.LatLog(element.index,data,element.ulica,element.adresniBroj,element.grad,this.rola);
      // Process the data for each address
    } catch (error) {
      // Handle the error
      console.error(`Error fetching data for ${element}:`, error);
    }
  };

  fetchDataDisp = async (korisnik) => {
    try {
      var requestOptions = {
        method: 'GET',
      };
      const response = await fetch("http://dev.virtualearth.net/REST/v1/Locations?countryRegion=RS&locality="+this.translate(korisnik.grad)+"&addressLine="+this.translate(korisnik.ulica)+"&o=json&key=AnEFyy5lm4z6A0DvrM73UrBRHFAA8HVKFp93EdmF7pC3foV-WzbsBKit5Xs4B2nv", requestOptions);
      const data = await response.json();
      this.LatLogDisp(data,korisnik.ulica,korisnik.adresniBroj,korisnik.grad,this.rola);
      // Process the data for each address
    } catch (error) {
      // Handle the error
      console.error(`Error fetching data for ${korisnik}:`, error);
    }
  };

  popup(event:any){
    var filter_array = this.objekat.filter(x => x.id == event.target.value);
    this.markers[filter_array[0].index].openPopup();
    let lat = this.markers[filter_array[0].index].getLatLng().lat;
    let lng = this.markers[filter_array[0].index].getLatLng().lng;
    this.mapa!!.setView(new L.LatLng(lat, lng), 13,{
      animate: true,
      duration: 1.5
    });
  }

  LatLog(index:number, data: any, adresa: string, broj: string, grad: string, rola: string) {
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
        zoom: 14,
        layers:[tiles]
      });
    }


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
    var ikona3 = L.icon({
      iconUrl: '../../../assets/img/dispecer.png',
      iconSize: [40, 40],
    });
    var ikona4 = L.icon({
      iconUrl: '../../../assets/img/admin.png',
      iconSize: [50, 50],
    });

  // if(this.addresLine!=undefined && this.drzava=="Serbia")
  // { 
    if (rola === 'prozumer') {
      this.markers.push(L.marker([this.lat, this.lon], { icon: ikona }).bindPopup(`${adresa} ${broj}, ${grad}`).addTo(this.mapa!!));
    }

    else if (rola === 'potrošač') {
      this.markers.push(L.marker([this.lat, this.lon], { icon: ikona1 }).bindPopup(`${adresa} ${broj}, ${grad}`).addTo(this.mapa!!));
    }

    else if (rola === 'proizvođač') {
      this.markers.push(L.marker([this.lat, this.lon], { icon: ikona2 }).bindPopup(`${adresa} ${broj}, ${grad}`).addTo(this.mapa!!));
    }
    else if (rola === 'dispečer') {
      this.markers.push(L.marker([this.lat, this.lon], { icon: ikona3 }).bindPopup(`${adresa} ${broj}, ${grad}`).addTo(this.mapa!!));
    }
    else if (rola === 'admin') {
      this.markers.push(L.marker([this.lat, this.lon], { icon: ikona4 }).bindPopup(`${adresa} ${broj}, ${grad}`).addTo(this.mapa!!));
    }
    if(index == 0) this.markers[index].openPopup();
  // }
}

LatLogDisp(data: any, adresa: string, broj: string, grad: string, rola: string) {
  this.lat=data.resourceSets[0].resources[0].point.coordinates[0];
  this.lon=data.resourceSets[0].resources[0].point.coordinates[1];

  
    var centroid: L.LatLngExpression = [this.lat,this.lon];
    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      minZoom: 8,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    this.mapa = L.map('map', {
      center: centroid,
      zoom: 14,
      layers:[tiles]
    });
  
 
  var ikona3 = L.icon({
    iconUrl: '../../../assets/img/dispecer.png',
    iconSize: [40, 40],
  });
  var ikona4 = L.icon({
    iconUrl: '../../../assets/img/admin.png',
    iconSize: [50, 50],
  });

// if(this.addresLine!=undefined && this.drzava=="Serbia")
// { 

  if (rola === 'dispečer') {
    this.markers.push(L.marker([this.lat, this.lon], { icon: ikona3 }).bindPopup(`${adresa} ${broj}, ${grad}`).addTo(this.mapa!!));
  }
  else if (rola === 'admin') {
    this.markers.push(L.marker([this.lat, this.lon], { icon: ikona4 }).bindPopup(`${adresa} ${broj}, ${grad}`).addTo(this.mapa!!));
  }
// }
}

  ObrisiKorisnika(id: string) {
    const dialogRef = this.dialog.open(DialogDeleteUserComponent, {
      data: this.korisnik.ime
    })
    dialogRef.afterClosed().subscribe(async res => {
      if (res.clicked == 'Ok') {

    // console.log(id)
    this.service.deteleUser(id).subscribe(res => {
      this.toast.initiate({
        title: 'Korisnik je uspešno obrisan!',
        content: ''
      })
    
      // this.backClicked()
    })
  }
  })
  }
  backClicked() {
    this._location.back();
  }





  //za edit

  edit(){
    this.isDivVisible = true
    this.EditUserForm.enable()
    this.isButtonDisabled = false
  }

  sacuvajIzmene(){
    // this.editDTO.id = this.id;
    // this.editDTO.ime = this.ime
    // this.editDTO.jmbg = this.jmbg
    // this.editDTO.email = this.email
    // this.editDTO.adresniBroj = this.adresniBroj
    // this.editDTO.brTelefona = this.brojTelefona
    // this.editDTO.grad = this.grad
    // this.editDTO.naselje = this.naselje
    // this.editDTO.ulica = this.ulica
    // if(this.rolaId == 3) this.editDTO.rola = 'prozumer'
    // else if(this.rolaId == 4) this.editDTO.rola = 'potrošač'
    // else this.editDTO.rola = 'proizvođač'
    // // console.log(this.EditUserForm)
    // this.service.editUser(this.editDTO).subscribe({
    //   next:(res)=>{
    //       this.toastr.success("Uspešna izmena informacija!","", {
    //         timeOut: 2000
    //       });
    //   }
    // })

    this.GetAddress()
      .then(result => {
        // ako postoji adresa
        if (result === 1) {
          this.editDTO.id = this.id;
          this.editDTO.ime = this.ime
          this.editDTO.jmbg = this.jmbg
          this.editDTO.email = this.email
          this.editDTO.adresniBroj = this.adresniBroj
          this.editDTO.brTelefona = this.brojTelefona
          this.editDTO.grad = this.grad
          this.editDTO.naselje = this.naselje
          this.editDTO.ulica = this.ulica
          if(this.rolaId == 3) this.editDTO.rola = 'prozumer'
          else if(this.rolaId == 4) this.editDTO.rola = 'potrošač'
          else this.editDTO.rola = 'proizvođač'
          // console.log(this.EditUserForm)
          this.service.editUser(this.editDTO).subscribe({
            next:(res)=>{
                // this.toastr.success("Uspešna izmena informacija!","", {
                //   timeOut: 2000
                // });
                this.openDialog2();
            }
          })
          
        // ako ne postoji ta adresa
        } else {
          this.openDialog1();
        }
      })
      .catch(error => {
        // Error handling
      });

  }

  openDialog1(){
    this.dialog.open(DialogRegistracijaComponent);
  }

  openDialog2(){
    this.dialog.open(DialogEditComponent);
  }


  GetAddress(): Promise<number>{
    return new Promise<number>((resolve, reject) => {
      var grad = (<HTMLInputElement>document.getElementById("GRAD")).value;
      var ulica = (<HTMLInputElement>document.getElementById("ULICA")).value;
      var broj = (<HTMLInputElement>document.getElementById("BROJ")).value;
  
      var requestOptions = {
        method: 'GET',
      };
  
      fetch("http://dev.virtualearth.net/REST/v1/Locations?countryRegion=RS&locality=" + this.translate(grad) + "&addressLine=" + broj + this.translate(ulica) + "&o=json&key=AnEFyy5lm4z6A0DvrM73UrBRHFAA8HVKFp93EdmF7pC3foV-WzbsBKit5Xs4B2nv", requestOptions)
        .then(response => response.json())
        .then(data => {
          const result = this.checkAddress(data);
          resolve(result);
        })
        .catch(error => {
          reject(error);
        });
    });

  }

  checkAddress(data:any)
  {   
    var adresa=data.resourceSets[0].resources[0].address.addressLine;
    var drzava=data.resourceSets[0].resources[0].address.countryRegion;

    if(adresa!=undefined && drzava=="Serbia")
        return 1;
    else 
      return 0;   
  } 
    
  odustani(){
    this.service.getUser(this.id).subscribe(response => {
      this.korisnik = response
      this.ime = this.korisnik.ime
      this.email = this.korisnik.email
      this.adresniBroj = this.korisnik.adresniBroj
      this.naselje = this.korisnik.naselje
      this.grad = this.korisnik.grad
      this.ulica = this.korisnik.ulica
      this.jmbg = this.korisnik.jmbg
      this.brojTelefona = this.korisnik.brTelefona
      this.rola = this.korisnik.rola

      if(this.rola == 'prozumer') this.rolaId = 3
      else if(this.rola == 'potrošač') this.rolaId = 4
      else if(this.rola == 'proizvođač') this.rolaId = 5
      else if(this.rola == 'dispečer' ) this.rolaId = 2
      else this.rolaId = 1
    })
    // this.EditUserForm.reset()
    this.EditUserForm.disable()
    this.isDivVisible = false

  }



  

}
