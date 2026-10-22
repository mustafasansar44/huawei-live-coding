package com.huawei.olympicranking.controller;

import com.huawei.olympicranking.dto.AddCountryRequestDto;
import com.huawei.olympicranking.dto.CountryResponseDto;
import com.huawei.olympicranking.dto.GetCountryStatisticResponseDto;
import com.huawei.olympicranking.model.enums.RankingStrategy;
import com.huawei.olympicranking.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping("/countries")
    public ResponseEntity<CountryResponseDto> createCountry(@Valid @RequestBody AddCountryRequestDto requestDto) {
        CountryResponseDto response = countryService.createCountry(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponseDto>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @GetMapping("/rankings/countries")
    public ResponseEntity<List<GetCountryStatisticResponseDto>> getCountryRankings(
            @RequestParam(name = "strategy", required = false, defaultValue = "OFFICIAL") RankingStrategy strategy) {
        return ResponseEntity.ok(countryService.getCountryStatistic(strategy));
    }
}
