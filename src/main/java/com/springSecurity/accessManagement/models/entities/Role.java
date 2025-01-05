package com.springSecurity.accessManagement.models.entities;

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
@Table(name = "roles")
public class Role extends BaseModel {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }

    public boolean hasPermission(String permissionName) {
        Optional<Permission> permissionItem = this.permissions
                .stream()
                .filter(permission -> permission.getName().equals(permissionName))
                .findFirst();

        return permissionItem.isPresent();
    }

    public Role removePermission(Permission permission) {
        Stream<Permission> newPermissions = this.permissions
                .stream()
                .filter(permission1 -> !permission1.getName().equals(permission.getName()));

        this.permissions = newPermissions.collect(Collectors.toSet());
        return this;
    }
}
