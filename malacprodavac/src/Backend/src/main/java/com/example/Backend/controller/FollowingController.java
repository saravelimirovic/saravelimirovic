package com.example.Backend.controller;

import com.example.Backend.dto.HomePageProductDTO;
import com.example.Backend.dto.MyFollowingDTO;
import com.example.Backend.dto.ProductDTO;
import com.example.Backend.entity.Company;
import com.example.Backend.entity.Following;
import com.example.Backend.entity.Product;
import com.example.Backend.service.CompanyService;
import com.example.Backend.service.FollowingService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.Backend.utils.ContextUtils.getUserDetails;


// OVAJ KONTROLER VRV NIJE POTREBAN (ZA SAD SAMO ZA TESTIRANJE)
@RestController
@RequestMapping("/web/following")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FollowingController {

    private final FollowingService followingService;
    private final CompanyService companyService;

    // zaprati (sacuva pracenje) - userId osobe koju sam zapratio
    @GetMapping("/addFollow/{companyId}")
    public ResponseEntity<?> saveFollow(@PathVariable Long companyId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Company company = companyService.getCompanyById(companyId, c -> {
                Hibernate.initialize(c.getProducts());
            });

            Long userToFollow = company.getUser().getId();

            Following response = followingService.saveFollow(userId, userToFollow);

            if(response != null)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("One of the users do not exist.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // otprati (brise pracenje)
    @DeleteMapping("deleteFollow/{companyId}")
    public ResponseEntity<?> deleteFollow(@PathVariable Long companyId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Company company = companyService.getCompanyById(companyId, c -> {
                Hibernate.initialize(c.getProducts());
            });

            Long userToFollow = company.getUser().getId();

            Long response = followingService.deleteFollow(userId, userToFollow);

            if(response != -1)
                return ResponseEntity.ok(
                        Map.of("Message", response)
                );
            else
                return new ResponseEntity<>("User you want to unfollow probably do not exists or you do not follow them.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // vraca korisnike koji mene prate
    @GetMapping("/myFollowers")
    public ResponseEntity<?> getMyFollowers() {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<MyFollowingDTO> response = followingService.getMyFollowers(userId);

            if (response.size() == 0)
                return new ResponseEntity<>("Nobody follows you yet.", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/doIfollow/{companyId}")
    public ResponseEntity<?> doIfollow(@PathVariable Long companyId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Company company = companyService.getCompanyById(companyId, c -> {
                Hibernate.initialize(c.getProducts());
            });

            Long userToFollow = company.getUser().getId();

            boolean response = followingService.doIFollow(userId, userToFollow);

            return ResponseEntity.ok(
                    Map.of("Message", response)
            );
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // vraca korisnike koje ja pratim
//    @GetMapping("/myFollowings")
//    public ResponseEntity<?> getMyFollowings() {
//        try {
//            Long userId = getUserDetails().getId(); // kupljenje iz tokena
//
//            List<MyFollowingDTO> response = followingService.getMyFollowings(userId);
//
//            if (response.size() == 0)
//                return new ResponseEntity<>("You do not follow anyone yet.", HttpStatus.NOT_FOUND);
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception ex) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
}
