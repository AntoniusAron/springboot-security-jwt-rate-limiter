package io.getarrays.userservice.repo;

import io.getarrays.userservice.domain.RoleData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 31/12/2025
 */
public interface RoleRepo extends JpaRepository<RoleData, Long> {
    RoleData findByName(String name);
}
