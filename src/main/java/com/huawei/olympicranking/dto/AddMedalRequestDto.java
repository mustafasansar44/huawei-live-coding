package com.huawei.olympicranking.dto;

import com.huawei.olympicranking.model.enums.MedalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMedalRequestDto {

    @NotNull(message = "Athlete ID cannot be null")
    private Long athleteId;

    @NotNull(message = "Sport ID cannot be null")
    private Long sportId;

    @NotNull(message = "Country ID cannot be null")
    private Long countryId;

    @NotNull(message = "Medal type cannot be null")
    private MedalType medalType;
}
