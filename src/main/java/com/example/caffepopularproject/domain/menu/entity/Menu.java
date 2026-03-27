package com.example.caffepopularproject.domain.menu.entity;

import com.example.caffepopularproject.common.entity.BaseDate;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "menus")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Menu extends BaseDate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long price;

    public static Menu register (
            String name,
            Long price
    ) {
        Menu menu = new Menu();

        menu.name = name;
        menu.price = price;

        return menu;
    }
}
