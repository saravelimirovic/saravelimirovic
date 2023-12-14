export class ModelUredjaja {
  id?: number;
  naziv?: string;
  pPrilikomMirovanja?: number;
  tipUredjaja?: string;
  tipUredjajaId?: number;
  vrstaUredjaja? : string;
  vrstaUredjajaId? : number;

  constructor(id:number,naziv:string){
    this.id = id;
    this.naziv = naziv;
  }
}
