﻿// <auto-generated />
using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using backend.Context;

#nullable disable

namespace backend.Migrations
{
    [DbContext(typeof(AppDbContext))]
    [Migration("20230325163542_init1")]
    partial class init1
    {
        /// <inheritdoc />
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder.HasAnnotation("ProductVersion", "7.0.3");

            modelBuilder.Entity("backend.Models.Grad", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.ToTable("Grad");
                });

            modelBuilder.Entity("backend.Models.Prostorija", b =>
            {
                b.Property<int>("Id")
                    .ValueGeneratedOnAdd()
                    .HasColumnType("INTEGER");

                b.Property<string>("Naziv")
                    .HasColumnType("TEXT");

                b.HasKey("Id");

                b.ToTable("Prostorija");
            });

            modelBuilder.Entity("backend.Models.IstorijaP", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<DateOnly>("Datum")
                        .HasColumnType("TEXT");

                    b.Property<int>("ObjekatUredjajId")
                        .HasColumnType("INTEGER");

                    b.Property<double>("VrednostRealizacije")
                        .HasColumnType("REAL");

                    b.Property<TimeOnly>("Vreme")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.HasIndex("ObjekatUredjajId");

                    b.ToTable("IstorijaP");
                });

            modelBuilder.Entity("backend.Models.Korisnik", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("AdresniBroj")
                        .HasColumnType("TEXT");

                    b.Property<string>("BrTelefona")
                        .HasColumnType("TEXT");

                    b.Property<string>("Email")
                        .HasColumnType("TEXT");

                    b.Property<string>("Ime")
                        .HasColumnType("TEXT");

                    b.Property<string>("JMBG")
                        .HasColumnType("TEXT");

                    b.Property<string>("Prezime")
                        .HasColumnType("TEXT");

                    b.Property<int>("RolaId")
                        .HasColumnType("INTEGER");

                    b.Property<string>("Sifra")
                        .HasColumnType("TEXT");

                    b.Property<int>("UlicaId")
                        .HasColumnType("INTEGER");

                    b.HasKey("Id");

                    b.HasIndex("RolaId");

                    b.HasIndex("UlicaId");

                    b.ToTable("Korisnik");
                });

            modelBuilder.Entity("backend.Models.Naselje", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<int>("GradId")
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.HasIndex("GradId");

                    b.ToTable("Naselje");
                });

            modelBuilder.Entity("backend.Models.Objekat", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("AdresniBroj")
                        .HasColumnType("TEXT");

                    b.Property<int>("KorisnikId")
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.Property<int>("UlicaId")
                        .HasColumnType("INTEGER");

                    b.HasKey("Id");

                    b.HasIndex("KorisnikId");

                    b.HasIndex("UlicaId");

                    b.ToTable("Objekat");
                });

            modelBuilder.Entity("backend.Models.ObjekatSkladiste", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<int>("ObjekatId")
                        .HasColumnType("INTEGER");

                    b.Property<int>("SkladisteId")
                        .HasColumnType("INTEGER");

                    b.Property<double>("TrenutnoStanje")
                        .HasColumnType("REAL");

                    b.HasKey("Id");

                    b.HasIndex("ObjekatId");

                    b.HasIndex("SkladisteId");

                    b.ToTable("ObjekatSkladiste");
                });

            modelBuilder.Entity("backend.Models.ObjekatUredjaj", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("Dozvola")
                        .HasColumnType("TEXT");

                    b.Property<string>("Kontrola")
                        .HasColumnType("TEXT");

                    b.Property<int>("ObjekatId")
                        .HasColumnType("INTEGER");

                    b.Property<string>("Ukljucen")
                        .HasColumnType("TEXT");

                    b.Property<int>("UredjajId")
                        .HasColumnType("INTEGER");

                    b.Property<int>("ProstorijaId")
                        .HasColumnType("INTEGER");

                    b.HasKey("Id");

                    b.HasIndex("ProstorijaId");

                    b.HasIndex("ObjekatId");

                    b.HasIndex("UredjajId");

                    b.ToTable("ObjekatUredjaj");
                });

            modelBuilder.Entity("backend.Models.PPoStanjuUredjaja", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.Property<double>("PPoSatuStanja")
                        .HasColumnType("REAL");

                    b.Property<int>("UredjajId")
                        .HasColumnType("INTEGER");

                    b.HasKey("Id");

                    b.HasIndex("UredjajId");

                    b.ToTable("PPoStanjuUredjaja");
                });

            modelBuilder.Entity("backend.Models.PredikcijaP", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<DateOnly>("Datum")
                        .HasColumnType("TEXT");

                    b.Property<int>("ObjekatUredjajId")
                        .HasColumnType("INTEGER");

                    b.Property<double>("VrednostPredikcije")
                        .HasColumnType("REAL");

                    b.Property<TimeOnly>("Vreme")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.HasIndex("ObjekatUredjajId");

                    b.ToTable("PredikcijaP");
                });

            modelBuilder.Entity("backend.Models.Rola", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.ToTable("Rola");
                });

            modelBuilder.Entity("backend.Models.Skladiste", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<double>("MaxSkladista")
                        .HasColumnType("REAL");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.Property<double>("PotrosnjaZaCuvanjePoSatu")
                        .HasColumnType("REAL");

                    b.HasKey("Id");

                    b.ToTable("Skladiste");
                });

            modelBuilder.Entity("backend.Models.TipUredjaja", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.ToTable("TipUredjaja");
                });

            modelBuilder.Entity("backend.Models.Ulica", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<int>("NaseljeId")
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.HasIndex("NaseljeId");

                    b.ToTable("Ulica");
                });

            modelBuilder.Entity("backend.Models.Uredjaj", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.Property<double>("PPrilikomMirovanja")
                        .HasColumnType("REAL");

                    b.Property<int>("TipUredjajaId")
                        .HasColumnType("INTEGER");

                    b.Property<int>("VrstaUredjajaId")
                        .HasColumnType("INTEGER");

                    b.HasKey("Id");

                    b.HasIndex("TipUredjajaId");

                    b.HasIndex("VrstaUredjajaId");

                    b.ToTable("Uredjaj");
                });

            modelBuilder.Entity("backend.Models.VrstaUredjaja", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("INTEGER");

                    b.Property<string>("Naziv")
                        .HasColumnType("TEXT");

                    b.HasKey("Id");

                    b.ToTable("VrstaUredjaja");
                });

            modelBuilder.Entity("backend.Models.IstorijaP", b =>
                {
                    b.HasOne("backend.Models.ObjekatUredjaj", "ObjekatUredjaj")
                        .WithMany()
                        .HasForeignKey("ObjekatUredjajId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("ObjekatUredjaj");
                });

            modelBuilder.Entity("backend.Models.Korisnik", b =>
                {
                    b.HasOne("backend.Models.Rola", "Rola")
                        .WithMany()
                        .HasForeignKey("RolaId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("backend.Models.Ulica", "Ulica")
                        .WithMany()
                        .HasForeignKey("UlicaId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Rola");

                    b.Navigation("Ulica");
                });

            modelBuilder.Entity("backend.Models.Naselje", b =>
                {
                    b.HasOne("backend.Models.Grad", "Grad")
                        .WithMany()
                        .HasForeignKey("GradId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Grad");
                });

            modelBuilder.Entity("backend.Models.Objekat", b =>
                {
                    b.HasOne("backend.Models.Korisnik", "Korisnik")
                        .WithMany()
                        .HasForeignKey("KorisnikId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("backend.Models.Ulica", "Ulica")
                        .WithMany()
                        .HasForeignKey("UlicaId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Korisnik");

                    b.Navigation("Ulica");
                });

            modelBuilder.Entity("backend.Models.ObjekatSkladiste", b =>
                {
                    b.HasOne("backend.Models.Objekat", "Objekat")
                        .WithMany()
                        .HasForeignKey("ObjekatId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("backend.Models.Skladiste", "Skladiste")
                        .WithMany()
                        .HasForeignKey("SkladisteId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Objekat");

                    b.Navigation("Skladiste");
                });

            modelBuilder.Entity("backend.Models.ObjekatUredjaj", b =>
                {
                    b.HasOne("backend.Models.Objekat", "Objekat")
                        .WithMany()
                        .HasForeignKey("ObjekatId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("backend.Models.Uredjaj", "Uredjaj")
                        .WithMany()
                        .HasForeignKey("UredjajId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Objekat");

                    b.Navigation("Uredjaj");
                });

            modelBuilder.Entity("backend.Models.PPoStanjuUredjaja", b =>
                {
                    b.HasOne("backend.Models.Uredjaj", "Uredjaj")
                        .WithMany()
                        .HasForeignKey("UredjajId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Uredjaj");
                });

            modelBuilder.Entity("backend.Models.PredikcijaP", b =>
                {
                    b.HasOne("backend.Models.ObjekatUredjaj", "ObjekatUredjaj")
                        .WithMany()
                        .HasForeignKey("ObjekatUredjajId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("ObjekatUredjaj");
                });

            modelBuilder.Entity("backend.Models.Ulica", b =>
                {
                    b.HasOne("backend.Models.Naselje", "Naselje")
                        .WithMany()
                        .HasForeignKey("NaseljeId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Naselje");
                });

            modelBuilder.Entity("backend.Models.Uredjaj", b =>
                {
                    b.HasOne("backend.Models.TipUredjaja", "TipUredjaja")
                        .WithMany()
                        .HasForeignKey("TipUredjajaId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("backend.Models.VrstaUredjaja", "VrstaUredjaja")
                        .WithMany()
                        .HasForeignKey("VrstaUredjajaId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("TipUredjaja");

                    b.Navigation("VrstaUredjaja");
                });
#pragma warning restore 612, 618
        }
    }
}
