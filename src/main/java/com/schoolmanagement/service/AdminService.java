package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Admin;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.payload.response.AdminResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.AdminRepository;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Mapper;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final FieldControl fieldControl;

    public ResponseMessage save(AdminRequest adminRequest) {

        fieldControl.checkDuplicate(adminRequest.getUsername(), adminRequest.getSsn(), adminRequest.getPhoneNumber());

//        checkDuplicate(adminRequest.getUsername(), adminRequest.getSsn(), adminRequest.getPhoneNumber());
        Admin admin = Mapper.adminFromAdminRequest(adminRequest);
        admin.setBuilt_in(false);

        if (Objects.equals(adminRequest.getUsername(), "Admin")) admin.setBuilt_in(true);

        admin.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));

        // password encode etme
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedData = adminRepository.save(admin);

        return ResponseMessage.<AdminResponse>builder()
                .message("Admin saved")
                .httpStatus(HttpStatus.CREATED)
                .object(Mapper.adminResponseFromAdmin(savedData))
                .build();

    }

/*
    public void checkDuplicate(String username, String ssn, String phone) {
        UserRepository[] repositories = {
                adminRepository,
                deanRepository,
                studentRepository,
                teacherRepository,
                viceDeanRepository,
                guestUserRepository};

        for (UserRepository repository : repositories) {
            if (repository.existsByUsername(username))
                throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_USERNAME, username));
            if (repository.existsBySsn(ssn))
                throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_SSN, ssn));
            if (repository.existsByPhoneNumber(phone))
                throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER, phone));
        }

        @NoRepositoryBean
        public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
            boolean existsByUsername(String username);
            boolean existsBySsn(String ssn);
            boolean existsByPhoneNumber(String phoneNumber);
        }

        @Repository
        public interface AdminRepository extends BaseRepository<Admin, Long> {
            // Diğer metotlar...
        }

        @Repository
        public interface DeanRepository extends BaseRepository<Dean, Long> {
            // Diğer metotlar...
        }
*/


    public Page<Admin> getAllAdmin(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public String deleteAdmin(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);

        if (admin.isPresent() && admin.get().isBuilt_in())
            throw new ConflictException(Messages.NOT_PERMITTED_METHOD_MESSAGE);
        if (admin.isPresent()) {
            adminRepository.deleteById(id);
            return "Admin is deleted successfully.";
        } else return Messages.NOT_FOUND_USER_MESSAGE;
    }

    // for RUNNER
    public long countAllAdmin() {
        return adminRepository.count();
    }
}
