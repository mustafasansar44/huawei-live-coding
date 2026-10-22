package com.huawei.olympicranking.integration;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AthleteRankingIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private MedalRecordsRepository medalRecordsRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        medalRecordsRepository.deleteAll();
        athleteRepository.deleteAll();
        sportRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    @DisplayName("Athlete Ranking Rules: Total (desc) > Gold (desc) > Silver (desc) > Bronze (desc) > Name (asc)")
    void shouldRankAthletesByRules() throws Exception {
        Country tur = countryRepository.save(Country.builder().countryCode(CountryCode.TUR).name("Türkiye").build());
        Country usa = countryRepository.save(Country.builder().countryCode(CountryCode.USA).name("United States").build());

        Sport archery = sportRepository.save(Sport.builder().name("Archery").build());
        Sport swimming = sportRepository.save(Sport.builder().name("Swimming").build());
        Sport judo = sportRepository.save(Sport.builder().name("Judo").build());
        Athlete athleteA = athleteRepository.save(Athlete.builder().name("Athlete A").country(tur).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteA).sport(archery).country(tur).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteA).sport(archery).country(tur).medalType(MedalType.SILVER).build());
        Athlete athleteB = athleteRepository.save(Athlete.builder().name("Athlete B").country(usa).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteB).sport(swimming).country(usa).medalType(MedalType.GOLD).build());

        mockMvc.perform(get("/api/rankings/athletes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].athleteName", is("Athlete A")))
                .andExpect(jsonPath("$[0].rank", is(1)))
                .andExpect(jsonPath("$[0].total", is(2)))
                .andExpect(jsonPath("$[1].athleteName", is("Athlete B")))
                .andExpect(jsonPath("$[1].rank", is(2)))
                .andExpect(jsonPath("$[1].total", is(1)));
    }

    @Test
    @DisplayName("Bonus 2 Top-N Query: Given 4 athletes, return only top 2 using PriorityQueue Min-Heap")
    void shouldReturnTopNAthletesUsingPriorityQueue() throws Exception {
        Country tur = countryRepository.save(Country.builder().countryCode(CountryCode.TUR).name("Türkiye").build());
        Sport sport1 = sportRepository.save(Sport.builder().name("Sport 1").build());
        Sport sport2 = sportRepository.save(Sport.builder().name("Sport 2").build());
        Sport sport3 = sportRepository.save(Sport.builder().name("Sport 3").build());
        Athlete a1 = athleteRepository.save(Athlete.builder().name("Top 1 Athlete").country(tur).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(a1).sport(sport1).country(tur).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(a1).sport(sport2).country(tur).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(a1).sport(sport3).country(tur).medalType(MedalType.GOLD).build());
        Athlete a2 = athleteRepository.save(Athlete.builder().name("Top 2 Athlete").country(tur).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(a2).sport(sport1).country(tur).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(a2).sport(sport2).country(tur).medalType(MedalType.SILVER).build());
        Athlete a3 = athleteRepository.save(Athlete.builder().name("Top 3 Athlete").country(tur).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(a3).sport(sport1).country(tur).medalType(MedalType.BRONZE).build());
        athleteRepository.save(Athlete.builder().name("No Medal Athlete").country(tur).build());
        mockMvc.perform(get("/api/rankings/athletes?top=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].athleteName", is("Top 1 Athlete")))
                .andExpect(jsonPath("$[0].rank", is(1)))
                .andExpect(jsonPath("$[0].total", is(3)))
                .andExpect(jsonPath("$[1].athleteName", is("Top 2 Athlete")))
                .andExpect(jsonPath("$[1].rank", is(2)))
                .andExpect(jsonPath("$[1].total", is(2)));
    }
}
