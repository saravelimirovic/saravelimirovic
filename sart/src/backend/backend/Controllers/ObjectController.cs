using backend.DTOs;
using backend.services;
using backend.services.interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ObjectController : ControllerBase
    {
        private IObjectService _objectService;

        public ObjectController(IObjectService objectService)
        {
            _objectService = objectService;
        }

        [HttpPost("prosAllObjects")]
        public Task<List<ObjectDTO>> GetObjectByUserId(int id)
        {
            return _objectService.getObjectByUserId(id);
        }

        // dodavanje novog objekta
        [HttpPost("addObject")]
        public async Task<IActionResult> AddNewObject([FromBody] ObjectAddDTO objectObj)
        {
            if (objectObj == null)
                throw new Exception("No params provided");

            try
            {
                await _objectService.addNewObject(objectObj);

                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpDelete("dsoDeleteObject")]
        public async Task<IActionResult> DeleteObject(int objectId)
        {
            try
            {
                if (objectId <= 0)
                {
                    return BadRequest("ID nije ispravan.");
                }

                await _objectService.deleteObjectById(objectId);


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
