package com.huawei.olympicranking.service;

import com.huawei.olympicranking.dto.AddCountryRequestDto;
import com.huawei.olympicranking.dto.CountryResponseDto;
import com.huawei.olympicranking.dto.GetCountryStatisticResponseDto;
import com.huawei.olympicranking.model.Country;
import com.huawei.olympicranking.model.enums.MedalType;
import com.huawei.olympicranking.model.enums.RankingStrategy;
import com.huawei.olympicranking.repository.CountryRepository;
import com.huawei.olympicranking.service.strategy.CountryRankingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final Map<RankingStrategy, CountryRankingStrategy> rankingStrategies;

    public CountryService(CountryRepository countryRepository,
                          List<CountryRankingStrategy> strategyList) {
        this.countryRepository = countryRepository;
        this.rankingStrategies = strategyList.stream()
                .collect(Collectors.toMap(CountryRankingStrategy::getStrategyType, Function.identity()));
    }

    public CountryResponseDto createCountry(AddCountryRequestDto requestDto) {
        if (countryRepository.existsByCountryCode(requestDto.getCountryCode())) {
            throw new IllegalArgumentException("Country with code " + requestDto.getCountryCode() + " already exists");
        }

        Country country = Country.builder()
                .countryCode(requestDto.getCountryCode())
                .name(requestDto.getName().trim())
                .build();

        Country saved = countryRepository.save(country);
        return new CountryResponseDto(saved.getId(), saved.getCountryCode(), saved.getName());
    }

    @Transactional(readOnly = true)
    public List<CountryResponseDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(c -> new CountryResponseDto(c.getId(), c.getCountryCode(), c.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetCountryStatisticResponseDto> getCountryStatistic() {
        return getCountryStatistic(RankingStrategy.OFFICIAL);
    }

    @Transactional(readOnly = true)
    public List<GetCountryStatisticResponseDto> getCountryStatistic(RankingStrategy strategy) {
        // NOTE: Veri seti buyurse N+1 olusmamasi adina @EntityGraph veya JOIN FETCH ile optimize edilebilir.
        // Proje olcegi ve live coding kapsaminda kod okunabilirligi icin dogrudan stream uzerinden hesaplandi.
        List<Country> countries = countryRepository.findAll();

        List<GetCountryStatisticResponseDto> rawStatistics = countries.stream()
                .map(country -> {
                    long goldCount = country.getMedalRecords() != null ? country.getMedalRecords().stream()
                            .filter(m -> m.getMedalType() == MedalType.GOLD).count() : 0L;

                    long silverCount = country.getMedalRecords() != null ? country.getMedalRecords().stream()
                            .filter(m -> m.getMedalType() == MedalType.SILVER).count() : 0L;

                    long bronzeCount = country.getMedalRecords() != null ? country.getMedalRecords().stream()
                            .filter(m -> m.getMedalType() == MedalType.BRONZE).count() : 0L;

                    return GetCountryStatisticResponseDto.builder()
                            .country(country.getCountryCode().name())
                            .gold(goldCount)
                            .silver(silverCount)
                            .bronze(bronzeCount)
                            .total(goldCount + silverCount + bronzeCount)
                            .build();
                })
                .collect(Collectors.toList());
        // rank strategy'i ayrı bir yapıya taşıdım. Mülakat öncesi burası sadece 1 taneydi. Sonradan bonus ekleyince yapı da temiz olsun diye böyle tercih ettim.
        CountryRankingStrategy rankingStrategy = rankingStrategies.getOrDefault(
                strategy != null ? strategy : RankingStrategy.OFFICIAL,
                rankingStrategies.get(RankingStrategy.OFFICIAL)
        );

        return rankingStrategy.rank(rawStatistics);
    }
}
