using backend.Common;
using backend.DTOs;
using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface ITableRepository
    {
        //Task<List<IstorijaP>> getThisMonthProizvodnjaForIdAsync(int korisnikId);
        //Task<List<IstorijaP>> getThisMonthPotrosnjaForIdAsync(int korisnikId);

        Task<double> getTodaysPotrosnjaForIdAsync(int korisnikId, ETipUredjaj tipUredjaja);
        Task<double> getThisMonthPotrosnjaForIdAsync(int korisnikId, ETipUredjaj tipUredjaja);

        Task<FilterMaxMinDTO> getMaxMin();
        Task<List<UserTabelaDTO>> getFilteredUsers(int pageIndex, int pageSize, string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null);
        Task<List<DispatcherDTO>> getFilteredDispatchers(int pageIndex, int pageSize,
                                                          string ime = null, string grad = null, string naselje = null, string ulica = null);

        Task<List<MapDTO>> getFilteredUsersForMap(string potrosnjaOdStr, string potrosnjaDoStr, string proizvodnjaOdStr, string proizvodnjaDoStr, int rola,
                                                                string ime = null, string grad = null, string naselje = null, string ulica = null);
    }
}
