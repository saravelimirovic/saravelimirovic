using backend.Context;
using backend.DTOs;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend.repositroy
{
    public class ProstorijaRepository : IProstorijaRepository
    {
        private readonly AppDbContext _context;
        public ProstorijaRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<Prostorija> getProstorijaByIdAsync(int id)
        {
            return await _context.Prostorija
                    .Where(x => x.Id == id)
                    .FirstOrDefaultAsync();
        }

        public async Task<Prostorija> getProstorijaByNazivAsync(string naziv)
        {
            return await _context.Prostorija
                    .Where(x => x.Naziv.ToUpper() ==  naziv.ToUpper())
                    .FirstOrDefaultAsync();
        }

        public async Task<List<ProstorijaDTO>> getAllProstorijaAsync(int objekatId)
        {
            List<ProstorijaDTO> lista = await _context.ObjekatUredjaj
                                        .Include(room => room.Prostorija)
                                        .Where(room => room.ObjekatId == objekatId)
                                        .Select(room => new ProstorijaDTO(room.ProstorijaId, room.Prostorija.Naziv))
                                        .Distinct()
                                        .ToListAsync();
            return lista;
        }

        public async Task<bool> InsertProstorijaAsync(Prostorija prostorija)
        {
            if (await this.getProstorijaByNazivAsync(prostorija.Naziv) == null)
            {
                await _context.Prostorija.AddAsync(prostorija);
                await _context.SaveChangesAsync();

                return true;
            }

            return false;
        }
    }
}
