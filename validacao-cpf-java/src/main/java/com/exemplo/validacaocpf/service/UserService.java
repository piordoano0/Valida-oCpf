package com.exemplo.validacaocpf.service;

import com.exemplo.validacaocpf.model.User;
import com.exemplo.validacaocpf.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Você pode usar orElse(null) para retornar null caso não encontre
        return optionalUser.orElse(null);
    }

}
