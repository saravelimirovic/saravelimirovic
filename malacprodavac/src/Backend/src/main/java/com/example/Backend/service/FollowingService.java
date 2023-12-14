package com.example.Backend.service;

import com.example.Backend.dto.MyFollowingDTO;
import com.example.Backend.dto.ProductDTO;
import com.example.Backend.entity.*;
import com.example.Backend.repository.FollowingRepository;
import com.example.Backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FollowingService {

    private final FollowingRepository followingRepository;
    private final UserRepository userRepository;

    // vraca korisnike koje ja pratim // novo
    @Transactional(readOnly = true)
    public List<Following> getMyFollowings(Long userId, Consumer<Following> init) {
        List<Following> followings = followingRepository.findByUserId(userId);

        if (init != null) {
            followings.forEach(init);
        }

        return followings;
    }

    // vraca korisnike koje ja pratim // novo
    @Transactional(readOnly = true)
    public boolean doIFollow(Long userId, Long userIfFollow) {
        Following following = followingRepository.findByUserIdAndUserFollowingId(userId, userIfFollow);

        if (following != null)
            return true;
        return false;
    }

    // vraca mapirano (idUsera, dalipratim)
    @Transactional(readOnly = true)
    public Map<Long, Boolean> getFavourites(Long userId, List<Company> companies) {
        // vraca sve koje pratim
        List<Following> followings = followingRepository.findByUserId(userId);

        // uzimam id-jeve ljudi koje pratim
        List<Long> usersIFollow = followings.stream()
                .map(following -> following.getUserFollowing().getId())
                .toList();

        Map<Long, Boolean> followingMap = new HashMap<>();

        System.out.println(companies.get(0));
        System.out.println(companies.get(0).getUser());

        for (Company company : companies) {
            boolean isFollowed = usersIFollow.contains(company.getUser().getId());
            followingMap.put(company.getId(), isFollowed);
        }

        return followingMap;
    }

    // vraca korisnike koji mene prate
    @Transactional(readOnly = true)
    public List<MyFollowingDTO> getMyFollowers(Long userId) {
        List<Following> followings = followingRepository.findByUserId(userId);

        // niko me ne prati
        if(followings == null) {
            return null;
        }

        List<MyFollowingDTO> followingUsers = followings.stream()
                .map(MyFollowingDTO::new) // novo
                .collect(Collectors.toList());

        return followingUsers;
    }

    // zaprati osobu (cuva pracenje)
    @Transactional
    public Following saveFollow(Long userId, Long userId1) {
        User user = userRepository.findUserById(userId).orElse(null);
        User user1 = userRepository.findUserById(userId1).orElse(null);

        Following following = new Following();
        following.setUser(user);
        following.setUserFollowing(user1);

        if(user != null && user1 != null) {
            return followingRepository.save(following);
        }

        return null;
    }

    // otprati osobu (brise pracenje)
    @Transactional
    public Long deleteFollow(Long userId, Long userId1) {
        User user = userRepository.findUserById(userId).orElse(null);
        User user1 = userRepository.findUserById(userId1).orElse(null);

        if(user != null && user1 != null) {
            return followingRepository.deleteByUserIdAndAndUserFollowingId(userId, userId1);

        }

        return (long)-1;
    }
}
