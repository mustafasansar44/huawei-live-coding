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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CountryRankingIntegrationTest {

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
    @DisplayName("Official Olympic Rule: USA (2G, 1S) vs TUR (1G, 3S) -> USA #1 (more gold), TUR #2")
    void shouldReturnCountriesRankedByGoldFirst() throws Exception {
        Country usa = countryRepository.save(Country.builder().countryCode(CountryCode.USA).name("United States").build());
        Country tur = countryRepository.save(Country.builder().countryCode(CountryCode.TUR).name("Türkiye").build());

        Sport sport1 = sportRepository.save(Sport.builder().name("Athletics").build());
        Sport sport2 = sportRepository.save(Sport.builder().name("Swimming").build());
        Sport sport3 = sportRepository.save(Sport.builder().name("Gymnastics").build());
        Sport sport4 = sportRepository.save(Sport.builder().name("Archery").build());

        Athlete athleteUsa = athleteRepository.save(Athlete.builder().name("USA Athlete").country(usa).build());
        Athlete athleteTur = athleteRepository.save(Athlete.builder().name("TUR Athlete").country(tur).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport1).country(usa).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport2).country(usa).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport3).country(usa).medalType(MedalType.SILVER).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport1).country(tur).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport2).country(tur).medalType(MedalType.SILVER).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport3).country(tur).medalType(MedalType.SILVER).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport4).country(tur).medalType(MedalType.SILVER).build());
        mockMvc.perform(get("/api/rankings/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country", is("USA")))
                .andExpect(jsonPath("$[0].rank", is(1)))
                .andExpect(jsonPath("$[0].gold", is(2)))
                .andExpect(jsonPath("$[0].silver", is(1)))
                .andExpect(jsonPath("$[1].country", is("TUR")))
                .andExpect(jsonPath("$[1].rank", is(2)))
                .andExpect(jsonPath("$[1].gold", is(1)))
                .andExpect(jsonPath("$[1].silver", is(3)));
    }

    @Test
    @DisplayName("Tie-break by Silver: Equal Gold -> Silver breaks tie (USA 1G, 1S vs CHN 1G, 0S)")
    void shouldBreakTieBySilverMedal() throws Exception {
        Country usa = countryRepository.save(Country.builder().countryCode(CountryCode.USA).name("United States").build());
        Country chn = countryRepository.save(Country.builder().countryCode(CountryCode.CHN).name("China").build());

        Sport sport1 = sportRepository.save(Sport.builder().name("Sport A").build());
        Sport sport2 = sportRepository.save(Sport.builder().name("Sport B").build());

        Athlete athleteUsa = athleteRepository.save(Athlete.builder().name("USA Athlete").country(usa).build());
        Athlete athleteChn = athleteRepository.save(Athlete.builder().name("CHN Athlete").country(chn).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport1).country(usa).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport2).country(usa).medalType(MedalType.SILVER).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteChn).sport(sport1).country(chn).medalType(MedalType.GOLD).build());
        mockMvc.perform(get("/api/rankings/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country", is("USA")))
                .andExpect(jsonPath("$[0].rank", is(1)))
                .andExpect(jsonPath("$[1].country", is("CHN")))
                .andExpect(jsonPath("$[1].rank", is(2)));
    }

    @Test
    @DisplayName("Bonus 1 Weighted Strategy: TUR (1G, 3S = 3 + 6 = 9 pts) vs USA (2G, 1S = 6 + 2 = 8 pts) -> TUR #1 (more points), USA #2")
    void shouldRankByWeightedStrategy() throws Exception {
        Country usa = countryRepository.save(Country.builder().countryCode(CountryCode.USA).name("United States").build());
        Country tur = countryRepository.save(Country.builder().countryCode(CountryCode.TUR).name("Türkiye").build());

        Sport sport1 = sportRepository.save(Sport.builder().name("Weight Sport 1").build());
        Sport sport2 = sportRepository.save(Sport.builder().name("Weight Sport 2").build());
        Sport sport3 = sportRepository.save(Sport.builder().name("Weight Sport 3").build());
        Sport sport4 = sportRepository.save(Sport.builder().name("Weight Sport 4").build());

        Athlete athleteUsa = athleteRepository.save(Athlete.builder().name("USA Weight Athlete").country(usa).build());
        Athlete athleteTur = athleteRepository.save(Athlete.builder().name("TUR Weight Athlete").country(tur).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport1).country(usa).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport2).country(usa).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteUsa).sport(sport3).country(usa).medalType(MedalType.SILVER).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport1).country(tur).medalType(MedalType.GOLD).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport2).country(tur).medalType(MedalType.SILVER).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport3).country(tur).medalType(MedalType.SILVER).build());
        medalRecordsRepository.save(MedalRecords.builder().athlete(athleteTur).sport(sport4).country(tur).medalType(MedalType.SILVER).build());
        mockMvc.perform(get("/api/rankings/countries?strategy=WEIGHTED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country", is("TUR")))
                .andExpect(jsonPath("$[0].rank", is(1)))
                .andExpect(jsonPath("$[0].points", is(9)))
                .andExpect(jsonPath("$[1].country", is("USA")))
                .andExpect(jsonPath("$[1].rank", is(2)))
                .andExpect(jsonPath("$[1].points", is(8)));
    }
}
