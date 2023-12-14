using backend.DTOs;
using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IProstorijaRepository
    {
        Task<Prostorija> getProstorijaByNazivAsync(string naziv);
        Task<Prostorija> getProstorijaByIdAsync(int id);
        Task<List<ProstorijaDTO>> getAllProstorijaAsync(int objekatId);
        Task<bool> InsertProstorijaAsync(Prostorija prostorija);
    }
}
