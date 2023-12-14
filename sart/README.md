# WattApp

U nastavku se nalaze instrukcije za pokretanje aplikacije na lokalnoj mašini.

# Potrebna tehnologija

* .NET 7 SDK
* Angular CLI (verzija 15 ili novija)
* SQLite3

# Pokretanje aplikacija na lokalu

Klonirati projekat na lokalnu mašinu. 

## Pokretanje .NET webapi

1. Otvoriti terminal i locirati src/backend/backend folder.
2. Pokrenuti sledeće komande za instaliranje NuGet paketa: <br> `dotnet tool install --global dotnet-ef` <br> `dotnet restore`
3. Podesiti SQLite konekciju u appsettings.json u .NET projektu: <br> 
"ConnectionStrings": { <br>
    "DefaultConnection": "Data Source=wattappDB.db"
 <br> }
 4. Pokrenuti sledeću komandu za kreiranje SQLite baze: <br> `dotnet ef database update`
 5. Pokrenuti sledeću komandu za startovanje backend-a: <br> `dotnet run`

 ## Pokretanje DSO aplikacije

 1. Otvoriti terminal i locirati src/dso folder.
 2. Pokrenuti sledeću komantu za instalaciju Node.js paketa: <br> `npm install`
 3. Pokrenuti sledeću komandu za pokretanje frontend-a: <br> `ng serve`
 4. Locirati server `http://localhost:4200/`.

## Pokretanje Prosumer aplikacije

 1. Otvoriti terminal i locirati src/dso folder.
 2. Pokrenuti sledeću komantu za instalaciju Node.js paketa: <br> `npm install`
 3. Pokrenuti sledeću komandu za pokretanje frontend-a: <br> `ng serve`
 4. Ukoliko se aplikacija pokreće u isto vreme kao i DSO aplikacija, locirati server `http://localhost:58711/`.


# Pokretanje aplikacija na Web serveru

Instalirati RemoteSSH esktenziju u Visual Studio Code-u. <br>
Konektovati se na ssh remote server. <br>

## Pokretanje .NET i SQLite baze remote

1. Unutar Properties/launchSettings.json podesiti: <br>
"profiles": { <br>
"http": { <br>
"applicationUrl": "http://website-address:API_PORT/"
}
}<br>
2. Odraditi publish aplikcije koristeći `dotnet publish` komandu. 
3. Na remote-u kreirati folder _back_ i kopirati unutar njega ceo sadržaj foldera _publish_.
4. Unutar foldera _publish_ na remote-u kopirati gotovu SQLite bazu.
5. Setovati port aplikacije pokretanjem komande `export ASPNETCORE_URLS="http://website-address:API_PORT/"`.
6. Pokrenuti .NET aplikaciju pokretanjem `dotnet back/API.dll`.

## Pokretanje Angular projekta remote - DSO

1. Setovati API server konekciju unutar _environment.ts_ u Angular projektu. <br>
apiUrl: "http://website-address:API_PORT"
2. Izvršiti bildovanje aplikacije komandom `ng build`.
3. Preuzeti [SERVER.rar](c:/Users/Sara/Desktop/SERVER.rar)
4. Ekstraktovati folder SERVER.rar i unutar njega prekopirati folder _dist_.
5. Na remote-u kreirati folder _dso_ i kopirati sadržaj foldera _server_ unutar njega.
6. Pokrenuti Angular aplikaciju lociranjem foldera _dso_ i pokrenuti komandu `node app.js`

## Pokretanje Angular projekta remote - Prosumer

1. Setovati API server konekciju unutar _environment.ts_ u Angular projektu. <br>
apiUrl: "http://website-address:API_PORT"
2. Izvršiti bildovanje aplikacije komandom `ng build`.
3. Preuzeti [SERVER.rar](c:/Users/Sara/Desktop/SERVER.rar)
4. Ekstraktovati folder SERVER.rar i unutar njega prekopirati folder _dist_.
5. Na remote-u kreirati folder _prosumer_ i kopirati sadržaj foldera _server_ unutar njega.
6. Pokrenuti Angular aplikaciju lociranjem foldera _prosumer_ i pokrenuti komandu `node app.js`


# Za testere

DSO Url: http://softeng.pmf.kg.ac.rs:10062 <br>
Prosumer Url: http://softeng.pmf.kg.ac.rs:10063 <br>

Nalog za DSO: <br>
admin: jovana@gmail.com - jovana <br>
dispečer: marko@gmail.com - marko <br>

Nalog za Prosumera: <br>
saravel01@gmail.com - sara