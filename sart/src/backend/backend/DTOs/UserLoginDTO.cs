using backend.Models;
using System.Text.Json.Serialization;

namespace backend.DTOs
{
    public class UserLoginDTO
    {
        public string email { get; set; }
        public string password { get; set; }
    }
}
