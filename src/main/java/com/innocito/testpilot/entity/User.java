package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String otp;
    @Column(name = "otp_generated_at")
    private Date otpGeneratedAt;
    @Column(name = "is_verified")
    private Integer isVerified;
    @OneToMany(mappedBy = "user")
    private List<UserProject> projects;
}