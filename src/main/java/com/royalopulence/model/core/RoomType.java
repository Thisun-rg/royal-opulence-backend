package com.royalopulence.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.royalopulence.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        /*
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
           // token = authHeader.substring(7);
           // username = jwtUtil.extractUsername(token);
        }
         */
        // ✅ SAFE TOKEN EXTRACTION
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                token = authHeader.substring(7);
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                token = null;
                username = null;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                                .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

}
,package com.royalopulence.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints — no token required
                        .requestMatchers("/api/auth/**").permitAll()
                        // .requestMatchers("/api/setup/**").permitAll() // Disabled after setup

                        .requestMatchers("/error").permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // No session, we use JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
,package com.royalopulence.controller.invoice;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.service.base.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // CREATE invoice
    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    // GET all invoices
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // GET invoice by invoiceId
    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable String invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    // GET invoice by paymentId
    @GetMapping("/by-payment/{paymentId}")
    public ResponseEntity<InvoiceResponse> getInvoiceByPaymentId(
            @PathVariable String paymentId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByPaymentId(paymentId));
    }

    // GET invoices by reservationId
    @GetMapping("/by-reservation/{reservationId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByReservationId(
            @PathVariable String reservationId) {
        return ResponseEntity.ok(
                invoiceService.getInvoicesByReservationId(reservationId)
        );
    }

    // DOWNLOAD invoice PDF
    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable String invoiceId) {

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=invoice.pdf")
                .body(invoiceService.downloadInvoicePdf(invoiceId));
    }
}
,package com.royalopulence.controller.payment;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.royalopulence.dto.common.ApiResponse;
import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.service.base.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment created", response));
    }

    @PostMapping("/stripe")
    public ResponseEntity<ApiResponse<PaymentResponse>> createStripePayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createStripePayment(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stripe payment created", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment found", paymentService.getPaymentById(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payments list", paymentService.getAllPayments())
        );
    }

    @PatchMapping("/{id}/success")
    public ResponseEntity<ApiResponse<PaymentResponse>> markSuccess(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment SUCCESS", paymentService.markPaymentSuccess(id))
        );
    }

    @PatchMapping("/{id}/failed")
    public ResponseEntity<ApiResponse<PaymentResponse>> markFailed(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment FAILED", paymentService.markPaymentFailed(id))
        );
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment REFUNDED", paymentService.markPaymentRefunded(id))
        );
    }

    @PostMapping("/invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Invoice created", invoiceService.createInvoice(request))
        );
    }

    // ---------------- HEALTH CHECK ----------------
    @GetMapping("/health")
    public String health() {
        return "Payment module is up";
    }
}
,package com.royalopulence.controller.report;

import com.royalopulence.dto.common.ApiResponse;
import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.service.base.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/payments-summary")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> getPaymentsSummary() {
        PaymentSummaryResponse summary = reportService.getPaymentSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments summary", summary));
    }

    @GetMapping("/payments-summary/pdf")
    public ResponseEntity<byte[]> downloadPaymentSummaryPdf() {
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=payment-summary.pdf")
                .body(reportService.downloadPaymentSummaryPdf());
    }

}
,package com.royalopulence.controller;

import com.royalopulence.model.core.Role;
import com.royalopulence.model.core.User;
import com.royalopulence.repository.RoleRepository;
import com.royalopulence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

        import java.util.Collections;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class AdminSetupController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create-admin")
    public String createAdmin() {
        if (userRepository.findByEmail("admin@royalopulence.com").isPresent()) {
            return "Admin user already exists.";
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));

        User admin = new User();
        admin.setName("Super Admin");
        admin.setEmail("admin@royalopulence.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        // store role *name*
        admin.setRoles(Collections.singleton(adminRole.getName()));
        userRepository.save(admin);

        return "Admin user created successfully!";
    }

}
,package com.royalopulence.controller;

import com.royalopulence.dto.auth.*;
        import com.royalopulence.service.base.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/health")
    public String health() {
        return "Royal Opulence Backend is running on port 8090!";
    }
}
,package com.royalopulence.controller;

import org.springframework.web.bind.annotation.*;
        import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String hello(@AuthenticationPrincipal UserDetails userDetails) {
        return "✅ Hello " + userDetails.getUsername() + ", your token is valid!";
    }
}
,package com.royalopulence.controller;

import com.royalopulence.model.core.User;
import com.royalopulence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // Get all users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Get current logged-in user
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // Get specific user by Mongo id (String)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // Update profile info (accepts user object in body)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(updatedUser.getName());
        // update other fields as needed (avoid changing email/password here unless intentional)
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    // Delete a user (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

,package com.royalopulence.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
}
,package com.royalopulence.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
,package com.royalopulence.dto.auth;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank private String name;
    @Email private String email;
    @NotBlank private String password;
}
,package com.royalopulence.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}

,package com.royalopulence.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InvoiceRequest {

    @NotBlank(message = "Reservation ID is required")
    private String reservationId;

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    private String currency;
}

,package com.royalopulence.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceResponse {
    private String id;
    private String reservationId;
    private String paymentId;
    private Double totalAmount;
    private String currency;
    private String invoiceNumber;
    private Long issuedAt;
}

,package com.royalopulence.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank(message = "Reservation ID is required")
    private String reservationId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Currency is required")
    private String currency; // e.g. LKR, USD

    private String description; // optional
}
,package com.royalopulence.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {

    private String paymentId;
    private String reservationId;

    // 🔹 Clear financial breakdown
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;

    private String currency;
    private String status;        // PENDING, SUCCESS, FAILED
    private String method;        // NOT_SET, STRIPE
    private Long createdAt;
}
,package com.royalopulence.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSummaryResponse {
    private Double totalRevenue;
    private Long totalPayments;
}

,package com.royalopulence.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}

,package com.royalopulence.exception;

import com.royalopulence.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = (fieldError != null) ? fieldError.getDefaultMessage() : "Validation error";
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Internal server error", null));
    }
}
,package com.royalopulence.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
,package com.royalopulence.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
,package com.royalopulence.model.core;

import lombok.*;
        import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed; // if needed
import java.util.*; // List, Date, Set, etc.


public class LoyaltyAccount {

}
,package com.royalopulence.model.core;

import lombok.*;
        import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed; // if needed
import java.util.*; // List, Date, Set, etc.


public class Promotion {

}
,package com.royalopulence.model.core;

import lombok.*;
        import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.*;

@Document(collection = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    private String id;

    private String userId;     // Reference to User document
    private String roomId;     // Reference to Room document

    private Date checkInDate;
    private Date checkOutDate;

    private String status;     // PENDING / CONFIRMED / CANCELLED
    private double totalAmount;
}

,package com.royalopulence.model.core;

import lombok.*;
        import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
}
,package com.royalopulence.model.core;

import lombok.*;
        import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.*;

@Document(collection = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    private String id;

    private String roomNumber;
    private String type;
    private String description;
    private double pricePerNight;
    private int capacity;
    private String status; // AVAILABLE / BOOKED / MAINTENANCE
    private List<String> amenities;
}
,package com.royalopulence.model.core;

import lombok.*;
        import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed; // if needed
import java.util.*; // List, Date, Set, etc.

public class RoomType {

}
,
        package com.royalopulence.model.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    // store role names like "GUEST", "ADMIN"
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> (GrantedAuthority) () -> "ROLE_" + r)
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() { return email; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
,package com.royalopulence.model.operation;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "invoices")
public class Invoice {

    @Id
    private String id;

    private String reservationId;
    private String paymentId;
    private Double totalAmount;
    private String currency;

    private String invoiceNumber;
    private Long issuedAt;
}

,package com.royalopulence.model.operation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.royalopulence.model.utility.PaymentMethod;
import com.royalopulence.model.utility.PaymentStatus;

import lombok.Data;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String reservationId;

    // Financials
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String currency;

    // Lifecycle (ENUMS – Mongo handles as String automatically)
    private PaymentStatus status;
    private PaymentMethod method;

    private String description;

    private Long createdAt;
    private Long expiresAt;

    // Stripe
    private String stripeIntentId;

    // Hotel-grade accounting (optional, future-ready)
    private Double exchangeRate;
    private Double amountLkr;
}
,package com.royalopulence.model.utility;

public enum PaymentMethod {
    NOT_SET,
    STRIPE,
    PAYHERE
}
,package com.royalopulence.model.utility;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED,
    EXPIRED
}
,package com.royalopulence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String auditLogId;

    private String action;
    private String referenceId;
    private long timestamp;
}

,package com.royalopulence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalopulence.model.AuditLog;

public interface AuditLogRepository extends MongoRepository<AuditLog, String>
{

}

,package com.royalopulence.repository;

import com.royalopulence.model.operation.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    // Get invoice by Payment ID
    Optional<Invoice> findByPaymentId(String paymentId);

    // Get all invoices by Reservation ID
    List<Invoice> findByReservationId(String reservationId);
}
,package com.royalopulence.repository;

import com.royalopulence.model.operation.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByReservationId(String reservationId);
}
,package com.royalopulence.repository;

import com.royalopulence.model.core.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}

,package com.royalopulence.repository;

import com.royalopulence.model.core.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

,package com.royalopulence.service.base;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request);

    InvoiceResponse getInvoiceById(String id);

    List<InvoiceResponse> getAllInvoices();


    InvoiceResponse getInvoiceByPaymentId(String paymentId);

    List<InvoiceResponse> getInvoicesByReservationId(String reservationId);

    byte[] downloadInvoicePdf(String invoiceId);
}


,package com.royalopulence.service.base;

import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(String id);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByReservationId(String reservationId);

    PaymentResponse markPaymentSuccess(String id);

    PaymentResponse markPaymentFailed(String id);

    PaymentResponse createStripePayment(PaymentRequest request);

    PaymentResponse markPaymentRefunded(String paymentId);

}
,package com.royalopulence.service.base;

import com.royalopulence.dto.report.PaymentSummaryResponse;

public interface ReportService {

    PaymentSummaryResponse getPaymentSummary();

    byte[] downloadPaymentSummaryPdf();
}
,package com.royalopulence.service.base;

import com.royalopulence.dto.auth.*;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
,package com.royalopulence.service.impl;

import com.royalopulence.model.core.User;
import com.royalopulence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
,package com.royalopulence.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.exception.BusinessException;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Invoice;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.InvoiceRepository;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.util.PdfUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PdfUtil pdfUtil;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        invoiceRepository.findByPaymentId(request.getPaymentId())
                .ifPresent(i -> {
                    throw new BusinessException(
                            "Invoice already exists: " + i.getInvoiceNumber());
                });

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        // ENUM-SAFE RULE
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException(
                    "Invoice allowed only for SUCCESS payments");
        }

        Invoice invoice = new Invoice();
        invoice.setReservationId(request.getReservationId());
        invoice.setPaymentId(request.getPaymentId());
        invoice.setTotalAmount(payment.getTotalAmount());
        invoice.setCurrency(payment.getCurrency());
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setIssuedAt(System.currentTimeMillis());

        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found"));
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public InvoiceResponse getInvoiceByPaymentId(String paymentId) {
        return invoiceRepository.findByPaymentId(paymentId)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found"));
    }

    @Override
    public List<InvoiceResponse> getInvoicesByReservationId(String reservationId) {
        return invoiceRepository.findByReservationId(reservationId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public byte[] downloadInvoicePdf(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found"));
        return pdfUtil.generateInvoicePdf(invoice);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getReservationId(),
                invoice.getPaymentId(),
                invoice.getTotalAmount(),
                invoice.getCurrency(),
                invoice.getInvoiceNumber(),
                invoice.getIssuedAt()
        );
    }
}
,package com.royalopulence.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;
import com.royalopulence.exception.BusinessException;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentMethod;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.service.base.PaymentService;
import com.royalopulence.service.payment.StripePaymentService;
import com.royalopulence.util.AuditUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripePaymentService stripePaymentService;
    private final InvoiceService invoiceService;
    private final AuditUtil auditUtil;

    private static final double TAX_RATE = 0.10;

    private double total(double base) {
        return base + (base * TAX_RATE);
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        // ⭐ NEW SAFETY CHECK
        if (request.getAmount() <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());
        payment.setBaseAmount(request.getAmount());
        payment.setTaxAmount(request.getAmount() * TAX_RATE);
        payment.setTotalAmount(total(request.getAmount()));
        payment.setCurrency(request.getCurrency());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setMethod(PaymentMethod.NOT_SET);
        payment.setCreatedAt(System.currentTimeMillis());

        payment = paymentRepository.save(payment);
        auditUtil.log("PAYMENT_CREATED", payment.getId());

        return map(payment);
    }

    @Override
    public PaymentResponse createStripePayment(PaymentRequest request) {

        if (request.getAmount() <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        try {
            var intent = stripePaymentService.createPaymentIntent(
                    total(request.getAmount()),
                    request.getCurrency()
            );

            Payment payment = new Payment();
            payment.setReservationId(request.getReservationId());
            payment.setBaseAmount(request.getAmount());
            payment.setTaxAmount(request.getAmount() * TAX_RATE);
            payment.setTotalAmount(total(request.getAmount()));
            payment.setCurrency(request.getCurrency());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setMethod(PaymentMethod.STRIPE);
            payment.setStripeIntentId(intent.getId());
            payment.setCreatedAt(System.currentTimeMillis());
            payment.setExpiresAt(System.currentTimeMillis() + 15 * 60 * 1000);

            payment = paymentRepository.save(payment);
            auditUtil.log("STRIPE_PAYMENT_CREATED", payment.getId());

            return map(payment);

        } catch (Exception e) {
            throw new BusinessException("Stripe payment failed");
        }
    }

    @Override
    public PaymentResponse markPaymentSuccess(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS
                || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("Invalid payment state change");
        }

        if (payment.getExpiresAt() != null
                && System.currentTimeMillis() > payment.getExpiresAt()) {
            throw new BusinessException("Payment expired");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment = paymentRepository.save(payment);

        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setReservationId(payment.getReservationId());
        invoiceRequest.setPaymentId(payment.getId());
        invoiceRequest.setTotalAmount(payment.getTotalAmount());
        invoiceRequest.setCurrency(payment.getCurrency());

        invoiceService.createInvoice(invoiceRequest);
        auditUtil.log("PAYMENT_SUCCESS", id);

        return map(payment);
    }

    @Override
    public PaymentResponse markPaymentFailed(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("Cannot fail SUCCESS payment");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment = paymentRepository.save(payment);
        auditUtil.log("PAYMENT_FAILED", id);

        return map(payment);
    }

    @Override
    public PaymentResponse markPaymentRefunded(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Only SUCCESS payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        auditUtil.log("PAYMENT_REFUNDED", id);

        return map(payment);
    }

    @Override
    public PaymentResponse getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByReservationId(String reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .stream().map(this::map).collect(Collectors.toList());
    }

    private PaymentResponse map(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getReservationId(),
                p.getBaseAmount(),
                p.getTaxAmount(),
                p.getTotalAmount(),
                p.getCurrency(),
                p.getStatus().name(),
                p.getMethod().name(),
                p.getCreatedAt()
        );
    }
}
,package com.royalopulence.service.impl;

import org.springframework.stereotype.Service;

import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.ReportService;
import com.royalopulence.util.PdfUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final PdfUtil pdfUtil;

    @Override
    public PaymentSummaryResponse getPaymentSummary() {

        double revenue = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getTotalAmount)
                .sum();

        long count = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .count();

        return new PaymentSummaryResponse(revenue, count);
    }

    @Override
    public byte[] downloadPaymentSummaryPdf() {
        return pdfUtil.generatePaymentSummaryPdf(getPaymentSummary());
    }
}
,package com.royalopulence.service.impl;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.royalopulence.dto.auth.AuthResponse;
import com.royalopulence.dto.auth.LoginRequest;
import com.royalopulence.dto.auth.RegisterRequest;
import com.royalopulence.model.core.Role;
import com.royalopulence.model.core.User;
import com.royalopulence.repository.RoleRepository;
import com.royalopulence.repository.UserRepository;
import com.royalopulence.service.base.UserService;
import com.royalopulence.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        // ensure role exists in roles collection
        Role defaultRole = roleRepository.findByName("GUEST")
                .orElseGet(() -> roleRepository.save(new Role(null, "GUEST")));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // store only the role name to simplify reads
        user.setRoles(Collections.singleton(defaultRole.getName()));

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail());
        return new AuthResponse(token, saved.getEmail(), "GUEST");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        String role = user.getRoles().stream().findFirst().orElse("GUEST");

        return new AuthResponse(token, user.getEmail(), role);

    }
}

,package com.royalopulence.util;

import org.springframework.stereotype.Component;

import com.royalopulence.model.AuditLog;
import com.royalopulence.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditUtil {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String referenceId) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setReferenceId(referenceId);
        log.setTimestamp(System.currentTimeMillis());
        auditLogRepository.save(log);
    }
}
,package com.royalopulence.util;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private JwtParser getParser() {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // replaced parserBuilder()
                .build();
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username) // updated API
                .issuedAt(new Date(now)) // updated API
                .expiration(new Date(now + expiration)) // updated API
                .signWith(getSigningKey()) // no SignatureAlgorithm needed
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = getParser()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Claims claims = getParser()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().before(new Date());
    }
}
,package com.royalopulence.util;

import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.model.operation.Invoice;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfUtil {

    public byte[] generateInvoicePdf(Invoice invoice) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setLeading(20f);
            content.newLineAtOffset(50, 700);

            content.showText("Invoice");
            content.newLine();
            content.showText("Invoice Number: " + invoice.getInvoiceNumber());
            content.newLine();
            content.showText("Payment ID: " + invoice.getPaymentId());
            content.newLine();
            content.showText("Reservation ID: " + invoice.getReservationId());
            content.newLine();
            content.showText("Amount: " + invoice.getTotalAmount() + " " + invoice.getCurrency());

            content.endText();
            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    // 👇 REQUIRED for reports
    public byte[] generatePaymentSummaryPdf(PaymentSummaryResponse summary) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setLeading(20f);
            content.newLineAtOffset(50, 700);

            content.showText("Payment Summary Report");
            content.newLine();
            content.showText("Total Revenue: " + summary.getTotalRevenue());
            content.newLine();
            content.showText("Total Payments: " + summary.getTotalPayments());

            content.endText();
            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payment summary PDF", e);
        }
    }
}
,package com.royalopulence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoyalOpulenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoyalOpulenceApplication.class, args);
    }
}
,# -------------------------------------------------
        # 🔧 SERVER CONFIGURATION
# -------------------------------------------------
spring.application.name=royal-opulence-backend
server.port=9001


        # -------------------------------------------------
        # 🍃 MONGODB (LOGS, MEDIA, NOTIFICATIONS)
# -------------------------------------------------
spring.data.mongodb.uri=mongodb+srv://thisunrenuka15_db_user:ATUxSknctcHqNs91@hotelcluster.wffqm6j.mongodb.net/
spring.data.mongodb.database=royal_opulence_mongo

# -------------------------------------------------
        # 🔐 JWT CONFIGURATION
# -------------------------------------------------
jwt.secret=U2VjdXJlU3ByaW5nQm9vdEtleUdvZXNIU0gyNTYyMDI1Q29uZmlybWVk
jwt.expiration=3600000

        # -------------------------------------------------
        # 📧 MAIL CONFIGURATION (Optional)
# -------------------------------------------------
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_email_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

        # -------------------------------------------------
        # 🌐 CORS CONFIG (Optional)
# -------------------------------------------------
spring.web.cors.allowed-origins=http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

        #-------------------------------------------------
        # Stripe (Test mode only)
#-------------------------------------------------
stripe.secret.key=sk_test_XXXXXXXXXXXXXXXX

,<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.7</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.royalopulence</groupId>
	<artifactId>royal-opulence-backend</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>royal-opulence-backend</name>
<description>Demo project for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
	
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-mongodb</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-websocket</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>

		<!-- JWT Authentication -->
		<dependency>
    		<groupId>io.jsonwebtoken</groupId>
    		<artifactId>jjwt-api</artifactId>
    		<version>0.12.5</version>
		</dependency>
		<dependency>
    		<groupId>io.jsonwebtoken</groupId>
    		<artifactId>jjwt-impl</artifactId>
    		<version>0.12.6</version>
    		<scope>runtime</scope>
		</dependency>
		<dependency>
    		<groupId>io.jsonwebtoken</groupId>
    		<artifactId>jjwt-jackson</artifactId>
    		<version>0.12.5</version>
    		<scope>runtime</scope>
		</dependency>

		<!-- MySQL Connector -->
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>

		<!-- PDF generation -->
		<dependency>
    		<groupId>org.apache.pdfbox</groupId>
    		<artifactId>pdfbox</artifactId>
    		<version>2.0.30</version>
		</dependency>

		<!-- Stripe Payment Gateway (Test Mode) -->
		<dependency>
    		<groupId>com.stripe</groupId>
    		<artifactId>stripe-java</artifactId>
    		<version>24.16.0</version>
		</dependency>
	
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<annotationProcessorPaths>
						<path>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>