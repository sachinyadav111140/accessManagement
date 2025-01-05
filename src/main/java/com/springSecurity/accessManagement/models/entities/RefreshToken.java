package com.springSecurity.accessManagement.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;



@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@Entity
@Table(name = "refresh_tokens")
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends BaseModel {

    private String value;
}