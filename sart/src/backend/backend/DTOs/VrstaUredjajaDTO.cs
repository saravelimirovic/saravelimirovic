namespace backend.DTOs
{
    public class VrstaUredjajaDTO
    {
        private int id;
        private string naziv;

        public VrstaUredjajaDTO(int id, string naziv)
        {
            this.id = id;
            this.naziv = naziv;
        }
        public int Id { get => id; set => id = value; }
        public string Naziv { get => naziv; set => naziv = value; }
    }
}
