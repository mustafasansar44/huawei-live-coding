package com.huawei.olympicranking.service.strategy;

import com.huawei.olympicranking.dto.GetCountryStatisticResponseDto;
import com.huawei.olympicranking.model.enums.RankingStrategy;

import java.util.List;

// Mülakat sonrası yapıldı
public interface CountryRankingStrategy {
    RankingStrategy getStrategyType();
    List<GetCountryStatisticResponseDto> rank(List<GetCountryStatisticResponseDto> statistics);
}
