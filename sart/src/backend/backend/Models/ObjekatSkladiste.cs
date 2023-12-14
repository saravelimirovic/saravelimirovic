using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class ObjekatSkladiste
    {
        [Key]
        private int id;

        [ForeignKey("SkladisteId")]
        private int skladisteId;
        private Skladiste skladiste;

        [ForeignKey("ObjekatId")]
        private int objekatId;
        private Objekat objekat;

        private double trenutnoStanje;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public int SkladisteId { get => skladisteId; set => skladisteId = value; }
        public Skladiste Skladiste { get => skladiste; set => skladiste = value; }
        public int ObjekatId { get => objekatId; set => objekatId = value; }
        public Objekat Objekat { get => objekat; set => objekat = value; }
        public double TrenutnoStanje { get => trenutnoStanje; set => trenutnoStanje = value; }
    }
}
