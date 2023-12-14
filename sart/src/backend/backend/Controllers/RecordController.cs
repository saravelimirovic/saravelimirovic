using backend.Common;
using backend.DTOs;
using backend.Models;
using backend.services.interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class RecordController : ControllerBase
    {
        private readonly IRecordService _recordService;
        
        public RecordController(IRecordService recordService)
        {
            _recordService = recordService;
        }

        // ************************** D S O ************************** //

        // --------------------- U R E DJ A J I ------------------------//

        // broj svake od vrste uredjaja
        [HttpGet("dsoUredjajiBroj")]
        public Task<List<DeviceBrojDTO>> GetBrojSvakogUredjaja()
        {
            return _recordService.getBrojSvakogUredjaja();
        }

        // vraca ukupnu potrosnju i proizvodnju po svakom uredjaju
        // vraca broj koliko zapravo uredjaja trosi toliko, u smislu 4 klime ukupno trose toliko ??????????? nez sta je ovaj api
        [HttpGet("dsoUredjajiSumPotrPro")]
        public Task<List<DeviceBrojDTO>> GetPotrosnjaIProizvodnjaPoUredjaju()
        {
            return _recordService.getPotrosnjaIProizvodnjaPoUredjaju();
        }

        // vraca npr ukupnu potrosnju ves masine, u procentima, u odnosu na ukupnu potr -> tabela uz pie chart
        [HttpGet("dsoPotrPoUredjaju")]
        public Task<List<DeviceBrojDTO>> GetPotrosnjaPoVrstiUredjaja()
        {
            return _recordService.getPotrosnjaPoVrstiUredjaja();
        }

        // vraca npr ukupnu potrosnju ves masine, u procentima, u odnosu na ukupnu potr -> pie chart
        [HttpGet("dsoPotrPoUredjajuPieChart")]
        public Task<List<DeviceBrojDTO>> GetPotrosnjaPoVrstiUredjajaPieChart()
        {
            return _recordService.getPotrosnjaPoVrstiUredjajaPieChart();
        }

        // vraca npr ukupnu proizvodnju panela, u procentima, u odnosu na ukupnu proiz -> tabela uz pie chart
        [HttpGet("dsoProizPoUredjaju")]
        public Task<List<DeviceBrojDTO>> GetProizvodnjaPoVrstiUredjaja()
        {
            return _recordService.getProizvodnjaPoVrstiUredjaja();
        }
        // vraca npr ukupnu proizvodnju panela, u procentima, u odnosu na ukupnu proiz -> pie chart
        [HttpGet("dsoProizPoUredjajuPieChart")]
        public Task<List<DeviceBrojDTO>> GetProizvodnjaPoVrstiUredjajaPieChart()
        {
            return _recordService.getProizvodnjaPoVrstiUredjajaPieChart();
        }

        // vraca uredjaje korisnika po objektu, za poslat njegov id
        [HttpGet("dsoUredjajiKorisnika/{id}")]
        public Task<List<DeviceDsoDTO>> GetUredjajKorisnikaById(int id)
        {
            return _recordService.getUredjajKorisnikaById(id);
        }

        // vraca uredjaje objekta, koje dso moze da vidi
        [HttpGet("dsoUredjajiObjekta/{id}")]
        public Task<List<DeviceDsoDTO>> GetUredjajObjektaById(int id)
        {
            return _recordService.getUredjajObjektaById(id);
        }

        // vraca potrosnju uredjaja tog objekta, koje dso moze da vidi
        [HttpGet("dsoPotrosnjaUredjajaObjektaVidi/{idObjekta}")]
        public double GetPotrosnjaUredjajaObjektaVidi(int idObjekta)
        {
            return _recordService.getPotrProizUredjajaObjektaVidi(idObjekta, ETipUredjaj.Potrosac);
        }

        // vraca proizvodnju uredjaja tog objekta, koje dso moze da vidi
        [HttpGet("dsoProizvodnjaUredjajaObjektaVidi/{idObjekta}")]
        public double GetProizvodnjaUredjajaObjektaVidi(int idObjekta)
        {
            return _recordService.getPotrProizUredjajaObjektaVidi(idObjekta, ETipUredjaj.Proizvodjac);
        }



        // --------------------- S K L A D I S T A ------------------------//

        //zbir stanja u skladistima u svim objektima

        [HttpGet("dsoSkladistaAll")]
        public double GetTodaysSkladistaAll()
        {
            return _recordService.getTodaysSkladistaAll().Result;
        }
        

        // --------------------- P O T R O S NJ A ------------------------------ //

        // zbir potrošnje svih uredjaja za juce u svim objektima u jednom broju
        [HttpGet("dsoPotrosnjaAllYesterday")]
        public double GetYesterdayPotrosnjaForAllInOne()
        {
            return _recordService.getYesterdayRecordForAllInOne(ETipUredjaj.Potrosac).Result;
        }

        // zbir potrošnje svih uredjaja za danas u svim objektima u jednom broju
        [HttpGet("dsoPotrosnjaAll1d")]
        public double GetTodaysPotrosnjaForAllInOne()
        {
            return _recordService.getTodaysRecordForAllInOne(ETipUredjaj.Potrosac).Result;
        }

        // zbir potrošnje svih uredjaja za danas u svim objektima
        [HttpGet("dsoPotrosnja1d")]
        public List<GraphHourDTO> GetTodaysPotrosnjaForAll()
        {
            return _recordService.getTodaysRecordForAll(ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za danas u svim objektima
        [HttpGet("dsoPotrosnjaPredTodays")]
        public List<GraphHourDTO> GetTodaysPotrosnjaPred()
        {
            return _recordService.getTodaysRecordPr(ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za danas u svim objektima
        [HttpGet("dsoPotrosnjaPred1d")]
        public List<GraphHourDTO> GetTomorrowPotrosnjaPred()
        {
            return _recordService.getTomorrowRecordPr(ETipUredjaj.Potrosac).Result;
        }

        // zbir potrosnje svih uredjaja za 7 dana
        [HttpGet("dsoPotrosnja7d")]
        public List<GraphDateDto> GetPotrosnjaFor7Days()
        {
            return _recordService.getRecordForSomePeriod(PeriodOfTime.Week, ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrosnje svih uredjaja za prethodnih 7 dana
        [HttpGet("dsoPotrosnjaPredPast7d")]
        public List<GraphDateDto> GetPotrosnjaForPast7DaysPr()
        {
            return _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Week, ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrosnje svih uredjaja za narednih 7 dana
        [HttpGet("dsoPotrosnjaPred7d")]
        public List<GraphDateDto> GetPotrosnjaFor7DaysPr()
        {
            return _recordService.getRecordForSomePeriodPr(PeriodOfTime.Week, ETipUredjaj.Potrosac).Result;
        }

        // zbir potrosnje svih uredjaja za prethodnih 31 dana
        [HttpGet("dsoPotrosnja31d")]
        public List<GraphDateDto> GetPotrosnjaFor31Days()
        {
            return _recordService.getRecordForSomePeriod(PeriodOfTime.Month, ETipUredjaj.Potrosac).Result;
        }

        // zbir potrosnje svih uredjaja za prethodnih 31 dana
        [HttpGet("dsoPotrosnjaPredPast31d")]
        public List<GraphDateDto> GetPotrosnjaForPast31DaysPr()
        {
            return _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Month, ETipUredjaj.Potrosac).Result;
        }
        
        // zbir potrosnje svih uredjaja za prethodnih 365 dana
        [HttpGet("dsoPotrosnjaPast365d")]
        public List<GraphMonthDTO> GetPotrosnjaForPast365Days()
        {
            return _recordService.getRecordForPastYear(ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrosnje svih uredjaja za prethodnih 365 dana
        [HttpGet("dsoPotrosnjaPredPast365d")]
        public List<GraphMonthDTO> GetPotrosnjaForPast365DaysPr()
        {
            return _recordService.getRecordForPastYearPr(ETipUredjaj.Potrosac).Result;
        }

        // ------------------- P R O I Z V O D NJ A ---------------------------- //


        // zbir potrošnje svih uredjaja za juce u svim objektima u jednom broju
        [HttpGet("dsoProizvodnjaAllYesterday")]
        public double GetYesterdayProizvodnjaForAllInOne()
        {
            return _recordService.getYesterdayRecordForAllInOne(ETipUredjaj.Proizvodjac).Result;
        }

        // zbir potrošnje svih uredjaja za danas u svim objektima u jednom broju
        [HttpGet("dsoProizvodnjaAll1d")]
        public double GetTodaysProizvodnjaForAllInOne()
        {
            return _recordService.getTodaysRecordForAllInOne(ETipUredjaj.Proizvodjac).Result;
        }

        // zbir potrošnje svih uredjaja za danas u svim objektima
        [HttpGet("dsoProizvodnja1d")]
        public List<GraphHourDTO> GetTodaysProizvodnjaForAll()
        {
            return _recordService.getTodaysRecordForAll(ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije proizvodnje svih uredjaja za danas u svim objektima
        [HttpGet("dsoProizvodnjaPredTodays")]
        public List<GraphHourDTO> GetTodaysProizvodnjaPred()
        {
            return _recordService.getTodaysRecordPr(ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za danas u svim objektima
        [HttpGet("dsoProizvodnjaPred1d")]
        public List<GraphHourDTO> GetTomorrowProizvodnjaPred()
        {
            return _recordService.getTomorrowRecordPr(ETipUredjaj.Proizvodjac).Result;
        }

        // zbir proizvodnje svih uredjaja za 7 dana
        [HttpGet("dsoProizvodnja7d")]
        public List<GraphDateDto> GetProizvodnjaFor7Days()
        {
            return _recordService.getRecordForSomePeriod(PeriodOfTime.Week, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije proizvodnje svih uredjaja za prethodnih 7 dana
        [HttpGet("dsoProizvodnjaPredPast7d")]
        public List<GraphDateDto> GetProizvodnjaForPast7DaysPr()
        {
            return _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Week, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije proizvodnje svih uredjaja za 7 dana
        [HttpGet("dsoProizvodnjaPred7d")]
        public List<GraphDateDto> GetProizvodnjaFor7DaysPr()
        {
            return _recordService.getRecordForSomePeriodPr(PeriodOfTime.Week, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir proizvodnje svih uredjaja za 7 dana
        [HttpGet("dsoProizvodnja31d")]
        public List<GraphDateDto> GetProizvodnjaFor31Days()
        {
            return _recordService.getRecordForSomePeriod(PeriodOfTime.Month, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir proizvodnje svih uredjaja za prethodnih 31 dana
        [HttpGet("dsoProizvodnjaPredPast31d")]
        public List<GraphDateDto> GetProizvodnjaForPast31DaysPr()
        {
            return _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Month, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir proizvodnje svih uredjaja za prethodnih 365 dana
        [HttpGet("dsoProizvodnjaPast365d")]
        public List<GraphMonthDTO> GetProizvodnjaForPast365Days()
        {
            return _recordService.getRecordForPastYear(ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije proizvodnje svih uredjaja za prethodnih 365 dana
        [HttpGet("dsoProizvodnjaPredPast365d")]
        public List<GraphMonthDTO> GetProizvodnjaForPast365DaysPr()
        {
            return _recordService.getRecordForPastYearPr(ETipUredjaj.Proizvodjac).Result;
        }


        // ************************** P R O S U M E R ************************** //

        // --------------------- U R E DJ A J I ------------------------//

        // procentualni prikaz proizvodnje u odnosu na juce za jedan uredjaj
        [HttpPost("prosRecordPercent1d")]
        public double GetPercentRecordForDeviceForOneDay(int objekatUredjajId)
        {
            return _recordService.getPercentRecordForDeviceForOneDay(objekatUredjajId).Result;
        }
        // procentualni prikaz odnosa proizvodnje prosla dve nedelje za jedan uredjaj
        [HttpPost("prosRecordPercent7d")]
        public double GetPercentRecordForDeviceFor7Days(int objekatUredjajId)
        {
            return _recordService.getPercentRecordForDeviceForSomePeriod(objekatUredjajId, PeriodOfTime.Week).Result;
        }
        // procentualni prikaz odnosa proizvodnje prosla dva meseca za jedan uredjaj
        [HttpPost("prosRecordPercent31d")]
        public double GetPercentRecordForDeviceFor31Days(int objekatUredjajId)
        {
            return _recordService.getPercentRecordForDeviceForSomePeriod(objekatUredjajId, PeriodOfTime.Month).Result;
        }

        // zbir proizvodnje/potrosnje odredjenog uredjaja za danas 
        [HttpPost("prosRecordDevice1d")]
        public List<GraphHourDTO> GetTodaysRecordForDevice(int objekatUredjajId)
        {
            return _recordService.getTodaysRecordForDevice(objekatUredjajId).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za sutra u jednom objektu
        [HttpPost("prosRecordPred2d")]
        public List<GraphHourDTO> GetTomorrowRecordPredForDevice(int objekatUredjajId)
        {
            return _recordService.getTwoDayRecordForDevicePred(objekatUredjajId).Result;
        }

        // zbir proizvodnje/potrosnje odredjenog uredjaja za proslih 7 dana 
        [HttpPost("prosRecordDevice7d")]
        public List<GraphDateDto> GetRecordFor7DaysForDevice(int objekatUredjajId)
        {
            return _recordService.getRecordForSomePeriodForDevice(objekatUredjajId, PeriodOfTime.Week).Result;
        }

        // zbir predikcije proizvodnje/potrosnje odredjenog uredjaja za narednih 7 dana 
        [HttpPost("prosRecordDevicePred7d")]
        public List<GraphDateDto> GetRecordFor7DaysForDevicePred(int objekatUredjajId)
        {
            return _recordService.getRecordForSomePeriodForDevicePred(objekatUredjajId, PeriodOfTime.Week).Result;
        }

        // zbir predikcije proizvodnje/potrosnje odredjenog uredjaja za proslih 7 dana 
        [HttpPost("prosRecordDevicePredPast7d")]
        public List<GraphDateDto> GetRecordForPast7DaysForDevicePred(int objekatUredjajId)
        {
            return _recordService.getRecordForSomePastPeriodForDevicePred(objekatUredjajId, PeriodOfTime.Week).Result;
        }

        // zbir proizvodnje/potrosnje odredjenog uredjaja za proslih 31 dana 
        [HttpPost("prosRecordDevice31d")]
        public List<GraphDateDto> GetRecordFor31DaysForDevice(int objekatUredjajId)
        {
            return _recordService.getRecordForSomePeriodForDevice(objekatUredjajId, PeriodOfTime.Month).Result;
        }

        // zbir predikcije proizvodnje/potrosnje odredjenog uredjaja za prethodnih 31 dana 
        [HttpPost("prosRecordDevicePredPast31d")]
        public List<GraphDateDto> GetRecordForPast31DaysForDevicePred(int objekatUredjajId)
        {
            return _recordService.getRecordForSomePastPeriodForDevicePred(objekatUredjajId, PeriodOfTime.Month).Result;
        }

        // zbir proizvodnje/potrosnje odredjenog uredjaja za proslih 365 dana 
        [HttpPost("prosRecordDevice365d")]
        public List<GraphMonthDTO> GetRecordForPastYearForDevice(int objekatUredjajId)
        {
            return _recordService.getRecordForPastYearForDevice(objekatUredjajId).Result;
        }

        // zbir predikcije potrosnje svih uredjaja za prethodnih 365 dana u jednom objektu
        [HttpPost("prosRecordDevicePredPast365d")]
        public List<GraphMonthDTO> GetPotrosnjaForPastYearForDevicePred(int objekatUredjajId)
        {
            return _recordService.getRecordForPastYearForDevicePred(objekatUredjajId).Result;
        }

        // --------------------- P O T R O S NJ A ------------------------------ //

        // procentualni prikaz potrosnje u odnosu na juce
        [HttpPost("prosPotrosnjaPercent1d")]
        public double GetPercentPotrosnjaForObjectForOneDay(int objekatId)
        {
            return _recordService.getPercentRecordForObjectForOneDay(objekatId, ETipUredjaj.Potrosac).Result;
        }
        
        // zbir potrošnje svih uredjaja za danas u jednom objektu
        [HttpPost("prosPotrosnja1d")]
        public List<GraphHourDTO> GetTodaysPotrosnjaForObject(int objekatId)
        {
            return _recordService.getTodaysRecordForObject(objekatId, ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za danas i sutra u jednom objektu
        [HttpPost("prosPotrosnjaPred2d")]
        public List<GraphHourDTO> GetTwoDaysPotrosnjaPredForObject(int objekatId)
        {
            return _recordService.getTwoDaysRecordForObjectPr(objekatId, ETipUredjaj.Potrosac).Result;
        }

        // procentualni prikaz odnosa potrosnje prosla dve nedelje
        [HttpPost("prosPotrosnjaPercent7d")]
        public double GetPercentPotrosnjaForObjectFor7Days(int objekatId)
        {
            return _recordService.getPercentRecordForObjectForSomePeriod(objekatId, ETipUredjaj.Potrosac, PeriodOfTime.Week).Result;
        }

        // zbir potrošnje svih uredjaja za 7 dana u jednom objektu
        [HttpPost("prosPotrosnja7d")]
        public List<GraphDateDto> GetPotrosnjaFor7DaysForObject(int objekatId)
        {
            return _recordService.getRecordForSomePeriodForObject(objekatId, PeriodOfTime.Week, ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za narednih 7 dana u jednom objektu
        [HttpPost("prosPotrosnjaPred7d")]
        public List<GraphDateDto> GetPotrosnjaFor7DaysPredForObject(int objekatId)
        {
            return _recordService.getRecordForSomePeriodForObjectPr(objekatId, PeriodOfTime.Week, ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za prethodnih 7 dana u jednom objektu
        [HttpPost("prosPotrosnjaPredPast7d")]
        public List<GraphDateDto> GetPotrosnjaForPast7DaysPredForObject(int objekatId)
        {
            return _recordService.getRecordForSomePastPeriodForObjectPr(objekatId, PeriodOfTime.Week, ETipUredjaj.Potrosac).Result;
        }

        // procentualni prikaz odnosa potrosnje prosla dva meseca
        [HttpPost("prosPotrosnjaPercent31d")]
        public double GetPercentPotrosnjaForObjectFor31Days(int objekatId)
        {
            return _recordService.getPercentRecordForObjectForSomePeriod(objekatId, ETipUredjaj.Potrosac, PeriodOfTime.Month).Result;
        }

        // zbir potrošnje svih uredjaja za 31 dana u jednom objektu
        [HttpPost("prosPotrosnja31d")]
        public List<GraphDateDto> GetPotrosnjaFor30DaysForObject(int objekatId)
        {
            return _recordService.getRecordForSomePeriodForObject(objekatId, PeriodOfTime.Month, ETipUredjaj.Potrosac).Result;
        }

        // zbir predikcije potrošnje svih uredjaja za proslih 31 dana u jednom objektu
        [HttpPost("prosPotrosnjaPredPast31d")]
        public List<GraphDateDto> GetPotrosnjaForPast31DaysPredForObject(int objekatId)
        {
            return _recordService.getRecordForSomePastPeriodForObjectPr(objekatId, PeriodOfTime.Month, ETipUredjaj.Potrosac).Result;
        }
        
        // zbir potrošnje svih uredjaja za prethodnih 365 dana u jednom objektu
        [HttpPost("prosPotrosnjaPast365d")]
        public List<GraphMonthDTO> GetPotrosnjaForPastYearForObject(int objekatId)
        {
            return _recordService.getRecordForPastYearForObject(objekatId, ETipUredjaj.Potrosac).Result;
        }
        
        // zbir predikcije potrosnje svih uredjaja za prethodnih 365 dana u jednom objektu
        [HttpPost("prosPotrosnjaPredPast365d")]
        public List<GraphMonthDTO> GetPotrosnjaForPastYearPredForObject(int objekatId)
        {
            return _recordService.getRecordForPastYearForObjectPr(objekatId, ETipUredjaj.Potrosac).Result;
        }

        // ------------------- P R O I Z V O D NJ A ---------------------------- //

        // procentualni prikaz proizvodnje u odnosu na juce
        [HttpPost("prosProizvodnjaPercent1d")]
        public double GetPercentProizvodnjaForObjectForOneDay(int objekatId)
        {
            return _recordService.getPercentRecordForObjectForOneDay(objekatId, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir proizvodnje svih uredjaja za danas u jednom objektu
        [HttpPost("prosProizvodnja1d")]
        public List<GraphHourDTO> GetTodaysProizvodnjaForObject(int objekatId)
        {
            return _recordService.getTodaysRecordForObject(objekatId, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije proizvodnje svih uredjaja za danas i sutra u jednom objektu
        [HttpPost("prosProizvodnjaPred2d")]
        public List<GraphHourDTO> GetTwoDaysProizvodnjaPredForObject(int objekatId)
        {
            return _recordService.getTwoDaysRecordForObjectPr(objekatId, ETipUredjaj.Proizvodjac).Result;
        }

        // procentualni prikaz odnosa proizvodnje prosla dve nedelje
        [HttpPost("prosProizvodnjaPercent7d")]
        public double GetPercentProizvodnjaForObjectFor7Days(int objekatId)
        {
            return _recordService.getPercentRecordForObjectForSomePeriod(objekatId, ETipUredjaj.Proizvodjac, PeriodOfTime.Week).Result;
        }

        // zbir proizvodnje svih uredjaja za 7 dana u jednom objektu
        [HttpPost("prosProizvodnja7d")]
        public List<GraphDateDto> GetProizvodnjaFor7DaysForObject(int objekatId)
        {
            return _recordService.getRecordForSomePeriodForObject(objekatId, PeriodOfTime.Week, ETipUredjaj.Proizvodjac).Result;
        }


        // zbir predikcije proizvodnje svih uredjaja za narednih 7 dana u jednom objektu
        [HttpPost("prosProizvodnjaPred7d")]
        public List<GraphDateDto> GetProizvodnjaFor7DaysPredForObject(int objekatId)
        {
            return _recordService.getRecordForSomePeriodForObjectPr(objekatId, PeriodOfTime.Week, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije proizvodnje svih uredjaja za prethodnih 7 dana u jednom objektu
        [HttpPost("prosProizvodnjaPredPast7d")]
        public List<GraphDateDto> GetProizvodnjaForPast7DaysPredForObject(int objekatId)
        {
            return _recordService.getRecordForSomePastPeriodForObjectPr(objekatId, PeriodOfTime.Week, ETipUredjaj.Proizvodjac).Result;
        }

        // procentualni prikaz odnosa proizvodnje prosla dva meseca
        [HttpPost("prosProizvodnjaPercent31d")]
        public double GetPercentProizvodnjaForObjectFor31Days(int objekatId)
        {
            return _recordService.getPercentRecordForObjectForSomePeriod(objekatId, ETipUredjaj.Proizvodjac, PeriodOfTime.Month).Result;
        }

        // zbir proizvodnje svih uredjaja za 31 dana u jednom objektu
        [HttpPost("prosProizvodnja31d")]
        public List<GraphDateDto> GetProizvodnjaFor31DaysForObject(int objekatId)
        {
            return _recordService.getRecordForSomePeriodForObject(objekatId, PeriodOfTime.Month, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir predikcije proizvodnje svih uredjaja za prethodnih 31 dana u jednom objektu
        [HttpPost("prosProizvodnjaPredPast31d")]
        public List<GraphDateDto> GetProizvodnjaForPast31DaysForObject(int objekatId)
        {
            return _recordService.getRecordForSomePastPeriodForObjectPr(objekatId, PeriodOfTime.Month, ETipUredjaj.Proizvodjac).Result;
        }

        // zbir proizvodnje svih uredjaja za prethodnih 365 dana u jednom objektu
        [HttpPost("prosProizvodnjaPast365d")]
        public List<GraphMonthDTO> GetProizvodnjaForPastYearForObject(int objekatId)
        {
            return _recordService.getRecordForPastYearForObject(objekatId, ETipUredjaj.Proizvodjac).Result;
        }
        
        // zbir predikcije proizvodnje svih uredjaja za prethodnih 365 dana u jednom objektu
        [HttpPost("prosProizvodnjaPredPast365d")]
        public List<GraphMonthDTO> GetProizvodnjaForPastYearPredForObject(int objekatId)
        {
            return _recordService.getRecordForPastYearForObjectPr(objekatId, ETipUredjaj.Proizvodjac).Result;
        }
    }
}
