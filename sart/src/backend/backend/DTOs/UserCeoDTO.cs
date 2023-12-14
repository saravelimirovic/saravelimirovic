namespace backend.DTOs
{
    public class UserCeoDTO
    {
        private int id;
        private string ime;
        private string email;
        private string jmbg;
        private string brTelefona;
        private string grad;
        private string naselje;
        private string ulica;
        private string adresniBroj;
        private string rola;

        public UserCeoDTO() { }
        public UserCeoDTO(int id, string ime, string jmbg, string brTelefona, string grad, string naselje, string ulica, string adresniBroj, string rola, string email)
        {
            this.id = id;
            this.ime = ime;
            this.jmbg = jmbg;
            this.brTelefona = brTelefona;
            this.grad = grad;
            this.naselje = naselje;
            this.ulica = ulica;
            this.adresniBroj = adresniBroj;
            this.rola = rola;
            this.email = email;
        }
        public UserCeoDTO(int id, string ime, string jmbg, string brTelefona, string grad, string naselje, string ulica, string rola)
        {
            this.id = id;
            this.ime = ime;
            this.jmbg = jmbg;
            this.brTelefona = brTelefona;
            this.grad = grad;
            this.naselje = naselje;
            this.ulica = ulica;
            this.rola = rola;
        }

        public UserCeoDTO(int id, string ime, string jmbg, string brTelefona, string grad, string naselje, string ulica, string adresniBroj, string rola)
        {
            this.id = id;
            this.ime = ime;
            this.jmbg = jmbg;
            this.brTelefona = brTelefona;
            this.grad = grad;
            this.naselje = naselje;
            this.ulica = ulica;
            this.adresniBroj = adresniBroj;
            this.rola = rola;
        }

        public UserCeoDTO(string ime, string grad, string naselje, string ulica, string adresniBroj, string email, string brTelefona, string rola)
        {
            this.ime = ime;
            this.grad = grad;
            this.naselje = naselje;
            this.ulica = ulica;
            this.adresniBroj = adresniBroj;
            this.email = email;
            this.brTelefona = brTelefona;
            this.rola = rola;
        }

      

        public int Id { get => id; set => id = value; }
        public string Ime { get => ime; set => ime = value; }
        public string Jmbg { get => jmbg; set => jmbg = value; }
        public string BrTelefona { get => brTelefona; set => brTelefona = value; }
        public string Grad { get => grad; set => grad = value; }
        public string Naselje { get => naselje; set => naselje = value; }
        public string Ulica { get => ulica; set => ulica = value; }
        public string AdresniBroj { get => adresniBroj; set => adresniBroj = value; }
        public string Rola { get => rola; set => rola = value; }
        public string Email { get => email; set => email = value; }

    }
}
