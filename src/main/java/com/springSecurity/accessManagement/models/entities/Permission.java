package com.springSecurity.accessManagement.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
@Entity
@Table(name = "permissions")
public class Permission extends BaseModel {

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  private String description;

}
