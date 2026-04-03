package com.example.caffepopularproject.domain.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuRankingResponse {

    private String title;
    private double score;
}
