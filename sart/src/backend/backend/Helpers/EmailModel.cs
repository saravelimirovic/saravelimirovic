namespace backend.Helpers
{
    public class EmailModel
    {
        // za Zaboravili ste lozinku !
        private string to;
        private string subject;
        private string content;
        public EmailModel(string to, string subject, string content)  
        {
            To = to;
            Subject = subject;
            Content = content;
        }

        public string To { get => to; set => to = value; }
        public string Subject { get => subject; set => subject = value; }
        public string Content { get => content; set => content = value; }
    }
}
