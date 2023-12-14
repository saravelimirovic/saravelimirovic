namespace backend.Helpers.interfaces
{
    public interface IRecordCalculation
    {
        Task RecordForWinterDevices(int objekatUredjajId, List<double> potrosnja, double cekanje);
        Task RecordForSumemrDevices(int objekatUredjajId, List<double> potrosnja, double cekanje);
        Task RecordForDailyDevices(int objekatUredjajId, List<double> potrosnja, double cekanje);
        Task RecordForDailyConstantDevices(int objekatUredjajId, List<double> potrosnja, double cekanje);
        Task RecordForProducerDevices(int objekatUredjajId, List<double> potrosnja, double cekanje);
        Task RecordForRestDevices(int objekatUredjajId, List<double> potrosnja, double cekanje);

    }
}
