package com.example.caffepopularproject.domain.menu.listener;

import com.example.caffepopularproject.domain.menu.dto.response.MenuRankInfo;
import com.example.caffepopularproject.domain.order.entity.OrderItem;
import com.example.caffepopularproject.domain.payment.dto.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuRankingListener {

    private static final String RANKING_KEY_PREFIX = "menu_rank:";
    private final StringRedisTemplate stringRedisTemplate;

    @Async("dataPlatformTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateMenuRanking(OrderCompletedEvent event) {

        //Time Window 개념 : 오늘 날짜를 기준으로 키 생성
        String key = RANKING_KEY_PREFIX + LocalDate.now();

        // 주문한 메뉴들의 수량만큼 점수 증가
        for (MenuRankInfo info : event.getMenuRankInfos()) {
            stringRedisTemplate.opsForZSet().incrementScore(key, info.getMenuName(), info.getQuantity());
            log.info("[Redis 랭킹 업데이트] 메뉴: {}, 수량: {},키: {}", info.getMenuName(),info.getQuantity(),key);
        }
    }
}
