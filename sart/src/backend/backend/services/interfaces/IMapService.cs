using backend.DTOs;

namespace backend.services.interfaces
{
    public interface IMapService
    {
        Task<List<MapDTO>> getUsersForMaps();

        Task<List<MapDTO>> getObjectForMapsForUserId(int userId);
    }
}
