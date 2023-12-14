using backend.Common;
using backend.Controllers;
using backend.DTOs;
using backend.Models;

namespace backend.services.interfaces
{
    public interface IRecordService
    {

        // ************************* D S O ******************************* //
        // --------------------- S K L A D I S T A ------------------------//
        Task<double> getTodaysSkladistaAll();


        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //
        Task<double> getTodaysRecordForAllInOne(ETipUredjaj tipUredjaja);
        Task<double> getYesterdayRecordForAllInOne(ETipUredjaj tipUredjaja);
        Task<List<GraphHourDTO>> getTodaysRecordForAll(ETipUredjaj tipUredjaja);
        Task<List<GraphHourDTO>> getTodaysRecordPr(ETipUredjaj tipUredjaja);
        Task<List<GraphHourDTO>> getTomorrowRecordPr(ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePeriod(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePastPeriodPr(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePeriodPr(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYear(ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYearPr(ETipUredjaj tipUredjaja);




        // ********************* P R O S U M E R ************************ //

        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //
        Task<double> getPercentRecordForObjectForOneDay(int objekatId, ETipUredjaj tipUredjaja);
        Task<List<GraphHourDTO>> getTodaysRecordForObject(int objekatId, ETipUredjaj tipUredjaja);
        Task<List<GraphHourDTO>> getTwoDaysRecordForObjectPr(int objekatId, ETipUredjaj tipUredjaja);
        Task<double> getPercentRecordForObjectForSomePeriod(int objekatId, ETipUredjaj tipUredjaja, PeriodOfTime fromPeriodOfTime);
        Task<List<GraphDateDto>> getRecordForSomePeriodForObject(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePeriodForObjectPr(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphDateDto>> getRecordForSomePastPeriodForObjectPr(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYearForObject(int objekatId, ETipUredjaj tipUredjaja);
        Task<List<GraphMonthDTO>> getRecordForPastYearForObjectPr(int objekatId, ETipUredjaj tipUredjaja);


        // ------------------- U R E DJ A J I   V R E D N O S T ------------------ //
        Task<double> getPercentRecordForDeviceForOneDay(int objekatId);
        Task<List<GraphHourDTO>> getTodaysRecordForDevice(int objekatId);
        Task<List<GraphHourDTO>> getTwoDayRecordForDevicePred(int objekatUredjajId);
        Task<double> getPercentRecordForDeviceForSomePeriod(int objekatId, PeriodOfTime fromPeriodOfTime);
        Task<List<GraphDateDto>> getRecordForSomePeriodForDevice(int objekatUredjajId, PeriodOfTime periodOfTime);
        Task<List<GraphDateDto>> getRecordForSomePeriodForDevicePred(int objekatUredjajId, PeriodOfTime periodOfTime);
        Task<List<GraphDateDto>> getRecordForSomePastPeriodForDevicePred(int objekatUredjajId, PeriodOfTime periodOfTime);
        Task<List<GraphMonthDTO>> getRecordForPastYearForDevice(int objekatUredjajId);
        Task<List<GraphMonthDTO>> getRecordForPastYearForDevicePred(int objekatUredjajId);


        // ------------------- U R E DJ A J I ------------------ //

        Task<List<DeviceBrojDTO>> getBrojSvakogUredjaja();
        Task<List<DeviceBrojDTO>> getPotrosnjaIProizvodnjaPoUredjaju();
        Task<List<DeviceBrojDTO>> getPotrosnjaPoVrstiUredjaja();
        Task<List<DeviceBrojDTO>> getPotrosnjaPoVrstiUredjajaPieChart();
        Task<List<DeviceBrojDTO>> getProizvodnjaPoVrstiUredjaja();
        Task<List<DeviceBrojDTO>> getProizvodnjaPoVrstiUredjajaPieChart();

        Task<List<DeviceDsoDTO>> getUredjajKorisnikaById(int id);
        Task<List<DeviceDsoDTO>> getUredjajObjektaById(int id);

        double getPotrProizUredjajaObjektaVidi(int idObjekta, ETipUredjaj tipUredjaja);
    }
}
