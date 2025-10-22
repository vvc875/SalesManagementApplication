package com.example.sales.config;

import com.example.sales.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Cấu hình quyền truy cập cho các request HTTP
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép tất cả mọi người truy cập vào các đường dẫn này
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        // Yêu cầu tất cả các request còn lại phải được xác thực (đã đăng nhập)
                        .anyRequest().authenticated()
                )
                // Cấu hình form đăng nhập
                .formLogin(form -> form
                        // DÒNG QUAN TRỌNG NHẤT:
                        // Báo cho Spring Security biết trang đăng nhập của bạn ở đâu
                        .loginPage("/login")

                        // URL mà form của bạn sẽ POST tới (th:action="@{/login}")
                        .loginProcessingUrl("/login")

                        // Sau khi đăng nhập thành công, chuyển đến trang "/" (trang chủ)
                        .defaultSuccessUrl("/page_home", true)
                        .permitAll() // Cho phép tất cả mọi người truy cập form này
                )
                // Cấu hình đăng xuất
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Chuyển hướng đến trang login với thông báo logout
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
         return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService(AccountRepository accountRepository) {
        return username -> accountRepository.findById(username)
                .map(account -> User.builder()
                        .username(account.getUsername())
                        .password(account.getPassword())
                        .roles("USER") // Bạn có thể thêm vai trò (roles) để phân quyền phức tạp hơn
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
    }
}