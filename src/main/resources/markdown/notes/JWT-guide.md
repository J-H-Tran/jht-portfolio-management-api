# JWT Implementation Guide for Spring Boot 3.x (Production-Ready)

## Table of Contents
1. [Overview](#overview)
2. [Dependencies](#dependencies)
3. [Architecture Overview](#architecture-overview)
4. [Core Components](#core-components)
5. [Implementation Steps](#implementation-steps)
6. [Security Best Practices](#security-best-practices)
7. [Testing](#testing)

---

## Overview

JWT (JSON Web Token) is a stateless authentication mechanism where the server issues a signed token to authenticated users. The token contains claims (user information) and is sent with each request for authorization.

**Key Concepts:**
- **Authentication**: Verifying user identity (login with username/password).
- **Authorization**: Verifying user permissions (roles/authorities).
- **Stateless**: No session storage on the server; token contains all necessary info.
- **Claims**: Key-value pairs in the JWT payload (user ID, username, roles, expiration).

---

## Dependencies

Add these to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT Library -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Spring Web (for REST APIs) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- JPA (for User entity) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>
```

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Request                          │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│              Spring Security Filter Chain                       │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  1. JwtAuthenticationFilter                               │ │
│  │     - Extract JWT from Authorization header               │ │
│  │     - Validate token                                      │ │
│  │     - Extract username & roles                            │ │
│  │     - Set SecurityContext                                 │ │
│  └───────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  2. UsernamePasswordAuthenticationFilter (Login)          │ │
│  │     - Handle /login endpoint                              │ │
│  │     - Authenticate user                                   │ │
│  │     - Generate JWT                                        │ │
│  └───────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  3. AuthorizationFilter                                   │ │
│  │     - Check user roles/authorities                        │ │
│  └───────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                    REST Controller                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## Core Components

### 1. **User Entity & Repository**

```java
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password; // BCrypt hashed
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();
    
    private boolean enabled = true;
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return enabled; }
}

public enum Role {
    USER, ADMIN, MANAGER
}

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

---

### 2. **JWT Utility Service**

```java
@Service
public class JwtService {
    // Use environment variable in production
    @Value("${jwt.secret}")
    private String SECRET_KEY; // Minimum 256 bits for HS256
    
    @Value("${jwt.expiration}")
    private long JWT_EXPIRATION; // e.g., 86400000 (24 hours in ms)
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Generate JWT token with user details and roles.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
            .signWith(getSigningKey())
            .compact();
    }
    
    /**
     * Extract username from JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from JWT token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim from JWT token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from JWT token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    /**
     * Check if token is expired.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validate JWT token.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

**application.yml:**
```yaml
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-make-it-very-long-and-random}
  expiration: 86400000 # 24 hours
```

---

### 3. **JWT Authentication Filter**

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Extract JWT from Authorization header
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            
            // If username is valid and no authentication exists in SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validate token
                if (jwtService.validateToken(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    
                    // Set details (IP, session, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log error but don't block the filter chain
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

### 4. **Security Configuration**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize, @Secured
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Public endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin only
                .anyRequest().authenticated() // All other endpoints require authentication
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt for password hashing
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Frontend URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

### 5. **UserDetailsService Implementation**

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

---

### 6. **Authentication Controller**

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    
    /**
     * Login endpoint: Authenticate user and return JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        
        // Generate JWT
        String jwt = jwtService.generateToken(userDetails);
        
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    
    /**
     * Register endpoint: Create new user.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setRoles(Set.of(Role.USER)); // Default role
        
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully");
    }
    
    /**
     * Logout endpoint: In JWT, logout is handled client-side by removing token.
     * For server-side logout, you can implement token blacklisting.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Client should remove JWT from storage
        // Optional: Add token to blacklist (Redis, DB)
        return ResponseEntity.ok("Logged out successfully");
    }
}

// DTOs
@Data
public class AuthRequest {
    private String username;
    private String password;
}

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
}

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
```

---

## Implementation Steps

### Step 1: Add Dependencies
Add the dependencies listed above to your `pom.xml`.

### Step 2: Create User Entity & Repository
Define your `User` entity implementing `UserDetails` and create `UserRepository`.

### Step 3: Implement JwtService
Create the `JwtService` to handle token generation, validation, and claim extraction.

### Step 4: Create JWT Filter
Implement `JwtAuthenticationFilter` to intercept requests and validate tokens.

### Step 5: Configure Security
Set up `SecurityConfig` with JWT filter, authentication provider, and password encoder.

### Step 6: Create Auth Controller
Implement `/login`, `/register`, and `/logout` endpoints.

### Step 7: Test
Use Postman or curl to test authentication flow.

---

## Security Best Practices

### 1. **Password Hashing**
- Always use BCrypt or Argon2 for password hashing.
- Never store plain-text passwords.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Strength: 12 rounds
}
```

### 2. **JWT Secret Key**
- Use a strong, random secret key (minimum 256 bits for HS256).
- Store it in environment variables, not hardcoded.
- Use a key management service (AWS KMS, Azure Key Vault) in production.

```yaml
jwt:
  secret: ${JWT_SECRET} # Set via environment variable
```

### 3. **Token Expiration**
- Set short expiration times (15-60 minutes).
- Use refresh tokens for long-lived sessions.

### 4. **HTTPS Only**
- Always use HTTPS in production to prevent token interception.

### 5. **Token Storage (Client-Side)**
- Store JWT in `HttpOnly` cookies (not accessible via JavaScript, prevents XSS).
- Avoid storing in localStorage (vulnerable to XSS).

### 6. **Token Blacklisting (Logout)**
- For server-side logout, maintain a blacklist of revoked tokens in Redis or DB.
- Check blacklist in JWT filter before validating token.

```java
@Service
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    
    public void blacklistToken(String token, long expirationTime) {
        redisTemplate.opsForValue().set(token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
    }
    
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
```

### 7. **Rate Limiting**
- Implement rate limiting on `/login` endpoint to prevent brute-force attacks.

### 8. **CORS Configuration**
- Properly configure CORS to allow only trusted origins.

### 9. **Input Validation**
- Validate all user inputs to prevent injection attacks.

### 10. **Logging & Monitoring**
- Log authentication attempts (success and failure).
- Monitor for suspicious activity (multiple failed logins, unusual token usage).

---

## Minimal Production-Ready Setup Summary

### Required Components:
1. **User Entity** (implements `UserDetails`)
2. **UserRepository** (extends `JpaRepository`)
3. **JwtService** (token generation & validation)
4. **JwtAuthenticationFilter** (validates JWT on each request)
5. **SecurityConfig** (configures Spring Security)
6. **UserDetailsService** (loads user from DB)
7. **AuthController** (login, register, logout endpoints)
8. **PasswordEncoder** (BCrypt for password hashing)

### Configuration:
```yaml
jwt:
  secret: ${JWT_SECRET} # 256+ bit secret key
  expiration: 3600000 # 1 hour

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

### Security Checklist:
- ✅ Use BCrypt for password hashing
- ✅ Store JWT secret in environment variables
- ✅ Set short token expiration (15-60 min)
- ✅ Use HTTPS in production
- ✅ Implement CORS properly
- ✅ Disable CSRF for stateless JWT
- ✅ Use stateless session management
- ✅ Validate and sanitize all inputs
- ✅ Implement rate limiting on login
- ✅ Log authentication events
- ✅ Consider token blacklisting for logout

---

## Testing

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/portfolios \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4. Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Advanced Features

### 1. **Refresh Tokens**
Implement long-lived refresh tokens to renew access tokens without re-authentication.

### 2. **Role-Based Access Control (RBAC)**
Use `@PreAuthorize` for method-level security:

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public List<User> getAllUsers() {
    return userService.getAllUsers();
}
```

### 3. **Multi-Factor Authentication (MFA)**
Add TOTP or SMS-based MFA for enhanced security.

### 4. **OAuth2 Integration**
Integrate with OAuth2 providers (Google, GitHub) for social login.

---

## Conclusion

This guide provides a minimal yet production-ready JWT implementation for Spring Boot 3.x. It covers authentication, authorization, password hashing, token validation, and security best practices. Follow the checklist and security guidelines to ensure your application is secure and scalable.

For your multi-datasource portfolio management API, you can integrate this JWT setup to secure all endpoints and manage user authentication across both primary and audit databases.

---

**Next Steps:**
- Implement refresh tokens for better UX.
- Add rate limiting on login endpoint.
- Set up token blacklisting with Redis.
- Configure HTTPS and SSL certificates.
- Implement comprehensive logging and monitoring.