using backend.Context;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend.repositroy
{
    public class GradRepository : IGradRepository
    {
        private readonly AppDbContext _context;
        public GradRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<Grad> getGradById(int id)
        {
            return await _context.Grad
                    .Where(x => x.Id == id)
                    .FirstOrDefaultAsync();
        }

        public async Task<Grad> getGradByNaziv(string naziv)
        {
            return await _context.Grad
                    .Where(x => x.Naziv == naziv)
                    .FirstOrDefaultAsync();
        }

        public async Task<bool> Insert(Grad grad)
        {
            if (await this.getGradByNaziv(grad.Naziv) == null)
            {
                await _context.Grad.AddAsync(grad);
                await _context.SaveChangesAsync();

                return true;
            }

            return false;
        }
    }
}
