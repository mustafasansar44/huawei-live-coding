package com.huawei.olympicranking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCountryStatisticResponseDto {
    private int rank;
    private String country;
    private long gold;
    private long silver;
    private long bronze;
    private long total;
    private Long points;
}
