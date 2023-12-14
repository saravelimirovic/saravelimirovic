using backend.Models;
using System.Text.Json.Serialization;

namespace backend.DTOs
{
    public class ProsumerRegistrationDTO
    {
        private string ime;
        private string prezime;
        private string email;
        private string lozinka;
        private string jmbg;
        private string brojTelefona;
        private string ulica;
        private string adresniBroj;
        private string naselje;
        private string grad;
        private int rolaId;

        public string Ime { get => ime; set => ime = value; }
        public string Prezime { get => prezime; set => prezime = value; }
        public string Email { get => email; set => email = value; }
        public string Lozinka { get => lozinka; set => lozinka = value; }
        public string Jmbg { get => jmbg; set => jmbg = value; }
        public string BrojTelefona { get => brojTelefona; set => brojTelefona = value; }
        public string Ulica { get => ulica; set => ulica = value; }
        public string AdresniBroj { get => adresniBroj; set => adresniBroj = value; }
        public string Naselje { get => naselje; set => naselje = value; }
        public string Grad { get => grad; set => grad = value; }
        public int RolaId { get => rolaId; set => rolaId = value; }
    }
}
