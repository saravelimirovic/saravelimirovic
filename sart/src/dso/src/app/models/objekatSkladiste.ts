import { Objekat } from "./objekat";
import { Skladiste } from "./skladiste";

export interface ObjekatSkladiste{
    _id: string,
    sladiste: Skladiste,
    objekat: Objekat,
    trenutnoStanje: number
}