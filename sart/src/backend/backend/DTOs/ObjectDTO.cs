using backend.Models;
using System.Globalization;

namespace backend.DTOs
{
    public class ObjectDTO
    {
        private int id;
        private string ulica;
        private string adresniBroj;
        private string naselje;
        private string grad;
        private string naziv;

        public ObjectDTO(int id, string ulica, string adresniBroj, string naselje, string grad, string naziv)
        {
            this.id = id;
            this.ulica = ulica;
            this.naselje = naselje;
            this.grad = grad;
            this.adresniBroj = adresniBroj;
            this.naziv = naziv;
        }

        public int Id{ get => id; set => id = value; }
        public string Ulica { get => ulica; set => ulica = value; }
        public string AdresniBroj { get => adresniBroj; set =>  adresniBroj = value; }
        public string Naselje { get => naselje; set => naselje = value; }
        public string Grad { get => grad; set => grad = value; }
        public string Naziv { get => naziv; set => naziv = value; }
    }
}
