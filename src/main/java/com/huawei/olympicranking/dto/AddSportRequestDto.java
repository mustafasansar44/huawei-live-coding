package com.huawei.olympicranking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSportRequestDto {

    @NotBlank(message = "Sport name cannot be blank")
    private String name;
}
