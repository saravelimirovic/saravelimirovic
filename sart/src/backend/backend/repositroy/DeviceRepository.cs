using backend.Common;
using backend.Context;
using backend.DTOs;
using backend.Helpers.interfaces;
using backend.Models;
using backend.repositroy.interfaces;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel;

namespace backend.repositroy
{
    public class DeviceRepository : IDeviceRepository
    {
        private readonly AppDbContext _context;
        public DeviceRepository(AppDbContext context, IRecordCalculation recordCalculation)
        {
            _context = context;
        }

        // Vraca ukupno skladisteno u jednom objektu
        public async Task<double> getSumOfSkladisteForObjectAsync(int objektaId)
        {
            double result = await _context.ObjekatSkladiste
                                  .Where(device => device.ObjekatId == objektaId)
                                  .SumAsync(device => device.TrenutnoStanje);
            return result;
        }

        // Uzima sva skladista iz jednog objekta
        public async Task<List<ObjekatSkladiste>> getSkladisteForObjectAsync(int objektaId)
        {
            List<ObjekatSkladiste> lista = await _context.ObjekatSkladiste
                                  .Where(device => device.ObjekatId == objektaId)
                                  .ToListAsync();
            return lista;
        }

        // Vraca naziv skaldista za njegov id
        public async Task<string> getSkladisteNazivByIdAsync(int skladisteId)
        {
            string skladiste = await _context.Skladiste
                                  .Where(device => device.Id == skladisteId)
                                  .Select(device => device.Naziv)
                                  .FirstOrDefaultAsync();
            return skladiste;
        }

        // Vraca naziv skaldista za njegov id
        public async Task<double> getMaxSkladisteByIdAsync(int skladisteId)
        {
            double skladiste = await _context.Skladiste
                                  .Where(device => device.Id == skladisteId)
                                  .Select(device => device.MaxSkladista)
                                  .FirstOrDefaultAsync();
            return skladiste;
        }

         // Vraca naziv skaldista za njegov id
        public async Task<double> getPotrosnjaZaCuvanjeSkladisteByIdAsync(int skladisteId)
        {
            double skladiste = await _context.Skladiste
                                  .Where(device => device.Id == skladisteId)
                                  .Select(device => device.PotrosnjaZaCuvanjePoSatu)
                                  .FirstOrDefaultAsync();
            return skladiste;
        }

        // Uzima sve uređaje iz jednog objekta
        public async Task<List<ObjekatUredjaj>> getUredjajeForObjectAsync(int objektaId)
        {
            List<ObjekatUredjaj> lista = await _context.ObjekatUredjaj
                                  .Where(device => device.ObjekatId == objektaId)
                                  .ToListAsync();
            return lista;
        }
        // Trazi uređaj po imenu
        public async Task<Uredjaj> getUredjajByNaziv(string naziv)
        {
            return await _context.Uredjaj.
                         Where(device => device.Naziv.ToUpper() == naziv.ToUpper())
                         .FirstOrDefaultAsync();
        }


        // Uzima sve uredjaje iz jednog objekta u jednoj prostoriji
        public async Task<List<ObjekatUredjaj>> getUredjajeForObjectForOneProstorijaAsync(int objektaId, int prostorijaId)
        {
            List<ObjekatUredjaj> lista = await _context.ObjekatUredjaj
                                  .Where(device => device.ObjekatId == objektaId && device.ProstorijaId == prostorijaId)
                                  .ToListAsync();
            return lista;
        }

        //Vracam uredjaj za njegova id
        public async Task<Uredjaj> getUredjajByIdAsync(int uredjajId)
        {
            Uredjaj uredjaj = await _context.Uredjaj
                                  .Where(device => device.Id == uredjajId)
                                  .FirstOrDefaultAsync();
            return uredjaj;
        }

        //Vracam naziv uredjaja za njegova id
        public async Task<string> getUredjajNazivByIdAsync(int uredjajId)
        {
            string uredjaj = await _context.Uredjaj
                                  .Where(device => device.Id == uredjajId)
                                  .Select(device => device.Naziv)
                                  .FirstOrDefaultAsync();
            return uredjaj;
        }
        //vracam tabelu objekaturedjaj za odredjeni id

        public async Task<ObjekatUredjaj> getObjekatUredjajaByIdAsync(int objekatUredjajId)
        {
            ObjekatUredjaj objekatUredjaj = await _context.ObjekatUredjaj
                                  .Where(device => device.Id == objekatUredjajId)
                                  .FirstOrDefaultAsync();
            return objekatUredjaj;
        }



        //vracam trenutno stanje on/off
        public async Task<string> getStatusUredjajaAsync(int objekatUredjajId)
        {
            string status = await _context.IstorijaP
                           .Include(ukljuceno => ukljuceno.ObjekatUredjaj)
                           .ThenInclude(ukljuceno => ukljuceno.Uredjaj)
                           .Where(ukljuceno => ukljuceno.ObjekatUredjaj.Id == objekatUredjajId)
                           .Select(ukljuceno => ukljuceno.ObjekatUredjaj.Ukljucen)
                           .FirstOrDefaultAsync();
            return status;
        }


        //vracam tip uredjaja
        public async Task<string> getTipUredjajaByIdAsync(int uredjajId)
        {
            string tip = await _context.Uredjaj
                           .Include(device => device.TipUredjaja)
                           .Where(device => device.Id == uredjajId)
                           .Select(device => device.TipUredjaja.Naziv)
                           .FirstOrDefaultAsync();
            return tip;
        }

        //vracam vrstu uredjaja
        public async Task<string> getVrstaUredjajaByIdAsync(int uredjajId)
        {
            string tip = await _context.Uredjaj
                           .Include(device => device.VrstaUredjaja)
                           .Where(device => device.Id == uredjajId)
                           .Select(device => device.VrstaUredjaja.Naziv)
                           .FirstOrDefaultAsync();
            return tip;
        }

        //vraca vrste po tipu
        public async Task<List<VrstaUredjajaDTO>> getVrstaByTipIdAsync(ETipUredjaj tipUredjaja)
        {
            List<VrstaUredjajaDTO> lista = await _context.Uredjaj
                                              .Include(tip => tip.VrstaUredjaja)
                                              .Where(tip => tip.TipUredjajaId == (int)tipUredjaja)
                                              .Select(tip => new VrstaUredjajaDTO(tip.VrstaUredjajaId, tip.VrstaUredjaja.Naziv))
                                              .Distinct()
                                              .ToListAsync();

            return lista;
        }

        // vraca uređaje po vrsti 

        public async Task<List<Uredjaj>> getUredjajiByVrstaIdAsync(int vrstaId)
        {
            List<Uredjaj> lista = await _context.Uredjaj
                                              .Where(tip => tip.VrstaUredjajaId == vrstaId)
                                              .ToListAsync();

            return lista;
        }

        //vracam trenutnu potrosnju u ovom satu (u slucaju da je uredjaj iskljucen vraca standby potrosnju)
        public async Task<double> getUredjajPotrosnjaNowAsync(int objekatUredjajId)
        {
            double rez;
            DateTime temp = DateTime.Now;
            DateOnly date = new DateOnly(temp.Year, temp.Month, temp.Day);
            TimeOnly time = new TimeOnly(temp.Hour, temp.Minute, temp.Second);
            string status = await getStatusUredjajaAsync(objekatUredjajId);

            if (status.Equals("NE"))
            {
                rez = await _context.ObjekatUredjaj
                            .Include(device => device.Uredjaj)
                            .Where(device => device.Id == objekatUredjajId)
                            .Select(device => device.Uredjaj.PPrilikomMirovanja)
                            .FirstOrDefaultAsync();
            }
            else
            {
                rez = await _context.IstorijaP
                            .Where(device => device.ObjekatUredjajId == objekatUredjajId &&
                                    device.Datum.Day == date.Day && device.Datum.Month == date.Month
                                    && device.Vreme <= time)
                            .Select(device => device.VrednostRealizacije)
                            .FirstOrDefaultAsync();
            }
            return rez;
        }

        // vraca listu potrosnji za jedan uredjaja
        public async Task<List<double>> getPotrosnjaForDeviceAsync(int deviceId)
        {
            List<double> potrosnja = await _context.PPoStanjuUredjaja
                                                   .Where(potrosnja => potrosnja.UredjajId == deviceId)
                                                   .Select(potrosnja => potrosnja.PPoSatuStanja)
                                                   .ToListAsync();
            return potrosnja;
        }

        // vraca vrednost koju uredjaj trosi dok nije ukljucen za uredajaj
        public async Task<double> getCekanjeForDeviceAsync(int deviceId)
        {
            double cekanje = await _context.Uredjaj
                                           .Where(cekanje => cekanje.Id == deviceId)
                                           .Select(cekanje => cekanje.PPrilikomMirovanja)
                                           .FirstOrDefaultAsync();
            return cekanje;
        }

        // vraca maksimalni id kako bi uneo u istoriju i predikciju sa tim id-jem
        public async Task<int> getObjekatUredjajIdForDeviceAsync()
        {
            int ObjekatUredjajId = await _context.ObjekatUredjaj.MaxAsync(max => max.Id);
            return ObjekatUredjajId;
        }

        // ubacuje u tabelu objekat uredajja (povezuje uredjaj sa objektom)
        public async Task insertDeviceHistoryAndPredicitionAsync(ObjekatUredjaj objekatUredjaj)
        {
            await _context.ObjekatUredjaj.AddAsync(objekatUredjaj);
            await _context.SaveChangesAsync();
        }

        // menja stanje uredjaja on/off
        public async Task updateDeviceStateAsync(int objekatUredjajId)
        {
            ObjekatUredjaj objekatUredjaj = await getObjekatUredjajaByIdAsync(objekatUredjajId);

            if (objekatUredjaj.Ukljucen.ToUpper().Equals("NE"))
            {
                objekatUredjaj.Ukljucen = "Da";
                await _context.SaveChangesAsync();
            }
            else
            {
                objekatUredjaj.Ukljucen = "Ne";
                await _context.SaveChangesAsync();
            }
        }

        // menja kontrolu uredjaja da/ne
        public async Task updateDeviceControlAsync(int objekatUredjajId)
        {
            ObjekatUredjaj objekatUredjaj = await getObjekatUredjajaByIdAsync(objekatUredjajId);

            if (objekatUredjaj.Kontrola.ToUpper().Equals("NE"))
            {
                if (objekatUredjaj.Dozvola.ToUpper().Equals("NE"))
                    objekatUredjaj.Dozvola = "Da";
                objekatUredjaj.Kontrola = "Da";
                await _context.SaveChangesAsync();
            }
            else
            {
                objekatUredjaj.Kontrola = "Ne";
                await _context.SaveChangesAsync();
            }
        }

        // menja dozvolu uredjaja da/ne
        public async Task updateDevicePermissionAsync(int objekatUredjajId)
        {
            ObjekatUredjaj objekatUredjaj = await getObjekatUredjajaByIdAsync(objekatUredjajId);

            if (objekatUredjaj.Dozvola.ToUpper().Equals("NE"))
            {
                objekatUredjaj.Dozvola = "Da";
                await _context.SaveChangesAsync();
            }
            else
            {
                if (objekatUredjaj.Kontrola.ToUpper().Equals("DA"))
                    objekatUredjaj.Kontrola = "Ne";
                objekatUredjaj.Dozvola = "Ne";
                await _context.SaveChangesAsync();
            }
        }

        public async Task deleteDeviceByIdAsync(int objectUredjajId)
        { 
            _context.Database.ExecuteSqlInterpolated($"DELETE FROM PredikcijaP WHERE ObjekatUredjajId = '{objectUredjajId}'");
            _context.Database.ExecuteSqlInterpolated($"DELETE FROM IstorijaP WHERE ObjekatUredjajId = '{objectUredjajId}'");

            ObjekatUredjaj objekatUredjaj = _context.Set<ObjekatUredjaj>().Find(objectUredjajId);
            if (objekatUredjaj != null)
            {
                _context.Set<ObjekatUredjaj>().Remove(objekatUredjaj);
                _context.SaveChanges();
            }
        }
        
        public async Task deleteStoreByIdAsync(int objekatSkladisteId)
        {
            ObjekatSkladiste objekatSkladiste = _context.Set<ObjekatSkladiste>().Find(objekatSkladisteId);
            if (objekatSkladiste != null)
            {
                _context.Set<ObjekatSkladiste>().Remove(objekatSkladiste);
                _context.SaveChanges();
            }
        }

        public async Task<List<VrstaUredjaja>> getVrsteUredjajaAsync()
        {
            List<VrstaUredjaja> vrste = await _context.VrstaUredjaja.ToListAsync();
            return vrste;
        }

        public async Task insertDeviceAsync(Uredjaj uredjaj)
        {
            await _context.Uredjaj.AddAsync(uredjaj);
            await _context.SaveChangesAsync();
        }
        public async Task insertPowerConsumptionAsync(PPoStanjuUredjaja pPoStanjuUredjaja)
        {
            await _context.PPoStanjuUredjaja.AddAsync(pPoStanjuUredjaja);
            await _context.SaveChangesAsync();
        }
        public async Task<List<StoreDTO>> getAllStoreAsync()
        {
            List<StoreDTO> lista = await _context.Skladiste
                                        .Select(store => new StoreDTO(store.Id, store.Naziv))
                                        .ToListAsync();

            return lista;
        }

        public async Task AddStoreAsync(StoreDTO store)
        {
            int storeId = 0;
            Skladiste newStore = new Skladiste();
            if (store.MaxSkladiste != 0)
            {
                newStore.Naziv = store.Naziv;
                newStore.MaxSkladista = store.MaxSkladiste;
                newStore.PotrosnjaZaCuvanjePoSatu = store.PotrosnjaZaCuvanjePoSatu;
                await _context.Skladiste.AddAsync(newStore);
                await _context.SaveChangesAsync();
                newStore = await _context.Skladiste.Where(skladiste => skladiste.Naziv.ToUpper() == store.Naziv.ToUpper()).FirstOrDefaultAsync();
                storeId = newStore.Id;
            }
            ObjekatSkladiste objectStore = new ObjekatSkladiste();
            if (storeId == 0)
                storeId = store.Id;
            objectStore.SkladisteId = storeId;
            objectStore.ObjekatId = store.ObjekatId;
            objectStore.TrenutnoStanje = store.TrenutnoStanje;
            await _context.ObjekatSkladiste.AddAsync(objectStore);
            await _context.SaveChangesAsync();
        }


        public async Task<List<Prostorija>> getAllProstorije()
        {
            var result = await _context.Prostorija.ToListAsync();

            return result;
        } 
    }
}
