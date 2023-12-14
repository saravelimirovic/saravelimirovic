using backend.DTOs;
using backend.Models;

namespace backend.services.interfaces
{
    public interface ITableService
    {
        //Task<List<GraphDateDto>> getThisMonthPotrosnjaForId(int korisnikId);
        Task<double> getThisMonthPotrosnjaForId(int korisnikId);


        //Task<List<GraphDateDto>> getThisMonthProizvodnjaForId(int korisnikId);
        Task<double> getThisMonthProizvodnjaForId(int korisnikId);



        Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja7d();
        Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja7dPr();
        Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja31days();
        Task<List<GraphDateAllDTO>> getPotrosnjaiProizvodnja1Year();

        Task<FilterMaxMinDTO> getMaxMin();
        Task<List<UserTabelaDTO>> getFilteredUsers(int pageIndex, int pageSize, string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null);
        Task<List<DispatcherDTO>> getFilteredDispatchers(int pageIndex, int pageSize,
                                                          string ime = null, string grad = null, string naselje = null, string ulica = null);

        Task<List<MapDTO>> getFilteredUsersForMap(string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null);
    }
}
