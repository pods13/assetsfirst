package com.topably.assets.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class CreateUserDto {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private boolean provideData;
}
