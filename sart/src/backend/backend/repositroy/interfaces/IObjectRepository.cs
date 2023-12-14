using backend.DTOs;
using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IObjectRepository
    {
        Task<List<Objekat>> GetObjectByUserIdAsync(int id);
        Task Insert(Objekat obj);
        Task deleteObjectByIdAsync(int objectID);
    }
}
