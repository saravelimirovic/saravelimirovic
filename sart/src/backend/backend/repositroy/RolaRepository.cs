using backend.Context;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend.repositroy
{
    public class RolaRepository : IRolaRepository
    {
        private readonly AppDbContext _context;
        public RolaRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<Rola> getRolaById(int id)
        {
            return await _context.Rola.Where(x => x.Id == id).FirstOrDefaultAsync();
        }

        public async Task<Rola> getRolaByNaziv(string naziv)
        {
            return await _context.Rola.Where(x => x.Naziv == naziv).FirstOrDefaultAsync();
        }
    }
}
