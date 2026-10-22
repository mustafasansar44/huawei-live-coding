package com.huawei.olympicranking.service;

import com.huawei.olympicranking.dto.AddSportRequestDto;
import com.huawei.olympicranking.dto.SportRankingResponseDto;
import com.huawei.olympicranking.dto.SportResponseDto;
import com.huawei.olympicranking.model.Sport;
import com.huawei.olympicranking.repository.MedalRecordsRepository;
import com.huawei.olympicranking.repository.SportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SportService {

    private final SportRepository sportRepository;
    private final MedalRecordsRepository medalRecordsRepository;

    public SportService(SportRepository sportRepository, MedalRecordsRepository medalRecordsRepository) {
        this.sportRepository = sportRepository;
        this.medalRecordsRepository = medalRecordsRepository;
    }

    public SportResponseDto createSport(AddSportRequestDto requestDto) {
        if (sportRepository.existsByNameIgnoreCase(requestDto.getName())) {
            throw new IllegalArgumentException("Sport with name " + requestDto.getName() + " already exists");
        }

        Sport sport = Sport.builder()
                .name(requestDto.getName().trim())
                .build();

        Sport saved = sportRepository.save(sport);
        return new SportResponseDto(saved.getId(), saved.getName());
    }

    @Transactional(readOnly = true)
    public List<SportResponseDto> getAllSports() {
        return sportRepository.findAll().stream()
                .map(s -> new SportResponseDto(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SportRankingResponseDto> getSportRanking() {
        List<Sport> sports = sportRepository.findAll();

        List<SportRankingResponseDto> rankedSports = sports.stream()
                .map(sport -> {
                    long totalMedals = medalRecordsRepository.findBySportId(sport.getId()).size();
                    return SportRankingResponseDto.builder()
                            .sportId(sport.getId())
                            .sportName(sport.getName())
                            .total(totalMedals)
                            .build();
                })
                .sorted(
                        Comparator.comparingLong(SportRankingResponseDto::getTotal).reversed()
                                .thenComparing(SportRankingResponseDto::getSportName, String.CASE_INSENSITIVE_ORDER)
                )
                .collect(Collectors.toList());

        int rank = 1;
        for (SportRankingResponseDto dto : rankedSports) {
            dto.setRank(rank++);
        }

        return rankedSports;
    }
}
