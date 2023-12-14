using backend.Common;
using backend.DTOs;
using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IDeviceRepository
    {
        Task<Uredjaj> getUredjajByNaziv(string naziv);
        Task<List<ObjekatUredjaj>> getUredjajeForObjectAsync(int objekatID);
        Task<Uredjaj> getUredjajByIdAsync(int uredjajId);
        Task<string> getUredjajNazivByIdAsync(int uredjajId);
        Task<double> getUredjajPotrosnjaNowAsync(int objekatUredjajId);
        Task<string> getStatusUredjajaAsync(int objekatUredjajId);
        Task<ObjekatUredjaj> getObjekatUredjajaByIdAsync(int objekatUredjajId);
        Task<List<ObjekatUredjaj>> getUredjajeForObjectForOneProstorijaAsync(int objektaId, int prostorijaId);
        Task<string> getTipUredjajaByIdAsync(int uredjajId);
        Task insertDeviceHistoryAndPredicitionAsync(ObjekatUredjaj objekatUredjaj);
        Task<List<double>> getPotrosnjaForDeviceAsync(int deviceId);
        Task<double> getCekanjeForDeviceAsync(int deviceId);
        Task<int> getObjekatUredjajIdForDeviceAsync();
        Task<string> getVrstaUredjajaByIdAsync(int uredjajId);
        Task<List<ObjekatSkladiste>> getSkladisteForObjectAsync(int objektaId);
        Task<string> getSkladisteNazivByIdAsync(int skladisteId);
        Task<double> getMaxSkladisteByIdAsync(int skladisteId);
        Task<double> getPotrosnjaZaCuvanjeSkladisteByIdAsync(int skladisteId);
        Task<double> getSumOfSkladisteForObjectAsync(int objektaId);
        Task updateDeviceStateAsync(int objekatUredjajId);
        Task updateDeviceControlAsync(int objekatUredjajId);
        Task updateDevicePermissionAsync(int objekatUredjajId);
        Task deleteDeviceByIdAsync(int objekatUredjajId);
        Task deleteStoreByIdAsync(int objekatSkladisteId);
        Task<List<VrstaUredjaja>> getVrsteUredjajaAsync();
        Task insertDeviceAsync(Uredjaj uredjaj);
        Task insertPowerConsumptionAsync(PPoStanjuUredjaja pPoStanjuUredjaja);
        Task<List<VrstaUredjajaDTO>> getVrstaByTipIdAsync (ETipUredjaj tipUredjaja);
        Task<List<Uredjaj>> getUredjajiByVrstaIdAsync(int vrstaId);
        Task<List<StoreDTO>> getAllStoreAsync();
        Task AddStoreAsync(StoreDTO store);

        Task<List<Prostorija>> getAllProstorije();
    }
}
