import { Uredjaj } from "./uredjaj";

export interface PPoStanjuUredjaja{
    _id: string,
    uredjaj: Uredjaj,
    nazivStanja: string,
    pPoSatuStanja: number
}