package com.huawei.olympicranking.service.strategy;

import com.huawei.olympicranking.dto.GetCountryStatisticResponseDto;
import com.huawei.olympicranking.model.enums.RankingStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


// Mülakat sonrası yapıldı önceden metodun içine gömülüydü.
@Component
public class OfficialCountryRankingStrategy implements CountryRankingStrategy {

    @Override
    public RankingStrategy getStrategyType() {
        return RankingStrategy.OFFICIAL;
    }

    @Override
    public List<GetCountryStatisticResponseDto> rank(List<GetCountryStatisticResponseDto> statistics) {
        List<GetCountryStatisticResponseDto> sorted = statistics.stream()
                .sorted(
                        Comparator.comparingLong(GetCountryStatisticResponseDto::getGold).reversed()
                                .thenComparing(Comparator.comparingLong(GetCountryStatisticResponseDto::getSilver).reversed())
                                .thenComparing(Comparator.comparingLong(GetCountryStatisticResponseDto::getBronze).reversed())
                                .thenComparing(GetCountryStatisticResponseDto::getCountry)
                )
                .collect(Collectors.toList());

        int rank = 1;
        for (GetCountryStatisticResponseDto dto : sorted) {
            dto.setRank(rank++);
        }
        return sorted;
    }
}
