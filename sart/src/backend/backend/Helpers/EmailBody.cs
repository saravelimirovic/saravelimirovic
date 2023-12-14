namespace backend.Helpers
{
    // za Zaboravili ste lozinku !
    public static class EmailBody
    {
        public static string EmailStringBody(string email, string emailToken, int rolaUsera)
        {
            // dispecer
            if (rolaUsera == 2)
            {
                return $@"
                        <html>
                        <head></head>
                        <body style""margin=0; padding:0; font-family: Arial, Helvetica, sans-serif;"">
                        <div style=""height: auto; background: linear-gradient(to top, #20262e 30%, #191e24 90%) no-repeat; width: 400px; padding: 30px""> 
                        <div>
                        <div>
                        <h1 style=""color: white;"">Resetuj svoju lozinku!</h1>
                        <hr>
                        <p style=""color: white;"">Poštovani, <br>
                        <br>
                        Primili smo vaš zahtev za resetovanje lozinke na našem sajtu i želimo da vam pomognemo da to uradite. Kao prvo, želimo da vas obavestimo da ste u dobrih rukama - naš tim stručnjaka je tu da vam pomogne u svemu što vam je potrebno. <br>
                        <br>
                        Da biste resetovali svoju lozinku, molimo vas da pritisnete sledeće dugme: <br></p>
                        <a href=""http://localhost:4200/reset?email={email}&code={emailToken}"" target=""_blank"" style=""background: #588157; padding:10px; border: none; color: white; border-radius: 4px;
                                                                                                                          display: block; margin: 0 auto; width: 50%; text-align: center; text-decoration: none"">
                        Resetuj lozinku </a> <br>

                        <p style=""color: white;"">Ako vam je potrebna dodatna pomoć, molimo vas da nas kontaktirate putem e-maila. Naš tim je tu da Vam pomogne u svemu što Vam je potrebno. <br>
                        <br>
                        Hvala Vam na poverenju koje ste nam ukazali. Ukoliko Vam je potrebna bilo kakva pomoć u vezi sa vašim nalogom na našem sajtu, slobodno nas kontaktirajte. <br>
                        <br> <br>
                        Srdačan pozdrav, <br>
                        Tim Sart
                        </p>
                        </div>
                        </div>
                        </div>
                        </body>
                        </html>
                     ";
            }
            else if(rolaUsera == 3 || rolaUsera == 4 || rolaUsera == 5)
            {
                return $@"
                        <html>
                        <head></head>
                        <body style""margin=0; padding:0; font-family: Arial, Helvetica, sans-serif;"">
                        <div style=""height: auto; background: linear-gradient(to top, #20262e 30%, #191e24 90%) no-repeat; width: 400px; padding: 30px""> 
                        <div>
                        <div>
                        <h1 style=""color: white;"">Resetuj svoju lozinku!</h1>
                        <hr>
                        <p style=""color: white;"">Poštovani, <br>
                        <br>
                        Primili smo vaš zahtev za resetovanje lozinke na našem sajtu i želimo da vam pomognemo da to uradite. Kao prvo, želimo da vas obavestimo da ste u dobrih rukama - naš tim stručnjaka je tu da vam pomogne u svemu što vam je potrebno. <br>
                        <br>
                        Da biste resetovali svoju lozinku, molimo vas da pritisnete sledeće dugme: <br></p>
                        <a href=""http://localhost:4200/reset?email={email}&code={emailToken}"" target=""_blank"" style=""background: #588157; padding:10px; border: none; color: white; border-radius: 4px;
                                                                                                                          display: block; margin: 0 auto; width: 50%; text-align: center; text-decoration: none"">
                        Resetuj lozinku </a> <br>

                        <p style=""color: white;"">Ako vam je potrebna dodatna pomoć, molimo vas da nas kontaktirate putem e-maila. Naš tim je tu da Vam pomogne u svemu što Vam je potrebno. <br>
                        <br>
                        Hvala Vam na poverenju koje ste nam ukazali. Ukoliko Vam je potrebna bilo kakva pomoć u vezi sa vašim nalogom na našem sajtu, slobodno nas kontaktirajte. <br>
                        <br> <br>
                        Srdačan pozdrav, <br>
                        Tim Sart
                        </p>
                        </div>
                        </div>
                        </div>
                        </body>
                        </html>
                     ";
            }

            return null;
        }
    }
}
