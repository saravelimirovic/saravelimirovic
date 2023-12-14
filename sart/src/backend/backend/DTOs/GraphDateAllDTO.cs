namespace backend.DTOs
{
    public class GraphDateAllDTO
    {
        private DateOnly date;
        private int month;
        private int year;
        private double potrosnjaPred;
        private double potrosnja;
        private double proizvodnjaPred;
        private double proizvodnja;

        public GraphDateAllDTO(DateOnly date, double potrosnjaPred, double potrosnja, double proizvodnjaPred, double proizvodnja)
        {
            this.Date = date;
            this.PotrosnjaPred = potrosnjaPred;
            this.Potrosnja = potrosnja;
            this.ProizvodnjaPred = proizvodnjaPred;
            this.Proizvodnja = proizvodnja;
        }

        public GraphDateAllDTO(DateOnly date, double potrosnja, double proizvodnja)
        {
            this.Date = date;
            this.Potrosnja = potrosnja;
            this.Proizvodnja = proizvodnja;
        }

        public GraphDateAllDTO(int month, int year, double potrosnjaPred, double potrosnja, double proizvodnjaPred, double proizvodnja)
        {
            this.Month = month;
            this.Year = year;
            this.PotrosnjaPred = potrosnjaPred;
            this.Potrosnja = potrosnja;
            this.ProizvodnjaPred = proizvodnjaPred;
            this.Proizvodnja = proizvodnja;
        }

        public DateOnly Date { get => date; set => date = value; }
        public double PotrosnjaPred { get => potrosnjaPred; set => potrosnjaPred = value; }
        public double Potrosnja { get => potrosnja; set => potrosnja = value; }
        public double ProizvodnjaPred { get => proizvodnjaPred; set => proizvodnjaPred = value; }
        public double Proizvodnja { get => proizvodnja; set => proizvodnja = value; }
        public int Month { get => month; set => month = value; }
        public int Year { get => year; set => year = value; }
    }
}
