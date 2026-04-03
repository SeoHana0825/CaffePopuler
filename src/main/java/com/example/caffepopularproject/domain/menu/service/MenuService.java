package com.example.caffepopularproject.domain.menu.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.menu.dto.request.MenuCreateRequest;
import com.example.caffepopularproject.domain.menu.dto.response.MenuResponse;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import com.example.caffepopularproject.domain.menu.dto.response.MenuRankingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private static final String RANKING_KEY_PREFIX = "menu_rank:";
    private final MenuRepository menuRepository;
    private final StringRedisTemplate stringRedisTemplate;

    /* 메뉴 API
    * 1. 메뉴 생성
    * 2. 메뉴 단건 조회
    * 3. 메뉴 목록 조회
    * 4. 인기 메뉴 조회
    * */

    @Transactional
    public MenuResponse createMenu (MenuCreateRequest request) {

        if (request.getPrice() < 0 || request.getStock() < 0) {
            throw new ServiceException(ErrorCode.INVALID_MENU_VALUE);
        }
        Menu menu = Menu.register(
                request.getName(),
                request.getPrice(),
                request.getStock()
        );

        Menu saveMenu = menuRepository.save(menu);

        return MenuResponse.from(saveMenu);
    }

    @Transactional(readOnly = true)
    public MenuResponse getMenu (Long id) {

        Menu menu = menuRepository.findById (id)
                .orElseThrow(() -> new ServiceException(ErrorCode.MENU_NOT_FOUND));

        return MenuResponse.from(menu);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenu () {
        List<Menu> menus = menuRepository.findAll();

        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    public List<MenuRankingResponse> findTop3MenuInLast7Days (LocalDate standardDate) {
        List<String> keys = List.of(
                RANKING_KEY_PREFIX + standardDate.toString(),
                RANKING_KEY_PREFIX + standardDate.minusDays(1).toString(),
                RANKING_KEY_PREFIX + standardDate.minusDays(2).toString(),
                RANKING_KEY_PREFIX + standardDate.minusDays(3).toString(),
                RANKING_KEY_PREFIX + standardDate.minusDays(4).toString(),
                RANKING_KEY_PREFIX + standardDate.minusDays(5).toString(),
                RANKING_KEY_PREFIX + standardDate.minusDays(6).toString()
        );

        log.info("조회할 Redis 키 목록: " +keys);
        // 결과가 저장될 임시 키
        String destKey = "menu_rank:last7Days:" + standardDate;

        // 결과 통합
        stringRedisTemplate.opsForZSet().unionAndStore(
                keys.get(0), // 중심이 되는 Zset의 이름
                keys.subList(1, keys.size()), // 나머지 Zset
                destKey
        );

        // 통합 결과 키가 영구적으로 남지 않도록 10분 뒤 자동 만료 TTL
        stringRedisTemplate.expire(destKey, java.time.Duration.ofMinutes(10));

        // 합친 값 조회
        Set<ZSetOperations.TypedTuple<String>> result = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(destKey, 0, 2);

        if (result == null || result.isEmpty()) {
            return Collections.emptyList();
        }

        return result.stream()
                .map(tuple -> new MenuRankingResponse(tuple.getValue(), tuple.getScore()))
                .toList();

    }
}
