package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Admin;
import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.Gender;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.payload.response.AdminResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.AdminRepository;
import com.schoolmanagement.utils.FieldControl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock // it allows the formation of a mock/fake object
//           for example if we use adminRepository.save() method, it doesn't save the data to DB.
    private AdminRepository adminRepository;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FieldControl fieldControl;

    @InjectMocks // yukaridakilerin tamami adminService'in icine otomatik enjekte oluyor
    private AdminService adminService;

    @Test
    void testSave_AdminSavedSuccessfully() {
        AdminRequest request = createAdminRequest();
        Admin savedAdmin = createAdmin();
        savedAdmin.setId(1L);

        UserRole adminRole = new UserRole(1, RoleType.ADMIN);
        doNothing().when(fieldControl).checkDuplicate(anyString(), anyString(), anyString());

        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(userRoleService.getUserRole(RoleType.ADMIN)).thenReturn(adminRole);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        ResponseMessage<AdminResponse> response = adminService.save(request);

        assertNotNull(response);
        assertEquals("Admin saved", response.getMessage());
        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertNotNull(response.getObject());
        assertEquals(savedAdmin.getId(), response.getObject().getUserId());
        assertEquals(savedAdmin.getName(), response.getObject().getName());

        Mockito.verify(fieldControl, Mockito.times(1))
                .checkDuplicate(anyString(), anyString(), anyString());
        Mockito.verify(adminRepository, Mockito.times(1)).save(any(Admin.class));
        Mockito.verify(userRoleService, Mockito.times(1)).getUserRole(RoleType.ADMIN);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(anyString());
    }


    private AdminRequest createAdminRequest() {
        AdminRequest request = new AdminRequest();
        request.setName("GUL");
        request.setSurname("ROSE");
        request.setUsername("admin1");
        request.setPassword("12345678");
        request.setSsn("123456789");
        request.setBirthPlace("Batman");
        request.setBirthDay(LocalDate.of(1995, 8, 1));
        request.setPhoneNumber("123456789");
        request.setGender(Gender.FEMALE);
        return request;
    }

    private Admin createAdmin() {
        Admin admin = new Admin();
        admin.setName("GUL");
        admin.setSurname("ROSE");
        admin.setUsername("admin1");
        admin.setPassword("12345678");
        admin.setSsn("123456789");
        admin.setBirthPlace("Batman");
        admin.setBirthday(LocalDate.of(1995, 8, 1));
        admin.setPhoneNumber("123456789");
        admin.setGender(Gender.FEMALE);
        return admin;
    }

    @Test
    void getAllAdmin() {

        Pageable pageable = Pageable.unpaged();
        Page<Admin> expectedPage = mock(Page.class);

        when(adminRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Admin> result = adminService.getAllAdmin(pageable);

        assertSame(expectedPage, result);
        verify(adminRepository, times(1)).findAll(pageable);
    }

    @Test
    void deleteAdmin_Successful() {
        Long id = 1L;
        Admin admin = new Admin();
        admin.setId(id);
        admin.setBuilt_in(false);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

        String result = adminService.deleteAdmin(id);

        assertEquals("Admin is deleted successfully.", result);
        verify(adminRepository, times(1)).deleteById(id);
    }
}