package com.huawei.olympicranking.service;

import com.huawei.olympicranking.dto.AddMedalRequestDto;
import com.huawei.olympicranking.dto.MedalRecordResponseDto;
import com.huawei.olympicranking.exception.DuplicateMedalException;
import com.huawei.olympicranking.exception.ResourceNotFoundException;
import com.huawei.olympicranking.model.Athlete;
import com.huawei.olympicranking.model.Country;
import com.huawei.olympicranking.model.MedalRecords;
import com.huawei.olympicranking.model.Sport;
import com.huawei.olympicranking.model.enums.CountryCode;
import com.huawei.olympicranking.model.enums.MedalType;
import com.huawei.olympicranking.repository.AthleteRepository;
import com.huawei.olympicranking.repository.CountryRepository;
import com.huawei.olympicranking.repository.MedalRecordsRepository;
import com.huawei.olympicranking.repository.SportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedalServiceTest {

    @Mock
    private MedalRecordsRepository medalRecordsRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private SportRepository sportRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private MedalService medalService;

    private Athlete athlete;
    private Sport sport;
    private Country country;
    private AddMedalRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        country = Country.builder()
                .countryCode(CountryCode.TUR)
                .name("Türkiye")
                .build();
        country.setId(1L);

        athlete = Athlete.builder()
                .name("Mete Gazoz")
                .country(country)
                .build();
        athlete.setId(10L);

        sport = Sport.builder()
                .name("Archery")
                .build();
        sport.setId(20L);

        validRequestDto = AddMedalRequestDto.builder()
                .athleteId(10L)
                .sportId(20L)
                .countryId(1L)
                .medalType(MedalType.GOLD)
                .build();
    }

    @Test
    @DisplayName("Should save medal when valid input")
    void shouldSaveMedal_WhenValidInput() {
        when(athleteRepository.findById(10L)).thenReturn(Optional.of(athlete));
        when(sportRepository.findById(20L)).thenReturn(Optional.of(sport));
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(medalRecordsRepository.existsByAthleteIdAndSportIdAndMedalType(10L, 20L, MedalType.GOLD))
                .thenReturn(false);

        MedalRecords savedMedal = MedalRecords.builder()
                .athlete(athlete)
                .sport(sport)
                .country(country)
                .medalType(MedalType.GOLD)
                .build();
        savedMedal.setId(100L);

        when(medalRecordsRepository.save(any(MedalRecords.class))).thenReturn(savedMedal);
        MedalRecordResponseDto response = medalService.recordMedal(validRequestDto);
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Mete Gazoz", response.getAthleteName());
        assertEquals("Archery", response.getSportName());
        assertEquals(CountryCode.TUR, response.getCountryCode());
        assertEquals(MedalType.GOLD, response.getMedalType());

        verify(medalRecordsRepository).save(any(MedalRecords.class));
    }

    @Test
    @DisplayName("Should throw DuplicateMedalException when duplicate medal exists")
    void shouldThrowException_WhenDuplicateMedal() {
        when(athleteRepository.findById(10L)).thenReturn(Optional.of(athlete));
        when(sportRepository.findById(20L)).thenReturn(Optional.of(sport));
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(medalRecordsRepository.existsByAthleteIdAndSportIdAndMedalType(10L, 20L, MedalType.GOLD))
                .thenReturn(true);
        DuplicateMedalException exception = assertThrows(DuplicateMedalException.class, () ->
                medalService.recordMedal(validRequestDto)
        );

        assertNotNull(exception.getMessage());
        verify(medalRecordsRepository, never()).save(any(MedalRecords.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when athlete not found")
    void shouldThrowException_WhenAthleteNotFound() {
        when(athleteRepository.findById(99L)).thenReturn(Optional.empty());

        AddMedalRequestDto invalidDto = AddMedalRequestDto.builder()
                .athleteId(99L)
                .sportId(20L)
                .countryId(1L)
                .medalType(MedalType.GOLD)
                .build();
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                medalService.recordMedal(invalidDto)
        );

        assertEquals("Athlete not found with id: 99", exception.getMessage());
        verify(medalRecordsRepository, never()).save(any(MedalRecords.class));
    }
}
