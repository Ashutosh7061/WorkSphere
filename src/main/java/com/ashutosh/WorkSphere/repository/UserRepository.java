package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.User;
import com.ashutosh.WorkSphere.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByBuilding_IdAndRole(Long buildingId, Role role);
}
