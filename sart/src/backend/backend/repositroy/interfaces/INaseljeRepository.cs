using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface INaseljeRepository
    {
        Task<Naselje> getNaseljeById(int id);
        Task<Naselje> getNaseljeByNaziv(string naziv, int gradId);
        Task<bool> Insert(Naselje naselje);
    }
}
