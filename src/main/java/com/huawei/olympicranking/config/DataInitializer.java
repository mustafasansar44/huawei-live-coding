package com.huawei.olympicranking.config;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final SportRepository sportRepository;
    private final AthleteRepository athleteRepository;
    private final MedalRecordsRepository medalRecordsRepository;

    @Override
    public void run(String... args) {
        if (countryRepository.count() > 0) {
            return;
        }

        log.info("Initializing sample Olympic data from README...");
        Country tur = countryRepository.save(Country.builder().countryCode(CountryCode.TUR).name("Türkiye").build());
        Country usa = countryRepository.save(Country.builder().countryCode(CountryCode.USA).name("United States").build());
        Country jpn = countryRepository.save(Country.builder().countryCode(CountryCode.JPN).name("Japan").build());
        Sport archery = sportRepository.save(Sport.builder().name("Archery").build());
        Sport swimming = sportRepository.save(Sport.builder().name("Swimming").build());
        Sport judo = sportRepository.save(Sport.builder().name("Judo").build());
        Athlete mete = athleteRepository.save(Athlete.builder().name("Mete Gazoz").country(tur).build());
        Athlete noah = athleteRepository.save(Athlete.builder().name("Noah Lyles").country(usa).build());
        Athlete abby = athleteRepository.save(Athlete.builder().name("Abby Steuter").country(usa).build());
        Athlete hifumi = athleteRepository.save(Athlete.builder().name("Hifumi Abe").country(jpn).build());
        medalRecordsRepository.saveAll(List.of(
                MedalRecords.builder().athlete(mete).sport(archery).country(tur).medalType(MedalType.GOLD).build(),
                MedalRecords.builder().athlete(mete).sport(archery).country(tur).medalType(MedalType.SILVER).build(),
                MedalRecords.builder().athlete(noah).sport(swimming).country(usa).medalType(MedalType.GOLD).build(),
                MedalRecords.builder().athlete(noah).sport(swimming).country(usa).medalType(MedalType.BRONZE).build(),
                MedalRecords.builder().athlete(hifumi).sport(judo).country(jpn).medalType(MedalType.GOLD).build()
        ));

        log.info("Sample Olympic data successfully loaded!");
    }
}
