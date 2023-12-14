package com.example.Backend.service;

import com.example.Backend.entity.Following;
import com.example.Backend.entity.Like;
import com.example.Backend.entity.Post;
import com.example.Backend.entity.User;
import com.example.Backend.repository.LikeRepository;
import com.example.Backend.repository.PostRepository;
import com.example.Backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // lajkuj post (cuva lajk)
    @Transactional
    public Like saveLike(Long userId, Long postId) {
        User user = userRepository.findUserById(userId).orElse(null);
        Post post = postRepository.findPostById(postId).orElse(null);

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        if(user != null && post != null) {
            return likeRepository.save(like);
        }

        return null;
    }

    // skini lajk (brise lajk)
    @Transactional
    public Long deleteLike(Long userId, Long postId) {
        User user = userRepository.findUserById(userId).orElse(null);
        Post post = postRepository.findPostById(postId).orElse(null);

        if(user != null && post != null) {
            return likeRepository.deleteByUserIdAndPostId(userId, postId);
        }

        return (long)-1;
    }
}
