namespace backend.DTOs
{
    public class DispatcherDTO
    {
        private int id;
        private string ime;
        private string grad;
        private string naselje;
        private string ulica;
        private int brojKorisnika;
        private int pageIndex;
        private int pageSize;

        public DispatcherDTO(int id, string ime, string grad, string naselje, string ulica, int brojKorisnika)
        {
            Id = id;
            Ime = ime;
            Grad = grad;
            Naselje = naselje;
            Ulica = ulica;
            BrojKorisnika = brojKorisnika;
        }

        public int Id { get => id; set => id = value; }
        public string Ime { get => ime; set => ime = value; }
        public string Grad { get => grad; set => grad = value; }
        public string Naselje { get => naselje; set => naselje = value; }
        public string Ulica { get => ulica; set => ulica = value; }
        public int BrojKorisnika { get => brojKorisnika; set => brojKorisnika = value; }
        public int PageIndex { get => pageIndex; set => pageIndex = value; }
        public int PageSize { get => pageSize; set => pageSize = value; }
    }
}
