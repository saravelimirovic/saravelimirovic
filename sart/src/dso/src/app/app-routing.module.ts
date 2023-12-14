import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { KorisniciTabsComponent } from './components/korisnici-tabs/korisnici-tabs.component';
import { LoginComponent } from './components/login/login.component';
import { MapaComponent } from './components/korisnici-tabs/mapa/mapa.component';
import { PocetnaComponent } from './components/pocetna/pocetna.component';
import { PreloginComponent } from './components/prelogin/prelogin.component';
import { SignupComponent } from './components/signup/signup.component';
import { UpravljanjeKorisnikomComponent } from './components/upravljanje-korisnikom/upravljanje-korisnikom.component';
import { AuthGuard } from './guards/auth.guard';
import { StatisticComponent } from './components/statistic/statistic.component';
import { PrikazUredjajaKorisnikaComponent } from './components/prikaz-uredjaja-korisnika/prikaz-uredjaja-korisnika.component';
import { RegistracijaObjektaComponent } from './components/registracija-objekta/registracija-objekta/registracija-objekta.component';
import { UredjajiPojedinacnoComponent } from './components/uredjaji-pojedinacno/uredjaji-pojedinacno.component';
import { ResetComponent } from './components/reset/reset.component';
import { TableDispecerComponent } from './components/table-dispecer/table-dispecer.component';
import { AdminGuardGuard } from './guards/adminGuard/admin-guard.guard';
import { LoginGuard } from './guards/loginGuard/login.guard';
import { RegistracijaDispeceraComponent } from './components/registracija-dispecera/registracija-dispecera.component';

const routes: Routes = [
  {path:'login', component:LoginComponent},
  {path:'', component:PreloginComponent},
  {path:'signup', component:SignupComponent, canActivate:[AuthGuard]},
  {path:'pocetna',component:PocetnaComponent, canActivate:[AuthGuard]},
  // {path:'pocetna',component:PocetnaComponent},
  {path:'upravljanje-korisnikom/:id',component:UpravljanjeKorisnikomComponent, canActivate:[LoginGuard]},
  {path:'mapa',component:MapaComponent, canActivate:[AuthGuard]},
  {path:'korisnici', component:KorisniciTabsComponent, canActivate:[AuthGuard]},
  {path: 'statistic', component: StatisticComponent, canActivate:[AuthGuard]},
  {path: 'uredjaji/:id', component: PrikazUredjajaKorisnikaComponent, canActivate:[AuthGuard]},
  {path: 'registracija-objekta/:id', component: RegistracijaObjektaComponent, canActivate:[AuthGuard]},
  {path:'uredjaji/:idK/uredjaj/:id',component : UredjajiPojedinacnoComponent, canActivate:[AuthGuard]},
  {path: 'reset', component: ResetComponent},
  {path: 'dispeceri' , component: TableDispecerComponent, canActivate:[AdminGuardGuard]},//dodati guard samo za admina
  // {path:'uredjaji:id/uredjaj/:id',component : UredjajiPojedinacnoComponent},
  {path: 'reset', component: ResetComponent},
  {path: 'registracija-dispecera', component: RegistracijaDispeceraComponent, canActivate:[AdminGuardGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
