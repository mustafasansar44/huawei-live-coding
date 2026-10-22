package com.huawei.olympicranking.controller;

import com.huawei.olympicranking.dto.AddAthleteRequestDto;
import com.huawei.olympicranking.dto.AthleteRankingResponseDto;
import com.huawei.olympicranking.dto.AthleteResponseDto;
import com.huawei.olympicranking.service.AthleteService;
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
public class AthleteController {

    private final AthleteService athleteService;

    public AthleteController(AthleteService athleteService) {
        this.athleteService = athleteService;
    }

    @PostMapping("/athletes")
    public ResponseEntity<AthleteResponseDto> createAthlete(@Valid @RequestBody AddAthleteRequestDto requestDto) {
        AthleteResponseDto response = athleteService.createAthlete(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/athletes")
    public ResponseEntity<List<AthleteResponseDto>> getAllAthletes() {
        return ResponseEntity.ok(athleteService.getAllAthletes());
    }

    @GetMapping("/rankings/athletes")
    public ResponseEntity<List<AthleteRankingResponseDto>> getAthleteRankings(
            @RequestParam(name = "top", required = false) Integer top) {
        return ResponseEntity.ok(athleteService.getAthleteRanking(top));
    }
}
