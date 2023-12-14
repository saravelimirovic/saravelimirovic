using backend.Common;
using backend.DTOs;
using backend.Models;

namespace backend.repositroy.interfaces
{
    public interface IUserRepository
    {
        Task<Korisnik> getUserByEmail(string email);
        Task<Korisnik> getUserByEmailDSO(string email);
        Task<Korisnik> getUserByEmailProsumer(string email);

        Task<int> Insert(Korisnik user);
        Task editUserForResetPassword(Korisnik korisnik);
        Task editUserAsync(UserCeoDTO userObj);
        Task<List<Korisnik>> getAllUsers();
        Task<List<Korisnik>> getAllUsers(PagingDTO paging);
        Task<Korisnik> getUserById(int id);
        Task deleteUserAsync(int userId);
        Task<List<UserTabelaDTO>> getTopUsersAsync(ETipUredjaj tipUredjaja);
        int countAllUsers();
        int countProzumer();
        int countPotrosaci();
        int countProizvodjaci();
        int objekatByUser(int id);
    }
}
