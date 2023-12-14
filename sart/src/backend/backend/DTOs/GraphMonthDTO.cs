namespace backend.DTOs
{
    public class GraphMonthDTO
    {
        private int month;
        private int year;
        private double usage;

        public GraphMonthDTO(int month, int year, double usage)
        {
            this.month = month;
            this.year = year;
            this.usage = usage;
        }

        public int Month { get => month; set => month = value; }
        public int Year { get => year; set => year = value; }
        public double Usage { get => usage; set => usage = value; }
    }
}
