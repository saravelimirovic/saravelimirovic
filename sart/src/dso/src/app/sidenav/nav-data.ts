import { INavbarData } from "./helper";

export const navbarData: INavbarData[] = [
  {
    routeLink: 'pocetna',
    icon: 'fal fa-home',
    label:'Početna strana'
  },
  {
    routeLink: 'korisnici',
    icon: 'fal fa-users',
    label:'Pregled korisnika  ',
  },
  {
    routeLink: 'signup',
    icon: "fa-solid  fa-user-plus",
    label:'Dodaj korisnika'
  },
  {
    routeLink: 'statistic',
    icon: 'fal fa-chart-bar',
    label:'Statistika uređaja'
  },
  {
    routeLink: 'login',
    icon: 'fal fa-sign-out-alt',
    label:'Izloguj se'
  },


];
