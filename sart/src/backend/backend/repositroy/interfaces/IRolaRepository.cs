using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IRolaRepository
    {
        Task<Rola> getRolaById(int id);
        Task<Rola> getRolaByNaziv(string naziv);

    }
}
