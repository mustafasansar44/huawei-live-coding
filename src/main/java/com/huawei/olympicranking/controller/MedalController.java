package com.huawei.olympicranking.controller;

import com.huawei.olympicranking.dto.AddMedalRequestDto;
import com.huawei.olympicranking.dto.MedalRecordResponseDto;
import com.huawei.olympicranking.service.MedalService;
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
public class MedalController {

    private final MedalService medalService;

    public MedalController(MedalService medalService) {
        this.medalService = medalService;
    }

    @PostMapping("/medals")
    public ResponseEntity<MedalRecordResponseDto> recordMedal(@Valid @RequestBody AddMedalRequestDto requestDto) {
        MedalRecordResponseDto response = medalService.recordMedal(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/medals")
    public ResponseEntity<List<MedalRecordResponseDto>> getAllMedals() {
        return ResponseEntity.ok(medalService.getAllMedals());
    }
}
