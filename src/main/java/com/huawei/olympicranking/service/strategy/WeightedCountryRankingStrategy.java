package com.huawei.olympicranking.service.strategy;

import com.huawei.olympicranking.dto.GetCountryStatisticResponseDto;
import com.huawei.olympicranking.model.enums.RankingStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Mülakat sonrası yapıldı önceden metodun içine gömülüydü.
@Component
public class WeightedCountryRankingStrategy implements CountryRankingStrategy {
    public static final long GOLD_POINTS = 3L;
    public static final long SILVER_POINTS = 2L;
    public static final long BRONZE_POINTS = 1L;

    @Override
    public RankingStrategy getStrategyType() {
        return RankingStrategy.WEIGHTED;
    }

    @Override
    public List<GetCountryStatisticResponseDto> rank(List<GetCountryStatisticResponseDto> statistics) {
        List<GetCountryStatisticResponseDto> sorted = statistics.stream()
                .peek(dto -> {
                    long points = (dto.getGold() * GOLD_POINTS)
                            + (dto.getSilver() * SILVER_POINTS)
                            + (dto.getBronze() * BRONZE_POINTS);
                    dto.setPoints(points);
                })
                .sorted(
                        Comparator.comparingLong(GetCountryStatisticResponseDto::getPoints).reversed()
                                .thenComparing(Comparator.comparingLong(GetCountryStatisticResponseDto::getGold).reversed())
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
