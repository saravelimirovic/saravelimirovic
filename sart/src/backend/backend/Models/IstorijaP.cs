using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace backend.Models
{
    public class IstorijaP
    {
        [Key]
        private int id;

        [ForeignKey("ObjekatUredjajId")]
        private int objekatUredjajId;
        private ObjekatUredjaj objekatUredjaj;

        private double vrednostRealizacije;
        private DateOnly datum;
        private TimeOnly vreme;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public int ObjekatUredjajId { get => objekatUredjajId; set => objekatUredjajId = value; }
        public ObjekatUredjaj ObjekatUredjaj { get => objekatUredjaj; set => objekatUredjaj = value; }
        public double VrednostRealizacije { get => vrednostRealizacije; set => vrednostRealizacije = value; }
        public DateOnly Datum { get => datum; set => datum = value; }
        public TimeOnly Vreme { get => vreme; set => vreme = value; }
    }
}
