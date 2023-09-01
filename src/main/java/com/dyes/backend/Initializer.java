package com.dyes.backend;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.entity.RoleType;
import com.dyes.backend.domain.admin.repository.AdminRepository;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.html.Option;
import java.util.Optional;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class Initializer {
    final private UserRepository userRepository;
    final private AdminRepository adminRepository;
    @Bean
    public CommandLineRunner onStartUp() {
        return args -> {
            String id = "113790538949418342098";
            User user = userRepository.findByStringId(id).get();

            Optional<Admin> maybeAdmin = adminRepository.findByUser(user);

            if (maybeAdmin.isEmpty()){
                Admin admin = Admin.builder()
                        .name("ㅋㅋ")
                        .user(user)
                        .roleType(RoleType.NORMAL_ADMIN)
                        .build();

                adminRepository.save(admin);
            }
        };
    }
}
