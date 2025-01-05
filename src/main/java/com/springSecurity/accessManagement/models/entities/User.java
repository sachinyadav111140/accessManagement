package com.springSecurity.accessManagement.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@Entity
@Table(name = "users")
public class User extends BaseModel {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String gender;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean confirmed;

    private String avatar;

    private String timezone;

    @Embedded
    private Coordinates coordinates;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public User() {
        permissions = new HashSet<>();
    }

    public User(String firstName, String lastName, String email, String password, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.enabled = true;
        this.confirmed = false;
        permissions = new HashSet<>();
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public boolean hasPermission(String permissionName) {
        Optional<Permission> permissionItem = this.permissions.stream()
                .filter(permission -> permission.getName().equals(permissionName))
                .findFirst();

        return permissionItem.isPresent();
    }

    public void removePermission(Permission permission) {
        Stream<Permission> newPermissions = this.permissions.stream()
                .filter(permission1 -> !permission1.getName().equals(permission.getName()));

        this.permissions = newPermissions.collect(Collectors.toSet());
    }

    public Set<Permission> allPermissions() {
        Set<Permission> all = new HashSet<>(this.permissions);
        if (this.role != null) {
            all.addAll(this.role.getPermissions());
        }
        return all;
    }
}
