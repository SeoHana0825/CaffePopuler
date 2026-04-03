package com.example.caffepopularproject.common.config;

import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import com.example.caffepopularproject.domain.payment.repository.PaymentRepository;
import com.example.caffepopularproject.domain.point.repository.PointRepository;
import com.example.caffepopularproject.domain.user.dto.request.SaveUserRequest;
import com.example.caffepopularproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataInitializer implements CommandLineRunner {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(String... args) throws Exception {
        if (menuRepository.count() == 0) {
            Menu menu1 = Menu.register("아메리카노",4000L, 100L);
            Menu menu2 = Menu.register("카페모카", 4500L, 100L);
            Menu menu3 = Menu.register("카페라테",4500L,100L);
            Menu menu4 = Menu.register("자몽블랙티", 5000L, 100L);
            Menu menu5 = Menu.register("아포카토", 7000L, 50L);
            log.info("기본 메뉴 셋팅 완료");
        }

        LocalDate today = LocalDate.now();
        for (int i = 0; i <= 6; i++) {
            String key = "menu_rank:" +today.minusDays(i).toString();

            if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
                stringRedisTemplate.opsForZSet().incrementScore(key, "아메리카노", 20.0);
                stringRedisTemplate.opsForZSet().incrementScore(key, "카페모카", 10.0);
                stringRedisTemplate.opsForZSet().incrementScore(key, "카페라테", 15.0);
                stringRedisTemplate.opsForZSet().incrementScore(key, "자몽블랙티", 17.0);
                stringRedisTemplate.opsForZSet().incrementScore(key, "아포카토", 2.0);
                log.info("Redis 7일치 랭킹 더미 데이터 세팅 완료 (기준일: {}), targetDate");
            }
        }
    }
}
