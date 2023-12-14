using backend.Models;
using Microsoft.IdentityModel.Protocols.OpenIdConnect;

namespace backend.DTOs
{
    public class ObjectDeviceDTO
    {
        private double pPrilikomMirovanja;
        private string nazivUredjaja;
        private int tipUredjajaId;
        private int vrstaUredjajaId;
        private int objekatId;
        private int prostorijaId;
        private string dozvola;
        private string kontrola;
        private string ukljucen;
        private double prosecnaPotrosnja;

        public ObjectDeviceDTO(int objekatId, int prostorijaId, string nazivUredjaja, int vrstaUredjajaId, int tipUredjajaId, double pPrilikomMirovanja, double prosecnaPotrosnja, string dozvola, string kontrola, string ukljucen)
        {
            this.objekatId = objekatId;
            this.prostorijaId = prostorijaId;
            this.nazivUredjaja = nazivUredjaja;
            this.vrstaUredjajaId = vrstaUredjajaId;
            this.tipUredjajaId = tipUredjajaId;
            this.pPrilikomMirovanja = pPrilikomMirovanja;
            this.prosecnaPotrosnja = prosecnaPotrosnja;
            this.ukljucen = ukljucen;
            this.dozvola = dozvola;
            this.kontrola = kontrola;
        }

        public int ObjekatId { get => objekatId; set => objekatId = value; }
        public int ProstorijaId { get => prostorijaId; set => prostorijaId = value; }
        public string NazivUredjaja { get => nazivUredjaja; set => nazivUredjaja = value; }
        public int VrstaUredjajaId { get => vrstaUredjajaId; set => vrstaUredjajaId = value; }
        public int TipUredjajaId { get => tipUredjajaId; set => tipUredjajaId = value; }
        public double PPrilikomMirovanja { get => pPrilikomMirovanja; set => pPrilikomMirovanja = value; }
        public double ProsecnaPotrosnja { get => prosecnaPotrosnja; set => prosecnaPotrosnja = value; }
        public string Ukljucen { get => ukljucen; set => ukljucen = value; }
        public string Dozvola { get => dozvola; set => dozvola = value; }
        public string Kontrola { get => kontrola; set => kontrola = value; }
    }
}
