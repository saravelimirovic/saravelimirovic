using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class ObjekatUredjaj
    {
        [Key]
        private int id;

        [ForeignKey("ProstorijaId")]
        private int prostorijaId;
        private Prostorija prostorija;

        [ForeignKey("UredjajId")]
        private int uredjajId;
        private Uredjaj uredjaj;

        [ForeignKey("ObjekatId")]
        private int objekatId;
        private Objekat objekat;


        private string dozvola;
        private string kontrola;
        private string ukljucen;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public int ProstorijaId { get => prostorijaId; set => prostorijaId = value; }
        public Prostorija Prostorija { get => prostorija; set => prostorija = value; }
        public int UredjajId { get => uredjajId; set => uredjajId = value; }
        public Uredjaj Uredjaj { get => uredjaj; set => uredjaj = value; }
        public int ObjekatId { get => objekatId; set => objekatId = value; }
        public Objekat Objekat { get => objekat; set => objekat = value; }
        public string Dozvola { get => dozvola; set => dozvola = value; }
        public string Kontrola { get => kontrola; set => kontrola = value; }
        public string Ukljucen { get => ukljucen; set => ukljucen = value; }
    }
}
