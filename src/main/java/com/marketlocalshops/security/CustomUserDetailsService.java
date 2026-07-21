package com.marketlocalshops.security;

import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));
        
        String roleName = user.getRole() != null ? user.getRole().name() : "CUSTOMER";
        java.util.Set<String> roles = new java.util.HashSet<>();
        
        switch (roleName) {
            case "SUPER_ADMIN":
                roles.add("SUPER_ADMIN");
                roles.add("ADMIN");
                roles.add("SELLER");
                roles.add("CUSTOMER");
                break;
            case "ADMIN":
                roles.add("ADMIN");
                roles.add("SELLER");
                roles.add("CUSTOMER");
                break;
            case "SELLER":
                roles.add("SELLER");
                roles.add("CUSTOMER");
                break;
            default:
                roles.add(roleName);
                roles.add("CUSTOMER");
                break;
        }

        java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
        for (String r : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
            authorities.add(new SimpleGrantedAuthority(r));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
