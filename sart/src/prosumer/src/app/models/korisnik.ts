import { Rola } from "./rola";
import { Ulica } from "./ulica";

export interface Korisnik{
    _id: string,
    jmbg: string,
    ime: string,
    prezime: string,
    email: string,
    sifra: string,//vrv hash+salt
    brTel: string,
    adresa: Ulica,
    adresniBroj: string,
    rola: Rola
}