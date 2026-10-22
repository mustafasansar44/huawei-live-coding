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
public class AthleteResponseDto {
    private Long id;
    private String name;
    private Long countryId;
    private CountryCode countryCode;
    private String countryName;
}
