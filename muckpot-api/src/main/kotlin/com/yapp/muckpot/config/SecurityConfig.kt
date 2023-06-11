package com.yapp.muckpot.config

import com.yapp.muckpot.common.EMAIL_REQUEST
import com.yapp.muckpot.common.EMAIL_VERIFY
import com.yapp.muckpot.common.LOGIN_URL
import com.yapp.muckpot.common.SIGN_UP_URL
import com.yapp.muckpot.common.USER_PROFILE_URL
import com.yapp.muckpot.common.security.CustomAuthenticationEntryPoint
import com.yapp.muckpot.domains.user.service.JwtService
import com.yapp.muckpot.filter.JwtAuthorizationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtService: JwtService,
    @Value("\${api.option.permit-all}")
    private val permitAll: Boolean
) {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .antMatchers(HttpMethod.POST, *POST_PERMIT_ALL_URLS.toTypedArray()).permitAll()
                    .antMatchers(HttpMethod.GET, "/api/**", "/swagger-ui/**")
                    .permitAll()
                    .antMatchers("/api/**").apply {
                        if (permitAll) {
                            permitAll()
                        } else {
                            hasRole("USER")
                        }
                    }
            }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .addFilterBefore(JwtAuthorizationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .exceptionHandling()
            .authenticationEntryPoint(CustomAuthenticationEntryPoint())

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        val source = UrlBasedCorsConfigurationSource()

        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowCredentials = true
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .antMatchers("/swagger-ui/**")
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    companion object {
        val POST_PERMIT_ALL_URLS = listOf(
            LOGIN_URL,
            SIGN_UP_URL,
            EMAIL_REQUEST,
            EMAIL_VERIFY,
            USER_PROFILE_URL
        )
    }
}
