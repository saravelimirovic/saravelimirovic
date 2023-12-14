using backend.Models;
using Microsoft.EntityFrameworkCore;

namespace backend.Context
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) 
        {
            
        }

        public DbSet<Korisnik> Korisnik { get; set; }
        public DbSet<Rola> Rola { get; set; }
        public DbSet<Ulica> Ulica { get; set; }
        public DbSet<Naselje> Naselje { get; set; }
        public DbSet<Grad> Grad { get; set; }

        // nove
        public DbSet<Prostorija> Prostorija{ get; set; }
        public DbSet<Objekat> Objekat { get; set; }
        public DbSet<ObjekatUredjaj> ObjekatUredjaj { get; set; }
        public DbSet<Uredjaj> Uredjaj { get; set; }
        public DbSet<TipUredjaja> TipUredjaja { get; set; }
        public DbSet<VrstaUredjaja> VrstaUredjaja { get; set; }
        public DbSet<IstorijaP> IstorijaP { get; set; }
        public DbSet<PredikcijaP> PredikcijaP { get; set; }
        public DbSet<Skladiste> Skladiste { get; set; }
        public DbSet<ObjekatSkladiste> ObjekatSkladiste { get; set; }
        public DbSet<PPoStanjuUredjaja> PPoStanjuUredjaja { get; set; }
    }
}
