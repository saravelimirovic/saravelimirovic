using System.Text.Json.Serialization;

namespace backend.DTOs
{
    public class UserFilterDTO
    {
        private int id; // vracam
        private string ime; // uzimam i vracam
        private string grad; // uzimam i vracam
        private string naselje; // uzimam i vracam
        private string ulica; // uzimam i vracam
        private double potrosnjaKorisnika; // vracam
        private double proizvodnjaKorisnika; // vracam
        private string rolaVracam; // vracam
        //private double minPotrosnja; // vracam
        //private double maxPotrosnja; // vracam
        //private double minProizvodnja; // vracam
        //private double maxProizvodnja; // vracam

        private string potrosnjaOd; // uzimam
        private string potrosnjaDo; // uzimam
        private string proizvodnjaOd; // uzimam
        private string proizvodnjaDo; // uzimam
        private int rolaUzimam; // uzimam
        private int pageIndex; // uzimam
        private int pageSize; // uzimam

        [JsonConstructor]
        public UserFilterDTO(int id, string ime, string grad, string naselje, string ulica, double potrosnjaKorisnika, double proizvodnjaKorisnika, string rolaVracam, string potrosnjaOd, string potrosnjaDo, string proizvodnjaOd, string proizvodnjaDo, int rolaUzimam, int pageIndex, int pageSize)
        {
            Id = id;
            Ime = ime;
            Grad = grad;
            Naselje = naselje;
            Ulica = ulica;
            PotrosnjaKorisnika = potrosnjaKorisnika;
            ProizvodnjaKorisnika = proizvodnjaKorisnika;
            RolaVracam = rolaVracam;
            PotrosnjaOd = potrosnjaOd;
            PotrosnjaDo = potrosnjaDo;
            ProizvodnjaOd = proizvodnjaOd;
            ProizvodnjaDo = proizvodnjaDo;
            RolaUzimam = rolaUzimam;
            PageIndex = pageIndex;
            PageSize = pageSize;
        }

        public UserFilterDTO(int id, string ime, string grad, string naselje, string ulica, string potrosnjaOd, string potrosnjaDo, string proizvodnjaOd, string proizvodnjaDo, string rola)
        {
            Id = id;
            Ime = ime;
            Grad = grad;
            Naselje = naselje;
            Ulica = ulica;
            PotrosnjaOd = potrosnjaOd;
            PotrosnjaDo = potrosnjaDo;
            ProizvodnjaOd = proizvodnjaOd;
            ProizvodnjaDo = proizvodnjaDo;
            RolaVracam = rola;
        }

        //[JsonConstructor]
        //public UserFilterDTO(int id, string ime, string grad, string naselje, string ulica, double potrosnjaKorisnika, double proizvodnjaKorisnika, string rolaVracam, double minPotrosnja, double maxPotrosnja, double minProizvodnja, double maxProizvodnja)
        //{
        //    this.id = id;
        //    this.ime = ime;
        //    this.grad = grad;
        //    this.naselje = naselje;
        //    this.ulica = ulica;
        //    this.rolaVracam = rolaVracam;
        //    this.MinPotrosnja = minPotrosnja;
        //    this.MaxPotrosnja = maxPotrosnja;
        //    this.MinProizvodnja = minProizvodnja;
        //    this.MaxProizvodnja = maxProizvodnja;
        //    this.PotrosnjaKorisnika = potrosnjaKorisnika;
        //    this.ProizvodnjaKorisnika = proizvodnjaKorisnika;
        //}

        public int Id { get => id; set => id = value; }
        public string Ime { get => ime; set => ime = value; }
        public string Grad { get => grad; set => grad = value; }
        public string Naselje { get => naselje; set => naselje = value; }
        public string Ulica { get => ulica; set => ulica = value; }
        public double PotrosnjaKorisnika { get => potrosnjaKorisnika; set => potrosnjaKorisnika = value; }
        public double ProizvodnjaKorisnika { get => proizvodnjaKorisnika; set => proizvodnjaKorisnika = value; }
        public string RolaVracam { get => rolaVracam; set => rolaVracam = value; }
        //public double MinPotrosnja { get => minPotrosnja; set => minPotrosnja = value; }
        //public double MaxPotrosnja { get => maxPotrosnja; set => maxPotrosnja = value; }
        //public double MinProizvodnja { get => minProizvodnja; set => minProizvodnja = value; }
        //public double MaxProizvodnja { get => maxProizvodnja; set => maxProizvodnja = value; }


        public string PotrosnjaOd { get => potrosnjaOd; set => potrosnjaOd = value; }
        public string PotrosnjaDo { get => potrosnjaDo; set => potrosnjaDo = value; }
        public string ProizvodnjaOd { get => proizvodnjaOd; set => proizvodnjaOd = value; }
        public string ProizvodnjaDo { get => proizvodnjaDo; set => proizvodnjaDo = value; }
        public int RolaUzimam { get => rolaUzimam; set => rolaUzimam = value; }
        public int PageIndex { get => pageIndex; set => pageIndex = value; }
        public int PageSize { get => pageSize; set => pageSize = value; }
    }
}
