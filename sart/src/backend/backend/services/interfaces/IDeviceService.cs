using backend.Common;
using backend.DTOs;
using backend.Models;

namespace backend.services.interfaces
{
    public interface IDeviceService
    {
        Task<List<DeviceDTO>> getUredjajeForObjectForAllProstorija(int objekatId);
        Task<List<DeviceDTO>> getUredjajeForObjectForOneProstorija(int objekatId, string prostorija);
        Task<List<ProstorijaDTO>> getAllProstorijaForObject(int objekatId);
        Task<DeviceDTO> getInfoAboutDeviceById(int objekatUredjajId);
        Task InsertDeviceInObject(ObjectDeviceDTO objectDeviceDTO);
        Task<List<StoreDTO>> getSkladistaForObject(int objekatId);
        Task<double> getSumOfSkladisteForObject(int objekatId);
        Task updateDeviceState(int objekatUredjajId);
        Task updateDeviceControl(int objekatUredjajId);
        Task updateDevicePermission(int objekatUredjajId);
        Task deleteDeviceById(int objekatUredjajId);
        Task deleteStoreById(int objekatSkladisteId);
        Task<List<VrstaUredjaja>> getVrsteUredjaja();
        Task<List<VrstaUredjajaDTO>> getVrstaByTipId(ETipUredjaj tipUredjaja);
        Task<List<Uredjaj>> getUredjajiByVrstaId(int vrstaId);
        Task<List<StoreDTO>> getAllStore();
        Task AddStore(StoreDTO store);
        Task<List<Prostorija>> getAllProstorije();
    }
}
