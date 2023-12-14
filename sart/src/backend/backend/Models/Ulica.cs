using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class Ulica
    {
        [Key]
        private int id;

        [ForeignKey("NaseljeId")]
        private int naseljeId;
        private Naselje naselje;

        private string naziv;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public int NaseljeId { get => naseljeId; set => naseljeId = value; } 
        public Naselje Naselje { get => naselje; set => naselje = value; }
        public string Naziv { get => naziv; set => naziv = value; }

    }
}
