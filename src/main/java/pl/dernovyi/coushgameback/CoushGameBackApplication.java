package pl.dernovyi.coushgameback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static pl.dernovyi.coushgameback.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
public class CoushGameBackApplication {

    @Value(value = "${front_address}")
    private String front_address;
    @Value(value = "${CREDENTIALS}")
    private String access;

    public static void main(String[] args) {
        SpringApplication.run(CoushGameBackApplication.class, args);
        new File(USER_FOLDER).mkdirs();
    }

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(Boolean.parseBoolean(access));
        corsConfiguration.setAllowedOrigins(Collections.singletonList(front_address));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Access", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested_With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
                "Access-Control-Allow-Origin","Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
