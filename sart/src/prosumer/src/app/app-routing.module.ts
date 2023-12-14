import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UredjajiComponent } from 'src/app/components/uredjaji/uredjaji.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LoginComponent } from './components/login/login.component';
import { MyProfileComponent } from './components/my-profile/my-profile.component';
import { PreloginComponent } from './components/prelogin/prelogin/prelogin.component';
import { UredjajiPojedinacnoComponent } from './components/uredjaji-pojedinacno/uredjaji-pojedinacno.component';
import { AuthGuard } from './guards/auth.guard';
import { SkladistePojedinacnoComponent } from './components/skladiste-pojedinacno/skladiste-pojedinacno.component';
import { ResetComponent } from './components/reset/reset.component';

const routes: Routes = [
  {path:'',redirectTo:'/prelogin',pathMatch:'full'},
  {path:'prelogin',component : PreloginComponent},
  {path:'login',component : LoginComponent},
  {path:'uredjaji',component : UredjajiComponent},
  {path:'uredjaj/:id',component : UredjajiPojedinacnoComponent},
  {path:'skladiste/:id',component : SkladistePojedinacnoComponent},
  {path:'pocetna-strana',component : DashboardComponent, canActivate:[AuthGuard]},
  {path:'moji-detalji',component : MyProfileComponent},
  {path: 'reset', component: ResetComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
