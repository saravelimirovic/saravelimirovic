import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { LOCALE_ID,NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import {MatChipsModule} from '@angular/material/chips';
import {ChartModule} from 'primeng/chart';
import { ChipModule } from 'primeng/chip';
import { FormsModule } from '@angular/forms';
import { MatTabsModule} from '@angular/material/tabs'
import { TabViewModule } from 'primeng/tabview';
// import { TableModule } from 'primeng/table';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon'
import { MatSliderModule} from '@angular/material/slider'
import { MatTooltipModule} from '@angular/material/tooltip';
import { MatDialogModule } from '@angular/material/dialog';
import { TagModule } from 'primeng/tag';



import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { PocetnaComponent } from './components/pocetna/pocetna.component';
import { BodyComponent } from './body/body.component';
import { SidenavComponent } from './sidenav/sidenav.component';
import { ChartZbirPPIComponent } from './components/chart-zbir-ppi/chart-zbir-ppi.component';
import { ChartZbirPPPComponent } from './components/chart-zbir-ppp/chart-zbir-ppp.component';
import { PreloginComponent } from './components/prelogin/prelogin.component';
import { UpravljanjeKorisnikomComponent } from './components/upravljanje-korisnikom/upravljanje-korisnikom.component';
import { MapaComponent } from './components/korisnici-tabs/mapa/mapa.component';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
// import { NgToastModule } from 'ng-angular-popup';
import { SublevelComponent } from './sidenav/sublevel.component';
import { KorisniciTabsComponent } from './components/korisnici-tabs/korisnici-tabs.component';
import { TabelaKorisniciComponent } from './components/korisnici-tabs/tabela-korisnici/tabela-korisnici.component';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { ChartKorisniciComponent } from './components/korisnici-tabs/chart-korisnici/chart-korisnici.component';
import { CardModule } from 'primeng/card';
import {ButtonModule} from 'primeng/button';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { LoadingInterceptor } from './interceptors/loading.interceptor';
import { CountUpDirective } from './count-up.directive';
import { TableDevicesComponent } from './components/korisnici-tabs/table-devices/table-devices.component';
import { HistoryComponent } from './components/table-history/history/history.component';
import { TablePrediction7daysComponent } from './components/table-prediction7days/table-prediction7days/table-prediction7days.component';
import { StatisticComponent } from './components/statistic/statistic.component';
import { PrikazUredjajaKorisnikaComponent } from './components/prikaz-uredjaja-korisnika/prikaz-uredjaja-korisnika.component';
import { ChartDevicesProizComponent } from './components/chart-devices-proiz/chart-devices-proiz.component';
import { TableDevicesProizComponent } from './components/table-devices-proiz/table-devices-proiz.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { RegistracijaObjektaComponent } from './components/registracija-objekta/registracija-objekta/registracija-objekta.component';
import { UredjajiPojedinacnoComponent } from './components/uredjaji-pojedinacno/uredjaji-pojedinacno.component';
import { DialogExcelComponent } from './components/dialog-excel/dialog-excel.component';
import { ToastComponent } from './components/toast/toast.component';
import { ToastrModule } from 'ngx-toastr';
import { ToastModule } from 'primeng/toast';
import { MessagesModule } from 'primeng/messages';
import { MessageService } from 'primeng/api';
import { TableDayComponent } from './components/table-day/table-day.component';
import { DatePipe } from '@angular/common';
import { TodayChartComponent } from './components/today-chart/today-chart.component';
import { ResetComponent } from './components/reset/reset.component';
import { KontrolaModalComponent } from './components/modal-kontrola/kontrola-modal/kontrola-modal.component';
import { TableDispecerComponent } from './components/table-dispecer/table-dispecer.component';
import { RegistracijaDispeceraComponent } from './components/registracija-dispecera/registracija-dispecera.component';
import { DialogObjectComponent } from './components/dialog-object/dialog-object.component';
import { DialogRegistracijaComponent } from './components/dialog-registracija/dialog-registracija.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DialogRegistracija1Component } from './components/dialog-registracija1/dialog-registracija1.component';
import { DialogRegistrObjektaComponent } from './components/dialog-registr-objekta/dialog-registr-objekta.component';
import { DialogEditComponent } from './components/dialog-edit/dialog-edit.component';
import { DialogDeleteUserComponent } from './components/dialog-delete-user/dialog-delete-user.component';



@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    PocetnaComponent,
    BodyComponent,
    SidenavComponent,
    ChartZbirPPIComponent,
    ChartZbirPPPComponent,
    PreloginComponent,
    UpravljanjeKorisnikomComponent,
    MapaComponent,
    SublevelComponent,
    KorisniciTabsComponent,
    TabelaKorisniciComponent,
    ChartKorisniciComponent,
    SpinnerComponent,
    CountUpDirective,
    TableDevicesComponent,
    HistoryComponent,
    TablePrediction7daysComponent,
    StatisticComponent,
    PrikazUredjajaKorisnikaComponent,
    ChartDevicesProizComponent,
    TableDevicesProizComponent,
    RegistracijaObjektaComponent,
    UredjajiPojedinacnoComponent,
    DialogExcelComponent,
    ToastComponent,
    TableDayComponent,
    TodayChartComponent,
    ResetComponent,
    KontrolaModalComponent,
    TableDispecerComponent,
    RegistracijaDispeceraComponent,
    DialogObjectComponent,
    DialogRegistracijaComponent,
    DialogRegistracija1Component,
    DialogRegistrObjektaComponent,
    DialogEditComponent,
    DialogDeleteUserComponent
  
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatChipsModule,
    ChartModule,
    ChipModule,
    LeafletModule,
    FormsModule,
    // NgToastModule,
    MatTabsModule,
    TabViewModule,
    MatTableModule,
    MatFormFieldModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    CardModule,
    ButtonModule,
    NgxPaginationModule,
    MatSliderModule,
    MatTooltipModule,
    MatDialogModule,
    TagModule,
    ToastrModule.forRoot(),
    ToastModule,
    MessagesModule,
    MatProgressSpinnerModule

  ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true}, MessageService, DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
