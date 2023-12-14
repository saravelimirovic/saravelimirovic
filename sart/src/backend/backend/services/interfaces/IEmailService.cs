using backend.Helpers;

namespace backend.services.interfaces
{
    // za Zaboravili ste lozinku
    public interface IEmailService
    {
        void SendEmail(EmailModel emailModel);
    }
}
