namespace backend.DTOs
{
    public class ProstorijaDTO
    {
        private int id;
        private string naziv;

        public ProstorijaDTO(int id, string naziv)
        {
            this.id = id;
            this.naziv = naziv;
        }

        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv; set => naziv = value; }
    }
}
