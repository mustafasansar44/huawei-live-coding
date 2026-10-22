package com.huawei.olympicranking.service;

import com.huawei.olympicranking.dto.AddAthleteRequestDto;
import com.huawei.olympicranking.dto.AthleteRankingResponseDto;
import com.huawei.olympicranking.dto.AthleteResponseDto;
import com.huawei.olympicranking.exception.ResourceNotFoundException;
import com.huawei.olympicranking.model.Athlete;
import com.huawei.olympicranking.model.Country;
import com.huawei.olympicranking.model.MedalRecords;
import com.huawei.olympicranking.model.enums.MedalType;
import com.huawei.olympicranking.repository.AthleteRepository;
import com.huawei.olympicranking.repository.CountryRepository;
import com.huawei.olympicranking.repository.MedalRecordsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final CountryRepository countryRepository;
    private final MedalRecordsRepository medalRecordsRepository;

    private static final Comparator<AthleteRankingResponseDto> ATHLETE_RANKING_COMPARATOR =
            Comparator.comparingLong(AthleteRankingResponseDto::getTotal).reversed()
                    .thenComparing(Comparator.comparingLong(AthleteRankingResponseDto::getGold).reversed())
                    .thenComparing(Comparator.comparingLong(AthleteRankingResponseDto::getSilver).reversed())
                    .thenComparing(Comparator.comparingLong(AthleteRankingResponseDto::getBronze).reversed())
                    .thenComparing(AthleteRankingResponseDto::getAthleteName, String.CASE_INSENSITIVE_ORDER);

    public AthleteService(AthleteRepository athleteRepository,
                          CountryRepository countryRepository,
                          MedalRecordsRepository medalRecordsRepository) {
        this.athleteRepository = athleteRepository;
        this.countryRepository = countryRepository;
        this.medalRecordsRepository = medalRecordsRepository;
    }

    public AthleteResponseDto createAthlete(AddAthleteRequestDto requestDto) {
        Country country = countryRepository.findById(requestDto.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + requestDto.getCountryId()));

        Athlete athlete = Athlete.builder()
                .name(requestDto.getName().trim())
                .country(country)
                .build();

        Athlete saved = athleteRepository.save(athlete);
        return new AthleteResponseDto(
                saved.getId(),
                saved.getName(),
                country.getId(),
                country.getCountryCode(),
                country.getName()
        );
    }

    @Transactional(readOnly = true)
    public List<AthleteResponseDto> getAllAthletes() {
        return athleteRepository.findAll().stream()
                .map(a -> new AthleteResponseDto(
                        a.getId(),
                        a.getName(),
                        a.getCountry().getId(), // Transaction olma sebebi getCountry derken hibernate session devam etmeli ki lazy exception almayalım.
                        a.getCountry().getCountryCode(),
                        a.getCountry().getName()
                ))
                .collect(Collectors.toList());
    }

    // Standart method.
    @Transactional(readOnly = true)
    public List<AthleteRankingResponseDto> getAthleteRanking() {
        List<AthleteRankingResponseDto> rankedList = calculateAthleteStatistics().stream()
                .sorted(ATHLETE_RANKING_COMPARATOR)
                .collect(Collectors.toList());

        assignRanks(rankedList);
        return rankedList;
    }

    // Bonus ister. (Mülakat sonrası yapıldı.)
    @Transactional(readOnly = true)
    public List<AthleteRankingResponseDto> getTopNAthletes(int top) {
        if (top <= 0) {
            return getAthleteRanking();
        }

        List<AthleteRankingResponseDto> athleteList = calculateAthleteStatistics();

        Comparator<AthleteRankingResponseDto> minHeapComparator = ATHLETE_RANKING_COMPARATOR.reversed();
        PriorityQueue<AthleteRankingResponseDto> minHeap = new PriorityQueue<>(top, minHeapComparator);

        for (AthleteRankingResponseDto dto : athleteList) {
            if (minHeap.size() < top) {
                minHeap.offer(dto);
            } else if (ATHLETE_RANKING_COMPARATOR.compare(dto, minHeap.peek()) < 0) {
                minHeap.poll();
                minHeap.offer(dto);
            }
        }

        List<AthleteRankingResponseDto> topNList = new ArrayList<>(minHeap);
        topNList.sort(ATHLETE_RANKING_COMPARATOR);

        assignRanks(topNList);
        return topNList;
    }

    @Transactional(readOnly = true)
    public List<AthleteRankingResponseDto> getAthleteRanking(Integer top) {
        if (top != null && top > 0) {
            return getTopNAthletes(top);
        }
        return getAthleteRanking();
    }

    private List<AthleteRankingResponseDto> calculateAthleteStatistics() {
        // büyük verilerde döngüde sorgu atarken join fetch/grouping by vs. sorgusu yazilabilir.
        // Proje şu anlık küçük olarak varsaydığım için n+1'leri görmezden geliyorum.
        return athleteRepository.findAll().stream()
                .map(athlete -> {
                    List<MedalRecords> medals = medalRecordsRepository.findByAthleteId(athlete.getId());
                    long goldCount = medals.stream().filter(m -> m.getMedalType() == MedalType.GOLD).count();
                    long silverCount = medals.stream().filter(m -> m.getMedalType() == MedalType.SILVER).count();
                    long bronzeCount = medals.stream().filter(m -> m.getMedalType() == MedalType.BRONZE).count();
                    long total = goldCount + silverCount + bronzeCount;

                    return AthleteRankingResponseDto.builder()
                            .athleteId(athlete.getId())
                            .athleteName(athlete.getName())
                            .countryCode(athlete.getCountry().getCountryCode())
                            .total(total)
                            .gold(goldCount)
                            .silver(silverCount)
                            .bronze(bronzeCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private void assignRanks(List<AthleteRankingResponseDto> list) {
        int rank = 1;
        for (AthleteRankingResponseDto dto : list) {
            dto.setRank(rank++);
        }
    }
}
