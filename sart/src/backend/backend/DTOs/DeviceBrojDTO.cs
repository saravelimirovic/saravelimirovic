using backend.repositroy.interfaces;

namespace backend.DTOs
{
    public class DeviceBrojDTO
    {
        private double ukupno; //ukupna potrosnja ili proizovdnja
        private string nazivUredjaja;
        private int brojUredjaja;
        private double potrosnja;
        private double proizvodnja;
        private double procenat;

        public DeviceBrojDTO(string nazivUredjaja, int brojUredjaja, double potrosnja, double proizvodnja, double ukupna)
        {
            this.NazivUredjaja = nazivUredjaja;
            this.BrojUredjaja = brojUredjaja;
            this.Potrosnja = Math.Round(potrosnja, 2, MidpointRounding.AwayFromZero);
            this.Proizvodnja = Math.Round(proizvodnja, 2, MidpointRounding.AwayFromZero); ;
            this.ukupno = ukupna;
            if(potrosnja!=0)
                this.Procenat = Math.Round(potrosnja / ukupno * 100, 2, MidpointRounding.AwayFromZero);
            else
                this.Procenat = Math.Round(proizvodnja / ukupno * 100, 2, MidpointRounding.AwayFromZero);

        }

        public DeviceBrojDTO(string nazivUredjaja, int brojUredjaja, double potrosnja, double proizvodnja)
        {
            this.NazivUredjaja = nazivUredjaja;
            this.BrojUredjaja = brojUredjaja;
            this.Potrosnja = potrosnja;
            this.Proizvodnja = proizvodnja;
        }

        public DeviceBrojDTO(string nazivUredjaja, int brojUredjaja)
        {
            this.NazivUredjaja = nazivUredjaja;
            this.BrojUredjaja = brojUredjaja;
        }

        public DeviceBrojDTO(string nazivUredjaja, double procenat)
        {
            this.NazivUredjaja = nazivUredjaja;
            this.Procenat = procenat;
        }

        public string NazivUredjaja { get => nazivUredjaja; set => nazivUredjaja = value; }
        public int BrojUredjaja { get => brojUredjaja; set => brojUredjaja = value; }
        public double Potrosnja { get => potrosnja; set => potrosnja = value; }
        public double Proizvodnja { get => proizvodnja; set => proizvodnja = value; }
        public double Procenat { get => procenat; set => procenat = value; }
        public double Ukupno { get => ukupno; set => ukupno = value; }
    }
}
