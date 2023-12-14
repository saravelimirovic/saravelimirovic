namespace backend.DTOs
{
    public class GraphDateDto
    {
        private DateOnly date;
        private double usage;

        public GraphDateDto(DateOnly date, double usage) 
        {
            this.Date = date;
            this.Usage = usage;
        }

        public DateOnly Date { get => date; set => date = value; }
        public double Usage { get => usage; set => usage = value; }
    }
}
