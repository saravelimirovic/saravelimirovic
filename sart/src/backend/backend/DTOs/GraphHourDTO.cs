using static System.Runtime.InteropServices.JavaScript.JSType;

namespace backend.DTOs
{
    public class GraphHourDTO
    {
        private DateOnly date;
        private TimeOnly time;
        private double usage;

        public GraphHourDTO(TimeOnly time, double usage)
        {
            this.Time = time;
            this.Usage = usage;
        }


        public GraphHourDTO(TimeOnly time, DateOnly date, double usage)
        {
            this.Date = date;
            this.Time = time;
            this.Usage = usage;
        }
        public GraphHourDTO(DateTime time, double usage)
        {
            this.Date = DateOnly.FromDateTime(time);
            this.Time = TimeOnly.FromDateTime(time);
            this.Usage = usage;
        }

        public DateOnly Date { get => date; set => date = value; }
        public TimeOnly Time { get => time; set => time = value; }
        public double Usage { get => usage; set => usage = value; }
    }
}
