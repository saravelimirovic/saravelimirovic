namespace backend.DTOs
{
    // za Zaboravili ste lozinku !
    public record ResetPasswordDTO
    {
        private string email;
        private string emailToken;
        private string newPassword;
        private string confirmPassword;

        public string Email { get => email; set => email = value; }
        public string EmailToken { get => emailToken; set => emailToken = value; }
        public string NewPassword { get => newPassword; set => newPassword = value; }
        public string ConfirmPassword { get => confirmPassword; set => confirmPassword = value; }
    }
}
