using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class Uredjaj
    {
        [Key]
        private int id;
        private string naziv;
        private double pPrilikomMirovanja; // real?

        [ForeignKey("TipUredjajaId")]
        private int tipUredjajaId;
        private TipUredjaja tipUredjaja;

        [ForeignKey("VrstaUredjajaId")]
        private int vrstaUredjajaId;
        private VrstaUredjaja vrstaUredjaja;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv;  set => naziv = value; }
        public double PPrilikomMirovanja { get => pPrilikomMirovanja; set => pPrilikomMirovanja = value; }
        public int TipUredjajaId { get => tipUredjajaId; set => tipUredjajaId = value; }
        public TipUredjaja TipUredjaja { get => tipUredjaja; set => tipUredjaja = value; }
        public int VrstaUredjajaId { get => vrstaUredjajaId; set => vrstaUredjajaId = value; }
        public VrstaUredjaja VrstaUredjaja { get => vrstaUredjaja; set => vrstaUredjaja = value; }
    }
}
