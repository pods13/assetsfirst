package com.topably.assets.securities.domain.dto;

import com.topably.assets.securities.domain.SecurityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityDto {

    private Long id;

    private String ticker;

    private String name;

    private SecurityType securityType;
}
