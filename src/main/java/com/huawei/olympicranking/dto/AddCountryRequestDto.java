package com.huawei.olympicranking.dto;

import com.huawei.olympicranking.model.enums.CountryCode;
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
public class AddCountryRequestDto {

    @NotNull(message = "Country code cannot be null")
    private CountryCode countryCode;

    @NotBlank(message = "Country name cannot be blank")
    private String name;
}
