package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
