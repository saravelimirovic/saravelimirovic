using System.ComponentModel.DataAnnotations;

namespace backend.Models
{
    public class Grad
    {
        [Key]
        private int id { get; set; }
        private string naziv { get; set; }

        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv; set => naziv = value; }
    }
}
