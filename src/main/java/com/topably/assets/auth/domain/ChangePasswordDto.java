package com.topably.assets.auth.domain;

import javax.validation.constraints.NotBlank;

public record ChangePasswordDto(String currentPassword, @NotBlank String newPassword, @NotBlank String confirmNewPassword) {
}
