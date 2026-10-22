package com.huawei.olympicranking.dto;

import com.huawei.olympicranking.model.enums.CountryCode;
import com.huawei.olympicranking.model.enums.MedalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedalRecordResponseDto {
    private Long id;
    private Long athleteId;
    private String athleteName;
    private Long sportId;
    private String sportName;
    private Long countryId;
    private CountryCode countryCode;
    private MedalType medalType;
}
