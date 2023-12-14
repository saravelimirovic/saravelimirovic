using backend.Common;
using backend.DTOs;
using backend.Helpers;
using backend.Models;
using backend.repositroy.interfaces;
using backend.services.interfaces;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using SQLitePCL;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace backend.services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _userRepository;

        private readonly IUlicaRepository _ulicaRepository;
        private readonly INaseljeRepository _naseljeRepository;
        private readonly IGradRepository _gradRepository;
        private readonly IRolaRepository _rolaRepository;
        private readonly ITableService _tableService;

        public UserService(IUserRepository userRepository, IGradRepository gradRepository, INaseljeRepository naseljeRepository, IUlicaRepository ulicaRepository, IRolaRepository rolaRepository, ITableService tableService)
        {
            _userRepository = userRepository;
            _gradRepository = gradRepository;
            _naseljeRepository = naseljeRepository;
            _ulicaRepository = ulicaRepository;
            _rolaRepository = rolaRepository;
            _tableService = tableService;
        }


        // -- KREIRANJE TOKENA --
        public string CreateJwtToken(Korisnik user)
        {
            var jwtTokenHandler = new JwtSecurityTokenHandler();
            var key = Encoding.ASCII.GetBytes("veryverysecret.....");
            var objectId = _userRepository.objekatByUser(user.Id);
            // !(idu samo stringovi)
            var identity = new ClaimsIdentity(new Claim[]
            {
                new Claim("UserId", user.Id.ToString()),
                new Claim(ClaimTypes.Role, user.Rola.Naziv),
                new Claim(ClaimTypes.Name, user.Email),
                new Claim("ImePrezime", user.Ime + " " + user.Prezime),
                new Claim("objectId", objectId.ToString())
            });

            var credentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256);

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = identity,
                Expires = DateTime.Now.AddDays(1),
                SigningCredentials = credentials
            };

            var token = jwtTokenHandler.CreateToken(tokenDescriptor);

            return jwtTokenHandler.WriteToken(token);
        }

        // -- LOGIN DSO --
        public async Task<TokenDTO> LoginDSO(UserLoginDTO param)
        {
            Korisnik user = await _userRepository.getUserByEmailDSO(param.email);
            if (user == null)
            {
                throw new Exception("There is no user with such email or user is not dispatcher or admin");
            }

            if (!PasswordHasher.VerifyPassword(param.password, user.Sifra))
            {
                throw new Exception("Incorrect password");
            }

            TokenDTO token = new TokenDTO();
            token.Token = CreateJwtToken(user);

            return token;
        }

        // -- LOGIN PROSUMER --
        public async Task<TokenDTO> LoginProsumer(UserLoginDTO param)
        {
            Korisnik user = await _userRepository.getUserByEmailProsumer(param.email);
            if (user == null)
            {
                throw new Exception("There is no user with such email or user is not prosumer");
            }

            if (!PasswordHasher.VerifyPassword(param.password, user.Sifra))
            {
                throw new Exception("Incorrect password");
            }

            TokenDTO token = new TokenDTO();
            token.Token = CreateJwtToken(user);

            return token;
        }


        // -- REGISTRATION --
        public async Task<int> Register(ProsumerRegistrationDTO userObj)
        {
            if ((await _userRepository.getUserByEmail(userObj.Email)) != null)
            {
                throw new Exception("Already exists user with that email.");
            }

            Grad grad = new Grad();
            grad.Naziv = userObj.Grad;
            await _gradRepository.Insert(grad);

            Naselje naselje = new Naselje();
            naselje.Naziv = userObj.Naselje;
            naselje.GradId = (await _gradRepository.getGradByNaziv(grad.Naziv)).Id;
            //naselje.Grad = grad; // ?
            await _naseljeRepository.Insert(naselje);

            Ulica ulica = new Ulica();
            ulica.Naziv = userObj.Ulica;
            ulica.NaseljeId = (await _naseljeRepository.getNaseljeByNaziv(naselje.Naziv, naselje.GradId)).Id;
            //ulica.Naselje = naselje; // ?
            await _ulicaRepository.Insert(ulica);

            Korisnik user = new Korisnik();
            user.Sifra = PasswordHasher.HashPassword(userObj.Lozinka);
            user.Ime = userObj.Ime;
            user.Prezime = userObj.Prezime;
            user.JMBG = userObj.Jmbg;
            user.Email = userObj.Email;
            user.BrTelefona = userObj.BrojTelefona;
            //user.Ulica = ulica; // ?
            user.UlicaId = (await _ulicaRepository.getUlicaByNaziv(ulica.Naziv, ulica.NaseljeId)).Id;
            if (userObj.AdresniBroj == null)
            {
                user.AdresniBroj = "BB";
            }
            else
            {
                user.AdresniBroj = userObj.AdresniBroj;
            }

            user.RolaId = userObj.RolaId;
            return await _userRepository.Insert(user);
        }


        public async Task<Korisnik> getUserByEmail(string email)
        {
            return await _userRepository.getUserByEmail(email);
        }

        public async Task editUserForResetPassword(Korisnik korisnik)
        {
            _userRepository.editUserForResetPassword(korisnik);
        }


        // TABELA
        //public async Task<List<UserTabelaDTO>> getAllUsers(PagingDTO paging)
        //{
        //    //Korisnik user = new Korisnik();

        //    List<Korisnik> allUsers = await _userRepository.getAllUsers();
        //    List<UserTabelaDTO> UsersDTO = new List<UserTabelaDTO>();

        //    for(int i = paging.PageSize*paging.PageIndex-paging.PageSize; i<paging.PageSize*paging.PageIndex; i++)
        //    {
        //        if (allUsers.Count > i)
        //        {
        //            var id = allUsers[i].Id; // salje
        //            var ime = allUsers[i].Ime + " " + allUsers[i].Prezime; // salje

        //            var ulica = allUsers[i].Ulica;
        //            var ulicaNaziv = ulica.Naziv; // salje

        //            var naselje = ulica.Naselje;
        //            var naseljeNaziv = naselje.Naziv; // salje

        //            var grad = naselje.Grad;
        //            var gradNaziv = grad.Naziv; // salje

        //            var rola = allUsers[i].Rola;
        //            var rolaNaziv = rola.Naziv; // salje

        //            // mora sa ovim ifovima unutar ifova zato sto npr ako imamo novog unetog, nemamo za njega nikakvu potrosnju/proiz
        //            // pa ne mozemo uzeti [0] od prazne liste
        //            var potrosnja = 0.0;
        //            var proizvodnja = 0.0;

        //            // prosumer
        //            if (rola.Id == 3)
        //            {
        //                var p1 = await _tableService.getThisMonthPotrosnjaForId(id);
        //                var p2 = await _tableService.getThisMonthProizvodnjaForId(id);

        //                if (p1.Count != 0)
        //                {
        //                    potrosnja = p1[0].Usage;
        //                }
        //                if (p2.Count != 0)
        //                {
        //                    proizvodnja = p2[0].Usage;
        //                }
        //            }
        //            // potrosac
        //            else if (rola.Id == 4)
        //            {
        //                var p1 = await _tableService.getThisMonthPotrosnjaForId(id);

        //                if (p1.Count != 0)
        //                {
        //                    potrosnja = p1[0].Usage;
        //                }
        //            }
        //            // proizvodjac
        //            else
        //            {
        //                var p2 = await _tableService.getThisMonthProizvodnjaForId(id);

        //                if (p2.Count != 0)
        //                {
        //                    proizvodnja = p2[0].Usage;
        //                }
        //            }

        //            UsersDTO.Add(new UserTabelaDTO(id, ime, gradNaziv, naseljeNaziv, ulicaNaziv, potrosnja, proizvodnja, rolaNaziv));
        //        }
        //    }

        //    return UsersDTO;
        //}

        public async Task<List<UserTabelaDTO>> getAllUsers(PagingDTO paging)
        {
            //Korisnik user = new Korisnik();

            List<Korisnik> allUsers = await _userRepository.getAllUsers(paging);
            List<UserTabelaDTO> UsersDTO = new List<UserTabelaDTO>();

            foreach(var u in allUsers)
            { 
                var id = u.Id; // salje
                var ime = u.Ime + " " + u.Prezime; // salje

                var ulica = u.Ulica;
                var ulicaNaziv = ulica.Naziv; // salje

                var naselje = ulica.Naselje;
                var naseljeNaziv = naselje.Naziv; // salje

                var grad = naselje.Grad;
                var gradNaziv = grad.Naziv; // salje

                var rola = u.Rola;
                var rolaNaziv = rola.Naziv; // salje

                // mora sa ovim ifovima unutar ifova zato sto npr ako imamo novog unetog, nemamo za njega nikakvu potrosnju/proiz
                // pa ne mozemo uzeti [0] od prazne liste
                var potrosnja = 0.0;
                var proizvodnja = 0.0;

                // prosumer
                if (rola.Id == 3)
                {
                    potrosnja = await _tableService.getThisMonthPotrosnjaForId(id);
                    proizvodnja = await _tableService.getThisMonthProizvodnjaForId(id);
                }
                // potrosac
                else if (rola.Id == 4)
                {
                    potrosnja = await _tableService.getThisMonthPotrosnjaForId(id);
                }
                // proizvodjac
                else
                {
                    proizvodnja = await _tableService.getThisMonthProizvodnjaForId(id);
                }

                //UsersDTO.Add(new UserTabelaDTO(id, ime, gradNaziv, naseljeNaziv, ulicaNaziv, Math.Round(potrosnja, 2, MidpointRounding.AwayFromZero), Math.Round(proizvodnja, 2, MidpointRounding.AwayFromZero), rolaNaziv));
            }

            return UsersDTO;
        }

        // vraca jednog korisnika
        public async Task<UserCeoDTO> getUserById(int Id)
        {
            Korisnik user = await _userRepository.getUserById(Id);

            var id = user.Id; // salje
            var ime = user.Ime + " " + user.Prezime; // salje

            var jmbg = user.JMBG; // salje
            var brTelefona = user.BrTelefona;

            var email = user.Email;
            var ulicaNaziv = user.Ulica.Naziv;
            var adresniBroj = user.AdresniBroj; // salje
            var naseljeNaziv = user.Ulica.Naselje.Naziv; // salje
            var gradNaziv = user.Ulica.Naselje.Grad.Naziv; // salje

            var rolaNaziv = user.Rola.Naziv; // salje

            return new UserCeoDTO(id, ime, jmbg, brTelefona, gradNaziv, naseljeNaziv, ulicaNaziv, adresniBroj, rolaNaziv, email);
        }

        public async Task<UserCeoDTO> getUserInfoById(int Id)
        {
            Korisnik user = await _userRepository.getUserById(Id);

            var ime = user.Ime + " " + user.Prezime;
            var brTelefona = user.BrTelefona;
            var ulicaNaziv = user.Ulica.Naziv;
            var adresniBroj = user.AdresniBroj; 
            var naseljeNaziv = user.Ulica.Naselje.Naziv;
            var gradNaziv = user.Ulica.Naselje.Grad.Naziv;
            var email = user.Email;
            var rolaNaziv = user.Rola.Naziv; 

            return new UserCeoDTO(ime, gradNaziv, naseljeNaziv, ulicaNaziv, adresniBroj, email, brTelefona, rolaNaziv);
        }


        // edit korisnika
        public async Task editUser(UserCeoDTO userObj)
        {
            await _userRepository.editUserAsync(userObj);
        }

        public async Task<UserCountDTO> countUsers()
        {
            var allUsers = _userRepository.countAllUsers();
            var prosumers = _userRepository.countProzumer();
            var potrosaci = _userRepository.countPotrosaci();
            var proizvodjaci = _userRepository.countProizvodjaci();

            return new UserCountDTO(allUsers, prosumers, potrosaci, proizvodjaci);
        }

        public async Task deleteUserById(int userId)
        {
            await _userRepository.deleteUserAsync(userId);
        }

        public int objekatByUser(int id)
        {
            return _userRepository.objekatByUser(id);
        }

        public async Task<List<UserTabelaDTO>> getTopUsers(ETipUredjaj tipUredjaja)
        {
            return await _userRepository.getTopUsersAsync(tipUredjaja);
        }

    }
}
