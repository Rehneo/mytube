package com.rehneo.mytubeapi.user;

import com.rehneo.mytubeapi.service.BanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BanService banService;

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(
                        "User with username: " + username + " not found"
                )
        );
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public boolean isBanned(User user) {
        return banService.isBanned(user);
    }
    
    public void banByStrikes(User user) {
        banService.banByStrikes(user);
    }

    public User getById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(
                        "User with id: " + id + " not found"
                )
        );
    }
}
