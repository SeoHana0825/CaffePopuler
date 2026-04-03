package com.example.caffepopularproject.domain.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuRankInfo {

    private final String menuName;
    private final Long quantity;
}
