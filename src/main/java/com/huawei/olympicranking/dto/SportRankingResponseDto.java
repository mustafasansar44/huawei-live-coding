package com.huawei.olympicranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportRankingResponseDto {
    private int rank;
    private Long sportId;
    private String sportName;
    private long total;
}
