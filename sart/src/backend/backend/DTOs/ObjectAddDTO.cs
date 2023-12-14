namespace backend.DTOs
{
    public class ObjectAddDTO
    {
        private int korisnikId;
        private string ulica;
        private string broj;
        private string naselje;
        private string grad;
        private string naziv;

        public ObjectAddDTO(int korisnikId, string ulica, string broj, string naselje, string grad, string naziv)
        {
            KorisnikId = korisnikId;
            Ulica = ulica;
            Broj = broj;
            Naselje = naselje;
            Grad = grad;
            Naziv = naziv;
        }

        public int KorisnikId { get => korisnikId; set => korisnikId = value; }
        public string Ulica { get => ulica; set => ulica = value; }
        public string Broj { get => broj; set => broj = value; }
        public string Naselje { get => naselje; set => naselje = value; }
        public string Grad { get => grad; set => grad = value; }
        public string Naziv { get => naziv; set => naziv = value; }
    }
}
