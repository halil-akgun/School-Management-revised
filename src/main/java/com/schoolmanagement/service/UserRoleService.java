package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole getUserRole(RoleType roleType) {

        Optional<UserRole> userRole = userRoleRepository.findByERoleEquals(roleType);

        return userRole.orElse(null);
    }

    public List<UserRole> getAllUserRole() {
        return userRoleRepository.findAll();
    }

    public UserRole save(RoleType roleType) {
        // existsByERoleEquals: hazir kod calismasin diye fazladan E koyuldu
        if (userRoleRepository.existsByERoleEquals(roleType))
            throw new ConflictException("This role is already registered.");

        UserRole userRole = UserRole.builder().roleType(roleType).build();
        return userRoleRepository.save(userRole);
    }
}
