using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class Naselje
    {
        [Key]
        private int id;

        [ForeignKey("GradId")]
        private int gradId;
        private Grad grad;

        private string naziv;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public int GradId { get => gradId; set => gradId = value; }
        public Grad Grad { get => grad; set => grad = value; }
        public string Naziv { get => naziv; set => naziv = value; }
    }
}
