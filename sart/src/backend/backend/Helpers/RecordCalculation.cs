using backend.Context;
using backend.Helpers.interfaces;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;
using System.Data;

namespace backend.Helpers
{
    public class RecordCalculation : IRecordCalculation
    {
        private readonly AppDbContext _context;
        public RecordCalculation(AppDbContext context)
        {
            _context = context;
        }

        // vraca broj dana do kraja godine
        public int GetNumberOfDays()
        {
            DateTime currentDate = DateTime.Today;
            DateTime endOfYear = new DateTime(currentDate.Year, 12, 31);
            int restDate = (endOfYear - currentDate).Days;
            return (365 + restDate) * 24;
        }

        // racuna koliko procenata sata radi uredajja
        public double DajStepenUcestalosti(int min,int max)
        {
            Random rnd = new Random();
            double stepenUcestalosti = rnd.Next(min, max) / 1000.0;
            return stepenUcestalosti;
        }

        // racuna krajnju vrednost u jednom satu
        public double DajVriednost(List<double> potrosnja, int min, int max)
        {
            Random rnd = new Random();
            double stepenUcestalosti = DajStepenUcestalosti(min, max);
            int indeks = rnd.Next(0, potrosnja.Count);
            double vrednost = potrosnja[indeks] * stepenUcestalosti;
            return vrednost;
        }

        // racuna istoriju za uredjaje koji rade zimi
        public async Task RecordForWinterDevices(int objekatUredjajId, List<double> potrosnja, double cekanje)
        {
            List<IstorijaP> records = new List<IstorijaP>();

            DateTime temp = DateTime.Now;
            DateTime time = new DateTime(temp.Year, 12, 31, 23, 00, 00);
            int timeRange = GetNumberOfDays();

            for (int i = 0; i < timeRange; i++) 
            {
                double value;
                if ((time.Date.Month >= 10 && time.Date.Month <= 12) || (time.Date.Month >= 1 && time.Date.Month <= 3))
                    if ( time.Hour > 7 && time.Hour < 23)
                        value = DajVriednost(potrosnja, 600, 1000);
                    else
                        value = cekanje;
                else
                    value = 0;

                IstorijaP istorijaP = new IstorijaP();
                istorijaP.ObjekatUredjajId = objekatUredjajId;
                istorijaP.Datum = new DateOnly(time.Year, time.Month, time.Day);
                istorijaP.Vreme = new TimeOnly(time.Hour, time.Minute, time.Second);
                istorijaP.VrednostRealizacije = value;
                records.Add(istorijaP);
                time = time.AddHours(-1);
            }
            await FillIstorijaRecordAsync(records);
        }

        // racuna istoriju za uredjaje koji rade leti
        public async Task RecordForSumemrDevices(int objekatUredjajId, List<double> potrosnja, double cekanje)
        {
            List<IstorijaP> records = new List<IstorijaP>();

            DateTime temp = DateTime.Now;
            DateTime time = new DateTime(temp.Year, 12, 31, 23, 00, 00);
            int timeRange = GetNumberOfDays();

            for (int i = 0; i < timeRange; i++)
            {
                double value;
                if (time.Date.Month >= 5 && time.Date.Month <= 9)
                    if (time.Hour > 10 && time.Hour < 19)
                        value = DajVriednost(potrosnja, 600, 1000);
                    else
                        value = cekanje;
                else
                    value = 0;

                IstorijaP istorijaP = new IstorijaP();
                istorijaP.ObjekatUredjajId = objekatUredjajId;
                istorijaP.Datum = new DateOnly(time.Year, time.Month, time.Day);
                istorijaP.Vreme = new TimeOnly(time.Hour, time.Minute, time.Second);
                istorijaP.VrednostRealizacije = value;
                records.Add(istorijaP);
                time = time.AddHours(-1);
            }
            await FillIstorijaRecordAsync(records);
        }

        // racuna istoriju za uredjaje koji rade na dnevnom novou ali ne konstantu
        public async Task RecordForDailyDevices(int objekatUredjajId, List<double> potrosnja, double cekanje)
        {
            List<IstorijaP> records = new List<IstorijaP>();

            DateTime temp = DateTime.Now;
            DateTime time = new DateTime(temp.Year, 12, 31, 23, 00, 00);
            int timeRange = GetNumberOfDays();

            for (int i = 0; i < timeRange; i++)
            {
                double value;
                if ((time.Hour >= 6  && time.Hour <= 8) || (time.Hour >= 12 && time.Hour <= 13) || (time.Hour >= 16 && time.Hour <= 18) || (time.Hour >= 20 && time.Hour <= 22))
                    value = DajVriednost(potrosnja, 600, 1000);
                else
                    value = cekanje;


                IstorijaP istorijaP = new IstorijaP();
                istorijaP.ObjekatUredjajId = objekatUredjajId;
                istorijaP.Datum = new DateOnly(time.Year, time.Month, time.Day);
                istorijaP.Vreme = new TimeOnly(time.Hour, time.Minute, time.Second);
                istorijaP.VrednostRealizacije = value;
                records.Add(istorijaP);
                time = time.AddHours(-1);
            }
            await FillIstorijaRecordAsync(records);
        }

        // racuna istoriju za uredjaje koji rade dnevno i konstatnto
        public async Task RecordForDailyConstantDevices(int objekatUredjajId, List<double> potrosnja, double cekanje)
        {
            List<IstorijaP> records = new List<IstorijaP>();
            DateTime temp = DateTime.Now;
            DateTime time = new DateTime( temp.Year, 12, 31, 23, 00, 00);
            int timeRange = GetNumberOfDays();

            for (int i = 0; i < timeRange; i++)
            {
                Random rnd = new Random();
                int selections = rnd.Next(1, 20);
                double value;
                if (selections != 1)
                    value = DajVriednost(potrosnja, 700, 1000);
                else
                    value = cekanje;


                IstorijaP istorijaP = new IstorijaP();
                istorijaP.ObjekatUredjajId = objekatUredjajId;
                istorijaP.Datum = new DateOnly(time.Year, time.Month, time.Day);
                istorijaP.Vreme = new TimeOnly(time.Hour, time.Minute, time.Second);
                istorijaP.VrednostRealizacije = value;
                records.Add(istorijaP);
                time = time.AddHours(-1);
            }
            await FillIstorijaRecordAsync(records);
        }

        // racuna istoriju za uredjaje koji proizvode 
        public async Task RecordForProducerDevices(int objekatUredjajId, List<double> potrosnja, double cekanje)
        {
            List<IstorijaP> records = new List<IstorijaP>();

            DateTime temp = DateTime.Now;
            DateTime time = new DateTime(temp.Year, 12, 31, 23, 00, 00);
            int timeRange = GetNumberOfDays();

            for (int i = 0; i < timeRange; i++)
            {
                Random rnd = new Random();
                int selections = rnd.Next(1, 20);
                double value;
                int month = time.Month;
                int hour = time.Hour;
                if (month >= 11 || month <= 2)
                    if (hour >= 9 && hour <= 15)
                        value = DajVriednost(potrosnja, 500, 1000);
                    else
                        value = cekanje;
                else if (month >= 3 || month <= 4)
                    if (hour >= 7 && hour <= 17)
                        value = DajVriednost(potrosnja, 700, 1000);
                    else
                        value = cekanje;
                else if (month >= 5 || month <= 6)
                    if (hour >= 6 && hour <= 19)
                        value = DajVriednost(potrosnja, 800, 1000);
                    else
                        value = cekanje;
                else if (month >= 7 || month <= 8)
                    if (hour >= 6 && hour <= 20)
                        value = DajVriednost(potrosnja, 850, 1000);
                    else
                        value = cekanje;
                else if (month >= 9 || month <= 10)
                    if (hour >= 7 && hour <= 18)
                        value = DajVriednost(potrosnja, 650, 1000);
                    else
                        value = cekanje;
                else
                    value = cekanje;

                IstorijaP istorijaP = new IstorijaP();
                istorijaP.ObjekatUredjajId = objekatUredjajId;
                istorijaP.Datum = new DateOnly(time.Year, time.Month, time.Day);
                istorijaP.Vreme = new TimeOnly(time.Hour, time.Minute, time.Second);
                istorijaP.VrednostRealizacije = value;
                records.Add(istorijaP);
                time = time.AddHours(-1);
            }
            await FillIstorijaRecordAsync(records);
        }

        // racuna istoriju za sce ostale uredjaje
        public async Task RecordForRestDevices(int objekatUredjajId, List<double> potrosnja, double cekanje)
        {
            List<IstorijaP> records = new List<IstorijaP>();

            DateTime temp = DateTime.Now;
            DateTime time = new DateTime(temp.Year, 12, 31, 23, 00, 00);
            int timeRange = GetNumberOfDays();

            for (int i = 0; i < timeRange; i++)
            {
                Random rnd = new Random();
                int selections = rnd.Next(1, 2);
                double value;
                if (selections == 1)
                    value = DajVriednost(potrosnja, 500, 1000);
                else
                    value = cekanje;


                IstorijaP istorijaP = new IstorijaP();
                istorijaP.ObjekatUredjajId = objekatUredjajId;
                istorijaP.Datum = new DateOnly(time.Year, time.Month, time.Day);
                istorijaP.Vreme = new TimeOnly(time.Hour, time.Minute, time.Second);
                istorijaP.VrednostRealizacije = value;
                records.Add(istorijaP);
                time = time.AddHours(-1);
            }
            await FillIstorijaRecordAsync(records);
        }

        // popunjava tabelu istorije i predikcije u bazi sa 
        public async Task FillIstorijaRecordAsync(List<IstorijaP> records)
        {
            int timeRange = GetNumberOfDays();
            string queryHistory = "INSERT INTO IstorijaP (ObjekatUredjajId, VrednostRealizacije, Datum, Vreme) values ";
            string queryPrediction = "INSERT INTO PredikcijaP (ObjekatUredjajId, VrednostPredikcije, Datum, Vreme) values ";
            for (int i = 0; i < timeRange;  i++)
            {
                IstorijaP istorijaP = records[i];
                double predictionError = DajStepenUcestalosti(-200000, 200000);
                double vrednostPredikcije = istorijaP.VrednostRealizacije + predictionError;
                if (i == 0)
                {
                    queryHistory += "('" + istorijaP.ObjekatUredjajId + "', '" + istorijaP.VrednostRealizacije + "', '" + istorijaP.Datum.ToString("yyyy-MM-dd") + "', '" + istorijaP.Vreme.ToString("HH:mm:ss") + "')";
                    queryPrediction += "('" + istorijaP.ObjekatUredjajId + "', '" + vrednostPredikcije + "', '" + istorijaP.Datum.AddDays(365).ToString("yyyy-MM-dd") + "', '" + istorijaP.Vreme.ToString("HH:mm:ss") + "')";
                }
                else
                {
                    queryHistory += ", ('" + istorijaP.ObjekatUredjajId + "', '" + istorijaP.VrednostRealizacije + "', '" + istorijaP.Datum.ToString("yyyy-MM-dd") + "', '" + istorijaP.Vreme.ToString("HH:mm:ss") + "')";
                    queryPrediction += ", ('" + istorijaP.ObjekatUredjajId + "', '" + vrednostPredikcije + "', '" + istorijaP.Datum.AddDays(365).ToString("yyyy-MM-dd") + "', '" + istorijaP.Vreme.ToString("HH:mm:ss") + "')";
                }
            }
            await _context.Database.ExecuteSqlRawAsync(queryHistory);
            await _context.Database.ExecuteSqlRawAsync(queryPrediction);
            await _context.SaveChangesAsync();

        }
    }
}
