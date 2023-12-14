namespace backend.DTOs
{
    public class DeviceDTO
    {
        private int id;
        private string naziv;
        private string status;
        private double vrednost;
        private string tipUredjaja;
        private string dozvolaZaPregled;
        private string dozvolaZaUpravljanje;
        private string vrstaUredjaja;

        public DeviceDTO(string naziv, string status, double vrednost, int id, string tipUredjaja, string vrstaUredjaja)
        {
            this.naziv = naziv;
            this.status = status;
            this.vrednost = vrednost;
            this.id = id;
            this.tipUredjaja = tipUredjaja;
            this.vrstaUredjaja = vrstaUredjaja;
        }

        public DeviceDTO(int id, string naziv, string status, double vrednost, string tipUredjaja, string dozvolaZaPregled, string dozvolaZaUpravljanje, string vrstaUredjaja)
        {
            this.id = id;
            this.naziv = naziv;
            this.status = status;
            this.vrednost = vrednost;
            this.tipUredjaja = tipUredjaja;
            this.dozvolaZaPregled = dozvolaZaPregled;
            this.dozvolaZaUpravljanje = dozvolaZaUpravljanje;
            this.vrstaUredjaja = vrstaUredjaja;
        }

        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv;  set => naziv = value; }
        public string Status { get => status; set => status = value; }
        public double Vrednost { get => vrednost; set => vrednost = value; }
        public string TipUredjaja { get => tipUredjaja; set => tipUredjaja = value; }
        public string VrstaUredjaja { get => vrstaUredjaja; set => vrstaUredjaja = value; }
        public string DozvolaZaPregled { get => dozvolaZaPregled; set => dozvolaZaPregled = value; }
        public string DozvolaZaUpravljanje { get => dozvolaZaUpravljanje; set => dozvolaZaUpravljanje = value; }


    }
}
