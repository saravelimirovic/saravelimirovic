using backend.Common;
using backend.Context;
using backend.DTOs;
using backend.Models;
using backend.repositroy;
using backend.repositroy.interfaces;
using backend.services.interfaces;
using System.Collections.Generic;
using System.Net.NetworkInformation;

namespace backend.services
{
    public class TableService : ITableService
    {
        private readonly ITableRepository _tableService;
        private readonly IRecordService _recordService;

        // na manju satnicu
        public static DateTime RoundToHour(DateTime dateTime)
        {
            var updated = dateTime.AddMinutes(30);
            return new DateTime(updated.Year, updated.Month, updated.Day,
                                 updated.Hour, 0, 0, dateTime.Kind);
            //return dateTime.Date.AddHours(dateTime.Hour);
        }

        public TableService(ITableRepository recordTable, IRecordService recordService)
        {
            _tableService = recordTable;
            _recordService = recordService;
        }


        // ----------------------------------------- I S T O R I J A  ----------------------------------------- //

        // ************************** D S O ************************** //

        // -------------------- P O T R O S NJ A -------------------- //


        // zbir potrosnje svih uredjaja -> za tabelu korisnika
        public async Task<double> getThisMonthPotrosnjaForId(int korisnikId)
        {
            var allRecords = await _tableService.getThisMonthPotrosnjaForIdAsync(korisnikId, ETipUredjaj.Potrosac);

            return allRecords;
        }


        // -------------------- P R O I Z V O D NJ A -------------------- //


        // zbir proizvodnje svih uredjaja -> za tabelu korisnika
        public async Task<double> getThisMonthProizvodnjaForId(int korisnikId)
        {
            var allRecords = await _tableService.getThisMonthPotrosnjaForIdAsync(korisnikId, ETipUredjaj.Proizvodjac);

            return allRecords;
        }




        // -------------------- P O T R O S NJ A   I    P R O I Z V O D NJ A -------------------- //

        // vraca joki potrosnju i proizvodnju u istom DTOu zbog tabele na pocetnoj -> za prethodnih 7 dana
        public async Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja7d()
        {
            var potrosnjaPred = await _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Week, ETipUredjaj.Potrosac);
            var potrosnja = await _recordService.getRecordForSomePeriod(PeriodOfTime.Week, ETipUredjaj.Potrosac);
            var proizvodnjaPred = await _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Week, ETipUredjaj.Proizvodjac);
            var proizvodnja = await _recordService.getRecordForSomePeriod(PeriodOfTime.Week, ETipUredjaj.Proizvodjac);

            List<GraphDateAllDTO> lista = new List<GraphDateAllDTO>();

            for (int i = 0; i < 7; i++)
            {
                lista.Add(new GraphDateAllDTO(potrosnja[i].Date, potrosnjaPred[i].Usage, potrosnja[i].Usage, proizvodnjaPred[i].Usage, proizvodnja[i].Usage));
            }

            return lista;
        }

        // vraca joki potrosnju i proizvodnju u istom DTOu zbog tabele na pocetnoj -> za prethodnih mesec dana
        public async Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja31days()
        {
            var potrosnjaPred = await _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Month, ETipUredjaj.Potrosac);
            var potrosnja = await _recordService.getRecordForSomePeriod(PeriodOfTime.Month, ETipUredjaj.Potrosac);
            var proizvodnjaPred = await _recordService.getRecordForSomePastPeriodPr(PeriodOfTime.Month, ETipUredjaj.Proizvodjac);
            var proizvodnja = await _recordService.getRecordForSomePeriod(PeriodOfTime.Month, ETipUredjaj.Proizvodjac);

            List<GraphDateAllDTO> lista = new List<GraphDateAllDTO>();

            for (int i = 0; i < 31; i++)
            {
                lista.Add(new GraphDateAllDTO(potrosnja[i].Date, potrosnjaPred[i].Usage, potrosnja[i].Usage, proizvodnjaPred[i].Usage, proizvodnja[i].Usage));
            }

            return lista;
        }

        // vraca joki potrosnju i proizvodnju u istom DTOu zbog tabele na pocetnoj -> za prethodnih godinu dana
        public async Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja1Year()
        {
            var potrosnjaPred = await _recordService.getRecordForPastYearPr(ETipUredjaj.Potrosac);
            var potrosnja = await _recordService.getRecordForPastYear(ETipUredjaj.Potrosac);
            var proizvodnjaPred = await _recordService.getRecordForPastYearPr(ETipUredjaj.Proizvodjac);
            var proizvodnja = await _recordService.getRecordForPastYear(ETipUredjaj.Proizvodjac);

            List<GraphDateAllDTO> lista = new List<GraphDateAllDTO>();

            for (int i = 0; i < 13; i++)
            {
                lista.Add(new GraphDateAllDTO(potrosnja[i].Month, potrosnja[i].Year, potrosnjaPred[i].Usage, potrosnja[i].Usage, proizvodnjaPred[i].Usage, proizvodnja[i].Usage));
            }

            return lista;
        }

        // vraca joki potrosnju i proizvodnju u istom DTOu zbog tabele na pocetnoj -> za narednih 7 dana
        public async Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja7dPr()
        {
            var potrosnja = await _recordService.getRecordForSomePeriodPr(PeriodOfTime.Week, ETipUredjaj.Potrosac);
            var proizvodnja = await _recordService.getRecordForSomePeriodPr(PeriodOfTime.Week, ETipUredjaj.Proizvodjac);

            List<GraphDateAllDTO> lista = new List<GraphDateAllDTO>();

            for (int i = 0; i < 7; i++)
            {
                lista.Add(new GraphDateAllDTO(potrosnja[i].Date, potrosnja[i].Usage, proizvodnja[i].Usage));
            }

            return lista;
        }




        // -------------------- F I L T E R -------------------- //

        // vraca min i max zbog slajdera
        public async Task<FilterMaxMinDTO> getMaxMin()
        {
            var result = await _tableService.getMaxMin();

            return result;
        }

        public async Task<List<UserTabelaDTO>> getFilteredUsers(int pageIndex, int pageSize, string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null)
        {
            //var resut = await _tableService.getFilteredUsers(potrosnjaOd, potrosnjaDo, proizvodnjaOd, proizvodnjaDo, ime, grad, naselje, ulica, rola);

            //return resut;

            List<UserTabelaDTO> filteredUsers = await _tableService.getFilteredUsers(pageIndex, pageSize, potrosnjaOdStr, potrosnjaDoStr, proizvodnjaOdStr, proizvodnjaDoStr, rola, ime, grad, naselje, ulica);
            List<UserTabelaDTO> UsersDTO = new List<UserTabelaDTO>();

            if ((!string.IsNullOrEmpty(potrosnjaOdStr) || !string.IsNullOrEmpty(potrosnjaDoStr)) && (!string.IsNullOrEmpty(proizvodnjaOdStr) || !string.IsNullOrEmpty(proizvodnjaDoStr)))
            {
                foreach (var u in filteredUsers)
                {
                    var potrosnja = 0.0;
                    var proizvodnja = 0.0;

                    //// prosumer
                    //if (u.Rola == "prozumer")
                    //{
                    //    potrosnja = await getThisMonthPotrosnjaForId(u.Id);
                    //    proizvodnja = await getThisMonthProizvodnjaForId(u.Id);
                    //}
                    //else if (u.Rola == "potrošač")
                    //{
                    //    potrosnja = await getThisMonthPotrosnjaForId(u.Id);
                    //}
                    //else
                    //{
                    //    proizvodnja = await getThisMonthProizvodnjaForId(u.Id);
                    //}

                    UsersDTO.Add(new UserTabelaDTO(u.Id, u.Ime, u.Grad, u.Naselje, u.Ulica, Math.Round(u.Potrosnja / 1000, 2, MidpointRounding.AwayFromZero), Math.Round(u.Proizvodnja / 1000, 2, MidpointRounding.AwayFromZero), u.Rola, u.BrojKorisnika));

                }
            }
            else
            {
                foreach (var u in filteredUsers)
                {
                    var potrosnja = 0.0;
                    var proizvodnja = 0.0;

                    // prosumer
                    if (u.Rola == "prozumer")
                    {
                        potrosnja = await getThisMonthPotrosnjaForId(u.Id);
                        proizvodnja = await getThisMonthProizvodnjaForId(u.Id);
                    }
                    else if (u.Rola == "potrošač")
                    {
                        potrosnja = await getThisMonthPotrosnjaForId(u.Id);
                    }
                    else
                    {
                        proizvodnja = await getThisMonthProizvodnjaForId(u.Id);
                    }

                    UsersDTO.Add(new UserTabelaDTO(u.Id, u.Ime, u.Grad, u.Naselje, u.Ulica, Math.Round(potrosnja / 1000, 2, MidpointRounding.AwayFromZero), Math.Round(proizvodnja / 1000, 2, MidpointRounding.AwayFromZero), u.Rola, u.BrojKorisnika));

                }
            }

            return UsersDTO;
        }

        public async Task<List<DispatcherDTO>> getFilteredDispatchers(int pageIndex, int pageSize,
                                                                        string ime = null, string grad = null, string naselje = null, string ulica = null)
        {
            List<DispatcherDTO> filteredUsers = await _tableService.getFilteredDispatchers(pageIndex, pageSize, ime, grad, naselje, ulica);
            List<DispatcherDTO> UsersDTO = new List<DispatcherDTO>();

            foreach (var u in filteredUsers)
            {
                UsersDTO.Add(new DispatcherDTO(u.Id, u.Ime, u.Grad, u.Naselje, u.Ulica, u.BrojKorisnika));
            }

            return UsersDTO;
        }









        public async Task<List<MapDTO>> getFilteredUsersForMap(string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null)
        {
            List<MapDTO> filteredUsers = await _tableService.getFilteredUsersForMap(potrosnjaOdStr, potrosnjaDoStr, proizvodnjaOdStr, proizvodnjaDoStr, rola, ime, grad, naselje, ulica);
            List<MapDTO> UsersDTO = new List<MapDTO>();

            foreach (var u in filteredUsers)
            { 

               //UsersDTO.Add(new MapDTO(u.Id, u.Grad, u.Naselje, u.Ulica, u.Rola));
                UsersDTO.Add(new MapDTO(u.KorisnikaId, u.Ime, u.NazivUlice, u.AdrsniBroj, u.NazivGrada, u.RolaKorisnika));

            }

            return UsersDTO;
        }
    }
}
