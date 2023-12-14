using backend.DTOs;
using backend.Models;
using backend.repositroy.interfaces;
using backend.services.interfaces;

namespace backend.services
{
    public class MapService : IMapService
    {

        private readonly IUserRepository _userRepository;
        private readonly IUlicaRepository _ulicaRepository;
        private readonly IGradRepository _gradRepository;
        private readonly IRolaRepository _rolaRepository;
        private readonly INaseljeRepository _naseljeRepository;
        private readonly IObjectRepository _objectRepository;

        public MapService(IUserRepository userRepository, IUlicaRepository ulicaRepository, IGradRepository gradRepository, IRolaRepository rolaRepository, INaseljeRepository naseljeRepository, IObjectRepository objectRepository)
        {
            _userRepository = userRepository;
            _ulicaRepository = ulicaRepository;
            _gradRepository = gradRepository;
            _rolaRepository = rolaRepository;
            _naseljeRepository = naseljeRepository;
            _objectRepository = objectRepository;
        }

        public async Task<List<MapDTO>> getUsersForMaps()
        {
            List<Korisnik> users = await _userRepository.getAllUsers();
            List<MapDTO> mapInfo = new List<MapDTO>();
            foreach (var user in users)
            {
                List<Objekat> objekti = await _objectRepository.GetObjectByUserIdAsync(user.Id);
                foreach (var objekat in objekti)
                {
                    Ulica ulica = await _ulicaRepository.getUlicaById(objekat.UlicaId);
                    string adresniBroj= objekat.AdresniBroj;
                    Naselje naselje = await _naseljeRepository.getNaseljeById(ulica.NaseljeId);
                    Grad grad = await _gradRepository.getGradById(naselje.GradId);
                    Rola rola = await _rolaRepository.getRolaById(user.RolaId);
                    mapInfo.Add(new MapDTO(user.Id, ulica.Naziv, adresniBroj, grad.Naziv, rola.Naziv));
                }
            }

            return mapInfo;

        }

        public async Task<List<MapDTO>> getObjectForMapsForUserId(int userId)
        {
            List<MapDTO> mapInfo = new List<MapDTO>();
            List<Objekat> objekti = await _objectRepository.GetObjectByUserIdAsync(userId);
            Korisnik user = await _userRepository.getUserById(userId);
            Rola rola = await _rolaRepository.getRolaById(user.RolaId);

            foreach (var objekat in objekti)
            {
                Ulica ulica = await _ulicaRepository.getUlicaById(objekat.UlicaId);
                string adresniBroj = objekat.AdresniBroj;
                Naselje naselje = await _naseljeRepository.getNaseljeById(ulica.NaseljeId);
                Grad grad = await _gradRepository.getGradById(naselje.GradId);
                mapInfo.Add(new MapDTO(userId, ulica.Naziv, adresniBroj, grad.Naziv, rola.Naziv));
            }
            

            return mapInfo;

        }
    }
}
