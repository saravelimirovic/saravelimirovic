export class ModelTableDay{
    constructor(d: string, pot: string, proiz: string, potPred: string, proizPred: string) {
       this.dates = d;
       this.potrosnja = pot;
       this.proizvodnja = proiz
       this.proizvodnjaPred = proizPred;
       this.potrosnjaPred = potPred
      }
    dates: string ;
    potrosnja: string;
    potrosnjaPred: string;
    proizvodnja: string;
    proizvodnjaPred: string
}