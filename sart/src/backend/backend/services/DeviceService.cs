using backend.Common;
using backend.DTOs;
using backend.Helpers.interfaces;
using backend.Models;
using backend.repositroy;
using backend.repositroy.interfaces;
using backend.services.interfaces;

namespace backend.services
{
    public class DeviceService : IDeviceService
    {
        private readonly IDeviceRepository _deviceRepository;
        private readonly IRecordCalculation _recordCalculation;
        private readonly IProstorijaRepository _prostorijaRepository;

        public DeviceService(IDeviceRepository deviceRepository, IRecordCalculation recordCalculation, IProstorijaRepository prostorijaRepository)
        {
            _deviceRepository = deviceRepository;
            _recordCalculation = recordCalculation;
            _prostorijaRepository = prostorijaRepository; 
        }

        // vraca jednu cifru koja obuhvata ukupno skaldisteno vati u objketi
        public async Task<double> getSumOfSkladisteForObject(int objekatId)
        {
            double result = await _deviceRepository.getSumOfSkladisteForObjectAsync(objekatId);

            return result;

        }
        // vraca skladista za jedan objekat
        public async Task<List<DeviceDTO>> getUredjajeForObjectForAllProstorija(int objekatId)
        {
            List<DeviceDTO> lista = new List<DeviceDTO>();
            List<ObjekatUredjaj> uredjajiObjeka = await _deviceRepository.getUredjajeForObjectAsync(objekatId);

            foreach (var uredjajObjekta in uredjajiObjeka)
            {

                string status = await _deviceRepository.getStatusUredjajaAsync(uredjajObjekta.Id);
                string naziv = await _deviceRepository.getUredjajNazivByIdAsync(uredjajObjekta.UredjajId);
                string tipUredjaja = await _deviceRepository.getTipUredjajaByIdAsync(uredjajObjekta.UredjajId);
                double vrednost = await _deviceRepository.getUredjajPotrosnjaNowAsync(uredjajObjekta.Id);
                string vrstaUredaja = await _deviceRepository.getVrstaUredjajaByIdAsync(uredjajObjekta.UredjajId);
                lista.Add(new DeviceDTO(naziv, status, vrednost, uredjajObjekta.Id, tipUredjaja, vrstaUredaja));
            }
            return lista;
        }

        // vraca skladista za jedan objekat
        public async Task<List<DeviceDTO>> getUredjajeForObjectForOneProstorija(int objekatId, string nazivProstorije)
        {
            List<DeviceDTO> lista = new List<DeviceDTO>();
            Prostorija prostorija = await _prostorijaRepository.getProstorijaByNazivAsync(nazivProstorije);
            List<ObjekatUredjaj> uredjajiObjeka = await _deviceRepository.getUredjajeForObjectForOneProstorijaAsync(objekatId, prostorija.Id);

            foreach (var uredjajObjekta in uredjajiObjeka)
            {

                string status = await _deviceRepository.getStatusUredjajaAsync(uredjajObjekta.Id);
                string naziv = await _deviceRepository.getUredjajNazivByIdAsync(uredjajObjekta.UredjajId);
                string tipUredjaja = await _deviceRepository.getTipUredjajaByIdAsync(uredjajObjekta.UredjajId);
                double vrednost = await _deviceRepository.getUredjajPotrosnjaNowAsync(uredjajObjekta.Id);
                string vrstaUredaja = await _deviceRepository.getVrstaUredjajaByIdAsync(uredjajObjekta.UredjajId);
                lista.Add(new DeviceDTO(naziv, status, vrednost, uredjajObjekta.Id, tipUredjaja, vrstaUredaja));
            }
            return lista;
        }


        // vraca uredjaje za jedan objekat
        public async Task<List<StoreDTO>> getSkladistaForObject(int objekatId)
        {
            List<StoreDTO> lista = new List<StoreDTO>();
            List<ObjekatSkladiste> skladistaObjeka = await _deviceRepository.getSkladisteForObjectAsync(objekatId);

            foreach (var skladisteObjeka in skladistaObjeka)
            {
                string naziv = await _deviceRepository.getSkladisteNazivByIdAsync(skladisteObjeka.SkladisteId);
                double maxSkladiste = await _deviceRepository.getMaxSkladisteByIdAsync(skladisteObjeka.SkladisteId);
                double potrosnjaZaCuvanjePoSatu = await _deviceRepository.getPotrosnjaZaCuvanjeSkladisteByIdAsync(skladisteObjeka.SkladisteId);

                lista.Add(new StoreDTO(skladisteObjeka.Id, naziv, skladisteObjeka.TrenutnoStanje, maxSkladiste, potrosnjaZaCuvanjePoSatu));
            }
            return lista;
        }

        // vraca informacije o uredjaju u nekom objektu 
        public async Task<DeviceDTO> getInfoAboutDeviceById(int objekatUredjajId)
        {
            ObjekatUredjaj obj = await _deviceRepository.getObjekatUredjajaByIdAsync(objekatUredjajId);
            double vrednost = await _deviceRepository.getUredjajPotrosnjaNowAsync(objekatUredjajId);

            string naziv = await _deviceRepository.getUredjajNazivByIdAsync(obj.UredjajId);
            string tip = await _deviceRepository.getTipUredjajaByIdAsync(obj.UredjajId);
            string vrstaUredjaja = await _deviceRepository.getVrstaUredjajaByIdAsync(obj.UredjajId);
            return new DeviceDTO(objekatUredjajId, naziv, obj.Ukljucen, vrednost, tip, obj.Dozvola, obj.Kontrola, vrstaUredjaja);

        }


        // unosi uredjaj u objekat i racuna istoriju i predikciju za taj uredjaj
        public async Task InsertDeviceInObject(ObjectDeviceDTO objectDevice)
        {
            ObjekatUredjaj objekatUredjaj = new ObjekatUredjaj();
            Uredjaj uredjaj = await _deviceRepository.getUredjajByNaziv(objectDevice.NazivUredjaja);
            if(uredjaj is null) 
            {
                uredjaj = new Uredjaj();
                uredjaj.Naziv = objectDevice.NazivUredjaja;
                uredjaj.PPrilikomMirovanja = objectDevice.PPrilikomMirovanja;
                uredjaj.TipUredjajaId = objectDevice.TipUredjajaId;
                uredjaj.VrstaUredjajaId = objectDevice.VrstaUredjajaId;
                await _deviceRepository.insertDeviceAsync(uredjaj);
                uredjaj = await _deviceRepository.getUredjajByNaziv(objectDevice.NazivUredjaja);
                PPoStanjuUredjaja pPoStanjuUredjaja = new PPoStanjuUredjaja();
                pPoStanjuUredjaja.UredjajId = uredjaj.Id;
                pPoStanjuUredjaja.Naziv = "";
                pPoStanjuUredjaja.PPoSatuStanja = objectDevice.ProsecnaPotrosnja;
                await _deviceRepository.insertPowerConsumptionAsync(pPoStanjuUredjaja);
            }
            objekatUredjaj.UredjajId = uredjaj.Id;
            objekatUredjaj.ObjekatId = objectDevice.ObjekatId;
            objekatUredjaj.ProstorijaId = objectDevice.ProstorijaId;
            objekatUredjaj.Dozvola = objectDevice.Dozvola;
            objekatUredjaj.Kontrola = objectDevice.Kontrola;
            objekatUredjaj.Ukljucen = objectDevice.Ukljucen;
            await _deviceRepository.insertDeviceHistoryAndPredicitionAsync(objekatUredjaj);

            int deviceId = objekatUredjaj.UredjajId;

            List<double> potrosnja = await _deviceRepository.getPotrosnjaForDeviceAsync(deviceId);
            double cekanje = await _deviceRepository.getCekanjeForDeviceAsync(deviceId);
            int objekatUredjajId = await _deviceRepository.getObjekatUredjajIdForDeviceAsync();
            string vrstaUredjaja = await _deviceRepository.getVrstaUredjajaByIdAsync(deviceId);

            string[] radeKonstantno = { "Frižider", "Zamrzivač", "Bojler" };
            string[] radeLetnjuSezonu = { "Klima" };
            string[] radeZimskuSezonu = { "Norveški radijator", "Tea peć" };
            string[] radeDnevnoPovremeno = { "Veš mašina", "Mašina za sušenje veša", "Mašina za pranje sudova", "Šporet", "Rerna" };
            string[] proizvodjaci = { "Solarni panel", "Vetrogenerator" };
            
            if (radeKonstantno.Contains(vrstaUredjaja))
                await _recordCalculation.RecordForDailyConstantDevices(objekatUredjajId, potrosnja, cekanje);
            else if (radeLetnjuSezonu.Contains(vrstaUredjaja))
                await _recordCalculation.RecordForSumemrDevices(objekatUredjajId, potrosnja, cekanje);
            else if (radeZimskuSezonu.Contains(vrstaUredjaja))
                await _recordCalculation.RecordForWinterDevices(objekatUredjajId, potrosnja, cekanje);
            else if (radeDnevnoPovremeno.Contains(vrstaUredjaja))
                await _recordCalculation.RecordForDailyDevices(objekatUredjajId, potrosnja, cekanje);
            else if (proizvodjaci.Contains(vrstaUredjaja))
                await _recordCalculation.RecordForProducerDevices(objekatUredjajId, potrosnja, cekanje);
            else
                await _recordCalculation.RecordForRestDevices(objekatUredjajId, potrosnja, cekanje);


        }

        // vraca sve vrste za odredjeni tip
        public async Task<List<VrstaUredjajaDTO>> getVrstaByTipId(ETipUredjaj tipUredjaja)
        {
            return await _deviceRepository.getVrstaByTipIdAsync(tipUredjaja);
        }
        // vraca sve vrste za odredjeni tip
        public async Task<List<Uredjaj>> getUredjajiByVrstaId(int vrstaId)
        {
            return await _deviceRepository.getUredjajiByVrstaIdAsync(vrstaId);
        }

        //menja stanje uredjaja on/off
        public async Task updateDeviceState (int objekatUredjajId)
        {
            await _deviceRepository.updateDeviceStateAsync(objekatUredjajId);
        }

        //menja kontrolu uredjaja da/ne
        public async Task updateDeviceControl(int objekatUredjajId)
        {
            await _deviceRepository.updateDeviceControlAsync(objekatUredjajId);
        }

        //menja dozvolu uredjaja da/ne
        public async Task updateDevicePermission(int objekatUredjajId)
        {
            await _deviceRepository.updateDevicePermissionAsync(objekatUredjajId);
        }

        public async Task deleteDeviceById(int objekatUredjajId)
        {
            await _deviceRepository.deleteDeviceByIdAsync(objekatUredjajId);
        }
        public async Task deleteStoreById(int objekatSkladisteId)
        {
            await _deviceRepository.deleteStoreByIdAsync(objekatSkladisteId);
        }
        public async Task<List<VrstaUredjaja>> getVrsteUredjaja()
        {
            return  await _deviceRepository.getVrsteUredjajaAsync();
        }

        public async Task<List<ProstorijaDTO>> getAllProstorijaForObject(int objekatId)
        {
            return await _prostorijaRepository.getAllProstorijaAsync(objekatId);
        }

        public async Task<List<StoreDTO>> getAllStore()
        {
            return await _deviceRepository.getAllStoreAsync();
        }
        public async Task AddStore(StoreDTO store)
        {
            await _deviceRepository.AddStoreAsync(store);
        }

        public async Task<List<Prostorija>> getAllProstorije()
        {
            return await _deviceRepository.getAllProstorije();
        }

    }
}
