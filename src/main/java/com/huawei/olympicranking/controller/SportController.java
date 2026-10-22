package com.huawei.olympicranking.controller;

import com.huawei.olympicranking.dto.AddSportRequestDto;
import com.huawei.olympicranking.dto.SportRankingResponseDto;
import com.huawei.olympicranking.dto.SportResponseDto;
import com.huawei.olympicranking.service.SportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SportController {

    private final SportService sportService;

    public SportController(SportService sportService) {
        this.sportService = sportService;
    }

    @PostMapping("/sports")
    public ResponseEntity<SportResponseDto> createSport(@Valid @RequestBody AddSportRequestDto requestDto) {
        SportResponseDto response = sportService.createSport(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/sports")
    public ResponseEntity<List<SportResponseDto>> getAllSports() {
        return ResponseEntity.ok(sportService.getAllSports());
    }

    @GetMapping("/rankings/sports")
    public ResponseEntity<List<SportRankingResponseDto>> getSportRankings() {
        return ResponseEntity.ok(sportService.getSportRanking());
    }
}
