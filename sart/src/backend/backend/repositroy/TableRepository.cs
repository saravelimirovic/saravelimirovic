using backend.Common;
using backend.Context;
using backend.DTOs;
using backend.Models;
using backend.repositroy;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;
using System.Linq;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace backend.repositroy
{
    public class TableRepository : ITableRepository
    {
        private readonly AppDbContext _context;
        public TableRepository(AppDbContext context)
        {
            _context = context;
        }

        // ----------------------------------------- I S T O R I J A  ----------------------------------------- //

        // ************************** D S O ************************** //

        // -------------------- P O T R O S NJ A    I     P R O I Z V O D NJ A -------------------- //

        //potrosnja/proizvodnja svih uredjaja u danansnjem danu do sadasnjeg sata
        public async Task<double> getTodaysPotrosnjaForIdAsync(int korisnikId, ETipUredjaj tipUredjaja)
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            var lista = _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => istorija.Vreme <= time2
                                    && istorija.Datum.Day == date.Day && istorija.Datum.Month == date.Month
                                     && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                     && istorija.ObjekatUredjaj.Objekat.KorisnikId == korisnikId)
                .GroupBy(istorija => istorija.Datum.Day)
               .Select(istorija => istorija.Sum(istorija => istorija.VrednostRealizacije))
               .FirstOrDefault();


            return lista;
        }

        // novo ->  za kolonu potrosnja/proizvodnja u tekucem mesecu (nez dal treba da dodam i ono za taj dan potrosnja(ovde je dodato))
        public async Task<double> getThisMonthPotrosnjaForIdAsync(int korisnikId, ETipUredjaj tipUredjaja)
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            var lista = _context.IstorijaP
               .Include(istorija => istorija.ObjekatUredjaj)
               .ThenInclude(istorija => istorija.Uredjaj)
               .Where(istorija => ((istorija.Datum.Day < date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year)
                                  || (istorija.Datum.Day == date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year && istorija.Vreme < time2))
                                  && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)tipUredjaja
                                  && istorija.ObjekatUredjaj.Objekat.KorisnikId == korisnikId)
               .GroupBy(istorija => istorija.Datum.Month)
               .Select(istorija => istorija.Sum(istorija => istorija.VrednostRealizacije))
               .FirstOrDefault();

            return lista;
        }




        // -------------------- F I L T E R -------------------- //

        public async Task<FilterMaxMinDTO> getMaxMin()
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            var query = _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => ((istorija.Datum.Day < date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year)
                                    || (istorija.Datum.Day == date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year && istorija.Vreme < time2))
                                    && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)ETipUredjaj.Potrosac)
                .GroupBy(istorija => new
                {
                    istorija.Datum.Month,
                    istorija.ObjekatUredjaj.Objekat.KorisnikId
                })
                .Select(istorija => new
                {
                    Suma = istorija.Sum(q => q.VrednostRealizacije),
                    UserId = istorija.Key.KorisnikId
                })
                .OrderByDescending(g => g.Suma);

            var maxPotr = query.FirstOrDefault().Suma;
            var minPotr = query.LastOrDefault().Suma;

            var query1 = _context.IstorijaP
                .Include(istorija => istorija.ObjekatUredjaj)
                .ThenInclude(istorija => istorija.Uredjaj)
                .Where(istorija => ((istorija.Datum.Day < date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year)
                                    || (istorija.Datum.Day == date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year && istorija.Vreme < time2))
                                    && istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId == (int)ETipUredjaj.Proizvodjac)
                .GroupBy(istorija => new
                {
                    istorija.Datum.Month,
                    istorija.ObjekatUredjaj.Objekat.KorisnikId
                })
                .Select(istorija => new
                {
                    Suma = istorija.Sum(q => q.VrednostRealizacije),
                    UserId = istorija.Key.KorisnikId
                })
                .OrderByDescending(g => g.Suma);

            var maxProiz = query1.FirstOrDefault().Suma;
            var minProiz = query1.LastOrDefault().Suma;

            return new FilterMaxMinDTO(0, (int)Math.Ceiling(maxPotr/1000), 0, (int)Math.Ceiling(maxProiz / 1000));

        }




        public async Task<List<UserTabelaDTO>> getFilteredUsers(int pageIndex, int pageSize, string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null)
        {
            //AKO JE I PO PROIZVODNJI I POTROSNJI

            if (!string.IsNullOrEmpty(potrosnjaOdStr) || !string.IsNullOrEmpty(potrosnjaDoStr) || !string.IsNullOrEmpty(proizvodnjaOdStr) || !string.IsNullOrEmpty(proizvodnjaDoStr)) {

                DateTime temp = DateTime.Now;
                DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
                DateOnly start = new DateOnly(temp.Year, temp.Month, 01);
                TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
                var query = _context.IstorijaP
                    .Include(istorija => istorija.ObjekatUredjaj)
                    .ThenInclude(istorija => istorija.Uredjaj)
                    .Where(istorija => ((istorija.Datum.Day < date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year)
                                        || (istorija.Datum.Day == date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year && istorija.Vreme < time2)))
                    .GroupBy(istorija => new
                    {
                        istorija.Datum.Month,
                        istorija.ObjekatUredjaj.Objekat.KorisnikId,
                        istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId,
                    })
                    .Select(istorija => new
                    {
                        UserId = istorija.Key.KorisnikId,
                        TipUredjaja = istorija.Key.TipUredjajaId,
                        //MesecnoGrup = istorija.Key.Month,
                        Vrednost = istorija.Sum(q => q.VrednostRealizacije)
                    })
                    .AsQueryable();

                //// vracamo min i max za potrosnju
                //var minmaxPotrosnja = query
                //                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Potrosac).OrderByDescending(g => g.Vrednost);
                //var minPotrosnja = minmaxPotrosnja.FirstOrDefault().Vrednost;
                //var maxPotrosnja = minmaxPotrosnja.LastOrDefault().Vrednost;

                //// vracamo min i max za proizvodnju
                //var minmaxProizvodja = query
                //                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Proizvodjac).OrderByDescending(g => g.Vrednost);
                //var minProizvodnja = minmaxProizvodja.FirstOrDefault().Vrednost;
                //var maxProizvodnja = minmaxProizvodja.LastOrDefault().Vrednost;


                // ako je i po proizvodji i po potrosnji
                if ((!string.IsNullOrEmpty(potrosnjaOdStr) || !string.IsNullOrEmpty(potrosnjaDoStr)) && (!string.IsNullOrEmpty(proizvodnjaOdStr) || !string.IsNullOrEmpty(proizvodnjaDoStr)))
                {
                    var potrosnjaOd = potrosnjaOdStr == null ? 0.0 : double.Parse(potrosnjaOdStr);
                    var potrosnjaDo = potrosnjaDoStr == null ? 0.0 : double.Parse(potrosnjaDoStr);
                    var proizvodnjaOd = proizvodnjaOdStr == null ? 0 : double.Parse(proizvodnjaOdStr);
                    var proizvodnjaDo = proizvodnjaDoStr == null ? 0 : double.Parse(proizvodnjaDoStr);

                    var query1 = query
                                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Potrosac && i.Vrednost >= potrosnjaOd * 1000 && i.Vrednost < potrosnjaDo * 1000)
                                .Select(istorija => new
                                {
                                    UserId = istorija.UserId,
                                    Potrosnja = istorija.Vrednost
                                })
                                .AsQueryable();

                    var query2 = query
                                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Proizvodjac && i.Vrednost >= proizvodnjaOd * 1000 && i.Vrednost < proizvodnjaDo * 1000)
                                .Select(istorija => new
                                {
                                    UserId = istorija.UserId,
                                    Proizvodnja = istorija.Vrednost
                                })
                                .AsQueryable();

                    var proba = query1.Join(query2, k => k.UserId,
                                                    q => q.UserId,
                                                    (k, q) => new { k, q }
                                                );

                    var query3 = _context.Korisnik
                                .Include(u => u.Ulica)
                                .ThenInclude(n => n.Naselje)
                                .ThenInclude(g => g.Grad)
                                .Include(r => r.Rola)
                                .Join(proba, k => k.Id,
                                                q => q.k.UserId,
                                                (k, q) => new
                                                {
                                                    k.Id,
                                                    k.Ime,
                                                    k.Prezime,
                                                    Grad = k.Ulica.Naselje.Grad.Naziv,
                                                    Naselje = k.Ulica.Naselje.Naziv,
                                                    Ulica = k.Ulica.Naziv + " " + k.AdresniBroj,
                                                    RolaId = k.Rola.Id,
                                                    Rola = k.Rola.Naziv,
                                                    q.k.Potrosnja,
                                                    q.q.Proizvodnja
                                                }
                                        );


                    if (!string.IsNullOrEmpty(ime))
                    {
                        query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                    }
                    if (!string.IsNullOrEmpty(grad))
                    {
                        query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                    }

                    if (!string.IsNullOrEmpty(naselje))
                    {
                        query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                    }

                    // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                    if (!string.IsNullOrEmpty(ulica))
                    {
                        query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                    }

                    if (rola == 3 || rola == 4 || rola == 5)
                    {
                        query3 = query3.Where(l => l.RolaId == rola);
                    }

                    var brojKorisnika = query3.Count();

                    var result = query3
                        .Skip(pageSize * pageIndex - pageSize)
                        .Take(pageSize)
                        .Select(q => new UserTabelaDTO(q.Id, q.Ime + " " + q.Prezime, q.Grad, q.Naselje, q.Ulica , q.Potrosnja, q.Proizvodnja, q.Rola, brojKorisnika))
                        .ToList();


                    return result;
                }
                // ako je samo po potrosnji (ne vraca mi proizvodnju kod korisnika posle)
                else if (!string.IsNullOrEmpty(potrosnjaOdStr) || !string.IsNullOrEmpty(potrosnjaDoStr))
                {
                    var potrosnjaOd = potrosnjaOdStr == null ? 0.0 : double.Parse(potrosnjaOdStr);
                    var potrosnjaDo = potrosnjaDoStr == null ? 0.0 : double.Parse(potrosnjaDoStr);

                    var query1 = query
                                    .Where(i => i.TipUredjaja == (int)ETipUredjaj.Potrosac && i.Vrednost >= potrosnjaOd * 1000 && i.Vrednost < potrosnjaDo * 1000)
                                    .Select(istorija => new
                                    {
                                        UserId = istorija.UserId,
                                        Potrosnja = istorija.Vrednost,

                                    })
                                    .AsQueryable();

                    //var query2 = query1.Join(query, k => k.UserId,
                    //                            q => q.UserId,
                    //                            (k, q) => new { k, q }
                    //                    )
                    //            .Where(i => i.k.Potrosnja != i.q.Vrednost)
                    //            .Select(i => new
                    //            {
                    //                UserId = i.k.UserId,
                    //                Potrosnja = i.k.Potrosnja,
                    //                Proizvodnja = i.q.Vrednost
                    //            });


                    var query3 = _context.Korisnik
                                .Include(u => u.Ulica)
                                .ThenInclude(n => n.Naselje)
                                .ThenInclude(g => g.Grad)
                                .Include(r => r.Rola)
                                .Join(query1, k => k.Id,
                                                q => q.UserId,
                                                (k, q) => new
                                                {
                                                    k.Id,
                                                    k.Ime,
                                                    k.Prezime,
                                                    Grad = k.Ulica.Naselje.Grad.Naziv,
                                                    Naselje = k.Ulica.Naselje.Naziv,
                                                    Ulica = k.Ulica.Naziv + " " + k.AdresniBroj,
                                                    RolaId = k.Rola.Id,
                                                    Rola = k.Rola.Naziv,
                                                    Potrosnja = 0,
                                                    Proizvodnja = 0
                                                }
                                        );


                    if (!string.IsNullOrEmpty(ime))
                    {
                        query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                    }
                    if (!string.IsNullOrEmpty(grad))
                    {
                        query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                    }

                    if (!string.IsNullOrEmpty(naselje))
                    {
                        query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                    }

                    // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                    if (!string.IsNullOrEmpty(ulica))
                    {
                        query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                    }

                    if (rola == 3 || rola == 4 || rola == 5)
                    {
                        query3 = query3.Where(l => l.RolaId == rola);
                    }

                    var brojKorisnika = query3.Count();

                    var result = query3
                        .Skip(pageSize * pageIndex - pageSize)
                        .Take(pageSize)
                        .Select(q => new UserTabelaDTO(q.Id, q.Ime + " " + q.Prezime, q.Grad, q.Naselje, q.Ulica, q.Potrosnja, q.Proizvodnja, q.Rola, brojKorisnika))
                        .ToList();


                    return result;
                }
                // ako je samo po proizvodnji
                else if (!string.IsNullOrEmpty(proizvodnjaOdStr) || !string.IsNullOrEmpty(proizvodnjaDoStr)) {
                    var proizvodnjaOd = proizvodnjaOdStr == null ? 0 : double.Parse(proizvodnjaOdStr);
                    var proizvodnjaDo = proizvodnjaDoStr == null ? 0 : double.Parse(proizvodnjaDoStr);
                    var query2 = query
                                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Proizvodjac && i.Vrednost >= proizvodnjaOd * 1000 && i.Vrednost < proizvodnjaDo * 1000)
                                .Select(istorija => new
                                {
                                    UserId = istorija.UserId,
                                    Proizvodnja = istorija.Vrednost
                                })
                                .AsQueryable();

                    var query3 = _context.Korisnik
                                .Include(u => u.Ulica)
                                .ThenInclude(n => n.Naselje)
                                .ThenInclude(g => g.Grad)
                                .Include(r => r.Rola)
                                .Join(query2, k => k.Id,
                                                q => q.UserId,
                                                (k, q) => new
                                                {
                                                    k.Id,
                                                    k.Ime,
                                                    k.Prezime,
                                                    Grad = k.Ulica.Naselje.Grad.Naziv,
                                                    Naselje = k.Ulica.Naselje.Naziv,
                                                    Ulica = k.Ulica.Naziv + " " + k.AdresniBroj,
                                                    RolaId = k.Rola.Id,
                                                    Rola = k.Rola.Naziv,
                                                    Potrosnja = 0,
                                                    Proizvodnja = 0
                                                }
                                        );


                    if (!string.IsNullOrEmpty(ime))
                    {
                        query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                    }
                    if (!string.IsNullOrEmpty(grad))
                    {
                        query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                    }

                    if (!string.IsNullOrEmpty(naselje))
                    {
                        query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                    }

                    // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                    if (!string.IsNullOrEmpty(ulica))
                    {
                        query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                    }

                    if (rola == 3 || rola == 4 || rola == 5)
                    {
                        query3 = query3.Where(l => l.RolaId == rola);
                    }

                    var brojKorisnika = query3.Count();

                    var result = query3
                        .Skip(pageSize * pageIndex - pageSize)
                        .Take(pageSize)
                        .Select(q => new UserTabelaDTO(q.Id, q.Ime + " " + q.Prezime, q.Grad, q.Naselje, q.Ulica, q.Potrosnja, q.Proizvodnja, q.Rola, brojKorisnika))
                        .ToList();


                    return result;
                }
            }
            // nista sa potrosnjom i proizvodnjom
            else
            {
                var query3 = _context.Korisnik
                        .Include(u => u.Ulica)
                        .ThenInclude(n => n.Naselje)
                        .ThenInclude(g => g.Grad)
                        .Include(r => r.Rola)
                        .Where(r => r.RolaId != 1 && r.RolaId != 2)
                        .Select(k => new
                        {
                            k.Id,
                            k.Ime,
                            k.Prezime,
                            Grad = k.Ulica.Naselje.Grad.Naziv,
                            Naselje = k.Ulica.Naselje.Naziv,
                            Ulica = k.Ulica.Naziv + " " + k.AdresniBroj,
                            RolaId = k.Rola.Id,
                            Rola = k.Rola.Naziv,
                            Potrosnja = 0,
                            Proizvodnja = 0
                        }).AsQueryable();


                if (!string.IsNullOrEmpty(ime))
                {
                    query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                }
                if (!string.IsNullOrEmpty(grad))
                {
                    query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                }

                if (!string.IsNullOrEmpty(naselje))
                {
                    query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                }

                // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                if (!string.IsNullOrEmpty(ulica))
                {
                    query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                }

                if (rola == 3 || rola == 4 || rola == 5)
                {
                    query3 = query3.Where(l => l.RolaId == rola);
                }

                var brojKorisnika = query3.Count();

                var result = query3
                    .Skip(pageSize * pageIndex - pageSize)
                    .Take(pageSize)
                    .Select(q => new UserTabelaDTO(q.Id, q.Ime + " " + q.Prezime, q.Grad, q.Naselje, q.Ulica, q.Potrosnja, q.Proizvodnja, q.Rola, brojKorisnika))
                    .ToList();


                return result;
            }

            return null;

        }











        public async Task<List<DispatcherDTO>> getFilteredDispatchers(int pageIndex, int pageSize,
                                                                        string ime = null, string grad = null, string naselje = null, string ulica = null)
        {
            var query = _context.Korisnik
                            .Include(u => u.Ulica)
                            .ThenInclude(n => n.Naselje)
                            .ThenInclude(g => g.Grad)
                            .Include(r => r.Rola)
                            .Where(l => l.RolaId == 2)
                            .AsQueryable();

            if (!string.IsNullOrEmpty(ime))
            {
                query = query.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
            }

            if (!string.IsNullOrEmpty(grad))
            {
                query = query.Where(l => l.Ulica.Naselje.Grad.Naziv.ToLower().Contains(grad.ToLower()));
            }

            if (!string.IsNullOrEmpty(naselje))
            {
                query = query.Where(l => l.Ulica.Naselje.Naziv.ToLower().Contains(naselje.ToLower()));
            }

            // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
            if (!string.IsNullOrEmpty(ulica))
            {
                query = query.Where(l => l.Ulica.Naziv.ToLower().Contains(ulica.ToLower())
                                            || l.AdresniBroj.Contains(ulica.ToLower())
                                           );
            }

            var brojKorisnika = query.Count();

            var result = query
                .Skip(pageSize * pageIndex - pageSize)
                .Take(pageSize)
                .Select(q => new DispatcherDTO(q.Id, q.Ime + " " + q.Prezime, q.Ulica.Naselje.Grad.Naziv, q.Ulica.Naselje.Naziv, q.Ulica.Naziv + " " + q.AdresniBroj, brojKorisnika))
                .ToList();

            return result;
        }












        public async Task<List<MapDTO>> getFilteredUsersForMap(string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null)
        {
            //AKO JE I PO PROIZVODNJI I POTROSNJI

            if (!string.IsNullOrEmpty(potrosnjaOdStr) || !string.IsNullOrEmpty(potrosnjaDoStr) || !string.IsNullOrEmpty(proizvodnjaOdStr) || !string.IsNullOrEmpty(proizvodnjaDoStr))
            {

                DateTime temp = DateTime.Now;
                DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
                DateOnly start = new DateOnly(temp.Year, temp.Month, 01);
                TimeOnly time2 = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
                var query = _context.IstorijaP
                    .Include(istorija => istorija.ObjekatUredjaj)
                    .ThenInclude(istorija => istorija.Uredjaj)
                    .Where(istorija => ((istorija.Datum.Day < date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year)
                                        || (istorija.Datum.Day == date.Day && istorija.Datum.Month == date.Month && istorija.Datum.Year == date.Year && istorija.Vreme < time2)))
                    .GroupBy(istorija => new
                    {
                        istorija.Datum.Month,
                        istorija.ObjekatUredjaj.Objekat.KorisnikId,
                        istorija.ObjekatUredjaj.Uredjaj.TipUredjajaId,
                    })
                    .Select(istorija => new
                    {
                        UserId = istorija.Key.KorisnikId,
                        TipUredjaja = istorija.Key.TipUredjajaId,
                        //MesecnoGrup = istorija.Key.Month,
                        Vrednost = istorija.Sum(q => q.VrednostRealizacije)
                    })
                    .AsQueryable();

                //// vracamo min i max za potrosnju
                //var minmaxPotrosnja = query
                //                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Potrosac).OrderByDescending(g => g.Vrednost);
                //var minPotrosnja = minmaxPotrosnja.FirstOrDefault().Vrednost;
                //var maxPotrosnja = minmaxPotrosnja.LastOrDefault().Vrednost;

                //// vracamo min i max za proizvodnju
                //var minmaxProizvodja = query
                //                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Proizvodjac).OrderByDescending(g => g.Vrednost);
                //var minProizvodnja = minmaxProizvodja.FirstOrDefault().Vrednost;
                //var maxProizvodnja = minmaxProizvodja.LastOrDefault().Vrednost;


                // ako je i po proizvodji i po potrosnji
                if ((!string.IsNullOrEmpty(potrosnjaOdStr) || !string.IsNullOrEmpty(potrosnjaDoStr)) && (!string.IsNullOrEmpty(proizvodnjaOdStr) || !string.IsNullOrEmpty(proizvodnjaDoStr)))
                {
                    var potrosnjaOd = potrosnjaOdStr == null ? 0.0 : double.Parse(potrosnjaOdStr);
                    var potrosnjaDo = potrosnjaDoStr == null ? 0.0 : double.Parse(potrosnjaDoStr);
                    var proizvodnjaOd = proizvodnjaOdStr == null ? 0 : double.Parse(proizvodnjaOdStr);
                    var proizvodnjaDo = proizvodnjaDoStr == null ? 0 : double.Parse(proizvodnjaDoStr);

                    var query1 = query
                                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Potrosac && i.Vrednost >= potrosnjaOd * 1000 && i.Vrednost < potrosnjaDo * 1000)
                                .Select(istorija => new
                                {
                                    UserId = istorija.UserId,
                                    Potrosnja = istorija.Vrednost
                                })
                                .AsQueryable();

                    var query2 = query
                                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Proizvodjac && i.Vrednost >= proizvodnjaOd * 1000 && i.Vrednost < proizvodnjaDo * 1000)
                                .Select(istorija => new
                                {
                                    UserId = istorija.UserId,
                                    Proizvodnja = istorija.Vrednost
                                })
                                .AsQueryable();

                    var proba = query1.Join(query2, k => k.UserId,
                                                    q => q.UserId,
                                                    (k, q) => new { k, q }
                                                );

                    var query3 = _context.Korisnik
                                .Include(u => u.Ulica)
                                .ThenInclude(n => n.Naselje)
                                .ThenInclude(g => g.Grad)
                                .Include(r => r.Rola)
                                .Join(proba, k => k.Id,
                                                q => q.k.UserId,
                                                (k, q) => new
                                                {
                                                    k.Id,
                                                    k.Ime,
                                                    k.Prezime,
                                                    Grad = k.Ulica.Naselje.Grad.Naziv,
                                                    Naselje = k.Ulica.Naselje.Naziv,
                                                    Ulica = k.Ulica.Naziv,
                                                    Broj = k.AdresniBroj,
                                                    RolaId = k.Rola.Id,
                                                    Rola = k.Rola.Naziv,
                                                    q.k.Potrosnja,
                                                    q.q.Proizvodnja
                                                }
                                        );


                    if (!string.IsNullOrEmpty(ime))
                    {
                        query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                    }
                    if (!string.IsNullOrEmpty(grad))
                    {
                        query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                    }

                    if (!string.IsNullOrEmpty(naselje))
                    {
                        query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                    }

                    // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                    if (!string.IsNullOrEmpty(ulica))
                    {
                        query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                    }

                    if (rola == 3 || rola == 4 || rola == 5)
                    {
                        query3 = query3.Where(l => l.RolaId == rola);
                    }

                    var brojKorisnika = query3.Count();

                    var result = query3
                        .Select(q => new MapDTO(q.Id, q.Ime + " " + q.Prezime, q.Ulica, q.Broj, q.Grad, q.Rola))
                        .ToList();


                    return result;
                }
                // ako je samo po potrosnji (ne vraca mi proizvodnju kod korisnika posle)
                else if (!string.IsNullOrEmpty(potrosnjaOdStr) || !string.IsNullOrEmpty(potrosnjaDoStr))
                {
                    var potrosnjaOd = potrosnjaOdStr == null ? 0.0 : double.Parse(potrosnjaOdStr);
                    var potrosnjaDo = potrosnjaDoStr == null ? 0.0 : double.Parse(potrosnjaDoStr);

                    var query1 = query
                                    .Where(i => i.TipUredjaja == (int)ETipUredjaj.Potrosac && i.Vrednost >= potrosnjaOd * 1000 && i.Vrednost < potrosnjaDo * 1000)
                                    .Select(istorija => new
                                    {
                                        UserId = istorija.UserId,
                                        Potrosnja = istorija.Vrednost,

                                    })
                                    .AsQueryable();

                    //var query2 = query1.Join(query, k => k.UserId,
                    //                            q => q.UserId,
                    //                            (k, q) => new { k, q }
                    //                    )
                    //            .Where(i => i.k.Potrosnja != i.q.Vrednost)
                    //            .Select(i => new
                    //            {
                    //                UserId = i.k.UserId,
                    //                Potrosnja = i.k.Potrosnja,
                    //                Proizvodnja = i.q.Vrednost
                    //            });


                    var query3 = _context.Korisnik
                                .Include(u => u.Ulica)
                                .ThenInclude(n => n.Naselje)
                                .ThenInclude(g => g.Grad)
                                .Include(r => r.Rola)
                                .Join(query1, k => k.Id,
                                                q => q.UserId,
                                                (k, q) => new
                                                {
                                                    k.Id,
                                                    k.Ime,
                                                    k.Prezime,
                                                    Grad = k.Ulica.Naselje.Grad.Naziv,
                                                    Naselje = k.Ulica.Naselje.Naziv,
                                                    Ulica = k.Ulica.Naziv,
                                                    Broj = k.AdresniBroj,
                                                    RolaId = k.Rola.Id,
                                                    Rola = k.Rola.Naziv,
                                                    Potrosnja = 0,
                                                    Proizvodnja = 0
                                                }
                                        );


                    if (!string.IsNullOrEmpty(ime))
                    {
                        query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                    }
                    if (!string.IsNullOrEmpty(grad))
                    {
                        query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                    }

                    if (!string.IsNullOrEmpty(naselje))
                    {
                        query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                    }

                    // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                    if (!string.IsNullOrEmpty(ulica))
                    {
                        query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                    }

                    if (rola == 3 || rola == 4 || rola == 5)
                    {
                        query3 = query3.Where(l => l.RolaId == rola);
                    }

                    var brojKorisnika = query3.Count();

                    var result = query3
                        .Select(q => new MapDTO(q.Id, q.Ime + " " + q.Prezime, q.Ulica, q.Broj, q.Grad, q.Rola))
                        .ToList();


                    return result;
                }
                // ako je samo po proizvodnji
                else if (!string.IsNullOrEmpty(proizvodnjaOdStr) || !string.IsNullOrEmpty(proizvodnjaDoStr))
                {
                    var proizvodnjaOd = proizvodnjaOdStr == null ? 0 : double.Parse(proizvodnjaOdStr);
                    var proizvodnjaDo = proizvodnjaDoStr == null ? 0 : double.Parse(proizvodnjaDoStr);
                    var query2 = query
                                .Where(i => i.TipUredjaja == (int)ETipUredjaj.Proizvodjac && i.Vrednost >= proizvodnjaOd * 1000 && i.Vrednost < proizvodnjaDo * 1000)
                                .Select(istorija => new
                                {
                                    UserId = istorija.UserId,
                                    Proizvodnja = istorija.Vrednost
                                })
                                .AsQueryable();

                    var query3 = _context.Korisnik
                                .Include(u => u.Ulica)
                                .ThenInclude(n => n.Naselje)
                                .ThenInclude(g => g.Grad)
                                .Include(r => r.Rola)
                                .Join(query2, k => k.Id,
                                                q => q.UserId,
                                                (k, q) => new
                                                {
                                                    k.Id,
                                                    k.Ime,
                                                    k.Prezime,
                                                    Grad = k.Ulica.Naselje.Grad.Naziv,
                                                    Naselje = k.Ulica.Naselje.Naziv,
                                                    Ulica = k.Ulica.Naziv,
                                                    Broj = k.AdresniBroj,
                                                    RolaId = k.Rola.Id,
                                                    Rola = k.Rola.Naziv,
                                                    Potrosnja = 0,
                                                    Proizvodnja = 0
                                                }
                                        );


                    if (!string.IsNullOrEmpty(ime))
                    {
                        query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                    }
                    if (!string.IsNullOrEmpty(grad))
                    {
                        query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                    }

                    if (!string.IsNullOrEmpty(naselje))
                    {
                        query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                    }

                    // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                    if (!string.IsNullOrEmpty(ulica))
                    {
                        query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                    }

                    if (rola == 3 || rola == 4 || rola == 5)
                    {
                        query3 = query3.Where(l => l.RolaId == rola);
                    }

                    var brojKorisnika = query3.Count();

                    var result = query3
                        .Select(q => new MapDTO(q.Id, q.Ime + " " + q.Prezime, q.Ulica, q.Broj, q.Grad, q.Rola))
                        .ToList();


                    return result;
                }
            }
            // nista sa potrosnjom i proizvodnjom
            else
            {
                var query3 = _context.Korisnik
                        .Include(u => u.Ulica)
                        .ThenInclude(n => n.Naselje)
                        .ThenInclude(g => g.Grad)
                        .Include(r => r.Rola)
                        .Where(r => r.RolaId != 1 && r.RolaId != 2)
                        .Select(k => new
                        {
                            k.Id,
                            k.Ime,
                            k.Prezime,
                            Grad = k.Ulica.Naselje.Grad.Naziv,
                            Naselje = k.Ulica.Naselje.Naziv,
                            Ulica = k.Ulica.Naziv,
                            Broj = k.AdresniBroj,
                            RolaId = k.Rola.Id,
                            Rola = k.Rola.Naziv,
                            Potrosnja = 0,
                            Proizvodnja = 0
                        }).AsQueryable();


                if (!string.IsNullOrEmpty(ime))
                {
                    query3 = query3.Where(l => l.Ime.ToLower().Contains(ime.ToLower()) || l.Prezime.ToLower().Contains(ime.ToLower()));
                }
                if (!string.IsNullOrEmpty(grad))
                {
                    query3 = query3.Where(l => l.Grad.ToLower().Contains(grad.ToLower()));
                }

                if (!string.IsNullOrEmpty(naselje))
                {
                    query3 = query3.Where(l => l.Naselje.ToLower().Contains(naselje.ToLower()));
                }

                // ovde mora izmena za strazenje npr stef 25 -> ovo sad ne prolazi to
                if (!string.IsNullOrEmpty(ulica))
                {
                    query3 = query3.Where(l => l.Ulica.ToLower().Contains(ulica.ToLower()));
                }

                if (rola == 3 || rola == 4 || rola == 5)
                {
                    query3 = query3.Where(l => l.RolaId == rola);
                }

                var brojKorisnika = query3.Count();

                var result = query3
                    .Select(q => new MapDTO(q.Id, q.Ime + " " + q.Prezime, q.Ulica, q.Broj, q.Grad, q.Rola))
                    .ToList();


                return result;
            }

            return null;

        }
    }
}
