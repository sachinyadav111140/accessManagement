package com.springSecurity.accessManagement.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@Entity
@Table(name = "users_accounts") // Maps the entity to the "users_accounts" table
public class UserAccount extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User table
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(name = "expire_at", nullable = false)
    private long expireAt;


    public boolean isExpired() {
        return expireAt < new Date().getTime();
    }
}
