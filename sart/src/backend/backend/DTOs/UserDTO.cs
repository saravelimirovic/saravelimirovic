using backend.Models;

namespace backend.DTOs
{
    public class UserDTO
    {
        public string ime { get; set; }
        public string prezime { get; set; }
        public string email { get; set; }
        public string role { get; set; }

        public UserDTO(Korisnik user)
        {
            this.ime = user.Ime;
            this.prezime = user.Prezime;
            this.email = user.Email;
            this.role = user.Rola.Naziv;
        }
    }
}
