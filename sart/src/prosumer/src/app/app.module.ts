import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { ChartModule } from 'primeng/chart';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {CascadeSelectModule} from 'primeng/cascadeselect';
import { FormsModule } from '@angular/forms';
import {MatGridListModule} from '@angular/material/grid-list';
import { BadgeModule } from "primeng/badge";
import { ButtonModule } from "primeng/button";
import { ToastrModule } from 'ngx-toastr';
import { CardModule } from 'primeng/card';
import {DropdownModule} from 'primeng/dropdown';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatTabsModule} from '@angular/material/tabs';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { TabViewModule } from 'primeng/tabview';
import { TagModule } from 'primeng/tag';
import {StyleClassModule} from 'primeng/styleclass';
import {TableModule} from 'primeng/table';
import {RadioButtonModule} from 'primeng/radiobutton';
import { TooltipModule } from 'primeng/tooltip';
import { MessagesModule } from 'primeng/messages';
import { ToastModule } from 'primeng/toast';
import { OrganizationChartModule } from 'primeng/organizationchart';
import { MdbTabsModule } from 'mdb-angular-ui-kit/tabs';
import { MdbModalModule } from 'mdb-angular-ui-kit/modal';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { SweetAlert2Module } from '@sweetalert2/ngx-sweetalert2';

import { AppComponent } from './app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LoginComponent } from './components/login/login.component';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { BodyComponent } from './body/body/body.component';
import { SidenavComponent } from './sidenav/sidenav/sidenav.component';
import { ArrowComponent } from 'src/helperComponents/arrow/arrow.component';
import { UredjajiComponent } from 'src/app/components/uredjaji/uredjaji.component';
import { MyProfileComponent } from './components/my-profile/my-profile.component';
import { PreloginComponent } from './components/prelogin/prelogin/prelogin.component';
import { WeatherWidgetMainComponent } from './components/weather-widget-main/weather-widget-main.component';
import { SwitchUredjajaComponent } from 'src/helperComponents/switch-uredjaja/switch-uredjaja.component';
import { UredjajiPojedinacnoComponent } from './components/uredjaji-pojedinacno/uredjaji-pojedinacno.component';

import { MessageService } from 'primeng/api';
import { ModalComponent } from './components/modal/modal/modal.component';
import { SkladistePojedinacnoComponent } from './components/skladiste-pojedinacno/skladiste-pojedinacno.component';
import { ResetComponent } from './components/reset/reset.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    BodyComponent,
    SidenavComponent,
    ArrowComponent,
    UredjajiComponent,
    MyProfileComponent,
    PreloginComponent,
    WeatherWidgetMainComponent,
    SwitchUredjajaComponent,
    UredjajiPojedinacnoComponent,
    ModalComponent,
    SkladistePojedinacnoComponent,
    ResetComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ChartModule,
    MatSlideToggleModule,
    CascadeSelectModule,
    FormsModule,
    MatGridListModule,
    BadgeModule,
    ButtonModule,
    ToastrModule.forRoot(),
    CardModule,
    DropdownModule,
    MatTooltipModule,
    MatTabsModule,
    NgxPageScrollCoreModule,
    TabViewModule,
    TagModule,
    StyleClassModule,
    TableModule,
    RadioButtonModule,
    TooltipModule,
    MessagesModule,
    ToastModule,
    OrganizationChartModule,
    MdbTabsModule,
    MdbModalModule,
    LeafletModule,
    SweetAlert2Module.forRoot()
  ],
  providers: [{
    provide:HTTP_INTERCEPTORS,
    useClass:TokenInterceptor,
    multi:true,
  },
  MessageService],
  bootstrap: [AppComponent]
})
export class AppModule { }
