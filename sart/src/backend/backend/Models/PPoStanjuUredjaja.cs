using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class PPoStanjuUredjaja
    {
        [Key]
        private int id;

        [ForeignKey("UredjajId")]
        private int uredjajId;
        private Uredjaj uredjaj;

        private string naziv;
        private double pPoSatuStanja;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public int UredjajId { get => uredjajId; set =>  uredjajId = value; }
        public Uredjaj Uredjaj { get => uredjaj; set => uredjaj = value; }
        public string Naziv { get => naziv; set => naziv = value; }
        public double PPoSatuStanja { get => pPoSatuStanja; set => pPoSatuStanja = value; }
    }
}
