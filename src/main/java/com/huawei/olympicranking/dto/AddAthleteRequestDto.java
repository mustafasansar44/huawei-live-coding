package com.huawei.olympicranking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAthleteRequestDto {

    @NotBlank(message = "Athlete name cannot be blank")
    private String name;

    @NotNull(message = "Country ID cannot be null")
    private Long countryId;
}
