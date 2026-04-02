package com.example.caffepopularproject.domain.payment.listener;

import com.example.caffepopularproject.domain.payment.dto.OrderCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class DataPlatformMockListener {

    /* 비동기 처리를 위한 이벤트 Mock API 리스너 설정
     * @Async : 별도 스레드에서 비동기로 동작
     * @TransactionalEventListener : DB 커밋 완료 후에만 동기화
     */

    @Async("dataPlatformTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToDataPlatform(OrderCompletedEvent event) {
        log.info("[Mock API] 데이터 플랫폼 전송 시작 - 주문번호 {}", event.getOrderNo());

        try {
            // 외부 API 통신 지연 (2초 대기)
            Thread.sleep(2000);

            log.info("[Mock API] 데이터 플랫폼 전송 완료, 회원: {}, 결제금액: {}원", event.getUserId(),event.getPaymentAmount());
        } catch (InterruptedException e) {
            log.error("[Mock API] 전송 중 예외 발생", e);
        } catch (Exception e) {
            // 전송에 실패해도 원래의 결제 로직은 이미 저장되어있어서 롤백되지 않음
            log.error("[Mock API] 전송 실패", e);
        }
    }
}
