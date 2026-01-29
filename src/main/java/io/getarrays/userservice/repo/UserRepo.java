package io.getarrays.userservice.repo;

import io.getarrays.userservice.domain.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 31/12/2025
 */
public interface UserRepo extends JpaRepository<UserData, Long> {
    UserData findByUsername(String username);

    @Query("SELECT COUNT(u) > 0 FROM UserData u JOIN u.roleData r WHERE u.username = :username AND r.name = :roleName")
    boolean isRoleMapped(@Param("username") String username, @Param("roleName") String roleName);
}
