using System.Text.Json.Serialization;

namespace backend.DTOs
{
    public class UserTabelaDTO
    {
        private int id;
        private string ime;
        private string grad;
        private string naselje;
        private string ulica;
        private double potrosnja;
        private double proizvodnja;
        private string rola;
        private int brojKorisnika;

        public UserTabelaDTO(int id, string ime, string grad, string naselje, string ulica, double potrosnja, double proizvodnja, string rola, int brojKorisnika)
        {
            Id = id;
            Ime = ime;
            Grad = grad;
            Naselje = naselje;
            Ulica = ulica;
            Potrosnja = potrosnja;
            Proizvodnja = proizvodnja;
            Rola = rola;
            BrojKorisnika = brojKorisnika;
        }

        [JsonConstructor]
        public UserTabelaDTO(int id, string ime, string grad, string naselje, string ulica, string rola, int brojKorisnika)
        {
            Id = id;
            Ime = ime;
            Grad = grad;
            Naselje = naselje;
            Ulica = ulica;
            Rola = rola;
            BrojKorisnika = brojKorisnika;
        }

        public UserTabelaDTO(int id, string ime, string grad, string naselje, string ulica, string rola)
        {
            Id = id;
            Ime = ime;
            Grad = grad;
            Naselje = naselje;
            Ulica = ulica;
            Rola = rola;
        }

        public UserTabelaDTO(int id, string ime, double potrosnja, double proizvodnja)
        {
            Id = id;
            Ime = ime;
            Potrosnja = potrosnja;
            Proizvodnja = proizvodnja;
        }

        public int Id { get => id; set => id = value; }
        public string Ime { get => ime; set => ime = value; }
        public string Grad { get => grad; set => grad = value; }
        public string Naselje { get => naselje; set => naselje = value; }
        public string Ulica { get =>  ulica; set => ulica = value; }
        public double Potrosnja { get => potrosnja; set => potrosnja = value; }
        public double Proizvodnja { get => proizvodnja; set => proizvodnja = value; }
        public string Rola { get => rola; set => rola = value; }
        public int BrojKorisnika { get => brojKorisnika; set => brojKorisnika = value; }
    }
}
