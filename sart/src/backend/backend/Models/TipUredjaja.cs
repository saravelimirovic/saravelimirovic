using System.ComponentModel.DataAnnotations;

namespace backend.Models
{
    public class TipUredjaja
    {
        [Key]
        private int id;
        private string naziv;

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv; set => naziv = value; }
    }
}
