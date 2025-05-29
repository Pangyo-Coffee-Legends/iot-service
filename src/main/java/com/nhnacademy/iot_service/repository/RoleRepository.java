package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 역할(Role) 엔티티에 대한 JPA 리포지토리 인터페이스입니다.
 * <p>
 * 기본적인 CRUD 메서드와 역할명으로 Role을 조회하는 메서드를 제공합니다.
 * </p>
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 역할명을 기준으로 Role 엔티티를 조회합니다.
     *
     * @param roleName 조회할 역할명
     * @return 역할명이 일치하는 Role 엔티티의 Optional
     */
    Optional<Role> findByRoleName(String roleName);
}