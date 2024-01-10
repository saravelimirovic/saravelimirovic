package com.example.Backend.controller;

import com.example.Backend.dto.*;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.service.*;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.Backend.utils.ContextUtils.getUserDetails;


@RestController
@RequestMapping("/web/product")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController {

    private final ProductService productService;
    private final FollowingService followingService;
    private final CompanyService companyService;
    private final FavouriteService favouriteService;
    private final UserService userService;
    private final ProductLocationService productLocationService;

    private final FileSystemUtil fileSystem;

    @PostMapping("/add")
    public ResponseEntity<Object> addProduct(@RequestBody ProductDTO param) {
        if(param == null) {
            return ResponseEntity.badRequest().body("No params provided");
        }

        try {
            Long userId = getUserDetails().getId();
            Product dodat = productService.createProduct(param, userId);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idAddedProduct", dodat.getId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // prva zamisao
    //  ################################################# POCETNA STRANA #################################################

    // lista proizvoda od ljudi koje pratim (tvoja pracenja) --- ako ne postoji 4. strana baca bad request (videti sa frontom)
    @GetMapping("/iFollow/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getProductsFromPeopleIFollow(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            // samo primer kako isto ide
//            List<Long> followingUsers = followings.stream()
//                    .map(following -> following.getUserFollowing().getId())
//                    .toList();

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

            // i ovde prolazimo kroz companies i uzimamo samo Product a ne tabelu Company
            List<Product> products = companies.stream()
                    .flatMap(company -> company.getProducts().stream())
                    .toList();

            // ovako bi bacilo gresku (lista u listi)
//            List<Product> products = companies.stream()
//                    .map(company -> company.getProducts())
//                    .toList();

            if (products.size() == 0)
                return new ResponseEntity<>("User with id=" + userId + " does not follow anyone or those people do not have products.", HttpStatus.NOT_FOUND);


            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > products.size()) {
                return new ResponseEntity<>("No product available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, products.size()); // (pageIndex + 1) * pageSize
            List<Product> paginatedProducts = products.subList(start, end);

            Map<Long, Boolean> favourites = favouriteService.getFavourites(userId, paginatedProducts);

            List<HomePageProductDTO> homePageProducts = paginatedProducts.stream()
                    .map(product -> new HomePageProductDTO(product, favourites.getOrDefault(product.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(product.getId()), ImageType.PRODUCT)))
                    .toList();

            return new ResponseEntity<>(homePageProducts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // lista proizvoda koji su mi u blizini --- ako ne postoji 4. strana baca bad request (videti sa frontom)
    @GetMapping("/inNearby/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getProductsInNearby(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            User user = userService.getUserById(userId, u -> {
                Hibernate.initialize(u.getStreet());
            });

            List<ProductLocation> productLocations = productLocationService.getProductLocationsByStreetId(user.getStreet().getId(), pl -> {
                Hibernate.initialize(pl.getProduct());
            });

            List<Product> products = productLocations.stream()
                    .map(ProductLocation::getProduct)
                    .toList();

            if (products.size() == 0)
                return new ResponseEntity<>("There are no available products near you.", HttpStatus.NOT_FOUND);

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > products.size()) {
                return new ResponseEntity<>("No product available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, products.size()); // (pageIndex + 1) * pageSize
            List<Product> paginatedProducts = products.subList(start, end);

            Map<Long, Boolean> favourites = favouriteService.getFavourites(userId, paginatedProducts);

            List<HomePageProductDTO> homePageProducts = paginatedProducts.stream()
                    .map(product -> new HomePageProductDTO(product, favourites.getOrDefault(product.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(product.getId()), ImageType.PRODUCT)))
                    .toList();

            return new ResponseEntity<>(homePageProducts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // daje proizvode po kategoriji, ali od svih proizvoda
    @GetMapping("/category/{category}/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable int pageIndex, @PathVariable int pageSize, @PathVariable String category) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Product> products = productService.getProductsByCategory(category);

            if (products.size() == 0)
                return new ResponseEntity<>("There are no products with category=" + category + "yet.", HttpStatus.NOT_FOUND);

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > products.size()) {
                return new ResponseEntity<>("No product available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, products.size()); // (pageIndex + 1) * pageSize
            List<Product> paginatedProducts = products.subList(start, end);

            Map<Long, Boolean> favourites = favouriteService.getFavourites(userId, paginatedProducts);

            List<HomePageProductDTO> homePageProducts = paginatedProducts.stream()
                    .map(product -> new HomePageProductDTO(product, favourites.getOrDefault(product.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(product.getId()), ImageType.PRODUCT)))
                    .toList();

            return new ResponseEntity<>(homePageProducts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // **************************************************************************************************



    // daje info o jednom proizvodu
    @GetMapping("/productDetails/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Product product = productService.getProductDetails(productId, p -> {
                Hibernate.initialize(p.getCompany());
                Hibernate.initialize(p.getCompany().getUser());
            });

            boolean isFavorite = favouriteService.isFavourite(userId, product.getId(), null);

            ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO(product, isFavorite);
            productDetailsDTO.setImage(fileSystem.getImageInBytes(String.valueOf(product.getId()), ImageType.PRODUCT));

            return new ResponseEntity<>(productDetailsDTO, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // daje proizvode po kategoriji, ali od svih proizvoda
    @GetMapping("/category/{category}/{companyId}/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getProductsByCategoryForCompany(@PathVariable int pageIndex, @PathVariable int pageSize,
                                                             @PathVariable String category, @PathVariable Long companyId) {
        try {
            Company company = companyService.getCompanyById(companyId, c -> {
                Hibernate.initialize(c.getProducts());
            });

            Long userId = company.getUser().getId();

            List<Product> products = company.getProducts();

            ProductCategory paramProductCategory = ProductCategory.getById(category);

            // filtriranje kompanija prema gradu korisnika
            List<Product> filteredProducts = products.stream()
                    .filter(product -> product.getProductCategory().equals(paramProductCategory))
                    .toList();

            if (products.size() == 0)
                return new ResponseEntity<>("There are no products with category=" + category + "yet.", HttpStatus.NOT_FOUND);

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > filteredProducts.size()) {
                return new ResponseEntity<>("No product available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, filteredProducts.size()); // (pageIndex + 1) * pageSize
            List<Product> paginatedProducts = filteredProducts.subList(start, end);

            Map<Long, Boolean> favourites = favouriteService.getFavourites(userId, paginatedProducts);

            List<HomePageProductDTO> homePageProducts = paginatedProducts.stream()
                    .map(product -> new HomePageProductDTO(product, favourites.getOrDefault(product.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(product.getId()), ImageType.PRODUCT)))
                    .toList();

            return new ResponseEntity<>(homePageProducts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // vraca moje proizvode -- OVO NIJE U UPOTREBI VISE
    @GetMapping("/myProducts/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getMyProducts(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Company> companies = companyService.getCompanyByUserId(userId, c -> {
                Hibernate.initialize(c.getProducts());
            });

            List<Product> products = companies.stream()
                    .flatMap(company -> company.getProducts().stream())
                    .toList();

            if (products.size() == 0)
                return new ResponseEntity<>("User with id=" + userId + " does not have any products.", HttpStatus.NOT_FOUND);

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > products.size()) {
                return new ResponseEntity<>("No product available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, products.size()); // (pageIndex + 1) * pageSize
            List<Product> paginatedProducts = products.subList(start, end);

            Map<Long, Boolean> favourites = favouriteService.getFavourites(userId, paginatedProducts);

            List<HomePageProductDTO> homePageProducts = paginatedProducts.stream()
                    .map(product -> new HomePageProductDTO(product, favourites.getOrDefault(product.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(product.getId()), ImageType.PRODUCT)))
                    .toList();

            return new ResponseEntity<>(homePageProducts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // vraca proizvode jedne kompanije
    @GetMapping("/productList/{companyId}/{category}/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getProductsOfCompany(
            @PathVariable Long companyId,
            @PathVariable String category,
            @PathVariable int pageIndex,
            @PathVariable int pageSize) {
        try {
            Company company;
            Long userId;
            if (companyId == 0) {
                userId = getUserDetails().getId();
                List<Company> list = companyService.getCompanyByUserId(userId, c -> {
                    Hibernate.initialize(c.getProducts());
                });
                company = list.get(0);
            } else {
                company = companyService.getCompanyById(companyId, c -> {
                    Hibernate.initialize(c.getProducts());
                });

                userId = company.getUser().getId();
            }

            List<Product> filteredProducts = company.getProducts();
            System.out.println(filteredProducts.get(0).getProductCategory().getId());
            if(!"SVE".equalsIgnoreCase(category)) {
                filteredProducts = filteredProducts.stream()
                        .filter(product -> product.getProductCategory().getId().equalsIgnoreCase(category))
                        .collect(Collectors.toList());
            }


            if (filteredProducts.size() == 0) {
                return new ResponseEntity<>("No products available in the specified category.", HttpStatus.NOT_FOUND);
            }

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if (start > filteredProducts.size()) {
                return new ResponseEntity<>("No product available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, filteredProducts.size());
            List<Product> paginatedProducts = filteredProducts.subList(start, end);

            Map<Long, Boolean> favourites = favouriteService.getFavourites(userId, paginatedProducts);

            List<HomePageProductDTO> homePageProducts = paginatedProducts.stream()
                    .map(product -> new HomePageProductDTO(product, favourites.getOrDefault(product.getId(), false),
                            fileSystem.getImageInBytes(String.valueOf(product.getId()), ImageType.PRODUCT)))
                    .toList();

            return new ResponseEntity<>(homePageProducts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId) {
        try {
            Long result = productService.deleteProductById(productId);

            return ResponseEntity.ok(
                    Map.of("Message", result)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductUpdateDTO request) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

//            User user = userService.updateUser(userId, request);
            Product product = productService.updateProduct(request);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idUpdatedProduct", product.getId())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}