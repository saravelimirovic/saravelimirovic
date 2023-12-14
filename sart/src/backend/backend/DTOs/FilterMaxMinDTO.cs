namespace backend.DTOs
{
    public class FilterMaxMinDTO
    {
        private double minimumPotr;
        private double maximumPotr;
        private double minimumProiz;
        private double maximumProiz;

        public FilterMaxMinDTO(double minimumPotr, double maximumPotr, double minimumProiz, double maximumProiz)
        {
            MinimumPotr = minimumPotr;
            MaximumPotr = maximumPotr;
            MinimumProiz = minimumProiz;
            MaximumProiz = maximumProiz;
        }

        public double MinimumPotr { get => minimumPotr; set => minimumPotr = value; }
        public double MaximumPotr { get => maximumPotr; set => maximumPotr = value; }
        public double MinimumProiz { get => minimumProiz; set => minimumProiz = value; }
        public double MaximumProiz { get => maximumProiz; set => maximumProiz = value; }
    }
}
