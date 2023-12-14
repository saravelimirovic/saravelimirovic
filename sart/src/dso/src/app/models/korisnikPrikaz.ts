import { Rola } from "./rola";
import { Ulica } from "./ulica";

export interface KorisnikPrikaz{
    id: string,
    ime: string,
    grad: string,
    naselje: string,
    ulica: string,
    jmbg:string,
    brTelefona: string,
    adresniBroj: string,
    rola: string,
    email: string
}