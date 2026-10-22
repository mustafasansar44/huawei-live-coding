package com.huawei.olympicranking.service;

import com.huawei.olympicranking.dto.AddMedalRequestDto;
import com.huawei.olympicranking.dto.MedalRecordResponseDto;
import com.huawei.olympicranking.exception.DuplicateMedalException;
import com.huawei.olympicranking.exception.ResourceNotFoundException;
import com.huawei.olympicranking.model.Athlete;
import com.huawei.olympicranking.model.Country;
import com.huawei.olympicranking.model.MedalRecords;
import com.huawei.olympicranking.model.Sport;
import com.huawei.olympicranking.repository.AthleteRepository;
import com.huawei.olympicranking.repository.CountryRepository;
import com.huawei.olympicranking.repository.MedalRecordsRepository;
import com.huawei.olympicranking.repository.SportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedalService {

    private final MedalRecordsRepository medalRecordsRepository;
    private final AthleteRepository athleteRepository;
    private final SportRepository sportRepository;
    private final CountryRepository countryRepository;

    public MedalService(MedalRecordsRepository medalRecordsRepository,
                        AthleteRepository athleteRepository,
                        SportRepository sportRepository,
                        CountryRepository countryRepository) {
        this.medalRecordsRepository = medalRecordsRepository;
        this.athleteRepository = athleteRepository;
        this.sportRepository = sportRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional
    public MedalRecordResponseDto recordMedal(AddMedalRequestDto requestDto) {
        Athlete athlete = athleteRepository.findById(requestDto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + requestDto.getAthleteId()));

        Sport sport = sportRepository.findById(requestDto.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found with id: " + requestDto.getSportId()));

        Country country = countryRepository.findById(requestDto.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + requestDto.getCountryId()));
        if (medalRecordsRepository.existsByAthleteIdAndSportIdAndMedalType(
                athlete.getId(), sport.getId(), requestDto.getMedalType())) {
            throw new DuplicateMedalException("Medal record already exists for athlete " + athlete.getName() +
                    " in sport " + sport.getName() + " with medal type " + requestDto.getMedalType());
        }

        MedalRecords medal = MedalRecords.builder()
                .athlete(athlete)
                .sport(sport)
                .country(country)
                .medalType(requestDto.getMedalType())
                .build();

        MedalRecords saved = medalRecordsRepository.save(medal);

        return MedalRecordResponseDto.builder()
                .id(saved.getId())
                .athleteId(athlete.getId())
                .athleteName(athlete.getName())
                .sportId(sport.getId())
                .sportName(sport.getName())
                .countryId(country.getId())
                .countryCode(country.getCountryCode())
                .medalType(saved.getMedalType())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MedalRecordResponseDto> getAllMedals() {
        return medalRecordsRepository.findAll().stream()
                .map(m -> MedalRecordResponseDto.builder()
                        .id(m.getId())
                        .athleteId(m.getAthlete().getId())
                        .athleteName(m.getAthlete().getName())
                        .sportId(m.getSport().getId())
                        .sportName(m.getSport().getName())
                        .countryId(m.getCountry().getId())
                        .countryCode(m.getCountry().getCountryCode())
                        .medalType(m.getMedalType())
                        .build())
                .collect(Collectors.toList());
    }
}
