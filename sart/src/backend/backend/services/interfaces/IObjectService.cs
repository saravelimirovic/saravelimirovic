using backend.DTOs;

namespace backend.services.interfaces
{
    public interface IObjectService
    {
        Task<List<ObjectDTO>> getObjectByUserId(int id);

        Task addNewObject(ObjectAddDTO obj);

        Task deleteObjectById(int objectId);

    }
}
