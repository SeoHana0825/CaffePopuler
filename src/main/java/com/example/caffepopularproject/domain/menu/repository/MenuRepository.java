package com.example.caffepopularproject.domain.menu.repository;

import com.example.caffepopularproject.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository <Menu, Long> {
}
