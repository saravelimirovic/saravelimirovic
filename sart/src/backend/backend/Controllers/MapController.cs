using backend.DTOs;
using backend.services.interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace backend.Controllers
{

    [Route("api/[controller]")]
    [ApiController]
    public class MapController : ControllerBase
    {
        private IMapService _mapService;

        public MapController(IMapService mapService)
        {
            _mapService = mapService;
        }

        [HttpGet("dsoMap")]
        public List<MapDTO> GetAllUserForMap() 
        {
            return _mapService.getUsersForMaps().Result;
        }

        [HttpGet("dsoUserMap")]
        public List<MapDTO> GetAllObjectForMapForUserId(int userId)
        {
            return _mapService.getObjectForMapsForUserId(userId).Result;
        }

    }
}
