package com.crowdfund.userService.service;

import com.crowdfund.userService.entity.User;
import com.crowdfund.userService.model.request.LoginRequest;
import com.crowdfund.userService.model.request.UpdateUserRequest;
import com.crowdfund.userService.model.response.AuthResponse;
import com.crowdfund.userService.model.request.UserRequest;
import com.crowdfund.userService.model.response.UserProfileResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import com.crowdfund.userService.repository.UserRepository;
import com.crowdfund.userService.security.JwtUtil;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(UserRequest request) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        if (userRepository.existsByUsername(request.getUsername())){
            throw new IllegalArgumentException("This user already exist");
        }

        String ethAddress;
        String privateKeyHex;

        if (request.getEthereumAddress() != null && !request.getEthereumAddress().isBlank()) {
            ethAddress = request.getEthereumAddress();
            privateKeyHex = null;
        } else {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            ethAddress = "0x" + Keys.getAddress(keyPair.getPublicKey());
            privateKeyHex = keyPair.getPrivateKey().toString(16);
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .ethereumAddress(ethAddress)
                .privateKey(privateKeyHex)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(request.getUsername());

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NoSuchElementException("This user is not exist"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(request.getUsername());

        return new AuthResponse(token);
    }

    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .ethereumAddress(user.getEthereumAddress())
                .build();
    }

    public void updateProfile(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getName() != null) {
            user.setUsername(request.getName());
        }
        if (request.getEthAddress() != null && !request.getEthAddress().isBlank()) {
            user.setEthereumAddress(request.getEthAddress());
        }

        userRepository.save(user);
    }

}
