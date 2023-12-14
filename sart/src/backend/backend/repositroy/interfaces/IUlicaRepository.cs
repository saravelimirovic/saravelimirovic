using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IUlicaRepository
    {
        Task<Ulica> getUlicaById(int id);
        Task<Ulica> getUlicaByNaziv(string naziv, int naseljeId);
        Task<bool> Insert(Ulica ulica);
    }
}
