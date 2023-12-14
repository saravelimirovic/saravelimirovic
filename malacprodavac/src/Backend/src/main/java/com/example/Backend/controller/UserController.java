package com.example.Backend.controller;

import com.example.Backend.dto.*;
import com.example.Backend.dto.NumInfoDTO;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.service.*;
import com.example.Backend.service.FollowingService;
import com.example.Backend.service.MapService;
import com.example.Backend.service.UserService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/user")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService userService;
    private final FollowingService followingService;
    private final CompanyService companyService;
    private final OrderService orderService;
    private final MapService mapService;

    private final FileSystemUtil fileSystem;

    // daje sve korisnike
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // vraca info za profil korisnika
    @GetMapping("/profile")
    public ResponseEntity<?> getUsersDetails() {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena
//            List<String> userRoles = getUserDetails().getRoles(); // kupljenje iz tokena

            User user = userService.getUserById(userId, u -> {
                Hibernate.initialize(u.getStreet());
                Hibernate.initialize(u.getStreet().getCity());
            });
//            Integer followings = followingService.getMyFollowings(userId, null).size();

            UsersProfileDTO usersProfileDTO = new UsersProfileDTO(user);
            usersProfileDTO.setImage(fileSystem.getImageInBytes(String.valueOf(userId), ImageType.USER));

            return new ResponseEntity<>(usersProfileDTO, HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/map")
    public ResponseEntity<?> getCustomersLocation() {
        List<CustomerMapDTO> map = userService.getCustomersLocation();
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    // vraca broj mojih proizvoda, prodaja i pratilaca
    @GetMapping("/numberInfo/{companyId}")
    public ResponseEntity<?> getNumberInformation(@PathVariable Long companyId) {
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

            int numOfProducts = company.getProducts().size();

            List<Order> sales = orderService.getMySales(userId);
            int numOfSales;
            if (sales == null)
                numOfSales = 0;
            else
                numOfSales = sales.size();

            List<MyFollowingDTO> followers = followingService.getMyFollowers(userId);
            int numOfFollowers;
            if (followers == null)
                numOfFollowers = 0;
            else
                numOfFollowers = followers.size();


            NumInfoDTO numInfo = new NumInfoDTO(company, numOfProducts, numOfSales, numOfFollowers,
                    fileSystem.getImageInBytes(String.valueOf(company.getId()), ImageType.LOGO));

            return new ResponseEntity<>(numInfo, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/route")
    public ResponseEntity<?> getRoute(@RequestBody RouteMapRequestDTO request) {
        try {
            RouteMapResponseDTO routeMapResponseDTO = mapService.getRouteCoordinates(request.getCityStart(), request.getPostalCodeStart(), request.getCityEnd(), request.getPostalCodeEnd());
            return new ResponseEntity<>(routeMapResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UsersProfileDTO request) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            User user = userService.updateUser(userId, request);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idUpdatedUser", user.getId())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<?> updateUserInfo(@RequestBody MyInfoUpdateDTO request) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            User user = userService.updateMyInfoUser(userId, request);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idUpdatedUser", user.getId())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getUpdateInfo")
    public ResponseEntity<?> getUserInfo() {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            MyInfoUpdateDTO user = userService.getMyInfo(userId);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/updateLocation")
    public ResponseEntity<?> updateLocation(@RequestBody MyLocationUpdateDTO request) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            User user = userService.updateMyLocation(userId, request);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idUpdatedUser", user.getId())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/updatePic")
    public ResponseEntity<?> updateProfilePicture(@RequestBody UpdateProfilePicDTO request) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            boolean isChanged = userService.updateProfilePic(userId, request);

            return new ResponseEntity<>(isChanged, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getUpdateLocation")
    public ResponseEntity<?> getUserLocation() {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            MyLocationUpdateDTO locationUpdateDTO = userService.getMyLocation(userId);

            return new ResponseEntity<>(locationUpdateDTO, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            User user = userService.changePassword(userId, changePasswordDTO);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idUpdatedUser", user.getId())
            );
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUserById() {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Long result = userService.deleteUserById(userId);

            return ResponseEntity.ok(
                    Map.of("Message", result)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
