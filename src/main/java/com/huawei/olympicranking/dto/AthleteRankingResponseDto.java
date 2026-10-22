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
public class AthleteRankingResponseDto {
    private int rank;
    private Long athleteId;
    private String athleteName;
    private CountryCode countryCode;
    private long total;
    private long gold;
    private long silver;
    private long bronze;
}
