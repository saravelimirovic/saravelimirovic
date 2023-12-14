namespace backend.DTOs
{
    public class UserCountDTO
    {
        private int ukupno;
        private int prosumeri;
        private int potrosaci;
        private int proizvodjaci;

        public UserCountDTO(int ukupno, int prosumeri, int potrosaci, int proizvodjaci)
        {
            this.Ukupno = ukupno;
            this.Prosumeri = prosumeri;
            this.Potrosaci = potrosaci;
            this.Proizvodjaci = proizvodjaci;
        }

        public int Ukupno { get => ukupno; set => ukupno = value; }
        public int Prosumeri { get => prosumeri; set => prosumeri = value; }
        public int Potrosaci { get => potrosaci; set => potrosaci = value; }
        public int Proizvodjaci { get => proizvodjaci; set => proizvodjaci = value; }
    }
}
