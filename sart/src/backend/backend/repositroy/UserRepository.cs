using backend.Context;
using backend.repositroy.interfaces;
using backend.Models;
using Microsoft.EntityFrameworkCore;
using backend.DTOs;
using backend.Common;
using static System.Runtime.InteropServices.JavaScript.JSType;
using System.Collections.Generic;
using System.IO.Pipelines;

namespace backend.repositroy
{
    // update, DELETE nemaju task jer nisu async metode i one ne mogu da se izvrsavaju asinhrono moraju sinhrono
    public class UserRepository : IUserRepository
    {
        private readonly AppDbContext _context;
        private readonly IObjectRepository _objectRepository;
        private readonly IUlicaRepository _ulicaRepository;
        private readonly INaseljeRepository _naseljeRepository;
        private readonly IGradRepository _gradRepository;
        private readonly IRolaRepository _rolaRepository;

        public UserRepository(AppDbContext context, IObjectRepository objectRepository, IUlicaRepository ulicaRepository, INaseljeRepository naseljeRepository, IGradRepository gradRepository, IRolaRepository rolaRepository)
        {
            _context = context;
            _objectRepository = objectRepository;
            _ulicaRepository = ulicaRepository;
            _naseljeRepository = naseljeRepository;
            _gradRepository = gradRepository;
            _rolaRepository = rolaRepository;
        }

        public async Task<Korisnik> getUserByEmail(string email)
        {
            Korisnik user = await _context.Korisnik
                .Include(u => u.Rola)
                .Where(x => x.Email == email)
                .FirstOrDefaultAsync();

            return user;
        }

        public async Task<Korisnik> getUserByEmailDSO(string email)
        {
            Korisnik user = await _context.Korisnik
                .Include(u => u.Rola)
                .Where(x => x.Email == email && (x.RolaId == 2 || x.RolaId == 1))
                .FirstOrDefaultAsync();

            return user;
        }

        public async Task<Korisnik> getUserByEmailProsumer(string email)
        {
            Korisnik user = await _context.Korisnik
                .Include(u => u.Rola)
                .Where(x => x.Email == email && (x.RolaId == 3 || x.RolaId == 4 || x.RolaId == 5))
                .FirstOrDefaultAsync();

            return user;
        }

        public async Task editUserForResetPassword(Korisnik korisnik)
        {
            _context.Korisnik.Entry(korisnik).State = EntityState.Modified;
            await _context.SaveChangesAsync();
        }

        public async Task<int> Insert(Korisnik user)
        {
            await _context.Korisnik.AddAsync(user);
            await _context.SaveChangesAsync();

            return user.Id;
        }

        public async Task<List<Korisnik>> getAllUsers()
        {
            return await _context.Korisnik
                .Include(r => r.Rola)
                .Where(r => r.RolaId != 1 && r.RolaId != 2)
                .Include(u => u.Ulica)
                    .ThenInclude(n => n.Naselje)
                    .ThenInclude(g => g.Grad)
                .ToListAsync();
        }

        public async Task<List<Korisnik>> getAllUsers(PagingDTO paging)
        {
            return await _context.Korisnik
                .Include(r => r.Rola)
                .Where(r => r.RolaId != 1 && r.RolaId != 2)
                .Skip(paging.PageSize * paging.PageIndex - paging.PageSize)
                .Take(paging.PageSize)
                .Include(u => u.Ulica)
                    .ThenInclude(n => n.Naselje)
                    .ThenInclude(g => g.Grad)
                .ToListAsync();
        }

        public async Task<Korisnik> getUserById(int id)
        {
            Korisnik user = await _context.Korisnik
                .Where(x => x.Id == id)
                .Include(r => r.Rola)
                .Include(u => u.Ulica)
                    .ThenInclude(n => n.Naselje)
                    .ThenInclude(g => g.Grad)
                .FirstOrDefaultAsync();

            return user;
        }

        public int countAllUsers()
        {
            var count = _context.Korisnik
                        .Count(t => t.RolaId != 1 && t.RolaId != 2);

            return count;
        }

        public int countProzumer()
        {
            var count = _context.Korisnik
                        .Count(t => t.RolaId == 3);

            return count;
        }

        public int countPotrosaci()
        {
            var count = _context.Korisnik
                        .Count(t => t.RolaId == 4);

            return count;
        }

        public int countProizvodjaci()
        {
            var count = _context.Korisnik
                        .Count(t => t.RolaId == 5);

            return count;
        }

        public async Task deleteUserAsync(int userId)
        {
            List<Objekat> objects = await _objectRepository.GetObjectByUserIdAsync(userId);

            foreach (Objekat objekat in objects)
            {
                await _objectRepository.deleteObjectByIdAsync(objekat.Id);
            }
            Korisnik user = _context.Set<Korisnik>().Find(userId);
            if (user != null)
            {
                _context.Set<Korisnik>().Remove(user);
                _context.SaveChanges();
            }
        }

        // objekat na koji je korisnik prijavljen
        public int objekatByUser(int id)
        {
            return _context.Korisnik
                .Where(u => u.Id == id)
                .Join(_context.Objekat, k => k.UlicaId,
                                        q => q.UlicaId,
                                        (k, q) => new { k, q })
                .Select(u => u.q.Id).FirstOrDefault();
        }

        public async Task editUserAsync(UserCeoDTO userObj) 
        {
            var person = await _context.Korisnik.FirstOrDefaultAsync(p => p.Id == userObj.Id);
            if (person == null)
                return;
            else
            {
                string[] fullName = userObj.Ime.Split(' ');
                string ime = fullName[0];
                string prezime = fullName[1];
                

                Ulica userStreet = await _ulicaRepository.getUlicaById(person.UlicaId);
                
                
                
                Grad city = await _gradRepository.getGradByNaziv(userObj.Grad);
                if (city is null)
                {
                    Grad newCity = new Grad();
                    newCity.Naziv = userObj.Grad;
                    _context.Grad.AddAsync(newCity);
                    _context.SaveChangesAsync();
                    newCity = await _gradRepository.getGradByNaziv(userObj.Grad);
                    
                    Naselje newSettlement = new Naselje();
                    newSettlement.Naziv = userObj.Naselje;
                    newSettlement.GradId = newCity.Id;
                    _context.Naselje.AddAsync(newSettlement); 
                    _context.SaveChangesAsync();
                    newSettlement = await _naseljeRepository.getNaseljeByNaziv(userObj.Naselje, newCity.Id);

                    Ulica newStreet = new Ulica();
                    newStreet.Naziv = userObj.Ulica;
                    newStreet.NaseljeId = newSettlement.Id;
                    _context.Ulica.AddAsync(newStreet); 
                    _context.SaveChangesAsync();

                    newStreet = await _ulicaRepository.getUlicaByNaziv(userObj.Ulica, newSettlement.Id);
                    person.UlicaId = newStreet.Id;
                }
                else
                {
                    Naselje settlement = await _naseljeRepository.getNaseljeByNaziv(userObj.Naselje, city.Id);
                    if (settlement is null) 
                    {
                        Naselje newSettlement = new Naselje();
                        newSettlement.Naziv = userObj.Naselje;
                        newSettlement.GradId = city.Id;
                        _context.Naselje.AddAsync(newSettlement);
                        _context.SaveChangesAsync();
                        newSettlement = await _naseljeRepository.getNaseljeByNaziv(userObj.Naselje, city.Id);

                        Ulica newStreet = new Ulica();
                        newStreet.Naziv = userObj.Ulica;
                        newStreet.NaseljeId = newSettlement.Id;
                        _context.Ulica.AddAsync(newStreet);
                        _context.SaveChangesAsync();

                        newStreet = await _ulicaRepository.getUlicaByNaziv(userObj.Ulica, newSettlement.Id);
                        person.UlicaId = newStreet.Id;
                    }
                    else
                    {
                        Ulica street = await _ulicaRepository.getUlicaByNaziv(userObj.Ulica, settlement.Id);
                        if(street is null) 
                        {
                            Ulica newStreet = new Ulica();
                            newStreet.Naziv = userObj.Ulica;
                            newStreet.NaseljeId = settlement.Id;
                            _context.Ulica.AddAsync(newStreet);
                            _context.SaveChangesAsync();

                            newStreet = await _ulicaRepository.getUlicaByNaziv(userObj.Ulica, settlement.Id);
                            person.UlicaId = newStreet.Id;
                        }
                        else if (!userStreet.Equals(userObj.Ulica))
                        { 
                            person.UlicaId = street.Id;
                        }
                    }

                }

                if (!person.Ime.Equals(ime))
                    person.Ime = ime;
                if (!person.Prezime.Equals(prezime))
                    person.Prezime = prezime;
                if (!person.Email.Equals(userObj.Email))
                    person.Email = userObj.Email;
                if (!person.JMBG.Equals(userObj.Jmbg))
                    person.JMBG = userObj.Jmbg;
                if (!person.BrTelefona.Equals(userObj.BrTelefona))
                    person.BrTelefona = userObj.BrTelefona;
                if (!person.AdresniBroj.Equals(userObj.AdresniBroj))
                    person.AdresniBroj = userObj.AdresniBroj;

                Rola rola = await _rolaRepository.getRolaById(person.RolaId);
                if (!rola.Equals(userObj.Rola))
                {
                    Rola newRola = await _rolaRepository.getRolaByNaziv(userObj.Rola);
                    person.RolaId = newRola.Id;
                }


                _context.SaveChangesAsync();
            }
        }

        public async Task<List<UserTabelaDTO>> getTopUsersAsync(ETipUredjaj tipUredjaja)
        {
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            DateOnly start = new DateOnly(temp.Year, temp.Month, 01);
            List<UserTabelaDTO> lista = new List<UserTabelaDTO>();


            var conn = _context.Database.GetDbConnection();
            using (var command = conn.CreateCommand())
            {
                conn.Open();
                
                command.CommandText = " select kId, Ime || ' ' || Prezime as 'ime' , ukupnaVrednost " +
                                      " from " +
                                      " (select Objekat.KorisnikId as kId,  sum( IstorijaP.vrednostRealizacije ) as ukupnaVrednost" +
                                      " from istorijaP JOIN (ObjekatUredjaj JOIN Objekat on Objekat.Id = ObjekatUredjaj.ObjekatId JOIN Uredjaj on Uredjaj.Id = ObjekatUredjaj.UredjajId) on ObjekatUredjaj.Id = IstorijaP.ObjekatUredjajId" +
                                      " where IstorijaP.Datum <= '" + date.ToString("yyyy-MM-dd") + "' and IstorijaP.Datum >= '" + start.ToString("yyyy-MM-dd") + "' and Uredjaj.TipUredjajaId = '" + (int)tipUredjaja + "' " +
                                      " group by Objekat.KorisnikId) join Korisnik on Korisnik.Id = kId" +
                                      " ORDER BY ukupnaVrednost DESC" +
                                      " LIMIT 3";

                using (var reader = command.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        if(tipUredjaja == ETipUredjaj.Potrosac)
                            lista.Add(new UserTabelaDTO(reader.GetInt32(0), reader.GetString(1), Math.Round(reader.GetDouble(2)/1000, 2), 0));
                        else
                            lista.Add(new UserTabelaDTO(reader.GetInt32(0), reader.GetString(1), 0, Math.Round(reader.GetDouble(2) / 1000, 2)));
                    }
                }
                conn.Close();
            }


            
            return lista;
             
        }


    }
}
