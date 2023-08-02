package com.topably.assets.auth.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class CreateUserDto {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private boolean provideData;
}
