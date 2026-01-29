package io.getarrays.userservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 31/12/2025
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}
