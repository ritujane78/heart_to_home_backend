package com.chillies.hearttohome.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="users",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = "email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String username;

  @NotBlank(message = "First name is required")
  @Size(max = 20, message = "First name cannot exceed 20 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '\\u2019-][\\p{L}]+)*$",
    message = "Invalid name"
  )
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 20, message = "Last name cannot exceed 20 characters")
  @Pattern(
    regexp = "^[\\p{L}]+(?:[ '\\u2019-][\\p{L}]+)*$",
    message = "Invalid name"
  )
  private String lastName;

  @NotBlank(message = "Email is required")
  @Size(max = 50, message = "Email cannot exceed 50 characters")
  @Email(message = "Invalid email address")
  @Column(name = "email")
  private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;

    private String twoFactorSecret;
    private boolean isTwoFactorEnabled = false;
    private String signupMethod;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id", referencedColumnName = "roleId")
    @JsonBackReference
    @ToString.Exclude
    private Role role;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<GiftOrder> giftOrders = new ArrayList<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
