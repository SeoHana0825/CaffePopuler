package com.example.caffepopularproject.domain.menu.dto.response;

import com.example.caffepopularproject.domain.menu.entity.Menu;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class MenuResponse {

    private Long id;
    private String name;
    private Long price;
    private Long stock;

    public static MenuResponse from (Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getStock()
        );
    }
}
