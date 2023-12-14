using backend.Context;
using backend.Models;
using backend.Common;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;
using static System.Runtime.InteropServices.JavaScript.JSType;
using backend.DTOs;
using backend.Controllers;

namespace backend.repositroy
{
    public class RecordRepository : IRecordRepository
    {
        private readonly AppDbContext _context;

        public RecordRepository(AppDbContext context)
        {
            _context = context;
        }


        // ----------------------------------------- I S T O R I J A  ----------------------------------------- //

        // ************************** D S O ************************** //

        // --------------------- U R E DJ A J I ------------------------//

        // vraca broj svake vrste uredjaja
        public async Task<List<DeviceBrojDTO>> getBrojSvakogUredjaja()
        {
            var query = _context.ObjekatUredjaj
                  .Include(u => u.Uredjaj)
                       .ThenInclude(u => u.VrstaUredjaja)
                  .GroupBy(p => new { p.UredjajId, p.Uredjaj.VrstaUredjaja.Naziv })
                  .Select(g => new DeviceBrojDTO(g.Key.Naziv, g.Count()))
                  .ToList();

            return query;
        }

        // vraca ukupnu potrosnju i proizvodnju po svakom uredjaju
        public async Task<List<DeviceBrojDTO>> getPotrosnjaIProizvodnjaPoUredjaju()
        {
            var query = _context.IstorijaP
                        .Include(o => o.ObjekatUredjaj)
                        .ThenInclude(o => o.Uredjaj)
                        .ThenInclude(v => v.VrstaUredjaja)
                        .GroupBy(p => new { p.ObjekatUredjaj.UredjajId, p.ObjekatUredjaj.Uredjaj.VrstaUredjaja.Naziv })
                        .Select(q => new DeviceBrojDTO(q.Key.Naziv, 0, q.Where(x => x.ObjekatUredjaj.Uredjaj.TipUredjajaId == 1).Sum(i => i.VrednostRealizacije),
                                                                                 q.Where(x => x.ObjekatUredjaj.Uredjaj.TipUredjajaId == 2).Sum(i => i.VrednostRealizacije) / 1000))
                        .ToList();

            return query;
        }

        // vraca npr kolko su sve masine potrosile, procenat se vraca -> pie chart
        public async Task<List<DeviceBrojDTO>> getPotrosnjaPoVrstiUredjaja()
        {
            var ukupnaPotrosnja = getUkupnaPotrosnja();

            DateTime from = DateTime.Today.AddDays(-(int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);

            var query = await _context.IstorijaP
                        .Include(o => o.ObjekatUredjaj)
                        .ThenInclude(o => o.Uredjaj)
                        .ThenInclude(v => v.VrstaUredjaja)
                        .Where(x => x.Datum >= fromDate && x.Datum <= tempDate
                                    && x.ObjekatUredjaj.Uredjaj.TipUredjajaId == 1)
                        .GroupBy(p => new { p.ObjekatUredjaj.UredjajId, p.ObjekatUredjaj.Uredjaj.VrstaUredjaja.Naziv })
                        .Select(q => new DeviceBrojDTO(q.Key.Naziv, 0, q.Sum(i => i.VrednostRealizacije) / 1000, 0, ukupnaPotrosnja))
                        .ToListAsync();

            query = query.OrderByDescending(istorija => istorija.Procenat).ToList();

            return query;
        }

        // vraca npr koliko su svi paneli proizveli, procenat se vraca -> pie chart
        public async Task<List<DeviceBrojDTO>> getProizvodnjaPoVrstiUredjaja()
        {
            DateTime from = DateTime.Today.AddDays(-(int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);

            var ukupnaProizvodnja = getUkupnaProizvodnja();
            var query = await _context.IstorijaP
                        .Include(o => o.ObjekatUredjaj)
                        .ThenInclude(o => o.Uredjaj)
                        .ThenInclude(v => v.VrstaUredjaja)
                        .Where(x => x.Datum >= fromDate && x.Datum <= tempDate &&
                                    x.ObjekatUredjaj.Uredjaj.TipUredjajaId == 2)
                        .GroupBy(p => new { p.ObjekatUredjaj.UredjajId, p.ObjekatUredjaj.Uredjaj.VrstaUredjaja.Naziv })
                        .Select(q => new DeviceBrojDTO(q.Key.Naziv, 0, 0, q.Sum(i => i.VrednostRealizacije) / 1000, ukupnaProizvodnja))
                        .ToListAsync();

            query = query.OrderByDescending(istorija => istorija.Procenat).ToList();

            return query;
        }

        // vraca uredjaje korisnika za njegov id, koje dso moze da vidi
        public async Task<List<DeviceDsoDTO>> getUredjajKorisnikaById(int id)
        {
            var query = await _context.ObjekatUredjaj
                        .Where(u => u.Dozvola.ToUpper() == "DA")
                        .Include(o => o.Objekat)
                        .ThenInclude(u => u.Ulica)
                        .ThenInclude(n => n.Naselje)
                        .ThenInclude(g => g.Grad)
                        .Where(i => i.Objekat.KorisnikId == id && i.Objekat.Ulica.Id == i.Objekat.UlicaId)
                        .Include(u => u.Uredjaj)
                        .Include(t => t.Uredjaj.TipUredjaja)
                        .Include(v => v.Uredjaj.VrstaUredjaja)
                        .Select(q => new DeviceDsoDTO(q.ObjekatId, q.Objekat.Ulica.Naziv, q.Objekat.AdresniBroj, q.Objekat.Ulica.Naselje.Grad.Naziv, q.Uredjaj.Id, q.Uredjaj.Naziv, q.Uredjaj.TipUredjaja.Naziv, q.Uredjaj.VrstaUredjaja.Naziv))
                        .ToListAsync();

            return query;
        }

        // vraca uredjaje objekta, koje dso moze da vidi
        public async Task<List<DeviceDsoDTO>> getUredjajObjektaById(int id)
        {
            var query = await _context.ObjekatUredjaj
                        .Where(u => u.Dozvola.ToUpper() == "DA")
                        .Include(room => room.Prostorija)
                        .Include(o => o.Objekat)
                        .ThenInclude(u => u.Ulica)
                        .ThenInclude(n => n.Naselje)
                        .ThenInclude(g => g.Grad)
                        .Where(i => i.Objekat.Id == id && i.Objekat.Ulica.Id == i.Objekat.UlicaId)
                        .Include(u => u.Uredjaj)
                        .Include(t => t.Uredjaj.TipUredjaja)
                        .Include(v => v.Uredjaj.VrstaUredjaja)
                        .Select(q => new DeviceDsoDTO(q.ObjekatId, q.Objekat.Ulica.Naziv, q.Objekat.AdresniBroj, q.Objekat.Ulica.Naselje.Grad.Naziv, q.Uredjaj.Id, q.Uredjaj.Naziv, q.Uredjaj.TipUredjaja.Naziv, q.Uredjaj.VrstaUredjaja.Naziv, q.Id, q.Prostorija.Naziv))
                        .ToListAsync();

            return query;
        }

        // vraca potrosnju uredjaja tog objekta, koje dso moze da vidi
        public double getPotrProizUredjajaObjektaVidi(int idObjekta, ETipUredjaj tipUredjaja)
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            var lista = _context.IstorijaP
               .Include(istorija => istorija.ObjekatUredjaj)
               .Where(u => u.ObjekatUredjaj.Dozvola.ToUpper() == "DA" && u.ObjekatUredjaj.ObjekatId == idObjekta)
               .Include(istorija => istorija.ObjekatUredjaj.Uredjaj)
               .Where(istorija => ((istorija.Datum.Day < date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year)
                                  || (istorija.Datum.Day == date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year && istorija.Vreme < time2))
                                  && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(istorija => istorija.Datum.Month)
               .Select(istorija => istorija.Sum(istorija => istorija.VrednostRealizacije) / 1000)
               .FirstOrDefault();

            return Math.Round(lista, 2);
        }

        public double getUkupnaPotrosnja()
        {
            DateTime from = DateTime.Today.AddDays(-(int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);

            var query = _context.IstorijaP
                        .Include(o => o.ObjekatUredjaj)
                        .ThenInclude(o => o.Uredjaj)
                        .ThenInclude(v => v.VrstaUredjaja)
                        .Where(x => x.Datum >= fromDate && x.Datum <= tempDate
                                    && x.ObjekatUredjaj.Uredjaj.TipUredjajaId == 1)
                        .Sum(i => i.VrednostRealizacije);

            return query / 1000;
        }

        public double getUkupnaProizvodnja()
        {
            DateTime from = DateTime.Today.AddDays(-(int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);

            var query = _context.IstorijaP
                        .Include(o => o.ObjekatUredjaj)
                        .ThenInclude(o => o.Uredjaj)
                        .ThenInclude(v => v.VrstaUredjaja)
                        .Where(x => x.Datum >= fromDate && x.Datum <= tempDate
                                    && x.ObjekatUredjaj.Uredjaj.TipUredjajaId == 2)
                        .Sum(i => i.VrednostRealizacije);

            return query / 1000;
        }



        // --------------------- S K L A D I S T A ------------------------//

        //stanje svih skladista u sistemu
        public async Task<List<ObjekatSkladiste>> getTodaysSkladistaAllAsync()
        {
            List<ObjekatSkladiste> lista = await _context.ObjekatSkladiste.ToListAsync();

            return lista;
        }


        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //

        // potrosnja/proizvodnja svih uredjaja u jucerasnjom danu do sadasnjeg sata u jednom broju
        public async Task<double> getYesterdayRecordForAllAsync(ETipUredjaj tipUredjaja)
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            date = date.AddDays(-1);
            TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            double vrednost = await _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => istorija.Vreme <= time2
                                    && istorija.Datum == date
                                     && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
                .GroupBy(istorija => istorija.Datum)
                .Select(istorija => istorija.Sum(istorija => istorija.VrednostRealizacije))
                .FirstOrDefaultAsync();


            return vrednost;
        }

        // potrosnja/proizvodnja svih uredjaja u danasnjem danu do sadasnjeg sata u jednom broju
        public async Task<double> getTodayRecordForAllAsync(ETipUredjaj tipUredjaja)
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            double vrednost = await _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => istorija.Vreme <= time2
                                    && istorija.Datum == date
                                     && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
                .GroupBy(istorija => istorija.Datum)
                .Select(istorija => istorija.Sum(istorija => istorija.VrednostRealizacije))
                .FirstOrDefaultAsync();


            return vrednost;
        }

        // potrosnja/proizvodnja svih uredjaja u danansnjem danu do sadasnjeg sata po sa satu
        public async Task<List<GraphHourDTO>> getTodaysRecordForAllAsync(ETipUredjaj tipUredjaja)
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            List<GraphHourDTO> lista = await _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => istorija.Vreme <= time2
                                    && istorija.Datum == date
                                     && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
                .GroupBy(istorija => new { istorija.Vreme, istorija.Datum })
                .Select(istorija => new GraphHourDTO(istorija.Key.Vreme, istorija.Key.Datum, Math.Round(istorija.Sum(istorija=>istorija.VrednostRealizacije)/1000, 2)))
                .ToListAsync();


            return lista;
        }

        // potrosnja/proizvodnja svih uredjaja u prethodnih 7/31 dan po datumu (danu)
        public async Task<List<GraphDateDto>> getRecordForSomePeriodAsync(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            DateTime from = DateTime.Today.AddDays(-(int)periodOfTime);
            DateTime temp = DateTime.Now;
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            List<GraphDateDto> lista = await _context.IstorijaP
               .Include(istorija => istorija.ObjekatUredjaj)
               .ThenInclude(istorija => istorija.Uredjaj)
               .Where(istorija => ((istorija.Datum >= fromDate && istorija.Datum < tempDate) || (istorija.Datum == tempDate && istorija.Vreme < tempTime))
                                  && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(istorija => istorija.Datum)
               .Select(istorija => new GraphDateDto(istorija.Key, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije)/1000, 2)))
               .ToListAsync();

            return lista;
        }

        // potrosnja/proizvodnja svih uredjaja u prethodnih 365 dana po mesecu
        public async Task<List<GraphMonthDTO>> getRecordForPastYearAsync(ETipUredjaj tipUredjaja)
        {
            DateTime from = DateTime.Today.AddDays(-(int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, 01);
            List<GraphMonthDTO> lista = await _context.IstorijaP
               .Include(istorija => istorija.ObjekatUredjaj)
               .ThenInclude(istorija => istorija.Uredjaj)
               .Where(istorija => istorija.Datum >= fromDate && istorija.Datum <= tempDate
                                  && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(istorija => new { istorija.Datum.Month, istorija.Datum.Year})
               .Select(istorija => new GraphMonthDTO(istorija.Key.Month, istorija.Key.Year, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije) / 1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(istorija => istorija.Year).ThenBy(istorija => istorija.Month).ToList();

            return lista;
        }



        // ************************** P R O S U M E R ************************** //


        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //


        //ukupna potrosnja/proizvodnja po satu za sve uredjaje za odredjeni objekat po satu
        public async Task<List<GraphHourDTO>> getTodaysRecordForObjectAsync(int objekatId, ETipUredjaj tipUredjaja)
        {
        DateTime temp = DateTime.Now;
        DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
        TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
        List<GraphHourDTO> lista = await _context.IstorijaP
            .Include(istorija => istorija.ObjekatUredjaj)
            .ThenInclude(istorija => istorija.Uredjaj)
            .Where(istorija => istorija.Vreme <= tempTime && istorija.Datum == tempDate
                               && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                               && istorija.ObjekatUredjaj.ObjekatId == objekatId)
            .GroupBy(istorija => new { istorija.Vreme, istorija.Datum })
            .Select(istorija => new GraphHourDTO(istorija.Key.Vreme, istorija.Key.Datum, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije) / 1000, 2)))
            .ToListAsync();


            return lista;
        }

        //ukupna potrosnja/proizvodnja za sve uredjaje za odredjeni objekat u jednom broju
        public async Task<double> getPercentRecordForObjectForOneDayAsync(int objekatId, ETipUredjaj tipUredjaja, int days)
        {
            DateTime temp = DateTime.Now.AddDays(-days);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            double result = await _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => istorija.Vreme <= tempTime && istorija.Datum == tempDate
                                   && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                   && istorija.ObjekatUredjaj.ObjekatId == objekatId)
                .GroupBy(istorija => new { istorija.Datum, istorija.Vreme })
                .Select(istorija => Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije), 2))
                .FirstOrDefaultAsync();

            return result;
        }

        //ukupna potrosnja/proizvodnja za sve uredjaje za odredjeni objekat u jednom broju za neki period
        public async Task<double> getPercentRecordForObjectForSomePeriodAsync(int objekatId, ETipUredjaj tipUredjaja, int fromPeriodOfTime, PeriodOfTime toPeriodOfTime)
        {
            DateTime temp = DateTime.Now.AddDays(-(int)toPeriodOfTime);
            DateTime from = DateTime.Today.AddDays(fromPeriodOfTime * 2);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            double result = await _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => ((istorija.Datum >= fromDate && istorija.Datum < tempDate) || (istorija.Datum == tempDate && istorija.Vreme < tempTime))
                                   && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                   && istorija.ObjekatUredjaj.ObjekatId == objekatId)
                .GroupBy(istorija => new { istorija.Datum, istorija.Vreme})
                .Select(istorija => Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije), 2))
                .FirstOrDefaultAsync();

            return result;
        }

        // zbir potrosnje/proizvodnje svih uredjaja u određenom objektu u prethodnih 7/31 dana po datumu(danu)
        public async Task<List<GraphDateDto>> getRecordForSomePeriodForObjectAsync(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            DateTime from = DateTime.Today.AddDays(-(int)periodOfTime);
            DateTime temp = DateTime.Now;
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            List<GraphDateDto> lista = await _context.IstorijaP
               .Include(istorija => istorija.ObjekatUredjaj)
               .ThenInclude(istorija => istorija.Uredjaj)
               .Where(istorija => ((istorija.Datum >= fromDate && istorija.Datum < tempDate) || (istorija.Datum == tempDate && istorija.Vreme < tempTime))
                                  && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                  && istorija.ObjekatUredjaj.ObjekatId == objekatId)
               .GroupBy(istorija => istorija.Datum)
               .Select(istorija => new GraphDateDto(istorija.Key, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije) / 1000, 2)))
               .ToListAsync();

            return lista;
        }

        // potrosnja/proizvodnja svih uredjaja u odredjenom objektu u prethodnih 365 dana po mesecu
        public async Task<List<GraphMonthDTO>> getRecordForPastYearForObjectAsync(int objekatId, ETipUredjaj tipUredjaja)
        {
            DateTime from = DateTime.Today.AddDays(-(int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, 01);
            List<GraphMonthDTO> lista = await _context.IstorijaP
               .Include(istorija => istorija.ObjekatUredjaj)
               .ThenInclude(istorija => istorija.Uredjaj)
               .Where(istorija => istorija.Datum >= fromDate && istorija.Datum <= tempDate
                                  && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                  && istorija.ObjekatUredjaj.ObjekatId == objekatId)
               .GroupBy(istorija => new { istorija.Datum.Month, istorija.Datum.Year })
               .Select(istorija => new GraphMonthDTO(istorija.Key.Month, istorija.Key.Year, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije) / 1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(istorija => istorija.Year).ThenBy(istorija => istorija.Month).ToList();

            return lista;
        }


        // ------------------- U R E DJ A J I   V R E D N O S T ------------------ //

        // procentualni prikaz proizvodnje u odnosu juce za jedan uredjaj
        public async Task<double> getPercentRecordForDeviceForOneDayAsync(int objekatUredjajId, int days)
        {
            DateTime temp = DateTime.Now.AddDays(-days);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            double result = await _context.IstorijaP
                .Where(istorija => istorija.Vreme <= tempTime && istorija.Datum == tempDate
                                   && istorija.ObjekatUredjajId == objekatUredjajId)
                .GroupBy(istorija => new { istorija.Datum, istorija.Vreme })
                .Select(istorija => Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije), 2))
                .FirstOrDefaultAsync();

            return result;
        }

        // procentualni prikaz proizvodnje u odnosu na neki vremenski period za jedan uredjaj
        public async Task<double> getPercentRecordForDeviceForSomePeriodAsync(int objekatUredjajId, int fromPeriodOfTime, PeriodOfTime toPeriodOfTime)
        {
            DateTime temp = DateTime.Now.AddDays(-(int)toPeriodOfTime);
            DateTime from = DateTime.Today.AddDays(fromPeriodOfTime * 2);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            double result = await _context.IstorijaP
                .Where(istorija => ((istorija.Datum >= fromDate && istorija.Datum < tempDate) || (istorija.Datum == tempDate && istorija.Vreme < tempTime))
                                   && istorija.ObjekatUredjajId == objekatUredjajId)
                .GroupBy(istorija => new { istorija.Datum, istorija.Vreme })
                .Select(istorija => Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije), 2))
                .FirstOrDefaultAsync();

            return result;
        }
        // zbir potrosnje/proizvodnje jednog odredjenog uredjaja u prethodnih 7/31 dan

        public async Task<List<GraphDateDto>> getRecordForSomePeriodForDeviceAsync(int objekatUredjajId, PeriodOfTime periodOfTime)
        {
            DateTime from = DateTime.Today.AddDays(-(int)periodOfTime);
            DateTime temp = DateTime.Now;
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            List<GraphDateDto> lista = await _context.IstorijaP
                .Where(istorija => ((istorija.Datum >= fromDate && istorija.Datum < tempDate) || (istorija.Datum == tempDate && istorija.Vreme < tempTime))
                                    && istorija.ObjekatUredjajId == objekatUredjajId)
                .GroupBy(istorija => istorija.Datum)
                .Select(istorija => new GraphDateDto(istorija.Key, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije)/1000,2)))
                .ToListAsync();

            return lista;
        }

        //ukupna potrosnja/proizvodnja po satu za odredjeni uredjaj po satu
        public async Task<List<GraphHourDTO>> getTodaysRecordForDeviceAsync(int objekatUredjajId)
        {
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            List<GraphHourDTO> lista = await _context.IstorijaP
                .Where(istorija => istorija.Vreme <= tempTime && istorija.Datum == tempDate
                                   && istorija.ObjekatUredjajId == objekatUredjajId)
                .GroupBy(istorija => new { istorija.Vreme, istorija.Datum })
                .Select(istorija => new GraphHourDTO(istorija.Key.Vreme, istorija.Key.Datum, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije)/1000, 2)))
                .ToListAsync();


            return lista;
        }

        // potrosnja/proizvodnja za odredjeni uredjaj u prethodnih 365 dana po mesecu
        public async Task<List<GraphMonthDTO>> getRecordForPastYearForDeviceAsync(int objekatUredjajId)
        {
            DateTime from = DateTime.Today.AddDays(-(int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, 01);
            List<GraphMonthDTO> lista = await _context.IstorijaP
               .Where(istorija => istorija.Datum >= fromDate && istorija.Datum <= tempDate
                                  && istorija.ObjekatUredjajId == objekatUredjajId)
               .GroupBy(istorija => new { istorija.Datum.Month, istorija.Datum.Year })
               .Select(istorija => new GraphMonthDTO(istorija.Key.Month, istorija.Key.Year, Math.Round(istorija.Sum(istorija => istorija.VrednostRealizacije)/1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(istorija => istorija.Year).ThenBy(istorija => istorija.Month).ToList();

            return lista;
        }

















        // ----------------------------------------- P R E D I K C I J A  ----------------------------------------- //


        // ************************** D S O ************************** //

        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //

        // predikcija ukupne potrosnje/proizvodnje za danas za sve uredjaje za sve objekte po satu
        public async Task<List<GraphHourDTO>> getTodaysRecordPredAsync(ETipUredjaj tipUredjaja)
        {
            DateTime to = DateTime.Today.AddDays((int)PeriodOfTime.Year);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);

            List<GraphHourDTO> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum == toDate 
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(predikcija => new { predikcija.Vreme, predikcija.Datum })
               .Select(predikcija => new GraphHourDTO(predikcija.Key.Vreme, predikcija.Key.Datum.AddDays(-(int)PeriodOfTime.Year), Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije) / 1000, 2)))
               .ToListAsync();

            return lista;
        }

        // predikcija ukupne potrosnje/proizvodnje za sutra za sve uredjaje za sve objekte po satu
        public async Task<List<GraphHourDTO>> getTomorrowRecordPredAsync(ETipUredjaj tipUredjaja)
        {
            DateTime to = DateTime.Today.AddDays((int)PeriodOfTime.Year + 1);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);

            List<GraphHourDTO> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum == toDate
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(predikcija => new { predikcija.Vreme, predikcija.Datum })
               .Select(predikcija => new GraphHourDTO(predikcija.Key.Vreme, predikcija.Key.Datum.AddDays(-(int)PeriodOfTime.Year), Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije) / 1000, 2)))
               .ToListAsync();

            return lista;
        }

        // zbir potrosnje/proizvodnje svih uredjaja za narednih 7/31 dan po datumu (danu)
        public async Task<List<GraphDateDto>> getRecordForSomePeriodPrAsync(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            DateTime to = DateTime.Today.AddDays((int)periodOfTime + 1);
            DateTime temp = DateTime.Now;
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);
            List<GraphDateDto> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum <= toDate && predikcija.Datum > tempDate
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(predikcija => predikcija.Datum)
               .Select(predikcija => new GraphDateDto(predikcija.Key, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije) / 1000, 2)))
               .ToListAsync();

            return lista;
        }

        // zbir potrosnje/proizvodnje svih uredjaja za prethodnih 7/31 dan po datumu (danu)
        public async Task<List<GraphDateDto>> getRecordForSomePastPeriodPrAsync(PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            DateTime from = DateTime.Today.AddDays(-(int)periodOfTime + (int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now.AddDays((int)PeriodOfTime.Year);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            List<GraphDateDto> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum >= fromDate && predikcija.Datum <= tempDate                                    
                                    && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(predikcija => predikcija.Datum)
               .Select(predikcija => new GraphDateDto(predikcija.Key.AddDays(-(int)PeriodOfTime.Year), Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije) / 1000, 2)))
               .ToListAsync();

            return lista;
        }

        // predikcija potrosnja/proizvodnja svih uredjaja u prethodnih 365 dana po mesecu
        public async Task<List<GraphMonthDTO>> getRecordForPastYearPrAsync(ETipUredjaj tipUredjaja)
        {
            DateTime to = DateTime.Today.AddDays((int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now.AddDays((int)PeriodOfTime.Day);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, 01);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);
            List<GraphMonthDTO> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum <= toDate && predikcija.Datum >= tempDate
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja)
               .GroupBy(predikcija => new { predikcija.Datum.Month, predikcija.Datum.Year })
               .Select(predikcija => new GraphMonthDTO(predikcija.Key.Month, predikcija.Key.Year - 1, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije) / 1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(istorija => istorija.Year).ThenBy(istorija => istorija.Month).ToList();

            return lista;
        }


        // ************************** P R O S U M E R ************************** //

        // --------------------- P O T R O S NJ A   I   P R O I Z V O D NJ A ---------------------- //

        // predikcija ukupne potrosnje/proizvodnje za sve uredjaje za odredjeni objekat za danas i sutra po satu

        public async Task<List<GraphHourDTO>> getTwoDaysRecordForObjectPredAsync(int objekatId, ETipUredjaj tipUredjaja)
        {
            DateTime to = DateTime.Today.AddDays(1);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);

            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            List<GraphHourDTO> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum >= tempDate && predikcija.Datum <= toDate
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                  && predikcija.ObjekatUredjaj.ObjekatId == objekatId)
               .GroupBy(predikcija => new { predikcija.Vreme, predikcija.Datum })
               .Select(predikcija => new GraphHourDTO(predikcija.Key.Vreme, predikcija.Key.Datum, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(predikcija => predikcija.Date).ThenBy(predikcija => predikcija.Time).ToList();

            return lista;
        }

        // predikcija ukupne potrosnje/proizvodnje svih uredjaja za narednih 7/31 dan u odredjenom objektu po datumu (danu)
        public async Task<List<GraphDateDto>> getRecordForSomePeriodForObjectPredAsync(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            DateTime to = DateTime.Today.AddDays((int)periodOfTime + 1);
            DateTime temp = DateTime.Now;
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);
            List<GraphDateDto> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum <= toDate && predikcija.Datum > tempDate
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                   && predikcija.ObjekatUredjaj.ObjekatId == objekatId)
               .GroupBy(predikcija => predikcija.Datum)
               .Select(predikcija => new GraphDateDto(predikcija.Key, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
               .ToListAsync();

            return lista;
        }


        // predikcija ukupne potrosnje/proizvodnje svih uredjaja za prethodnih 7/31 dan u odredjenom objektu po datumu (danu)
        public async Task<List<GraphDateDto>> getRecordForSomePastPeriodForObjectPredAsync(int objekatId, PeriodOfTime periodOfTime, ETipUredjaj tipUredjaja)
        {
            DateTime from = DateTime.Today.AddDays(-(int)periodOfTime + (int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now.AddDays((int)PeriodOfTime.Year);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            List<GraphDateDto> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum >= fromDate && predikcija.Datum <= tempDate
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                  && predikcija.ObjekatUredjaj.ObjekatId == objekatId)
               .GroupBy(predikcija => predikcija.Datum)
               .Select(predikcija => new GraphDateDto(predikcija.Key.AddDays(-(int)PeriodOfTime.Year), Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
               .ToListAsync();

            return lista;
        }
        
        // predikcija potrosnja/proizvodnja svih uredjaja u odredjenom objektu u prethodnih 365 dana po mesecu
        public async Task<List<GraphMonthDTO>> getRecordForPastYearForObjectPredAsync(int objekatId, ETipUredjaj tipUredjaja)
        {
            DateTime to = DateTime.Today.AddDays((int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now.AddDays((int)PeriodOfTime.Day);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, 01);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);
            List<GraphMonthDTO> lista = await _context.PredikcijaP
               .Include(predikcija => predikcija.ObjekatUredjaj)
               .ThenInclude(predikcija => predikcija.Uredjaj)
               .Where(predikcija => predikcija.Datum <= toDate && predikcija.Datum >= tempDate
                                  && predikcija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                  && predikcija.ObjekatUredjaj.ObjekatId == objekatId)
               .GroupBy(predikcija => new { predikcija.Datum.Month, predikcija.Datum.Year })
               .Select(predikcija => new GraphMonthDTO(predikcija.Key.Month, predikcija.Key.Year - 1, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(istorija => istorija.Year).ThenBy(istorija => istorija.Month).ToList();

            return lista;
        }

        // ------------------- U R E DJ A J I   V R E D N O S T ------------------ //

        // predikcija ukupne potrosnje/proizvodnje za odredjeni uredjaje za danas i sutra po satu
        public async Task<List<GraphHourDTO>> getTwoDaysRecordForDevicePredAsync(int objekatUredjajId)
        {
            DateTime to = DateTime.Today.AddDays(1);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);
            DateTime temp = DateTime.Now;
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            List<GraphHourDTO> lista = await _context.PredikcijaP
               .Where(predikcija => predikcija.Datum >= tempDate && predikcija.Datum <= toDate
                                  && predikcija.ObjekatUredjajId == objekatUredjajId)
               .GroupBy(predikcija => new { predikcija.Vreme, predikcija.Datum })
               .Select(predikcija => new GraphHourDTO(predikcija.Key.Vreme, predikcija.Key.Datum, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(predikcija => predikcija.Date).ThenBy(predikcija => predikcija.Time).ToList();

            return lista;
        }

        
        // zbir potrosnje/proizvodnje jednog odredjenog uredjaja u prethodnih 7/31 dan

        public async Task<List<GraphDateDto>> getRecordForSomePeriodForDevicePredAsync(int objekatUredjajId, PeriodOfTime periodOfTime)
        {
            DateTime to = DateTime.Today.AddDays(+(int)periodOfTime + 1);
            DateTime temp = DateTime.Now;
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);
            List<GraphDateDto> lista = await _context.PredikcijaP
                .Where(predikcija => predikcija.Datum <= toDate && predikcija.Datum > tempDate
                                     && predikcija.ObjekatUredjajId == objekatUredjajId)
                .GroupBy(predikcija => predikcija.Datum)
                .Select(predikcija => new GraphDateDto(predikcija.Key, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
                .ToListAsync();

            return lista;
        }

        // zbir potrosnje/proizvodnje jednog odredjenog uredjaja u prethodnih 7/31 dan

        public async Task<List<GraphDateDto>> getRecordForSomePastPeriodForDevicePredAsync(int objekatUredjajId, PeriodOfTime periodOfTime)
        {
            DateTime from = DateTime.Today.AddDays(-(int)periodOfTime + (int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now.AddDays((int)PeriodOfTime.Year);
            TimeOnly tempTime = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly fromDate = new DateOnly(from.Year, from.Month, from.Day);
            List<GraphDateDto> lista = await _context.PredikcijaP
                .Where(predikcija => predikcija.Datum >= fromDate && predikcija.Datum <= tempDate
                                     && predikcija.ObjekatUredjajId == objekatUredjajId)
                .GroupBy(predikcija => predikcija.Datum)
                .Select(predikcija => new GraphDateDto(predikcija.Key.AddDays(-(int)PeriodOfTime.Year), Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
                .ToListAsync();

            return lista;
        }

        // predikcija potrosnja/proizvodnja odredjenog uredjaja u prethodnih 365 dana po mesecu
        public async Task<List<GraphMonthDTO>> getRecordForPastYearForDevicePredAsync(int objekatUredjajId)
        {
            DateTime to = DateTime.Today.AddDays((int)PeriodOfTime.Year);
            DateTime temp = DateTime.Now.AddDays((int)PeriodOfTime.Day);
            DateOnly tempDate = new DateOnly(temp.Year, temp.Month, 01);
            DateOnly toDate = new DateOnly(to.Year, to.Month, to.Day);
            List<GraphMonthDTO> lista = await _context.PredikcijaP
               .Where(predikcija => predikcija.Datum <= toDate && predikcija.Datum >= tempDate
                                  && predikcija.ObjekatUredjajId == objekatUredjajId)
               .GroupBy(predikcija => new { predikcija.Datum.Month, predikcija.Datum.Year })
               .Select(predikcija => new GraphMonthDTO(predikcija.Key.Month, predikcija.Key.Year - 1, Math.Round(predikcija.Sum(predikcija => predikcija.VrednostPredikcije)/1000, 2)))
               .ToListAsync();

            lista = lista.OrderBy(predikcija => predikcija.Year).ThenBy(predikcija => predikcija.Month).ToList();

            return lista;
        }


    }
}
