import { Rola } from "./rola";
import { Ulica } from "./ulica";

export interface Korisnik{
    brojKorisnika: number;
    id: string,
    ime: string,
    grad: string,
    naselje: string,
    ulica: string,
    potrosnja: Number,
    proizvodnja: Number,
    rola: string
}