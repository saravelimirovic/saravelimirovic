using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace backend.Migrations
{
    /// <inheritdoc />
    public partial class init1 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Grad",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Grad", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Prostorija",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Prostorija", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Rola",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Rola", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Skladiste",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true),
                    MaxSkladista = table.Column<double>(type: "REAL", nullable: false),
                    PotrosnjaZaCuvanjePoSatu = table.Column<double>(type: "REAL", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Skladiste", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "TipUredjaja",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TipUredjaja", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "VrstaUredjaja",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_VrstaUredjaja", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Naselje",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    GradId = table.Column<int>(type: "INTEGER", nullable: false),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Naselje", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Naselje_Grad_GradId",
                        column: x => x.GradId,
                        principalTable: "Grad",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Uredjaj",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true),
                    PPrilikomMirovanja = table.Column<double>(type: "REAL", nullable: false),
                    TipUredjajaId = table.Column<int>(type: "INTEGER", nullable: false),
                    VrstaUredjajaId = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Uredjaj", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Uredjaj_TipUredjaja_TipUredjajaId",
                        column: x => x.TipUredjajaId,
                        principalTable: "TipUredjaja",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Uredjaj_VrstaUredjaja_VrstaUredjajaId",
                        column: x => x.VrstaUredjajaId,
                        principalTable: "VrstaUredjaja",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Ulica",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    NaseljeId = table.Column<int>(type: "INTEGER", nullable: false),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Ulica", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Ulica_Naselje_NaseljeId",
                        column: x => x.NaseljeId,
                        principalTable: "Naselje",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "PPoStanjuUredjaja",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    UredjajId = table.Column<int>(type: "INTEGER", nullable: false),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true),
                    PPoSatuStanja = table.Column<double>(type: "REAL", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PPoStanjuUredjaja", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PPoStanjuUredjaja_Uredjaj_UredjajId",
                        column: x => x.UredjajId,
                        principalTable: "Uredjaj",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Korisnik",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Email = table.Column<string>(type: "TEXT", nullable: true),
                    Sifra = table.Column<string>(type: "TEXT", nullable: true),
                    RolaId = table.Column<int>(type: "INTEGER", nullable: false),
                    Ime = table.Column<string>(type: "TEXT", nullable: true),
                    Prezime = table.Column<string>(type: "TEXT", nullable: true),
                    JMBG = table.Column<string>(type: "TEXT", nullable: true),
                    BrTelefona = table.Column<string>(type: "TEXT", nullable: true),
                    UlicaId = table.Column<int>(type: "INTEGER", nullable: false),
                    AdresniBroj = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Korisnik", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Korisnik_Rola_RolaId",
                        column: x => x.RolaId,
                        principalTable: "Rola",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Korisnik_Ulica_UlicaId",
                        column: x => x.UlicaId,
                        principalTable: "Ulica",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Objekat",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    UlicaId = table.Column<int>(type: "INTEGER", nullable: false),
                    KorisnikId = table.Column<int>(type: "INTEGER", nullable: false),
                    AdresniBroj = table.Column<string>(type: "TEXT", nullable: true),
                    Naziv = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Objekat", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Objekat_Korisnik_KorisnikId",
                        column: x => x.KorisnikId,
                        principalTable: "Korisnik",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Objekat_Ulica_UlicaId",
                        column: x => x.UlicaId,
                        principalTable: "Ulica",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "ObjekatSkladiste",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    SkladisteId = table.Column<int>(type: "INTEGER", nullable: false),
                    ObjekatId = table.Column<int>(type: "INTEGER", nullable: false),
                    TrenutnoStanje = table.Column<double>(type: "REAL", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ObjekatSkladiste", x => x.Id);
                    table.ForeignKey(
                        name: "FK_ObjekatSkladiste_Objekat_ObjekatId",
                        column: x => x.ObjekatId,
                        principalTable: "Objekat",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ObjekatSkladiste_Skladiste_SkladisteId",
                        column: x => x.SkladisteId,
                        principalTable: "Skladiste",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "ObjekatUredjaj",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    ProstorijaId = table.Column<int>(type: "INTEGER", nullable: false),
                    UredjajId = table.Column<int>(type: "INTEGER", nullable: false),
                    ObjekatId = table.Column<int>(type: "INTEGER", nullable: false),
                    Dozvola = table.Column<string>(type: "TEXT", nullable: true),
                    Kontrola = table.Column<string>(type: "TEXT", nullable: true),
                    Ukljucen = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ObjekatUredjaj", x => x.Id);
                    table.ForeignKey(
                       name: "FK_ObjekatUredjaj_Prostorija_ProstorijaId",
                       column: x => x.ProstorijaId,
                       principalTable: "Prostorija",
                       principalColumn: "Id",
                       onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ObjekatUredjaj_Objekat_ObjekatId",
                        column: x => x.ObjekatId,
                        principalTable: "Objekat",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ObjekatUredjaj_Uredjaj_UredjajId",
                        column: x => x.UredjajId,
                        principalTable: "Uredjaj",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "IstorijaP",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    ObjekatUredjajId = table.Column<int>(type: "INTEGER", nullable: false),
                    VrednostRealizacije = table.Column<double>(type: "REAL", nullable: false),
                    Datum = table.Column<DateOnly>(type: "TEXT", nullable: false),
                    Vreme = table.Column<TimeOnly>(type: "TEXT", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_IstorijaP", x => x.Id);
                    table.ForeignKey(
                        name: "FK_IstorijaP_ObjekatUredjaj_ObjekatUredjajId",
                        column: x => x.ObjekatUredjajId,
                        principalTable: "ObjekatUredjaj",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "PredikcijaP",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    ObjekatUredjajId = table.Column<int>(type: "INTEGER", nullable: false),
                    VrednostPredikcije = table.Column<double>(type: "REAL", nullable: false),
                    Datum = table.Column<DateOnly>(type: "TEXT", nullable: false),
                    Vreme = table.Column<TimeOnly>(type: "TEXT", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PredikcijaP", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PredikcijaP_ObjekatUredjaj_ObjekatUredjajId",
                        column: x => x.ObjekatUredjajId,
                        principalTable: "ObjekatUredjaj",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_IstorijaP_ObjekatUredjajId",
                table: "IstorijaP",
                column: "ObjekatUredjajId");

            migrationBuilder.CreateIndex(
                name: "IX_Korisnik_RolaId",
                table: "Korisnik",
                column: "RolaId");

            migrationBuilder.CreateIndex(
                name: "IX_Korisnik_UlicaId",
                table: "Korisnik",
                column: "UlicaId");

            migrationBuilder.CreateIndex(
                name: "IX_Naselje_GradId",
                table: "Naselje",
                column: "GradId");

            migrationBuilder.CreateIndex(
                name: "IX_Objekat_KorisnikId",
                table: "Objekat",
                column: "KorisnikId");

            migrationBuilder.CreateIndex(
                name: "IX_Objekat_UlicaId",
                table: "Objekat",
                column: "UlicaId");

            migrationBuilder.CreateIndex(
                name: "IX_ObjekatSkladiste_ObjekatId",
                table: "ObjekatSkladiste",
                column: "ObjekatId");

            migrationBuilder.CreateIndex(
                name: "IX_ObjekatSkladiste_SkladisteId",
                table: "ObjekatSkladiste",
                column: "SkladisteId");

            migrationBuilder.CreateIndex(
                name: "IX_ObjekatUredjaj_ObjekatId",
                table: "ObjekatUredjaj",
                column: "ObjekatId");

            migrationBuilder.CreateIndex(
                name: "IX_ObjekatUredjaj_UredjajId",
                table: "ObjekatUredjaj",
                column: "UredjajId");

            migrationBuilder.CreateIndex(
                name: "IX_PPoStanjuUredjaja_UredjajId",
                table: "PPoStanjuUredjaja",
                column: "UredjajId");

            migrationBuilder.CreateIndex(
                name: "IX_PredikcijaP_ObjekatUredjajId",
                table: "PredikcijaP",
                column: "ObjekatUredjajId");

            migrationBuilder.CreateIndex(
                name: "IX_Ulica_NaseljeId",
                table: "Ulica",
                column: "NaseljeId");

            migrationBuilder.CreateIndex(
                name: "IX_Uredjaj_TipUredjajaId",
                table: "Uredjaj",
                column: "TipUredjajaId");

            migrationBuilder.CreateIndex(
                name: "IX_Uredjaj_VrstaUredjajaId",
                table: "Uredjaj",
                column: "VrstaUredjajaId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "IstorijaP");

            migrationBuilder.DropTable(
                name: "ObjekatSkladiste");

            migrationBuilder.DropTable(
                name: "PPoStanjuUredjaja");

            migrationBuilder.DropTable(
                name: "PredikcijaP");

            migrationBuilder.DropTable(
                name: "Skladiste");

            migrationBuilder.DropTable(
                name: "ObjekatUredjaj");

            migrationBuilder.DropTable(
                name: "Objekat");

            migrationBuilder.DropTable(
                name: "Uredjaj");

            migrationBuilder.DropTable(
                name: "Korisnik");

            migrationBuilder.DropTable(
                name: "TipUredjaja");

            migrationBuilder.DropTable(
                name: "VrstaUredjaja");

            migrationBuilder.DropTable(
                name: "Rola");

            migrationBuilder.DropTable(
                name: "Ulica");

            migrationBuilder.DropTable(
                name: "Naselje");

            migrationBuilder.DropTable(
                name: "Grad");
        }
    }
}
