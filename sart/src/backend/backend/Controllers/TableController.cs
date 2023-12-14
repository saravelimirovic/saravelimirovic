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
    public class TableController : ControllerBase
    {
        private readonly ITableService _tableService;
        public TableController(ITableService tableService)
        {
            _tableService = tableService;
        }


        // ************************** D S O  ************************** //

        // tabela korisnika -> izmena -> ovo ne ide nigde direktno na front (samo zbog probe vrednosti)
        [HttpPost("potrosnjaThisM")]
        public Task<double> GetTodaysPotrosnja(int UserId)
        {
            return _tableService.getThisMonthPotrosnjaForId(UserId);
        }

        // tabela korisnika -> izmena -> ovo ne ide nigde direktno na front (samo zbog probe vrednosti)
        [HttpPost("prozivodnjaThisM")]
        public Task<double> GetTodaysPotrosnjaPred(int UserId)
        {
            return _tableService.getThisMonthProizvodnjaForId(UserId);
        }



        // tabela na pocetnoj
        [HttpGet("dsoPotrProiz7d")]
        public Task<List<GraphDateAllDTO>> GetPotrosnjaiProizvodnja7d()
        {
            return _tableService.getPotrosnjaiProizvodnja7d();
        }

        // tabela na pocetnoj
        [HttpGet("dsoPotrProizPred7d")]
        public Task<List<GraphDateAllDTO>> GetPotrosnjaiProizvodnja7dPr()
        {
            return _tableService.getPotrosnjaiProizvodnja7dPr();
        }

        // tabela na pocetnoj
        [HttpGet("dsoPotrProiz31d")]
        public Task<List<GraphDateAllDTO>> GetPotrosnjaiProizvodnja31days()
        {
            return _tableService.getPotrosnjaiProizvodnja31days();
        }

        // tabela na pocetnoj
        [HttpGet("dsoPotrProiz1Y")]
        public Task<List<GraphDateAllDTO>> GetPotrosnjaiProizvodnja1Year()
        {
            return _tableService.getPotrosnjaiProizvodnja1Year();
        }



        // -------------------- F I L T E R -------------------- //

        [HttpGet("minMaxSlider")]
        public Task<FilterMaxMinDTO> GetMinMaxFilter()
        {
            return _tableService.getMaxMin();
        }

        [HttpPost("filterTabela")]
        public Task<List<UserTabelaDTO>> GetFilteredUsers([FromBody] UserFilterDTO userFilter)
        {
            return _tableService.getFilteredUsers(userFilter.PageIndex, userFilter.PageSize, userFilter.PotrosnjaOd, userFilter.PotrosnjaDo, userFilter.ProizvodnjaOd, userFilter.ProizvodnjaDo,
                                                    userFilter.RolaUzimam, userFilter.Ime, userFilter.Grad, userFilter.Naselje, userFilter.Ulica);
        }

        [HttpPost("filterTabelaAdmin")]
        public Task<List<DispatcherDTO>> GetFilteredDispatcher([FromBody] DispatcherDTO userFilter)
        {
            return _tableService.getFilteredDispatchers(userFilter.PageIndex, userFilter.PageSize,
                                                    userFilter.Ime, userFilter.Grad, userFilter.Naselje, userFilter.Ulica);
        }


        [HttpPost("filterTabelaMapa")]
        public Task<List<MapDTO>> GetFilteredUsersForMap([FromBody] UserFilterDTO userFilter)
        {
            return _tableService.getFilteredUsersForMap(userFilter.PotrosnjaOd, userFilter.PotrosnjaDo, userFilter.ProizvodnjaOd, userFilter.ProizvodnjaDo,
                                                    userFilter.RolaUzimam, userFilter.Ime, userFilter.Grad, userFilter.Naselje, userFilter.Ulica);
        }
    }
}
