using backend.Context;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend.repositroy
{
    public class NaseljeRepository : INaseljeRepository
    {
        private readonly AppDbContext _context;
        public NaseljeRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<Naselje> getNaseljeById(int id)
        {
            return await _context.Naselje
                    .Where(x => x.Id == id)
                    .FirstOrDefaultAsync();
        }

        public async Task<Naselje> getNaseljeByNaziv(string naziv, int gradId)
        {
            return await _context.Naselje
                    .Where(x => x.Naziv == naziv && x.GradId == gradId)
                    .FirstOrDefaultAsync();
        }

        public async Task<bool> Insert(Naselje naselje)
        {
            if (await this.getNaseljeByNaziv(naselje.Naziv, naselje.GradId) == null)
            {
                await _context.Naselje.AddAsync(naselje);
                await _context.SaveChangesAsync();

                return true;
            }

            return false;
        }
    }
}
