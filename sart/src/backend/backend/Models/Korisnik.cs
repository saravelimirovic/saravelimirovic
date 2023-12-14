using Microsoft.AspNetCore.Identity;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Reflection.Metadata.Ecma335;
using System.Transactions;

namespace backend.Models
{
    public class Korisnik
    {
        [Key]
        private int id;
        private string email;
        private string sifra;

        [ForeignKey("RolaId")]
        private int rolaId;
        private Rola rola;

        private string ime;
        private string prezime;
        private string jmbg;
        private string brTelefona;

        [ForeignKey("UlicaId")]
        private int ulicaId;
        private Ulica ulica;

        private string adresniBroj;
        // private string token;


        // za Zaboravili ste lozinku !
        private string resetSifraToken;
        private DateTime resetSifraIsticanje;
        
        // -------------------------------------------------------------------------

        public int Id { get => id; set => id = value; }
        public string Email { get => email; set => email = value; }
        public string Sifra { get => sifra; set => sifra = value; }
        public int RolaId { get => rolaId; set => rolaId = value; } 
        public Rola Rola { get => rola; set => rola = value; }
        public string Ime { get => ime; set => ime = value; }
        public string Prezime { get => prezime; set => prezime = value; }
        public string JMBG { get => jmbg; set => jmbg = value; }
        public string BrTelefona { get => brTelefona; set => brTelefona = value; }
        public int UlicaId { get => ulicaId; set => ulicaId = value; } 
        public Ulica Ulica { get => ulica; set => ulica = value; }
        public string AdresniBroj { get => adresniBroj; set => adresniBroj = value; }
        // public string Token { get => token; set => token = value; }
        public string ResetSifraToken { get => resetSifraToken; set => resetSifraToken = value; }
        public DateTime ResetSifraIsticanje { get => resetSifraIsticanje; set => resetSifraIsticanje = value; }
    }
}
