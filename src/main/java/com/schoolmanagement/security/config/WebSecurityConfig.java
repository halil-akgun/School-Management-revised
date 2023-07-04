package com.schoolmanagement.security.config;

import com.schoolmanagement.security.jwt.AuthEntryPointJwt;
import com.schoolmanagement.security.jwt.AuthTokenFilter;
import com.schoolmanagement.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
// yeni Spring Security sürümlerinde (2.7.1 ve sonrasi) WebSecurityConfigurerAdapter'i extend etmek zorunlu değildir

    private final AuthEntryPointJwt unauthorizedHandler;

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }

    @Bean // eski surumde bunu configure methodunu @Override ederek yapiyorduk
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//   cors(): tarayici tabanli guvenlik - domain,protocol,port farkli bir yerden istek geldiginde sunucunun
//   alanlariyla eslesmiyorsa tarayici otomatik engelliyor.ancak gunumuzde genelde backend,frontend farkli sunucularda
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//          authenticationEntryPoint(unauthorizedHandler): exception belirledigimiz gibi handle edilecek
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers(AUTH_WHITE_LIST).permitAll()
                .anyRequest().authenticated();

        http.headers().frameOptions().sameOrigin();
        http.authenticationProvider(authenticationProvider()); // Provider tanitma
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//        addFilterBefore metodu, Filter'ı belirtilen filtre sınıfının önüne eklemek için kullanılır.
//        Bu durumda, authenticationJwtTokenFilter() metodundan dönen filtre, UsernamePasswordAuthenticationFilter
//        sınıfının önüne eklenir.
//        Bu yapılandırma, gelen isteklerde önce authenticationJwtTokenFilter() tarafından JWT doğrulama
//        işlemlerinin gerçekleştirilmesini sağlar ve ardından UsernamePasswordAuthenticationFilter tarafından
//        kullanıcı adı ve parola tabanlı kimlik doğrulaması yapılır. Bu sayede, isteklerde hem JWT doğrulaması
//        hem de kullanıcı adı/parola doğrulaması işlemleri gerçekleştirilebilir.

        return http.build();
    }

    private static final String AUTH_WHITE_LIST[] = {
            "/",
            "/index*",
            "/static/**",
            "/*.js",
            "/*.json",
            "/contactMessages/save",
            "/swagger-ui/**",
            "/swagger*/**", // aslinda yukaridakini de kapsiyor - swagger* * ile devami ne olursa kapsar
            "/v3/api-docs/**",
            "/auth/login"

    };

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**") // tum URL leri kapsayacagini soyledik
                        .allowedOrigins("*") // tum kaynaklara izin veriliyor
                        .allowedHeaders("*") // tum header lara izin verilecegini soyledik
                        .allowedMethods("*"); // butun HTTP methodlarina izin verildi
//          mesela frontend,backend farkli sunuculara konuldu, o zaman buralar ozellestirilebilir
            }
        };
    }
}