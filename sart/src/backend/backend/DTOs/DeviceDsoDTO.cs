namespace backend.DTOs
{
    public class DeviceDsoDTO
    {
        int idObjekta;
        string lokacijaObjekta; // ulica broj, grad
        int idUredjaja;
        string nazivUredjaja;
        string tipUredjaja;
        string vrstaUredjaja;
        int idObjekatUredjaj;
        string nazivProstorije;

        public DeviceDsoDTO(int idObjekta, string ulicaObjekta, string brojUlice, string gradObjekta, int idUredjaja, string nazivUredjaja, string tipUredjaja, string vrstaUredjaja)
        {
            this.IdObjekta = idObjekta;
            this.LokacijaObjekta = ulicaObjekta + " " + brojUlice + ", " + gradObjekta;
            this.IdUredjaja = idUredjaja;
            this.NazivUredjaja = nazivUredjaja;
            this.TipUredjaja = tipUredjaja;
            this.VrstaUredjaja = vrstaUredjaja;
        }

        public DeviceDsoDTO(int idObjekta, string ulicaObjekta, string brojUlice, string gradObjekta, int idUredjaja, string nazivUredjaja, string tipUredjaja, string vrstaUredjaja, int idObjekatUredjaj, string nazivProstorije)
        {
            this.IdObjekta = idObjekta;
            this.LokacijaObjekta = ulicaObjekta + " " + brojUlice + ", " + gradObjekta;
            this.IdUredjaja = idUredjaja;
            this.NazivUredjaja = nazivUredjaja;
            this.TipUredjaja = tipUredjaja;
            this.VrstaUredjaja = vrstaUredjaja;
            this.IdObjekatUredjaj = idObjekatUredjaj;
            this.nazivProstorije = nazivProstorije;
        }

        public int IdObjekta { get => idObjekta; set => idObjekta = value; }
        public string LokacijaObjekta { get => lokacijaObjekta; set => lokacijaObjekta = value; }
        public int IdUredjaja { get => idUredjaja; set => idUredjaja = value; }
        public string NazivUredjaja { get => nazivUredjaja; set => nazivUredjaja = value; }
        public string TipUredjaja { get => tipUredjaja; set => tipUredjaja = value; }
        public string VrstaUredjaja { get => vrstaUredjaja; set => vrstaUredjaja = value; }
        public int IdObjekatUredjaj { get => idObjekatUredjaj; set => idObjekatUredjaj = value; }
        public string NazivProstorije { get => nazivProstorije; set => nazivProstorije = value; }

    }
}
