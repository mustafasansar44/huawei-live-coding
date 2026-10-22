package com.huawei.olympicranking.dto;

import com.huawei.olympicranking.model.enums.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryResponseDto {
    private Long id;
    private CountryCode countryCode;
    private String name;
}
