using backend.Common;
using backend.DTOs;
using backend.Models;
using backend.services;
using backend.services.interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace backend.Controllers
{

    [Route("api/[controller]")]
    [ApiController]
    public class DeviceController : ControllerBase
    {
        private IDeviceService _deviceSercice;

        public DeviceController(IDeviceService deviceSercice)
        {
            _deviceSercice = deviceSercice;
        }

        // vraca jednu cifru koja obuhvata ukupno skaldisteno vati u objketi
        [HttpPost("prosObjectSumOfStore")]
        public async Task<double> GetSumOfSkladisteForObject(int objekatId)
        {
            return await _deviceSercice.getSumOfSkladisteForObject(objekatId);
        }

        // vraca skladista za jedan objekat
        [HttpPost("prosObjectStore")]
        public async Task<List<StoreDTO>> GetSkladisteForObject(int objekatId)
        {
            return await _deviceSercice.getSkladistaForObject(objekatId);
        }

        // vraca uredjaje za jedan objekat za sve prostorije
        [HttpPost("prosObjectDevices")]
        public async Task<List<DeviceDTO>> GetUredjajeForObjectForAllProstorija(int objekatId)
        {
            return await _deviceSercice.getUredjajeForObjectForAllProstorija(objekatId);
        }

        // vraca uredjaje za jedan objekat za jednu prostorji
        [HttpPost("prosObjectDevicesForRoom")]
        public async Task<List<DeviceDTO>> GetUredjajeForObjectForOneProstorija(int objekatId, string nazivPostorije)
        {
            return await _deviceSercice.getUredjajeForObjectForOneProstorija(objekatId, nazivPostorije);
        }

        // vraca sve prostorije 
        [HttpPost("prosRooms")]
        public async Task<List<ProstorijaDTO>> GetAllProstorija(int objekatId)
        {
            return await _deviceSercice.getAllProstorijaForObject(objekatId);
        }

        [HttpGet("prosGetAllProstorije")]
        public async Task<List<Prostorija>> GetAllProstorije()
        {
            return await _deviceSercice.getAllProstorije();
        }

        // vraca informacije o uredjaju u nekom objektu
        [HttpPost("prosDeviceInfo")]
        public async Task<DeviceDTO> GetInfoAboutDeviceById(int objekatUredjajId)
        {
            return await _deviceSercice.getInfoAboutDeviceById(objekatUredjajId);
        }

        // unosi uredjaj u objekat i racuna istoriju i predikciju za taj uredjaj
        [HttpPost("prosInsertObjectDevice")]
        public async Task<IActionResult> InsertObjectDevice([FromBody] ObjectDeviceDTO objectDeviceDTO)
        {
            if (objectDeviceDTO == null)
                throw new Exception("No params provided");

            try
            {
                await _deviceSercice.InsertDeviceInObject(objectDeviceDTO);

                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // menja stanje uredjaja on/off
        [HttpPost("prosUpdateStatusObjectDevice")]
        public async Task<IActionResult> UpdateDeviceStatus(int objectDeviceId)
        {
            try
            {
                await _deviceSercice.updateDeviceState(objectDeviceId);

                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // menja kontrolu uredjaja da/ne
        [HttpPost("prosUpdateControlObjectDevice")]
        public async Task<IActionResult> UpdateDeviceControl(int objectDeviceId)
        {
            try
            {
                await _deviceSercice.updateDeviceControl(objectDeviceId);

                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // menja kontrolu uredjaja da/ne
        [HttpPost("prosUpdatePermissionObjectDevice")]
        public async Task<IActionResult> UpdateDevicePermission(int objectDeviceId)
        {
            try
            {
                await _deviceSercice.updateDevicePermission(objectDeviceId);

                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // brise uredjaj 
        [HttpDelete("prosDeleteDevice")]
        public async Task<IActionResult> DeleteDevice(int objectDeviceId)
        {
            try
            {
                if (objectDeviceId <= 0)
                {
                    return BadRequest("ID nije ispravan.");
                }

                await _deviceSercice.deleteDeviceById(objectDeviceId);


                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // brise skaldiste 
        [HttpDelete("prosDeleteStore")]
        public async Task<IActionResult> DeleteStore(int objectStoreId)
        {
            try
            {
                if (objectStoreId <= 0)
                {
                    return BadRequest("ID nije ispravan.");
                }

                await _deviceSercice.deleteStoreById(objectStoreId);


                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // vraca uredjaje za jedan objekat
        [HttpGet("prosSpeciesOfDevice")]
        public async Task<List<VrstaUredjaja>> GetVrsteUredjaja()
        {
            return await _deviceSercice.getVrsteUredjaja();
        }

        // vraca vrste za potrosace
        [HttpGet("prosGetSpeciesDevicePotrosaci")]
        public async Task<List<VrstaUredjajaDTO>> GetVrstePotrosaca()
        {
            return await _deviceSercice.getVrstaByTipId(ETipUredjaj.Potrosac);
        }

        // vraca vrste za proizvodnace
        [HttpGet("prosGetSpeciesDeviceProizvodjaci")]
        public async Task<List<VrstaUredjajaDTO>> GetVrsteProizvodjaca()
        {
            return await _deviceSercice.getVrstaByTipId(ETipUredjaj.Proizvodjac);
        }

        // vraca vrste za proizvodnace
        [HttpPost("prosGetDeviceBySpecies")]
        public async Task<List<Uredjaj>> GetUredjajiByVrstaId(int vrstaId)
        {
            return await _deviceSercice.getUredjajiByVrstaId(vrstaId);
        }

        // vraca sva skaldista za dropdown
        [HttpGet("prosGetAllStore")]
        public async Task<List<StoreDTO>> GetAllStore()
        {
            return await _deviceSercice.getAllStore();
        }

        // vraca sva skaldista za dropdown
        [HttpPost("prosAddStore")]
        public async Task<IActionResult> AddStore(StoreDTO store)
        {
            try
            {
                if (store.MaxSkladiste != 0 && store.MaxSkladiste < store.TrenutnoStanje)
                {
                    return BadRequest("Unos nije ispravan.");
                }

                await _deviceSercice.AddStore(store);


                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

    }
}
