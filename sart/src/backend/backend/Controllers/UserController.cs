using backend.Context;
using backend.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.IdentityModel.Tokens.Jwt;
using System;
using System.Text;
using System.Security.Claims;
using Microsoft.IdentityModel.Tokens;
using backend.Helpers;
using backend.services.interfaces;
using backend.DTOs;
using Microsoft.AspNetCore.Http.HttpResults;
using static System.Net.WebRequestMethods;
using static System.Runtime.InteropServices.JavaScript.JSType;
using System.Security.Cryptography;
using backend.Common;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly IConfiguration _configuration;
        private readonly IEmailService _emailService;
        public UserController(IUserService userService, IConfiguration configuration, IEmailService emailService)
        {
            _userService = userService;
            _configuration = configuration;
            _emailService = emailService;
        }

        [HttpPost("dsoObjekatId")]
        public int Objkat(int id)
        {
            return _userService.objekatByUser(id);
        }

        // -- LOGIN DSO --
        [HttpPost("dsoAuthenticate")]
        public async Task<ActionResult<TokenDTO>> AuthenticateDSO([FromBody] UserLoginDTO userObj)
        {
            Console.Write(userObj);
            if (userObj == null)
                return BadRequest();

            try
            {
                TokenDTO token = _userService.LoginDSO(userObj).Result;

                return Ok(token);
            }
            catch(Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // -- LOGIN PROSUMER -- 
        [HttpPost("prosumerAuthenticate")]
        public async Task<ActionResult<TokenDTO>> AuthenticateProsumer([FromBody] UserLoginDTO userObj)
        {
            Console.Write(userObj);
            if (userObj == null)
                return BadRequest();

            try
            {
                TokenDTO token = _userService.LoginProsumer(userObj).Result;

                return Ok(token);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // nesto za autorizaciju, trenutno nije potrebno
        //[HttpGet]
        //public async Task<ActionResult<User>> GetAllUsers()
        //{
        //    return Ok(await _authContext.Users.ToListAsync());
        //}

        // -- REGISTRACIJA --
        [HttpPost("register")]
        public async Task<IActionResult> SignUpUser([FromBody] ProsumerRegistrationDTO userObj)
        {
            if (userObj == null)
                throw new Exception("No params provided");

            try
            {
                int idDodat = await _userService.Register(userObj);

                return Ok(
                    new { 
                        Message = 1, 
                        idDodatogKorisnika = idDodat
                    }
                );
            }
            catch(Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // za Zaboravili ste lozinku !
        [HttpPost("send-reset-email/{email}")]
        public async Task<IActionResult> SendEmail(string email)
        {
            var user = await _userService.getUserByEmail(email);
            if(user == null)
            {
                return NotFound(new
                {
                    StatusCode = 404,
                    Message = "Email ne postoji."
                });
            }

            var tokenBytes = RandomNumberGenerator.GetBytes(64);
            var emailToken = Convert.ToBase64String(tokenBytes);

            user.ResetSifraToken = emailToken;
            user.ResetSifraIsticanje = DateTime.Now.AddMinutes(5);

            string from = _configuration["EmailSettings:From"];
            var emailModel = new EmailModel(email, "Resetovanje lozinke!", EmailBody.EmailStringBody(email, emailToken, user.RolaId));

            _emailService.SendEmail(emailModel);
            await _userService.editUserForResetPassword(user);

            return Ok(new
            {
                StatusCode = 200,
                Message = "Email poslat!"
            });
        }

        [HttpPost("reset-password")]
        public async Task<IActionResult> ResetPassword(ResetPasswordDTO resetPasswordDTO)
        {
            var newToken = resetPasswordDTO.EmailToken.Replace(" ", "+");
            var user = await _userService.getUserByEmail(resetPasswordDTO.Email);
            if (user == null)
            {
                return NotFound(new
                {
                    StatusCode = 404,
                    Message = "Korisnik ne postoji."
                });
            }

            var tokenCode = user.ResetSifraToken;
            DateTime emailTokenExpiry = user.ResetSifraIsticanje;

            if(tokenCode != resetPasswordDTO.EmailToken || emailTokenExpiry < DateTime.Now)
            {
                return BadRequest(new
                {
                    StatusCode = 400,
                    Message = "Neupotrebljiv link za resetovanje lozinke."
                });
            }

            user.Sifra = PasswordHasher.HashPassword(resetPasswordDTO.NewPassword);
            await _userService.editUserForResetPassword(user);

            return Ok(new
            {
                StatusCode = 200,
                Message = "Lozinka je uspesno resetovana."
            });
        }





        // vraca sve korisnike
        [HttpPost("tabelaUsers")]
        public async Task<List<UserTabelaDTO>> GetAllUsers([FromBody] PagingDTO paging)
        {
            return await _userService.getAllUsers(paging);
        }

        // vrac podatke o jednom korisniku
        [HttpGet("profilUser/{id}")]
        public async Task<UserCeoDTO> GetUserById(int id)
        {
            return await _userService.getUserById(id);
        }

        [HttpPost("editUser")]
        public async Task<IActionResult> EditUser([FromBody] UserCeoDTO userObj)
        {
            if (userObj == null)
                throw new Exception("No params provided");

            try
            {
                await _userService.editUser(userObj);

                return Ok(
                    new { Message = 1 }
                );
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("usersCount")]
        public async Task<UserCountDTO> CountUsers()
        {
            return await _userService.countUsers();
        }

        [HttpGet("dsoTopPotrosaca")]
        public async Task<List<UserTabelaDTO>> TopPotrosaci()
        {
            return await _userService.getTopUsers(ETipUredjaj.Potrosac);
        }

        [HttpGet("dsoTopProizvodjaca")]
        public async Task<List<UserTabelaDTO>> TopProizvodjaci()
        {
            return await _userService.getTopUsers(ETipUredjaj.Proizvodjac);
        }


        [HttpPost("prosUserInfo")]
        public async Task<UserCeoDTO> GetUserInfoById(int id)
        {
            return await _userService.getUserInfoById(id);
        }

        [HttpDelete("dsoDeleteUser")]
        public async Task<IActionResult> DeleteUser(int userId)
        {
            try
            {
                if (userId <= 0)
                {
                    return BadRequest("ID nije ispravan.");
                }

                await _userService.deleteUserById(userId);


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
