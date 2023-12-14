using backend.Common;
using backend.Controllers;
using backend.DTOs;
using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IRecordRepository
    {
        // ************************* D S O ******************************* //

        // --------------------- S K L A D I S T A ------------------------//
        Task<List<ObjekatSkladiste>> getTodaysSkladistaAllAsync();

        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //

        Task<double> getYesterdayRecordForAllAsync(ETipUredjaj tipUredjaja);
        Task<double> getTodayRecordForAllAsync(ETipUredjaj tipUredjaja);

        Task<List<GraphHourDTO>> getTodaysRecordForAllAsync(ETipUredjaj tipUredjaja);
        Task<List<GraphHourDTO>> getTodaysRecordPredAsync(ETipUredjaj tipUredjaja);
        Task<List<GraphHourDTO>> getTomorrowRecordPredAsync(ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePeriodAsync(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePeriodPrAsync(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePastPeriodPrAsync(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYearAsync(ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYearPrAsync(ETipUredjaj tipUredjaja);


        // ********************* P R O S U M E R ************************ //

        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //

        Task<List<GraphHourDTO>> getTodaysRecordForObjectAsync(int objekatId, ETipUredjaj tipUredjaja);
        Task<double> getPercentRecordForObjectForOneDayAsync(int objekatId, ETipUredjaj tipUredjaja, int days);
        Task<double> getPercentRecordForObjectForSomePeriodAsync(int objekatId, ETipUredjaj tipUredjaja, int fromPeriodOfTime, PeriodOfTime toPeriodOfTime);
        Task<List<GraphHourDTO>> getTwoDaysRecordForObjectPredAsync(int objekatId, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePeriodForObjectAsync(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePastPeriodForObjectPredAsync(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePeriodForObjectPredAsync(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYearForObjectAsync(int objekatId, ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYearForObjectPredAsync(int objekatId, ETipUredjaj tipUredjaja);

        // ------------------- U R E DJ A J I   V R E D N O S T ------------------ //
        Task<List<GraphHourDTO>> getTodaysRecordForDeviceAsync(int objekatUredjajId);
        Task<List<GraphHourDTO>> getTwoDaysRecordForDevicePredAsync(int objekatUredjajId);
        Task<double> getPercentRecordForDeviceForOneDayAsync(int objekatUredjajId, int days);
        Task<double> getPercentRecordForDeviceForSomePeriodAsync(int objekatUredjajId, int fromPeriodOfTime, PeriodOfTime toPeriodOfTime);
        Task<List<GraphDateDto>> getRecordForSomePeriodForDeviceAsync(int objekatUredjajId, PeriodOfTime periodOfTime);
        Task<List<GraphDateDto>> getRecordForSomePeriodForDevicePredAsync(int objekatUredjajId, PeriodOfTime periodOfTime);
        Task<List<GraphDateDto>> getRecordForSomePastPeriodForDevicePredAsync(int objekatUredjajId, PeriodOfTime periodOfTime);
        Task<List<GraphMonthDTO>> getRecordForPastYearForDeviceAsync(int objekatUredjajId);
        Task<List<GraphMonthDTO>> getRecordForPastYearForDevicePredAsync(int objekatUredjajId);

        // ------------------- U R E DJ A J I ------------------ //

        Task<List<DeviceBrojDTO>> getBrojSvakogUredjaja();
        Task<List<DeviceBrojDTO>> getPotrosnjaIProizvodnjaPoUredjaju();
        Task<List<DeviceBrojDTO>> getPotrosnjaPoVrstiUredjaja();
        Task<List<DeviceBrojDTO>> getProizvodnjaPoVrstiUredjaja();
        double getUkupnaPotrosnja();
        double getUkupnaProizvodnja();

        Task<List<DeviceDsoDTO>> getUredjajKorisnikaById(int id);
        Task<List<DeviceDsoDTO>> getUredjajObjektaById(int id);

        double getPotrProizUredjajaObjektaVidi(int idObjekta, ETipUredjaj tipUredjaja);
    }
}
