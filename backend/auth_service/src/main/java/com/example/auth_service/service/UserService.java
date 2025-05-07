package com.example.auth_service.service;

import com.example.auth_service.constant.Role;
import com.example.auth_service.model.Credential;
import com.example.auth_service.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Credential credential = credentialRepository.findCredentialByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credential not exists"));

        log.info("Credential found: {}", credential);

        List<Role> roles = credential.getRoles();

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return User.builder()
                .username(credential.getEmail())
                .password(credential.getPassword())
                .authorities(authorities)
                .build();
    }

}
