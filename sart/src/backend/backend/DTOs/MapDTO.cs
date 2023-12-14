using static System.Runtime.InteropServices.JavaScript.JSType;

namespace backend.DTOs
{
    public class MapDTO
    {
        private int korisnikaId;
        private string ime;
        private string nazivUlice;
        private string adresniBroj;
        private string nazivGrada;
        private string rolaKorisnika;

        public MapDTO(int korisnikaId, string ime, string nazivUlice, string adresniBroj, string nazivGrada, string rolaKorisnika) 
        {
            this.korisnikaId = korisnikaId;
            this.ime = ime;
            this.nazivGrada = nazivGrada;
            this.adresniBroj = adresniBroj;
            this.nazivUlice = nazivUlice;
            this.rolaKorisnika = rolaKorisnika;
        }

        public MapDTO(int korisnikaId, string nazivUlice, string adresniBroj, string nazivGrada, string rolaKorisnika)
        {
            this.korisnikaId = korisnikaId;
            this.nazivGrada = nazivGrada;
            this.adresniBroj = adresniBroj;
            this.nazivUlice = nazivUlice;
            this.rolaKorisnika = rolaKorisnika;
        }

        public int KorisnikaId { get => korisnikaId; set => korisnikaId = value; }
        public string NazivUlice { get => nazivUlice; set => nazivUlice = value; }
        public string AdrsniBroj{ get => adresniBroj; set => adresniBroj = value; }
        public string NazivGrada{ get => nazivGrada; set => nazivGrada = value; }
        public string RolaKorisnika { get => rolaKorisnika; set => rolaKorisnika = value; }
        public string Ime { get => ime; set => ime = value; }
    }
}
