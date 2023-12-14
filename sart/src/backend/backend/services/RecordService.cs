using backend.Common;
using backend.Controllers;
using backend.DTOs;
using backend.Helpers;
using backend.Models;
using backend.repositroy.interfaces;
using backend.services.interfaces;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace backend.services
{
    public class RecordService : IRecordService
    {
        private readonly IRecordRepository _recordRepository;

        // na manju satnicu
        public static DateTime RoundToHour(DateTime dateTime)
        {
            var updated = dateTime.AddMinutes(30);
            return new DateTime(updated.Year, updated.Month, updated.Day,
                                 updated.Hour, 0, 0, dateTime.Kind);
            //return dateTime.Date.AddHours(dateTime.Hour);
        }

        public RecordService(IRecordRepository recordRepository)
        {
            _recordRepository = recordRepository;
        }

        // ----------------------------------------- I S T O R I J A  ----------------------------------------- //

        // ************************** D S O ************************** //

        // --------------------- U R E DJ A J I ------------------------//

        // vraca broj svake vrste uredjaja
        public async Task<List<DeviceBrojDTO>> getBrojSvakogUredjaja()
        {
            var result = await _recordRepository.getBrojSvakogUredjaja();

            return result;
        }

        // vraca ukupnu potrosnju i proizvodnju po svakom uredjaju
        public async Task<List<DeviceBrojDTO>> getPotrosnjaIProizvodnjaPoUredjaju()
        {
            var result = await _recordRepository.getPotrosnjaIProizvodnjaPoUredjaju();
            List<DeviceBrojDTO> list = new List<DeviceBrojDTO>();

            var brVrste = await _recordRepository.getBrojSvakogUredjaja();

            foreach (var r in result)
            {
                foreach (var br in brVrste)
                {
                    if (r.NazivUredjaja == br.NazivUredjaja)
                    {
                        var potr = Math.Round(r.Potrosnja / br.BrojUredjaja, 2, MidpointRounding.AwayFromZero);
                        var proiz = Math.Round(r.Proizvodnja / br.BrojUredjaja, 2, MidpointRounding.AwayFromZero);

                        list.Add(new DeviceBrojDTO(r.NazivUredjaja, br.BrojUredjaja, potr, proiz));

                        break;
                    }
                }
            }

            return list;
        }

        public async Task<List<DeviceBrojDTO>> getPotrosnjaPoVrstiUredjaja()
        {
            var result = await _recordRepository.getPotrosnjaPoVrstiUredjaja();

            return result;
        }

        public async Task<List<DeviceBrojDTO>> getPotrosnjaPoVrstiUredjajaPieChart()
        {
            var result = await _recordRepository.getPotrosnjaPoVrstiUredjaja();

            if(result.Count() <= 8)
            {
                return result;
            }

            var procenat = 0.0;

            for (int i = 8; i < result.Count(); i++)
            {
                procenat += result[i].Procenat;
            }

            for (int i=8; i<result.Count();)
            {
                result.RemoveAt(i);
            }

            var ostalo = new DeviceBrojDTO("Ostalo", procenat);

            result.Add(ostalo);

            return result;
        }

        public async Task<List<DeviceBrojDTO>> getProizvodnjaPoVrstiUredjaja()
        {
            var result = await _recordRepository.getProizvodnjaPoVrstiUredjaja();

            return result;
        }

        public async Task<List<DeviceBrojDTO>> getProizvodnjaPoVrstiUredjajaPieChart()
        {
            var result = await _recordRepository.getProizvodnjaPoVrstiUredjaja();

            if (result.Count() <= 4)
            {
                return result;
            }

            var procenat = 0.0;

            for (int i = 8; i < result.Count(); i++)
            {
                procenat += result[i].Procenat;
            }

            for (int i = 8; i < result.Count();)
            {
                result.RemoveAt(i);
            }

            var ostalo = new DeviceBrojDTO("Ostalo", procenat);

            result.Add(ostalo);

            return result;
        }

        public async Task<List<DeviceDsoDTO>> getUredjajKorisnikaById(int id)
        {
            var result = await _recordRepository.getUredjajKorisnikaById(id);

            return result;
        }

        public async Task<List<DeviceDsoDTO>> getUredjajObjektaById(int id)
        {
            var result = await _recordRepository.getUredjajObjektaById(id);

            return result;
        }

        // vraca potrosnju uredjaja tog objekta, koje dso moze da vidi
        public double getPotrProizUredjajaObjektaVidi(int idObjekta, ETipUredjaj tipUredjaja)
        {
            var result = _recordRepository.getPotrProizUredjajaObjektaVidi(idObjekta, tipUredjaja);

            return result;
        }


        // --------------------- S K L A D I S T A ------------------------//

        //zbir stanja u skladistima u svim objektima
        public async Task<double> getTodaysSkladistaAll()
        {
            List<ObjekatSkladiste> allRecords = await _recordRepository.getTodaysSkladistaAllAsync();

            double usage = 0.0;

            foreach (var record in allRecords)
            {
                usage += record.TrenutnoStanje;
            }

            return Math.Round(usage, 2, MidpointRounding.AwayFromZero);
        }


        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //

        // zbir potrosnje/proizvodnje svih uredjaja za juce u svim objektima u jednom broju
        public async Task<double> getYesterdayRecordForAllInOne(ETipUredjaj tipUredjaja)
        {
            double yesterdayRecords = await _recordRepository.getYesterdayRecordForAllAsync(tipUredjaja);

            return Math.Round(yesterdayRecords/1000, 2, MidpointRounding.AwayFromZero);
        }


        // zbir potrosnje/proizvodnje svih uredjaja za danas u svim objektima u jednom broju
        public async Task<double> getTodaysRecordForAllInOne(ETipUredjaj tipUredjaja)
        {
            double todaysRecord = await _recordRepository.getTodayRecordForAllAsync(tipUredjaja);  
            

            return Math.Round(todaysRecord/1000, 2, MidpointRounding.AwayFromZero);
        }
  
        //ukupna potrosnja/proizvodnja po satu za sve uredjaje za sve objekte (zaokruzuje na pun manji sat)
        public async Task<List<GraphHourDTO>> getTodaysRecordForAll(ETipUredjaj tipUredjaja)
        {
            List<GraphHourDTO> allRecords = await _recordRepository.getTodaysRecordForAllAsync(tipUredjaja);
            return allRecords;
        }

        // zbir potrosnje/proizvodnje svih uredjaja za 7/31 dan po datumu (danu)
        public async Task<List<GraphDateDto>> getRecordForSomePeriod(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePeriodAsync(periodOfTime, tipUredjaja);
            return allRecords;
        }

        //ukupna potrosnja/proizvodnja po mesecu za 365 dana za sve uredjaje 
        public async Task<List<GraphMonthDTO>> getRecordForPastYear(ETipUredjaj tipUredjaja)
        {
            List<GraphMonthDTO> allRecords = await _recordRepository.getRecordForPastYearAsync(tipUredjaja);

            return allRecords;
        }

        // ************************** P R O S U M E R ************************** //

        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //
        
        
        //ukupna potrosnja/proizvodnja po satu za sve uredjaje za odredjeni objekat 
        public async Task<double> getPercentRecordForObjectForOneDay(int objekatId, ETipUredjaj tipUredjaja)
        {
            double today = await _recordRepository.getPercentRecordForObjectForOneDayAsync(objekatId, tipUredjaja, (int)PeriodOfTime.Day);
            double yesterday = await _recordRepository.getPercentRecordForObjectForOneDayAsync(objekatId, tipUredjaja, (int)PeriodOfTime.Day + 1);
            if (yesterday == 0)
            {
                return 0;
            }
            return Math.Round((((today - yesterday) / yesterday) * 100), 2);
           
        }

        //ukupna potrosnja/proizvodnja po satu za sve uredjaje za odredjeni objekat 
        public async Task<List<GraphHourDTO>> getTodaysRecordForObject(int objekatId, ETipUredjaj tipUredjaja)
        {
            List<GraphHourDTO> allRecords = await _recordRepository.getTodaysRecordForObjectAsync(objekatId, tipUredjaja);

            return allRecords;
        }

        public async Task<double> getPercentRecordForObjectForSomePeriod(int objekatId, ETipUredjaj tipUredjaja, PeriodOfTime fromPeriodOfTime)
        {
            double today = await _recordRepository.getPercentRecordForObjectForSomePeriodAsync(objekatId, tipUredjaja, (int)fromPeriodOfTime, PeriodOfTime.Day);
            double yesterday = await _recordRepository.getPercentRecordForObjectForSomePeriodAsync(objekatId, tipUredjaja, (int)fromPeriodOfTime  * 2, fromPeriodOfTime);
            if (yesterday == 0)
            {
                return 0;
            }
            return Math.Round(((today - yesterday) / yesterday * 100), 2);

        }

        //ukupna potrosnja/proizvodnja po danu za 7/31 dana za sve uredjaje za odredjeni objekat

        public async Task<List<GraphDateDto>> getRecordForSomePeriodForObject(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePeriodForObjectAsync(objekatId, periodOfTime, tipUredjaja);

            
            return allRecords;
        }
        
        //ukupna potrosnja/proizvodnja po mesecu za 365 dana za sve uredjaje za odredjeni objekat

        public async Task<List<GraphMonthDTO>> getRecordForPastYearForObject(int objekatId, ETipUredjaj tipUredjaja)
        {
            List<GraphMonthDTO> allRecords = await _recordRepository.getRecordForPastYearForObjectAsync(objekatId, tipUredjaja);


            return allRecords;
        }
        // ------------------- U R E DJ A J I   V R E D N O S T ------------------ //


        //ukupna potrosnja/proizvodnja po danu za 7/31 dana za odredjeni uredjaj

        public async Task<List<GraphDateDto>> getRecordForSomePeriodForDevice(int objekatUredjajId, PeriodOfTime periodOfTime)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePeriodForDeviceAsync(objekatUredjajId, periodOfTime);


            return allRecords;
        }

        // procentualni prikaz proizvodnje u odnosu na juce za jedan uredjaj
        public async Task<double> getPercentRecordForDeviceForOneDay(int objekatId)
        {
            double today = await _recordRepository.getPercentRecordForDeviceForOneDayAsync(objekatId, (int)PeriodOfTime.Day);
            double yesterday = await _recordRepository.getPercentRecordForDeviceForOneDayAsync(objekatId, (int)PeriodOfTime.Day + 1);
            if (yesterday == 0)
            {
                return 0;
            }
            return Math.Round((((today - yesterday) / yesterday) * 100), 2);

        }

        //ukupna potrosnja/proizvodnja po satu za odredjeni uredjaje 
        public async Task<List<GraphHourDTO>> getTodaysRecordForDevice(int objekatId)
        {
            List<GraphHourDTO> allRecords = await _recordRepository.getTodaysRecordForDeviceAsync(objekatId);

            return allRecords;
        }

        // procentualni prikaz proizvodnje u odnosu na neki vremenski period za jedan uredjaj
        public async Task<double> getPercentRecordForDeviceForSomePeriod(int objekatId, PeriodOfTime fromPeriodOfTime)
        {
            double today = await _recordRepository.getPercentRecordForDeviceForSomePeriodAsync(objekatId, (int)fromPeriodOfTime, PeriodOfTime.Day);
            double yesterday = await _recordRepository.getPercentRecordForDeviceForSomePeriodAsync(objekatId, (int)fromPeriodOfTime * 2, fromPeriodOfTime);
            if (yesterday == 0)
            {
                return 0;
            }
            return Math.Round(((today - yesterday) / yesterday * 100), 2);

        }

        public async Task<List<GraphMonthDTO>> getRecordForPastYearForDevice(int objekatUredjajId)
        {
            List<GraphMonthDTO> allRecords = await _recordRepository.getRecordForPastYearForDeviceAsync(objekatUredjajId);


            return allRecords;
        }











        // ----------------------------------------- P R E D I K C I J A  ----------------------------------------- //


        // ************************** D S O ************************** //

        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //

        // ukupna predikcija potrosnje/proizvodnje po danu svih uredjaja za narednih 7/31 dana
        public async Task<List<GraphHourDTO>> getTodaysRecordPr(ETipUredjaj tipUredjaja)
        {
            List<GraphHourDTO> allRecords = await _recordRepository.getTodaysRecordPredAsync(tipUredjaja);

            return allRecords;
        }

        //ukupna potrosnja/proizvodnja po satu za sve uredjaje za sve objekte (zaokruzuje na pun manji sat)
        public async Task<List<GraphHourDTO>> getTomorrowRecordPr(ETipUredjaj tipUredjaja)
        {
            List<GraphHourDTO> allRecords = await _recordRepository.getTomorrowRecordPredAsync(tipUredjaja);
            return allRecords;
        }
        public async Task<List<GraphDateDto>> getRecordForSomePeriodPr(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePeriodPrAsync(periodOfTime, tipUredjaja);

            return allRecords;
        }

        // ukupna predikcija potrosnje/proizvodnje po danu svih uredjaja za prethodnih 7/31 dana
        public async Task<List<GraphDateDto>> getRecordForSomePastPeriodPr(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePastPeriodPrAsync(periodOfTime, tipUredjaja);


            return allRecords;
        }


        // ukupna predikcija potrosnje/proizvodnje po mesecu svih uredjaja za prethodnih 365 dana
        public async Task<List<GraphMonthDTO>> getRecordForPastYearPr(ETipUredjaj tipUredjaja)
        {
            List<GraphMonthDTO> allRecords = await _recordRepository.getRecordForPastYearPrAsync(tipUredjaja);


            return allRecords;
        }

        // ************************** P R O S U M E R ************************** //

        // -------------------- P O T R O S NJ A -------------------- //

        //ukupna potrosnja/proizvodnja po satu za danas i sutra za sve uredjaje za odredjeni objekat
        public async Task<List<GraphHourDTO>> getTwoDaysRecordForObjectPr(int objekatId, ETipUredjaj tipUredjaja)
        {
            List<GraphHourDTO> allRecords = await _recordRepository.getTwoDaysRecordForObjectPredAsync(objekatId, tipUredjaja);

            return allRecords;
        }
        
        // ukupna potrosnja/proizvodnja po danu svih uredjaja za narednih 7/31 dana
        public async Task<List<GraphDateDto>> getRecordForSomePeriodForObjectPr(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePeriodForObjectPredAsync(objekatId, periodOfTime, tipUredjaja);

            return allRecords;
        }

        // ukupna potrosnja/proizvodnja po danu svih uredjaja za prethodnih 7/31 dana
        public async Task<List<GraphDateDto>> getRecordForSomePastPeriodForObjectPr(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePastPeriodForObjectPredAsync(objekatId, periodOfTime, tipUredjaja);

            return allRecords;
        }
        // ukupna predikcija potrosnje/proizvodnje po mesecu svih uredjaja za prethodnih 365 dana
        public async Task<List<GraphMonthDTO>> getRecordForPastYearForObjectPr(int objekatId, ETipUredjaj tipUredjaja)
        {
            List<GraphMonthDTO> allRecords = await _recordRepository.getRecordForPastYearForObjectPredAsync(objekatId, tipUredjaja);


            return allRecords;
        }

        // ------------------- U R E DJ A J I   V R E D N O S T ------------------ //

        //ukupna potrosnja/proizvodnja po satu za danas i sutra za odredjeni uredjaj 
        public async Task<List<GraphHourDTO>> getTwoDayRecordForDevicePred(int objekatUredjajId)
        {
            List<GraphHourDTO> allRecords = await _recordRepository.getTwoDaysRecordForDevicePredAsync(objekatUredjajId);

            return allRecords;
        }

        //ukupna predikcije potrosnja/proizvodnja po danu za 7/31 dana za odredjeni uredjaj
        public async Task<List<GraphDateDto>> getRecordForSomePeriodForDevicePred(int objekatUredjajId, PeriodOfTime periodOfTime)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePeriodForDevicePredAsync(objekatUredjajId, periodOfTime);


            return allRecords;
        }

        //ukupna predikcije potrosnja/proizvodnja po danu za prethodnih 7/31 dana za odredjeni uredjaj
        public async Task<List<GraphDateDto>> getRecordForSomePastPeriodForDevicePred(int objekatUredjajId, PeriodOfTime periodOfTime)
        {
            List<GraphDateDto> allRecords = await _recordRepository.getRecordForSomePastPeriodForDevicePredAsync(objekatUredjajId, periodOfTime);


            return allRecords;
        }

        // ukupna predikcija potrosnje/proizvodnje po mesecu oderedjenog uredjaja za prethodnih 365 dana
        public async Task<List<GraphMonthDTO>> getRecordForPastYearForDevicePred(int objekatUredjajId)
        {
            List<GraphMonthDTO> allRecords = await _recordRepository.getRecordForPastYearForDevicePredAsync(objekatUredjajId);


            return allRecords;
        }
        

    }

}
