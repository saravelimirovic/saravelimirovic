using backend.Context;
using backend.DTOs;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend.repositroy
{
    public class ObjectRepository : IObjectRepository
    {

        private readonly AppDbContext _context;

        public ObjectRepository(AppDbContext context)
        {
            _context = context;
        }
        public async Task<List<Objekat>> GetObjectByUserIdAsync(int id)
        {
            List<Objekat> lista = await _context.Objekat
                                  .Where(obj => obj.KorisnikId == id) 
                                  .Include(u => u.Ulica)
                                  .ThenInclude(n => n.Naselje)
                                  .ThenInclude(g => g.Grad)
                                  .ToListAsync();

            return lista;

        }

        public async Task Insert(Objekat obj)
        {
            await _context.Objekat.AddAsync(obj);
            await _context.SaveChangesAsync();
        }

        public async Task deleteObjectByIdAsync(int objectId)
        {
            List<ObjekatUredjaj> objekatUredjaji = await _context.ObjekatUredjaj
                                                   .Where(objekat => objekat.ObjekatId == objectId)
                                                   .ToListAsync();

            foreach (ObjekatUredjaj item in objekatUredjaji)
            {
                _context.Database.ExecuteSqlInterpolated($"DELETE FROM PredikcijaP WHERE ObjekatUredjajId = '{item.Id}'");

                _context.Database.ExecuteSqlInterpolated($"DELETE FROM IstorijaP WHERE ObjekatUredjajId = '{item.Id}'");

            }

            _context.Database.ExecuteSqlInterpolated($"DELETE FROM ObjekatUredjaj WHERE ObjekatId = '{objectId}'");

            _context.Database.ExecuteSqlInterpolated($"DELETE FROM ObjekatSkladiste WHERE ObjekatId = '{objectId}'");

            Objekat objekat = _context.Set<Objekat>().Find(objectId);
            if (objekat != null)
            {
                _context.Set<Objekat>().Remove(objekat);
                _context.SaveChanges();
            }


            _context.SaveChanges();
        }
    }
}
