using System.ComponentModel.DataAnnotations;

namespace backend.Models
{
    public class Skladiste
    {
        [Key]
        private int id;
        private string naziv;
        private double maxSkladista;
        private double potrosnjaZaCuvanjePoSatu;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv; set => naziv = value; }
        public double MaxSkladista { get =>  maxSkladista; set => maxSkladista = value;}
        public double PotrosnjaZaCuvanjePoSatu { get => potrosnjaZaCuvanjePoSatu; set => potrosnjaZaCuvanjePoSatu = value; } 
    }
}
