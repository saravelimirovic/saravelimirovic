using backend.Common;
using backend.DTOs;
using backend.Models;

namespace backend.services.interfaces
{
    public interface IUserService
    {
        string CreateJwtToken(Korisnik user);
        Task<TokenDTO> LoginDSO(UserLoginDTO user);
        Task<TokenDTO> LoginProsumer(UserLoginDTO user);
        Task<int> Register(ProsumerRegistrationDTO user);
        Task<Korisnik> getUserByEmail(string email);
        Task editUserForResetPassword(Korisnik korisnik);

        Task<List<UserTabelaDTO>> getAllUsers(PagingDTO paging);
        Task<List<UserTabelaDTO>> getTopUsers(ETipUredjaj tipUredjaja);
        Task<UserCeoDTO> getUserById(int id);
        Task editUser(UserCeoDTO user);

        Task<UserCountDTO> countUsers();
        
        //info za jednog korisnika za prosumera
        Task<UserCeoDTO> getUserInfoById(int Id);
        Task deleteUserById(int userId);

        int objekatByUser(int id);
    }
}
