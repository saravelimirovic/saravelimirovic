namespace backend.DTOs
{
    public class StoreDTO
    {
        private int id;
        private string naziv;
        private double trenutnoStanje;
        private double maxSkladiste;
        private double potrosnjaZaCuvanjePoSatu;
        private int objekatId;

        public StoreDTO(int id, string naziv, double trenutnoStanje, double maxSkladiste, double potrosnjaZaCuvanjePoSatu)
        {
            this.id = id;
            this.naziv = naziv;
            this.trenutnoStanje = trenutnoStanje;
            this.maxSkladiste = maxSkladiste;
            this.potrosnjaZaCuvanjePoSatu = potrosnjaZaCuvanjePoSatu;
        }

        public StoreDTO()
        { }
        public StoreDTO(int id, string naziv, double trenutnoStanje, double maxSkladiste, double potrosnjaZaCuvanjePoSatu, int objekatId)
        {
            this.id = id;
            this.naziv = naziv;
            this.trenutnoStanje = trenutnoStanje;
            this.maxSkladiste = maxSkladiste;
            this.potrosnjaZaCuvanjePoSatu = potrosnjaZaCuvanjePoSatu;
            this.objekatId = objekatId;
        }

        public StoreDTO(int id, string naziv)
        {
            this.id = id;
            this.naziv = naziv;
        }

        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv; set => naziv = value; }
        public double TrenutnoStanje { get => trenutnoStanje; set => trenutnoStanje = value; }
        public double MaxSkladiste { get => maxSkladiste; set => maxSkladiste = value; }
        public double PotrosnjaZaCuvanjePoSatu { get => potrosnjaZaCuvanjePoSatu; set => potrosnjaZaCuvanjePoSatu = value; }
        public int ObjekatId { get => objekatId; set => objekatId = value; }

    }
}
