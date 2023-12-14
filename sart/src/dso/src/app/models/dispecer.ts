import { Rola } from "./rola";
import { Ulica } from "./ulica";

export interface Dispecer{
    brojKorisnika: number;
    id: string,
    ime: string,
    grad: string,
    naselje: string,
    ulica: string,
}