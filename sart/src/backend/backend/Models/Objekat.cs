using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace backend.Models
{
    public class Objekat
    {
        [Key]
        private int id;

        [ForeignKey("UlicaId")]
        private int ulicaId;
        private Ulica ulica;

        [ForeignKey("KorisnikId")]
        private int korisnikId;
        private Korisnik korisnik;

        private string adresniBroj;
        private string naziv;

        // -------------------------------------------------------------------------

        public int Id { get => id;  set => id = value; }    
        public int UlicaId { get => ulicaId; set => ulicaId = value; }
        public Ulica Ulica { get => ulica; set => ulica = value; }
        public int KorisnikId { get => korisnikId; set => korisnikId = value; }
        public Korisnik Korisnik { get => korisnik; set => korisnik = value; }
        public string AdresniBroj { get => adresniBroj; set => adresniBroj = value; }
        public string Naziv { get => naziv; set => naziv = value; }

    }
}
