package io.getarrays.userservice.service;

import io.getarrays.userservice.domain.RoleData;
import io.getarrays.userservice.domain.UserData;
import io.getarrays.userservice.repo.RoleRepo;
import io.getarrays.userservice.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 05/01/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserData saveUser(UserData userData) {
        log.info("Saving new user {} to the database", userData.getName());
        userData.setPassword(passwordEncoder.encode(userData.getPassword()));
        return userRepo.save(userData);
    }

    @Override
    public RoleData saveRole(RoleData roleData) {
        log.info("Saving new role {} to the database", roleData.getName());
        return roleRepo.save(roleData);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {} to the database", username, roleName);
        if(!userRepo.isRoleMapped(username, roleName)) {
            UserData userData = userRepo.findByUsername(username);
            RoleData roleData = roleRepo.findByName(roleName);
            userData.getRoleData().add(roleData);
        }
    }

    @Override
    public UserData getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepo.findByUsername(username);
    }

    @Override
    public List<UserData> getUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
    }

    @Override
    public RoleData getRole(String roleName) {
        log.info("Fetching role {}", roleName);
        return roleRepo.findByName(roleName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserData userData = userRepo.findByUsername(username);
        if(userData == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
        }
        Collection<SimpleGrantedAuthority> auhtorities = new ArrayList<>();
        userData.getRoleData().forEach(roleData -> {
            auhtorities.add(new SimpleGrantedAuthority(roleData.getName()));
        });
        return new User(userData.getUsername(), userData.getPassword(), auhtorities);
    }
}