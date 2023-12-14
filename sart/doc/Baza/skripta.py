import datetime
import random
import connect

import base64
import hashlib
import os

def hashPassword(password):
    salt = os.urandom(16)
    iterations = 10000
    key_length = 20

    key = hashlib.pbkdf2_hmac('sha1', password.encode('utf-8'), salt, iterations, key_length)

    hash_bytes = salt + key
    base64_hash = base64.b64encode(hash_bytes).decode('utf-8')

    return base64_hash

#------------------ FIND ---------------

def FindGradId(grad):
    sql = "select Id from Grad where Naziv = '"+grad+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindNaseljeId(naselje):
    sql = "select Id from Naselje where Naziv = '"+naselje+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindUlicaId(ulica):
    sql = "select Id from Ulica where Naziv = '"+ulica+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindObjekatId(ulica, broj):
    UlicaId = FindUlicaId(ulica)
    sql = "select Id from Objekat where UlicaId = "+str(UlicaId)+" and AdresniBroj ='"+broj+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindRolaId(rola):
    sql = "select Id from Rola where Naziv = '"+rola+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindKorisnikId(email):
    sql = "select Id from Korisnik where Email = '"+email+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindTipUredjajaId(tipUredjaj):
    sql = "select Id from TipUredjaja where Naziv = '"+tipUredjaj+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindVrstaUredjajId(vrstaUredjaja):
    sql = "select Id from VrstaUredjaja where Naziv = '"+vrstaUredjaja+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]


def FindUredjajId(uredjaj):
    sql = "select Id from Uredjaj where Naziv = '"+uredjaj+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindSkladisteId(skladiste):
    sql = "select Id from Skladiste where Naziv = '"+skladiste+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindVrstaUredjaja(UredjajId):
    sql = "select VrstaUredjajaId from Uredjaj where Id = " + str(UredjajId)
    curs.execute(sql)
    res = curs.fetchone()
    sql = "select Naziv from VrstaUredjaja where Id = " + str(res[0])
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindPotrosnje(UredjajId):
    sql = "select PPoSatuStanja from PPoStanjuUredjaja where UredjajId = "+str(UredjajId)
    curs.execute(sql)
    res = curs.fetchall()
    return res


def FindCekanje(UredjajId):
    sql = "select PPrilikomMirovanja from Uredjaj where Id = "+str(UredjajId)
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

def FindProstorijaId(Prostorija):
    sql = "select id from prostorija where Naziv = '"+str(Prostorija)+"'"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

#def FindIstorijuZaDatumIVreme(date, time, ObjekatUredjajId,):
#    sql = """select VrednostRealizacije from IstorijaP where Vreme LIKE '%"""+str(time.hour)+"""%' 
#             and Datum LIKE '%"""+str(date.month)+"-%"+str(date.day)+"""' 
#             and UredjajId = """ + str(UredjajId) + " and ObjekatId = "+ str(ObjekatId)  + " Limit 1"
#    curs.execute(sql)
#    res = curs.fetchone()
#    return res[0]

def FindIstoriju(ObjekatUredjajId):
    sql = "select VrednostRealizacije, datum, vreme from IstorijaP where ObjekatUredjajId = " + str(ObjekatUredjajId)
    curs.execute(sql)
    res = curs.fetchall()
    return res

def FindObjekatUredjajId():
    sql = "select max(Id) from ObjekatUredjaj"
    curs.execute(sql)
    res = curs.fetchone()
    return res[0]

#------------------ FILL ----------------------

def FillGrad(grad):
    sql = "insert into Grad(Naziv) values ('"+grad+"')"
    curs.execute(sql)
    conn.commit() 

def FillNaselje(grad, naselje):
    GradId = FindGradId(grad)
    sql = "insert into Naselje(GradId, Naziv) values ("+str(GradId)+",'"+naselje+"')"
    curs.execute(sql)
    conn.commit() 

def FillUlica(naselje, ulica):
    NaseljeId = FindNaseljeId(naselje)
    sql = "insert into Ulica(NaseljeId, Naziv) values ("+str(NaseljeId)+",'"+ulica+"')"
    curs.execute(sql)
    conn.commit() 

def FillRole(role):
    for rola in role:
        sql = "insert into Rola(Naziv) values ('"+rola+"')"
        curs.execute(sql)
    conn.commit()

def FillKorisnik(jmbg, ime, prezime, email, sifra, BrTelefona, adresa, adresniBroj, rola):
    hesiranaSifra = hashPassword(sifra)
    RolaId = FindRolaId(rola)
    UlicaId = FindUlicaId(adresa)
    sql = """insert into Korisnik(JMBG, Ime, Prezime, Email, Sifra, BrTelefona, UlicaId, AdresniBroj, RolaId) 
             values ('"""+jmbg+"','"+ime+"','"+prezime+"','"+email+"','"+hesiranaSifra+"','"+BrTelefona+"""'
             ,"""+str(UlicaId)+",'"+adresniBroj+"',"+str(RolaId)+")"""
    curs.execute(sql)
    conn.commit()

def FillObjekat(email, adresa, adresniBroj, naziv):
    UlicaId = FindUlicaId(adresa)
    KorisnikId = FindKorisnikId(email)
    sql = """insert into Objekat(UlicaId, KorisnikId, AdresniBroj, Naziv) 
             values ("""+str(UlicaId)+", "+str(KorisnikId)+", '"+adresniBroj+"', '"+naziv+"')"""
    curs.execute(sql)
    conn.commit()

def FillProstorija(Prostorija):
    sql = "insert into Prostorija(Naziv) values ('"+Prostorija+"')"
    curs.execute(sql)
    conn.commit()

def FillTipUredjaja(tipUredjaja):
    sql = "insert into TipUredjaja(Naziv) values ('"+tipUredjaja+"')"
    curs.execute(sql)
    conn.commit()

def FillVrstaUredjaja(vrstaUredjaja):
    sql = "insert into VrstaUredjaja(Naziv) values ('"+vrstaUredjaja+"')"
    curs.execute(sql)
    conn.commit()

def FillUredjaj(naziv, standBy, tipUredjaja, vrstaUredjaja):
    TipUredjajaId = FindTipUredjajaId(tipUredjaja)
    VrstaUredjajaId = FindVrstaUredjajId(vrstaUredjaja)
    sql = """insert into Uredjaj(Naziv, PPrilikomMirovanja, TipUredjajaId, VrstaUredjajaId) 
             values ('"""+naziv+"', "+str(standBy)+", "+str(TipUredjajaId)+", "+str(VrstaUredjajaId)+")"""
    curs.execute(sql)
    conn.commit()


def FillPPoStanjuUredjaja(uredjaj, naziv, pPoSatuStanja):
    UredjajId = FindUredjajId(uredjaj)
    sql = """insert into PPoStanjuUredjaja(UredjajId, Naziv, PPoSatuStanja) 
             values ("""+str(UredjajId)+", '"+naziv+"', "+str(pPoSatuStanja)+" )"""
    curs.execute(sql)
    conn.commit()


def FillSkladista(naziv, maxSkladista, potrosnjaZaCuvanjePoSatu):
    sql = """insert into Skladiste(Naziv, MaxSkladista, PotrosnjaZaCuvanjePoSatu) 
             values ('"""+naziv+"', "+str(maxSkladista)+", "+str(potrosnjaZaCuvanjePoSatu)+" )"""
    curs.execute(sql)
    conn.commit()


def FillObjekatUredjaj(uredjaj, ulica, broj, prostorija):
    UredjajId = FindUredjajId(uredjaj)
    potrosnje = FindPotrosnje(UredjajId)
    cekanje = FindCekanje(UredjajId)
    ObjekatId = FindObjekatId(ulica, broj)
    ProstorijaId = FindProstorijaId(prostorija)
    vrstaUredjaja = FindVrstaUredjaja(UredjajId)
    sql = """insert into ObjekatUredjaj(ProstorijaId, UredjajId, ObjekatId, Dozvola, Kontrola, Ukljucen) 
          values ("""+str(ProstorijaId)+", "+str(UredjajId)+", "+str(ObjekatId)+", 'Ne', 'Ne', 'Ne')"
    curs.execute(sql)
    conn.commit()
    ObjekatUredjajId = FindObjekatUredjajId()
    radeKonstantno = ('Frižider', 'Zamrzivač', 'Bojler')
    radeLetnjuSezonu = ('Klima')
    radeZimskuSezonu = ('Norveški radijator', 'Tea peć')
    radeDnevnoPovremeno = ('Veš mašina', 'Mašina za sušenje veša', 'Mašina za pranje sudova', 'Šporet', 'Rerna')
    proizvodjaci = ('Solarni panel','Vetrogenerator')
    if vrstaUredjaja in radeKonstantno :
        IstorijaUredjajaKojiRadeKonstantno(ObjekatUredjajId, potrosnje, cekanje)
    elif vrstaUredjaja in radeLetnjuSezonu :
        IstorijaUredjajaLetnjeSezone(ObjekatUredjajId, potrosnje, cekanje)
    elif vrstaUredjaja in radeZimskuSezonu :
        IstorijaUredjajaZimskeSezone(ObjekatUredjajId, potrosnje, cekanje)
    elif vrstaUredjaja in radeDnevnoPovremeno :
        IstorijaUredjajaDnevneUpotrebePovremeno(ObjekatUredjajId, potrosnje, cekanje)
    elif vrstaUredjaja in proizvodjaci :
        IstorijaUredjajaProizvodjaca(ObjekatUredjajId, potrosnje, cekanje)
    else :
        IstorijaUredjajaOstalo(ObjekatUredjajId, potrosnje, cekanje)

    Predikcija(ObjekatUredjajId)

def FillObjekatSkladiste(skladiste, ulica, broj, trenutnoStanje):
    SkladisteId = FindSkladisteId(skladiste)
    ObjekatId = FindObjekatId(ulica, broj)
    sql = """insert into ObjekatSkladiste(SkladisteId, ObjekatId, TrenutnoStanje) 
          values ("""+str(SkladisteId)+", "+str(ObjekatId)+", '"+str(trenutnoStanje)+"')"
    curs.execute(sql)
    conn.commit()

def FillIstorijaP(df):
    timeRange = dajBrojDana()
    for i in range(1,timeRange+1):
        sql = """insert into IstorijaP(ObjekatUredjajId, VrednostRealizacije, Datum, Vreme) 
              values ("""+str(df[i][0])+""", '"""+str(df[i][1])+"""', '"""+str(df[i][2])+"""',
              '"""+str(df[i][3])+"""')"""

        conn.execute(sql)
    conn.commit() 
    
def FillPredikcijaP(df):
    timeRange = dajBrojDana()
    for i in range(1,timeRange+1):
        sql = """insert into PredikcijaP (ObjekatUredjajId, VrednostPredikcije, Datum, Vreme) values
               ("""+str(df[i][0])+""", '"""+str(df[i][1])+"""', '"""+str(df[i][2])+"""',
               '"""+str(df[i][3])+"""')"""
        conn.execute(sql)
    conn.commit() 

#------------- Istroija uredjaja -------------------------
def dajBrojDana():
    curerentDate = datetime.date.today()
    endOfYear = datetime.date(curerentDate.year, 12, 31)
    restDate = (endOfYear - curerentDate).days
    return (365 + restDate)*24

def IstorijaUredjajaKojiRadeKonstantno(ObjekatUredjajId, potrosnja, cekanje):
    rez = [()]
    time = datetime.datetime(datetime.date.today().year, 12, 31, 23, 00, 00)
    timeRange = dajBrojDana()

    for h in range(timeRange):
        odabir = random.randint(1,20)
        if odabir != 1:
            stepenUcestalosti = random.randint(700,1000)/1000
            vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
        else:
            vrednost = cekanje
        rez.append((ObjekatUredjajId, vrednost, time.date(), time.time()))
        time += datetime.timedelta(hours= -1)
    FillIstorijaP(rez)


def IstorijaUredjajaLetnjeSezone(ObjekatUredjajId, potrosnja, cekanje):
    rez = [()]
    time = datetime.datetime(datetime.date.today().year, 12, 31, 23, 00, 00)
    timeRange = dajBrojDana()

    for h in range(timeRange):
        if  5 < int(time.date().month) < 9:
            if 10 < int(time.hour) < 19:
                stepenUcestalosti = random.randint(600,1000)/1000
                vrednost = potrosnja[random.randint(0, (len(potrosnja)-1))][0] * stepenUcestalosti
            else:
                vrednost = cekanje
        else:
            vrednost = 0

        rez.append((ObjekatUredjajId, vrednost, time.date(), time.time()))
        time += datetime.timedelta(hours= -1)
    FillIstorijaP(rez)

def IstorijaUredjajaZimskeSezone(ObjekatUredjajId, potrosnja, cekanje):
    rez = [()]
    time = datetime.datetime(datetime.date.today().year, 12, 31, 23, 00, 00)
    timeRange = dajBrojDana()

    for h in range(timeRange):
        if  10 <= time.date().month <= 12 or 1 <= time.date().month <= 3:
            if 7 < time.hour < 23:
                stepenUcestalosti = random.randint(600,1000)/1000
                vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
            else:
                vrednost = cekanje
        else:
            vrednost = 0
        
        rez.append((ObjekatUredjajId, vrednost, time.date(), time.time()))
        time += datetime.timedelta(hours= -1)
    FillIstorijaP(rez)


def IstorijaUredjajaDnevneUpotrebePovremeno(ObjekatUredjajId, potrosnja, cekanje):
    rez = [()]
    time = datetime.datetime(datetime.date.today().year, 12, 31, 23, 00, 00)
    timeRange = dajBrojDana()

    for h in range(timeRange):
        if 6 <= time.hour <=8 or 12 <= time.hour <= 13 or 16 <= time.hour <= 18 or 20 <= time.hour <= 22:
            stepenUcestalosti = random.randint(600,1000)/1000
            vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
        else:
            vrednost = cekanje

        rez.append((ObjekatUredjajId, vrednost, time.date(), time.time()))
        time += datetime.timedelta(hours= -1)
    FillIstorijaP(rez)



def IstorijaUredjajaProizvodjaca(ObjekatUredjajId, potrosnja, cekanje):
    rez = [()]
    time = datetime.datetime(datetime.date.today().year, 12, 31, 23, 00, 00)
    timeRange = dajBrojDana()

    for h in range(timeRange):
        month = time.date().month
        hour = time.hour
        if month >= 11 or month <= 2:
            if 9 <= hour <= 15:
                stepenUcestalosti = random.randint(500,1000)/1000
                vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
            else:
                vrednost = cekanje
        elif 3 <= month <= 4:
            if 7 <= hour <= 17:
                stepenUcestalosti = random.randint(700,1000)/1000
                vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
            else:
                vrednost = cekanje
        elif 5 <= month <= 6:
            if 6 <= hour <= 19:
                stepenUcestalosti = random.randint(800,1000)/1000
                vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
            else:
                vrednost = cekanje
        elif 7 <= month <= 8:
            if 6 <= hour <= 20:
                stepenUcestalosti = random.randint(850,1000)/1000
                vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
            else:
                vrednost = cekanje
        elif 9 <= month <= 10:
            if 7 <= hour <= 18:
                stepenUcestalosti = random.randint(650,1000)/1000
                vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
            else:
                vrednost = cekanje

        rez.append((ObjekatUredjajId, vrednost, time.date(), time.time()))
        time += datetime.timedelta(hours= -1)
    FillIstorijaP(rez)

def IstorijaUredjajaOstalo(ObjekatUredjajId, potrosnja, cekanje):
    rez = [()]
    time = datetime.datetime(datetime.date.today().year, 12, 31, 23, 00, 00)
    timeRange = dajBrojDana()

    for h in range(timeRange):
        odabir = random.randint(1,2)
        if odabir == 1:
            stepenUcestalosti = random.randint(500,1000)/1000
            vrednost = potrosnja[random.randint(0, len(potrosnja)-1)][0] * stepenUcestalosti
        else:
            vrednost = cekanje
        rez.append((ObjekatUredjajId, vrednost, time.date(), time.time()))
        time += datetime.timedelta(hours= -1)
    FillIstorijaP(rez)

#-------------- Predikcija -----------------

def Predikcija(ObjekatUredjajId):
    rez = [()]
    vrednosti = FindIstoriju(ObjekatUredjajId)
    dateNow = datetime.datetime.now()
    for i in vrednosti:
        date =  datetime.datetime.strptime(str(i[1])+' '+str(i[2]), '%Y-%m-%d %H:%M:%S')
        date += datetime.timedelta(days = 365)
        while date < dateNow:
            date += datetime.timedelta(days = 365)
        stepenGreske = random.randint(-2000,2000)/100
        while (i[0] + stepenGreske) < 0 :
            stepenGreske = random.randint(-2000,2000)/100
        vrednost = i[0] + stepenGreske
        rez.append((ObjekatUredjajId, vrednost, str(date.date()), str(date.time())))
    FillPredikcijaP(rez)




#-------------- MAIN ---------------

#------- Konekcija ---------
database = "wattappDB.db"
conn = connect.create_connection(database)
curs = conn.cursor()


#-------- Adrese -------------

FillGrad('Jagodina')
FillNaselje('Jagodina','Sv. Petka')
FillNaselje('Jagodina','Tabane')
FillNaselje('Jagodina','Strelište')
FillNaselje('Jagodina','Sarina međa')
FillNaselje('Jagodina','Kolonija')

FillGrad('Kragujevac')
FillNaselje('Kragujevac','Bubanj')
FillNaselje('Kragujevac','Aerodrom')
FillNaselje('Kragujevac','Sušica')
FillNaselje('Kragujevac','Stanovo')
FillNaselje('Kragujevac','Korićani')


FillUlica('Sv. Petka', 'Stevana Jakovljevića')
FillUlica('Sv. Petka', 'Avrama Petronijevića')
FillUlica('Sv. Petka', 'Milana Rakića')
FillUlica('Sarina međa', 'Knjeginje Milice')
FillUlica('Sarina međa', 'Kneza Miloša')
FillUlica('Sarina međa', 'Stevana Jakovljevića')
FillUlica('Tabane', 'Sarajevska')
FillUlica('Tabane', 'Petra Kočića')
FillUlica('Strelište', 'Ive Andrića')
FillUlica('Strelište', 'Omladinskih brigada')
FillUlica('Kolonija', 'Vlade Zečevića')
FillUlica('Kolonija', 'Despota Stefana')

FillUlica('Korićani', 'Prve internacionalne')
FillUlica('Korićani', 'Koste Stamenkovića')
FillUlica('Korićani', 'Lipnička')
FillUlica('Stanovo', 'Gandijeva')
FillUlica('Stanovo', 'Levačkog odreda')
FillUlica('Stanovo', 'Stalaćka')
FillUlica('Sušica', 'Balkanska')
FillUlica('Sušica', 'Beogradska')
FillUlica('Aerodrom', 'Atinska')
FillUlica('Aerodrom', 'Neznanog Junaka')
FillUlica('Bubanj', 'Lepenički bulevar')
FillUlica('Bubanj', 'Save Kovačevića')

#-------- Role -------------

role = ['admin','dispečer','prozumer','potrošač','proizvođač']
FillRole(role)

#-------- Korisnici -------------

FillKorisnik('2711002721883','Uroš', 'Tatomir', 'uros@gmail.com', 'uros', '06655897166', 'Stevana Jakovljevića', '22', 'prozumer')
FillKorisnik('2605001721883','Sara', 'Velimirović', 'saravel01@gmail.com', 'sara', '0664596485', 'Knjeginje Milice', '25', 'prozumer')
FillKorisnik('1803000272188','Luka', 'Janković', 'luka@gmail.com', 'luka', '0636484891', 'Sarajevska', '27', 'proizvođač')
FillKorisnik('2604999721883','Jovana', 'Tomašević', 'jovana@gmail.com', 'jovana', '069164564647', 'Despota Stefana', '16', 'admin')
FillKorisnik('2907998721883','Marko', 'Kostić', 'marko@gmail.com', 'marko', '0619944971', 'Ive Andrića', '28', 'dispečer')

FillKorisnik('2010002721883','Marija', 'Mišković', 'marija@gmail.com', 'marija', '06654197166', 'Stevana Jakovljevića', '2', 'prozumer')
FillKorisnik('2005001721883','Nikola', 'Todorović', 'nikola@gmail.com', 'nikola', '0664946485', 'Knjeginje Milice', '27', 'prozumer')
FillKorisnik('1805000272188','Miljan', 'Lazić', 'miljan@gmail.com', 'miljan', '0636484849', 'Sarajevska', '29', 'prozumer')
FillKorisnik('2109999721883','Ivan', 'Jovović', 'ivan@gmail.com', 'ivan', '069164516547', 'Despota Stefana', '2', 'prozumer')
FillKorisnik('3007999721883','Ivana', 'Jovanović', 'ivana@gmail.com', 'ivana', '0619946471', 'Ive Andrića', '28', 'prozumer')

FillKorisnik('3011002721883','Jovan', 'Delić', 'jovan@gmail.com', 'jovan', '06655897191', 'Stevana Jakovljevića', '18', 'prozumer')
FillKorisnik('2205001721883','Miloš', 'Vidić', 'milos@gmail.com', 'milos', '0664591915', 'Knjeginje Milice', '28', 'prozumer')
FillKorisnik('2303000272198','Bogdan', 'Mikić', 'bogdan@gmail.com', 'bogdan', '0636416191', 'Sarajevska', '2', 'prozumer')
FillKorisnik('2504199721983','Slavica', 'Vikoćević', 'slavica@gmail.com', 'slavica', '069124964647', 'Despota Stefana', '1', 'prozumer')
FillKorisnik('2407199721983','Dejan', 'Milenković', 'dejan@gmail.com', 'dejan', '0619944941', 'Ive Andrića', '28', 'dispečer')

FillKorisnik('2111002724883','Nemanja', 'Nikodijević', 'nemanja@gmail.com', 'nemanja', '06661097166', 'Prve internacionalne', '12', 'prozumer')
FillKorisnik('2602004727883','Nikolina', 'Nedović', 'nikolina@gmail.com', 'nikolina', '0664494485', 'Gandijeva', '25', 'prozumer')
FillKorisnik('1801992272188','Mile', 'Marković', 'mile@gmail.com', 'mile', '0636484818', 'Balkanska', '12', 'prozumer')
FillKorisnik('2805989721883','Marica', 'Nestrović', 'marica@gmail.com', 'marica', '069134964647', 'Atinska', '12', 'prozumer')
FillKorisnik('3004959721883','Marina', 'Dangubić', 'marina@gmail.com', 'marina', '0619918171', 'Lepenički bulevar', '28', 'prozumer')

FillKorisnik('2011002721883','Andrej', 'Avramović', 'andrej@gmail.com', 'andrej', '06655216166', 'Prve internacionalne', '2', 'prozumer')
FillKorisnik('2105001721883','Andrea', 'Gendić', 'andrea@gmail.com', 'andrea', '0664591915', 'Gandijeva', '49', 'prozumer')
FillKorisnik('1003002721883','Željko', 'Vranić', 'zeljko@gmail.com', 'zeljko', '0636481181', 'Balkanska', '23', 'prozumer')
FillKorisnik('2409998721883','Bane', 'Ilić', 'bane@gmail.com', 'bane', '069164564488', 'Atinska', '13', 'prozumer')
FillKorisnik('2910997721883','Đorđe', 'Matić', 'djordje@gmail.com', 'djordje', '0619161971', 'Lepenički bulevar', '26', 'prozumer')

FillKorisnik('2011002721883','Aleksa', 'Matović', 'aleksa@gmail.com', 'aleksa', '06655163166', 'Prve internacionalne', '9', 'potrošač')
FillKorisnik('0205001721883','Mileva', 'Stošić', 'mileva@gmail.com', 'mileva', '0664591855', 'Gandijeva', '43', 'potrošač')
FillKorisnik('0303002721883','Milica', 'Bogdanović', 'milica@gmail.com', 'milica', '0636415391', 'Balkanska', '65', 'potrošač')
FillKorisnik('0504199721883','Katarina', 'Odanović', 'katarina@gmail.com', 'katarina', '069116164647', 'Atinska', '95', 'potrošač')
FillKorisnik('0907199721883','Teodor', 'Mitić', 'teodor@gmail.com', 'teodor', '0619944181', 'Lepenički bulevar', '23', 'potrošač')

#-------- Objekti -----------

FillObjekat('luka@gmail.com', 'Sarajevska', '27', 'Kuća')
FillObjekat('luka@gmail.com', 'Petra Kočića', '2', 'Stan')
FillObjekat('luka@gmail.com', 'Petra Kočića', '3', 'Stan')
FillObjekat('uros@gmail.com', 'Stevana Jakovljevića', '22', 'Kuća')
FillObjekat('uros@gmail.com', 'Avrama Petronijevića', '22', 'Kuća')
FillObjekat('saravel01@gmail.com', 'Knjeginje Milice', '25', 'Stan')
FillObjekat('saravel01@gmail.com', 'Kneza Miloša', '36', 'Stan')
 
FillObjekat('marija@gmail.com', 'Stevana Jakovljevića', '2', 'Stan')
FillObjekat('nikola@gmail.com', 'Knjeginje Milice', '27','Stan')
FillObjekat('miljan@gmail.com', 'Sarajevska', '29', 'Stan')
FillObjekat('ivan@gmail.com', 'Despota Stefana', '2', 'Kuća')
FillObjekat('ivana@gmail.com', 'Ive Andrića', '28', 'Kuća')
FillObjekat('jovan@gmail.com', 'Stevana Jakovljevića', '18', 'Stan')
FillObjekat('milos@gmail.com', 'Knjeginje Milice', '28', 'Stan')
FillObjekat('bogdan@gmail.com', 'Sarajevska', '2', 'Kuća')
FillObjekat('slavica@gmail.com', 'Despota Stefana', '1', 'Stan')



FillObjekat('nemanja@gmail.com', 'Prve internacionalne', '12', 'Stan')
FillObjekat('nikolina@gmail.com', 'Gandijeva', '25', 'Kuća')
FillObjekat('mile@gmail.com', 'Balkanska', '12', 'Kuća')
FillObjekat('marica@gmail.com', 'Atinska', '12', 'Stan')
FillObjekat('marina@gmail.com', 'Lepenički bulevar', '28', 'Stan')
FillObjekat('andrej@gmail.com', 'Koste Stamenkovića', '2', 'Kuća')
FillObjekat('andrej@gmail.com', 'Prve internacionalne', '2', 'Kuća')
FillObjekat('andrea@gmail.com', 'Levačkog odreda', '49', 'Stan')
FillObjekat('zeljko@gmail.com', 'Beogradska', '23','Stan')
FillObjekat('bane@gmail.com', 'Neznanog Junaka', '13', 'Stan')
FillObjekat('djordje@gmail.com', 'Save Kovačevića', '26', 'Kuća')
FillObjekat('aleksa@gmail.com', 'Lipnička', '9', 'Kuća')
FillObjekat('aleksa@gmail.com', 'Prve internacionalne', '9', 'Kuća')
FillObjekat('mileva@gmail.com', 'Stalaćka', '43', 'Stan')
FillObjekat('milica@gmail.com', 'Balkanska', '65', 'Stan')
FillObjekat('katarina@gmail.com', 'Atinska', '95', 'Kuća')
FillObjekat('teodor@gmail.com', 'Lepenički bulevar', '23', 'Stan')

#--------- Prostorije ------------

FillProstorija("Kuhinja")
FillProstorija("Trpezarija")
FillProstorija("Hodnik")
FillProstorija("Spavaca soba")
FillProstorija("Stepeniste")
FillProstorija("Dnevna soba")
FillProstorija("Garderober")
FillProstorija("Balkon")
FillProstorija("Terasa")
FillProstorija("Krov")
FillProstorija("Toalet")
FillProstorija("Dvorište")



#--------- Tip uredjaja -----------

FillTipUredjaja('Potrošač')
FillTipUredjaja('Proizvođač')

#--------- Vrsta uredjaja -----------

FillVrstaUredjaja('Klima')
FillVrstaUredjaja('Frižider')
FillVrstaUredjaja('Zamrzivač')
FillVrstaUredjaja('Bojler')
FillVrstaUredjaja('Šporet')
FillVrstaUredjaja('Rerna')
FillVrstaUredjaja('TA peć')
FillVrstaUredjaja('Norveški radijator')
FillVrstaUredjaja('Veš mašina')
FillVrstaUredjaja('Mašina za sušenje veša')
FillVrstaUredjaja('Mašina za pranje sudova')
FillVrstaUredjaja('Solarni panel')
FillVrstaUredjaja('Vetrogenerator')
FillVrstaUredjaja('Ostalo')

#--------- Uredjaji i stanja ----------

FillUredjaj('Bosch CL3000i-35', 0, 'Potrošač', 'Klima')
FillPPoStanjuUredjaja('Bosch CL3000i-35','Hlađenje','850')
FillPPoStanjuUredjaja('Bosch CL3000i-35','Greje','775')

FillUredjaj('Bosch KGN49XLEA', 30, 'Potrošač', 'Frižider')
FillPPoStanjuUredjaja('Bosch KGN49XLEA','Hlađenje','100')

FillUredjaj('Bosch RFNE290L31WN', 50, 'Potrošač', 'Zamrzivač')
FillPPoStanjuUredjaja('Bosch RFNE290L31WN','Hlađenje','120')

FillUredjaj('Vox WHF8021', 100, 'Potrošač', 'Bojler')
FillPPoStanjuUredjaja('Vox WHF8021','Greje','180')

FillUredjaj('Beko HIC 64402', 2, 'Potrošač', 'Šporet')
FillPPoStanjuUredjaja('Beko HIC 64402','Greje mala ringla','1000')
FillPPoStanjuUredjaja('Beko HIC 64402','Prosirena ringla','1900')
FillPPoStanjuUredjaja('Beko HIC 64402','Greje srednja ringla','1250')
FillPPoStanjuUredjaja('Beko HIC 64402','Greje velika ringla','1400')
FillPPoStanjuUredjaja('Beko HIC 64402','Greje najveća ringla','1800')

FillUredjaj('Candy FCP625XL', 20, 'Potrošač', 'Mašina za pranje sudova')
FillPPoStanjuUredjaja('Candy FCP625XL','Jedan grejač','1000')
FillPPoStanjuUredjaja('Candy FCP625XL','Oba grejač','2000')
FillPPoStanjuUredjaja('Candy FCP625XL','Ventilator','800')

FillUredjaj('Magnohrom MTA', 0, 'Potrošač', 'TA peć')
FillPPoStanjuUredjaja('Magnohrom MTA','Grejanje','4500')

FillUredjaj('Volt PRW2000B', 0, 'Potrošač', 'Norveški radijator')
FillPPoStanjuUredjaja('Volt PRW2000B','Grejanje stepen jedan','1000')
FillPPoStanjuUredjaja('Volt PRW2000B','Grejanje stepen dva','2000')

FillUredjaj('Gorenje WNPI84BDS', 2, 'Potrošač', 'Veš mašina')
FillPPoStanjuUredjaja('Gorenje WNPI84BDS','Pranje','260')

FillUredjaj('Bosch WQG24500BY', 2, 'Potrošač', 'Mašina za sušenje veša')
FillPPoStanjuUredjaja('Bosch WQG24500BY','Sušenje','400')

FillUredjaj('Bosch SMS2ITW04E', 0, 'Potrošač', 'Mašina za pranje sudova')
FillPPoStanjuUredjaja('Bosch SMS2ITW04E','Pranje','500')

FillUredjaj('EXE SOLAR A-HCM345/120 345W', 45, 'Proizvođač', 'Solarni panel')
FillPPoStanjuUredjaja('EXE SOLAR A-HCM345/120 345W','Proizvodnja','345')

FillUredjaj('Vetrenjaca 400watt', 45, 'Proizvođač', 'Vetrogenerator')
FillPPoStanjuUredjaja('Vetrenjaca 400watt','Proizvodnja','400')

#--------- Skladista -----------------

FillSkladista('LIFEPO4', 7200, 2)
FillSkladista('BYD', 5000, 2)


#--------- Objekat Uredjaj --------s




FillObjekatUredjaj("Bosch CL3000i-35", "Stevana Jakovljevića", "22", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Stevana Jakovljevića", "22", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Stevana Jakovljevića", "22", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Stevana Jakovljevića","22", "Hodnik")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Stevana Jakovljevića", "22", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Stevana Jakovljevića", "22", "Dnevna soba")

FillObjekatUredjaj("Bosch CL3000i-35", "Avrama Petronijevića", "22", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Avrama Petronijevića", "22", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Avrama Petronijevića", "22", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Avrama Petronijevića", "22", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Avrama Petronijevića", "22", "Krov")
FillObjekatUredjaj("Bosch WQG24500BY", "Avrama Petronijevića", "22", "Dnevna soba")

FillObjekatUredjaj("Bosch CL3000i-35", "Knjeginje Milice", "25", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Knjeginje Milice", "25", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Knjeginje Milice", "25", "Kuhinja")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Knjeginje Milice", "25", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Knjeginje Milice", "25", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Kneza Miloša", "36", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Kneza Miloša", "36", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Kneza Miloša", "36", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Kneza Miloša", "36", "Dnevna soba")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Kneza Miloša", "36", "Dnevna soba")

FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "27", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "27", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "27", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "27", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "27", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "27", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "27", "Krov")

FillObjekatUredjaj("Vetrenjaca 400watt", "Petra Kočića", "2", "Dvorište")
FillObjekatUredjaj("Vetrenjaca 400watt", "Petra Kočića", "2", "Dvorište")
FillObjekatUredjaj("Vetrenjaca 400watt", "Petra Kočića", "2", "Dvorište")
FillObjekatUredjaj("Vetrenjaca 400watt", "Petra Kočića", "2", "Dvorište")
FillObjekatUredjaj("Vetrenjaca 400watt", "Petra Kočića", "2", "Dvorište")

FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Petra Kočića", "3", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Petra Kočića", "3", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Petra Kočića", "3", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Petra Kočića", "3", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Petra Kočića", "3", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Stevana Jakovljevića", "2", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Stevana Jakovljevića", "2", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Stevana Jakovljevića", "2", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Stevana Jakovljevića", "2", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Stevana Jakovljevića", "2", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Stevana Jakovljevića", "2", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Knjeginje Milice", "27", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Knjeginje Milice", "27", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Knjeginje Milice", "27", "Hodnik")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Knjeginje Milice", "27", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Knjeginje Milice", "27", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Sarajevska", "29", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Sarajevska", "29", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Sarajevska", "29", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Sarajevska", "29", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "29", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Sarajevska", "29", "Dnevna soba")

FillObjekatUredjaj("Bosch CL3000i-35", "Despota Stefana", "2", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Despota Stefana", "2", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Despota Stefana", "2", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Despota Stefana", "2", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Despota Stefana", "2", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Despota Stefana", "2", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Ive Andrića", "28", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Ive Andrića", "28", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Ive Andrića", "28", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Ive Andrića", "28", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Ive Andrića", "28", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Ive Andrića", "28", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Stevana Jakovljevića", "18", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Stevana Jakovljevića", "18", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Stevana Jakovljevića", "18", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Stevana Jakovljevića", "18", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Stevana Jakovljevića", "18", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Stevana Jakovljevića", "18", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Knjeginje Milice", "28", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Knjeginje Milice", "28", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Knjeginje Milice", "28", "Hodnik")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Knjeginje Milice", "28", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Knjeginje Milice", "28", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Sarajevska", "2", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Sarajevska", "2", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Sarajevska", "2", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Sarajevska", "2", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Sarajevska", "2", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Sarajevska", "2", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Despota Stefana", "1", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Despota Stefana", "1", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Despota Stefana", "1", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Despota Stefana", "1", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Despota Stefana", "1", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Despota Stefana", "1", "Kuhinja")



FillObjekatUredjaj("Bosch CL3000i-35", "Prve internacionalne", "12", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Prve internacionalne", "12", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Prve internacionalne", "12", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Prve internacionalne", "12", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Prve internacionalne", "12", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Prve internacionalne", "12", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Gandijeva", "25", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Gandijeva", "25", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Gandijeva", "25", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Gandijeva", "25", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Gandijeva", "25", "Krov")
FillObjekatUredjaj("Bosch WQG24500BY", "Gandijeva", "25", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Balkanska", "12", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Balkanska", "12", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Balkanska", "12", "Hodnik")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Balkanska", "12", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Balkanska", "12", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Atinska", "12", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Atinska", "12", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Atinska", "12", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Atinska", "12", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Atinska", "12", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Atinska", "12", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Lepenički bulevar", "28", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Lepenički bulevar", "28", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Lepenički bulevar", "28", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Lepenički bulevar", "28", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Lepenički bulevar", "28", "Krov")
FillObjekatUredjaj("Bosch WQG24500BY", "Lepenički bulevar", "28", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Koste Stamenkovića", "2", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Koste Stamenkovića", "2", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Koste Stamenkovića", "2", "Hodnik")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Koste Stamenkovića", "2", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Koste Stamenkovića", "2", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Prve internacionalne", "2", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Prve internacionalne", "2", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Prve internacionalne", "2", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Prve internacionalne", "2", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Prve internacionalne", "2", "Krov")
FillObjekatUredjaj("Bosch WQG24500BY", "Prve internacionalne", "2", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Levačkog odreda", "49", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Levačkog odreda", "49", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Levačkog odreda", "49", "Hodnik")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Levačkog odreda", "49", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Levačkog odreda", "49", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Beogradska", "23", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Beogradska", "23", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Beogradska", "23", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Beogradska", "23", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Beogradska", "23", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Beogradska", "23", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Neznanog Junaka", "13", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Neznanog Junaka", "13", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Neznanog Junaka", "13", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Neznanog Junaka", "13", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Neznanog Junaka", "13", "Krov")
FillObjekatUredjaj("Bosch WQG24500BY", "Neznanog Junaka", "13", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Save Kovačevića", "26", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Save Kovačevića", "26", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Save Kovačevića", "26", "Hodnik")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Save Kovačevića", "26", "Krov")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Save Kovačevića", "26", "Krov")

FillObjekatUredjaj("Bosch CL3000i-35", "Lipnička", "9", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Lipnička", "9", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Lipnička", "9", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Lipnička", "9", "Dnevna soba")
FillObjekatUredjaj("EXE SOLAR A-HCM345/120 345W", "Lipnička", "9", "Krov")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Lipnička", "9", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Prve internacionalne", "9", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Prve internacionalne", "9", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Prve internacionalne", "9", "Hodnik")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Prve internacionalne", "9", "Dnevna soba")
FillObjekatUredjaj("Bosch WQG24500BY", "Prve internacionalne", "9", "Kuhinja")

FillObjekatUredjaj("Bosch CL3000i-35", "Stalaćka", "43", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Stalaćka", "43", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Stalaćka", "43", "Hodnik")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Stalaćka", "43", "Dnevna soba")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Stalaćka", "43", "Dnevna soba")

FillObjekatUredjaj("Bosch CL3000i-35", "Balkanska", "65", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Balkanska", "65", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Balkanska", "65", "Hodnik")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Balkanska", "65", "Dnevna soba")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Balkanska", "65", "Dnevna soba")

FillObjekatUredjaj("Bosch CL3000i-35", "Atinska", "95", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Atinska", "95", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Atinska", "95", "Hodnik")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Atinska", "95", "Dnevna soba")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Atinska", "95", "Dnevna soba")

FillObjekatUredjaj("Bosch CL3000i-35", "Lepenički bulevar", "23", "Kuhinja")
FillObjekatUredjaj("Bosch KGN49XLEA", "Lepenički bulevar", "23", "Kuhinja")
FillObjekatUredjaj("Volt PRW2000B", "Lepenički bulevar", "23", "Hodnik")
FillObjekatUredjaj("Bosch SMS2ITW04E", "Lepenički bulevar", "23", "Dnevna soba")
FillObjekatUredjaj("Gorenje WNPI84BDS", "Lepenički bulevar", "23", "Dnevna soba")

#--------- Objekat Skladiste --------

FillObjekatSkladiste("LIFEPO4", "Stevana Jakovljevića", "22", "2000")
FillObjekatSkladiste("LIFEPO4", "Stevana Jakovljevića", "22", "6000")

FillObjekatSkladiste("BYD", "Avrama Petronijevića", "22", "2000")
FillObjekatSkladiste("BYD", "Avrama Petronijevića", "22", "2600")

FillObjekatSkladiste("BYD", "Kneza Miloša", "36", "2000")

FillObjekatSkladiste("LIFEPO4", "Sarajevska", "27", "3600")
FillObjekatSkladiste("LIFEPO4", "Sarajevska", "27", "200")
FillObjekatSkladiste("LIFEPO4", "Sarajevska", "27", "300")
FillObjekatSkladiste("LIFEPO4", "Sarajevska", "27", "260")
FillObjekatSkladiste("LIFEPO4", "Sarajevska", "27", "4690")

FillObjekatSkladiste("BYD", "Petra Kočića", "2", "6000")
FillObjekatSkladiste("BYD", "Petra Kočića", "2", "6800")
FillObjekatSkladiste("BYD", "Petra Kočića", "2", "7000")
FillObjekatSkladiste("BYD", "Petra Kočića", "2", "7200")
FillObjekatSkladiste("BYD", "Petra Kočića", "2", "6500")

FillObjekatSkladiste("LIFEPO4", "Petra Kočića", "3", "2000")
FillObjekatSkladiste("LIFEPO4", "Petra Kočića", "3", "3000")
FillObjekatSkladiste("BYD", "Petra Kočića", "3", "300")
FillObjekatSkladiste("BYD", "Petra Kočića", "3", "100")
FillObjekatSkladiste("BYD", "Petra Kočića", "3", "0")


FillObjekatSkladiste("LIFEPO4", "Prve internacionalne", "12", "0")

FillObjekatSkladiste("LIFEPO4", "Gandijeva", "25", "6000")

FillObjekatSkladiste("BYD", "Balkanska", "12", "2000")

FillObjekatSkladiste("BYD", "Atinska", "12", "2600")

FillObjekatSkladiste("BYD", "Lepenički bulevar", "28", "2000")

FillObjekatSkladiste("LIFEPO4", "Koste Stamenkovića", "2", "3600")

FillObjekatSkladiste("LIFEPO4", "Prve internacionalne", "2", "200")

FillObjekatSkladiste("LIFEPO4", "Levačkog odreda", "49", "300")

FillObjekatSkladiste("BYD", "Beogradska", "23", "2000")

FillObjekatSkladiste("LIFEPO4", "Neznanog Junaka", "13", "3600")

FillObjekatSkladiste("LIFEPO4", "Save Kovačevića", "26", "200")

FillObjekatSkladiste("LIFEPO4", "Lipnička", "9", "300")