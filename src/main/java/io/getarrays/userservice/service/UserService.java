package io.getarrays.userservice.service;

import io.getarrays.userservice.domain.RoleData;
import io.getarrays.userservice.domain.UserData;

import java.util.List;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 05/01/2026
 */
public interface UserService {
    UserData saveUser(UserData userData);
    RoleData saveRole(RoleData roleData);
    void addRoleToUser(String username, String roleName);
    UserData getUser(String username);
    RoleData getRole(String roleName);
    List<UserData> getUsers();
}
