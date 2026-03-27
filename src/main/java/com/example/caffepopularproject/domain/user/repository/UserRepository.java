package com.example.caffepopularproject.domain.user.repository;

import com.example.caffepopularproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Long> {
}
