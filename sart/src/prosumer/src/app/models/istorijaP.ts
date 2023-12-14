import { Objekat } from "./objekat";
import { Uredjaj } from "./uredjaj";

export interface istorijaP{
    _id: string,
    uredjaj: Uredjaj,
    objekat: Objekat,
    vrednostRealizacije: number,
    datum: Date,
    vreme: Date//nez sta je ovo tacno
}