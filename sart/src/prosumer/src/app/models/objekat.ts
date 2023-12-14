import { Korisnik } from "./korisnik";
import { Ulica } from "./ulica";

export interface Objekat{
    _id: string,
    adresa: Ulica,
    korisnik: Korisnik,
    AdresniBroj: string
}