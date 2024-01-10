package com.example.Backend.controller;

import com.example.Backend.dto.*;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/company")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompanyController {

    private final CompanyService companyService;
    private final FollowingService followingService;
    private final UserService userService;
    private final ProductService productService;


    private final FileSystemUtil fileSystem;



    // OVO JE SAMO ZA TESTIRANJE DA LI MI RADI CUSTOMIZOVANA ANOTACIJA @Validated ispred ovog @RequestBody
    @PostMapping("/companyProba")
    public void getAllCompanies(@Validated @RequestBody CompanyDTO param) {

        companyService.getAllCompanies(null);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addCompany(@RequestBody CompanyDTO param) {
        if(param == null) {
            return ResponseEntity.badRequest().body("No params provided");
        }

        try {
            Long userId = getUserDetails().getId();
            Company dodat = companyService.createCompany(param, userId);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idAddedCompany", dodat.getId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/myCompany")
    public ResponseEntity<?> getMyCompany() {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Company> companies = companyService.getCompanyByUserId(userId, u -> {
                Hibernate.initialize(u.getStreet());
                Hibernate.initialize(u.getStreet().getCity());
            });

            if (companies.size() == 0)
                return new ResponseEntity<>("User with id=" + userId + " does not have any companies yet.", HttpStatus.NOT_FOUND);

            MyCompanyDTO myCompany = new MyCompanyDTO(companies.get(0));

            return new ResponseEntity<>(myCompany, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

//  ################################################# POCETNA STRANA #################################################

    // pocetna strana -> vraca kompanije sa najvecim rejtingom
    @GetMapping("/popular/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getPopularCompanies(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            // ovde uzimamo kompanije svih usera koje nisu moje!, i JOIN-ujemo sa tabelom Product
            List<Company> companies = companyService.getAllCompaniesNot(userId, c -> {
                Hibernate.initialize(c.getUser());
            });

            if (companies.size() == 0)
                return new ResponseEntity<>("There is no available companies.", HttpStatus.NOT_FOUND);

            // za kompanije koje su izabrane, treba uzeti proizvode te kompanije i izracunati prosecnu ocenu :))
            Map<Long, Double> ratesForCompanies = companyService.getRatesForCompanies(companies);

            // kompanije koje pratim (tj. ide preko usera za sad, tj. da vidim dal pravi problem ili ne)
            Map<Long, Boolean> following = followingService.getFavourites(userId, companies);


            List<HomePageCompanyDTO> homePageCompany = companies.stream()
                    .map(company -> new HomePageCompanyDTO(company, ratesForCompanies.getOrDefault(company.getId(), 0.0),
                            following.getOrDefault(company.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(company.getId()), ImageType.LOGO)))
                    .toList();

            // sortiranje
            List<HomePageCompanyDTO> sortedCompanies = homePageCompany.stream()
                    .sorted(Comparator.comparingDouble(HomePageCompanyDTO::getTotalRate).reversed())
                    .collect(Collectors.toList());

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > companies.size()) {
                return new ResponseEntity<>("No companies available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, companies.size()); // (pageIndex + 1) * pageSize
            List<HomePageCompanyDTO> paginatedCompanies = sortedCompanies.subList(start, end);

            return new ResponseEntity<>(paginatedCompanies, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // pocetna -> vraca kompanije od ljudi koje pratim
    @GetMapping("/iFollow/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getCompaniesFromPeopleIFollow(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            // ovde uzmimam ljude koje pratim, i JOIN-ujem sa tabelom User
            List<Following> followings = followingService.getMyFollowings(userId, f -> {
                Hibernate.initialize(f.getUserFollowing());
            });

            // ovde idem kroz followings i vracam samo Usera a ne tabelu Following
            List<User> followingUsers = followings.stream()
                    .map(Following::getUserFollowing)
                    .toList();

            // ovde uzimamo kompanije svih usera, i JOIN-ujemo sa tabelom Product
            List<Company> companies = companyService.getCompaniesByUsers(followingUsers, c -> {
                Hibernate.initialize(c.getProducts());
            });

            if (companies.size() == 0)
                return new ResponseEntity<>("User with id=" + userId + " does not follow anyone.", HttpStatus.NOT_FOUND);

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > companies.size()) {
                return new ResponseEntity<>("No companies available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, companies.size()); // (pageIndex + 1) * pageSize
            List<Company> paginatedCompanies = companies.subList(start, end);


            // za kompanije koje su izabrane, treba uzeti proizvode te kompanije i izracunati prosecnu ocenu :))
            Map<Long, Double> ratesForCompanies = companyService.getRatesForCompanies(paginatedCompanies);

            // kompanije koje pratim (tj. ide preko usera za sad, tj. da vidim dal pravi problem ili ne)
            Map<Long, Boolean> following = followingService.getFavourites(userId, paginatedCompanies);


            List<HomePageCompanyDTO> homePageCompany = paginatedCompanies.stream()
                    .map(company -> new HomePageCompanyDTO(company, ratesForCompanies.getOrDefault(company.getId(), 0.0),
                            following.getOrDefault(company.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(company.getId()), ImageType.LOGO)))
                    .toList();

            return new ResponseEntity<>(homePageCompany, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // pocetna -> vraca kompanije u mojoj blizini -> za sad ide preko grada
    @GetMapping("/inNearby/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getCompaniesInNearby(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            User user = userService.getUserById(userId, u -> {
                Hibernate.initialize(u.getStreet());
                Hibernate.initialize(u.getStreet().getCity());
            });

            if(user.getStreet() == null || user.getStreet().getCity() == null)
                return new ResponseEntity<>("User does not have location.", HttpStatus.NOT_FOUND);

            List<Company> companies = companyService.getAllCompaniesNot(userId, c -> {
                Hibernate.initialize(c.getStreet());
                Hibernate.initialize(c.getStreet().getCity());
            });

            if (companies.size() == 0)
                return new ResponseEntity<>("There are no available companies near you.", HttpStatus.NOT_FOUND);

            // filtriranje kompanija prema gradu korisnika
            List<Company> nearbyCompanies = companies.stream()
                    .filter(company -> company.getStreet() != null &&
                            company.getStreet().getCity() != null &&
                            company.getStreet().getCity().getName().equals(user.getStreet().getCity().getName()))
                    .toList();

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > nearbyCompanies.size()) {
                return new ResponseEntity<>("No companies available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, nearbyCompanies.size()); // (pageIndex + 1) * pageSize
            List<Company> paginatedCompanies = nearbyCompanies.subList(start, end);

            // za kompanije koje su izabrane, treba uzeti proizvode te kompanije i izracunati prosecnu ocenu :))
            Map<Long, Double> ratesForCompanies = companyService.getRatesForCompanies(paginatedCompanies);

            // kompanije koje pratim (tj. ide preko usera za sad, tj. da vidim dal pravi problem ili ne)
            Map<Long, Boolean> following = followingService.getFavourites(userId, paginatedCompanies);


            List<HomePageCompanyDTO> homePageCompany = paginatedCompanies.stream()
                    .map(company -> new HomePageCompanyDTO(company, ratesForCompanies.getOrDefault(company.getId(), 0.0),
                            following.getOrDefault(company.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(company.getId()), ImageType.LOGO)))
                    .toList();

            return new ResponseEntity<>(homePageCompany, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //  ####################################################################################



    //  ######################################## KATEGORIJE ############################################

    // vraca dostupne kategorije proizvoda za tu kompaniju
    @GetMapping("/category/{companyId}")
    public ResponseEntity<?> getCategoriesForCompany(@PathVariable Long companyId) {
        try {
            Company company;
            Long userId;
            if(companyId == 0) {
                userId = getUserDetails().getId();
                List<Company> list = companyService.getCompanyByUserId(userId, c -> {
                    Hibernate.initialize(c.getProducts());
                });
                company = list.get(0);
            }
            else {
                company = companyService.getCompanyById(companyId, c -> {
                    Hibernate.initialize(c.getProducts());
                });

                userId = company.getUser().getId();
            }

            List<Product> products = company.getProducts();

            List<String> distinctCategories = products.stream()
                    .map(product -> product.getProductCategory().getId())
                    .distinct()
                    .toList();

            return new ResponseEntity<>(distinctCategories, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //  ####################################################################################


    // za poslat naziv proizvoda  -> vraca kompanije koje to poseduju -> lista
    @GetMapping("/search/{search}/{pageIndex}/{pageSize}")
    public ResponseEntity<Object> getSearchedCompanies(@PathVariable String search, @PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Product> products;

            if (search.equals("sve")) {
                // Ako pretraga nije uneta, uzmi sve proizvode bez filtera
                products = productService.getAllProducts();
            } else {
                // Ako je unet kriterijum pretrage, koristi ga za pretragu
                products = productService.getSearchedProducts(search, p -> {
                    Hibernate.initialize(p.getCompany());
                    Hibernate.initialize(p.getCompany().getUser());
                });
            }

            List<Company> companies = new java.util.ArrayList<>(products.stream()
                    .map(Product::getCompany)
                    .toList());


            List<Company> companies1;
            if (search.equals("sve")) {
                companies1 = companyService.getSearchedCompanies("");
            } else {
                companies1 = companyService.getSearchedCompanies(search);
            }


            companies.addAll(companies1);

            if (companies.isEmpty()) {
                return new ResponseEntity<>("No companies available.", HttpStatus.BAD_REQUEST);
            }

            int start = pageIndex * pageSize - pageSize;
            if (start >= companies.size()) {
                return new ResponseEntity<>("No companies available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, companies.size());
            List<Company> paginatedCompanies = companies.subList(start, end);



            // za kompanije koje su izabrane, treba uzeti proizvode te kompanije i izracunati prosecnu ocenu :))
            Map<Long, Double> ratesForCompanies = companyService.getRatesForCompanies(paginatedCompanies);

            // kompanije koje pratim (tj. ide preko usera za sad, tj. da vidim dal pravi problem ili ne)
            Map<Long, Boolean> following = followingService.getFavourites(userId, paginatedCompanies);

            List<HomePageCompanyDTO> uniqueHomePageCompany = paginatedCompanies.stream()
                    .map(company -> new HomePageCompanyDTO(company, ratesForCompanies.getOrDefault(company.getId(), 0.0),
                            following.getOrDefault(company.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(company.getId()), ImageType.LOGO)))
                    .distinct()
                    .toList();

            return new ResponseEntity<>(uniqueHomePageCompany, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // za poslat naziv proizvoda  -> vraca kompanije koje to poseduju -> mapa
    @GetMapping("/searchMap/{search}/{pageIndex}/{pageSize}")
    public ResponseEntity<Object> getSearchedCompaniesMap(@PathVariable String search, @PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            List<Product> products = productService.getSearchedProducts(search, p -> {
                Hibernate.initialize(p.getCompany());
            });

            if (search.equals("sve")) {
                // Ako pretraga nije uneta, uzmi sve proizvode bez filtera
                products = productService.getAllProducts();
            } else {
                // Ako je unet kriterijum pretrage, koristi ga za pretragu
                products = productService.getSearchedProducts(search, p -> {
                    Hibernate.initialize(p.getCompany());
                    Hibernate.initialize(p.getCompany().getUser());
                });
            }

            List<Company> companies = new java.util.ArrayList<>(products.stream()
                    .map(Product::getCompany)
                    .toList());

            List<Company> companies1 = companyService.getSearchedCompanies(search);
            companies.addAll(companies1);

            if (companies.isEmpty()) {
                return new ResponseEntity<>("No companies available.", HttpStatus.BAD_REQUEST);
            }

            List<CompanyMapDTO> uniqueHomePageCompany = companies.stream()
                    .map(company -> new CompanyMapDTO(company))
                    .distinct()
                    .toList();

            return new ResponseEntity<>(uniqueHomePageCompany, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}