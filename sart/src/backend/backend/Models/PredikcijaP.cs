using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class PredikcijaP
    {
        [Key]
        private int id;

        [ForeignKey("ObjekatUredjajId")]
        private int objekatUredjajId;
        private ObjekatUredjaj objekatUredjaj;

        private double vrednostPredikcije;
        private DateOnly datum;
        private TimeOnly vreme;

        // -------------------------------------------------------------------------

        public int Id { get =>  id; set => id = value; }
        public int ObjekatUredjajId { get => objekatUredjajId; set => objekatUredjajId = value; }
        public ObjekatUredjaj ObjekatUredjaj { get => objekatUredjaj; set => objekatUredjaj = value; }
        public double VrednostPredikcije { get => vrednostPredikcije; set => vrednostPredikcije = value; }
        public DateOnly Datum { get => datum; set => datum = value; }
        public TimeOnly Vreme { get => vreme; set => vreme = value; }
    }
}
