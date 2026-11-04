package com.bbu.attendance.security;

import com.bbu.attendance.entity.User;
import com.bbu.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        // Convert the user's roles into Spring Security SimpleGrantedAuthority objects
        var authorities = java.util.Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getName())
        );
        // Check if the user's status is ACTIVE
        boolean isActive = "ACTIVE".equalsIgnoreCase(user.getStatus());
        // Return a Spring Security User object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),          // Using email as the principal
                user.getPasswordHash(),   // Matches your passwordHash field
                isActive,                 // Checked against your String status field
                true,                     // account non-expired
                true,                     // credentials non-expired
                true,                     // account non-locked
                authorities
        );
    }
}