using backend.DTOs;
using backend.Models;
using backend.repositroy;
using backend.repositroy.interfaces;

namespace backend.services.interfaces
{
    public class ObjectService : IObjectService
    {

        private readonly IObjectRepository _objectRepository;

        private readonly IUlicaRepository _ulicaRepository;
        private readonly INaseljeRepository _naseljeRepository;
        private readonly IGradRepository _gradRepository;

        public ObjectService(IObjectRepository objectRepository, IUlicaRepository ulicaRepository, IGradRepository gradRepository, INaseljeRepository naseljeRepository)
        {
            _objectRepository = objectRepository;
            _ulicaRepository = ulicaRepository;
            _gradRepository = gradRepository;
            _naseljeRepository = naseljeRepository;
        }

        public async Task<List<ObjectDTO>> getObjectByUserId(int id)
        {
            List<Objekat> allObject = await _objectRepository.GetObjectByUserIdAsync(id);
            List<ObjectDTO> lista = new List<ObjectDTO>();
            foreach (var obj in allObject)
            {
                Ulica ulica = await _ulicaRepository.getUlicaById(obj.UlicaId);
                lista.Add(new ObjectDTO(obj.Id, ulica.Naziv, obj.AdresniBroj, obj.Ulica.Naselje.Naziv, obj.Ulica.Naselje.Grad.Naziv, obj.Naziv));
            }

            return lista;
        }

        // dodavanje novog objekta
        public async Task addNewObject(ObjectAddDTO objObj)
        {
            //if ((await _userRepository.getUserByEmail(userObj.Email)) != null)
            //{
            //    throw new Exception("Already exists user with that email.");
            //}

            Grad grad = new Grad();
            grad.Naziv = objObj.Grad;
            await _gradRepository.Insert(grad);

            Naselje naselje = new Naselje();
            naselje.Naziv = objObj.Naselje;
            naselje.GradId = (await _gradRepository.getGradByNaziv(grad.Naziv)).Id;
            //naselje.Grad = grad; // ?
            await _naseljeRepository.Insert(naselje);

            Ulica ulica = new Ulica();
            ulica.Naziv = objObj.Ulica;
            ulica.NaseljeId = (await _naseljeRepository.getNaseljeByNaziv(naselje.Naziv, naselje.GradId)).Id;
            //ulica.Naselje = naselje; // ?
            await _ulicaRepository.Insert(ulica);

            Objekat obj = new Objekat();
            obj.KorisnikId = objObj.KorisnikId;
            obj.UlicaId = (await _ulicaRepository.getUlicaByNaziv(ulica.Naziv, ulica.NaseljeId)).Id;
            if (objObj.Broj == null)
            {
                obj.AdresniBroj = "BB";
            }
            else
            {
                obj.AdresniBroj = objObj.Broj;
            }

            obj.Naziv = objObj.Naziv;

            await _objectRepository.Insert(obj);
        }

        public async Task deleteObjectById(int objectId)
        {
            await _objectRepository.deleteObjectByIdAsync(objectId);
        }
    }
}
