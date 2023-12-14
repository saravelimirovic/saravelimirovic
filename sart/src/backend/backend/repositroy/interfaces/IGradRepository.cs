using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IGradRepository
    {
        Task<Grad> getGradByNaziv(string naziv);
        Task<Grad> getGradById(int id);
        Task<bool> Insert(Grad grad);
    }
}
