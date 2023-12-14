using backend.Context;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend.repositroy
{
    public class UlicaRepository : IUlicaRepository
    { 
        private readonly AppDbContext _context;
        public UlicaRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<Ulica> getUlicaById(int id)
        {
            return await _context.Ulica
                    .Where(x => x.Id == id)
                    .FirstOrDefaultAsync();
        }

        public async Task<Ulica> getUlicaByNaziv(string naziv, int naseljeId)
        {
            return await _context.Ulica
                    .Where(x => x.Naziv == naziv && x.NaseljeId == naseljeId)
                    .FirstOrDefaultAsync();
        }

        public async Task<bool> Insert(Ulica ulica)
        {
            if (await this.getUlicaByNaziv(ulica.Naziv, ulica.NaseljeId) == null)
            {
                await _context.Ulica.AddAsync(ulica);
                await _context.SaveChangesAsync();

                return true;
            }

            return false;
        }
    }
}
