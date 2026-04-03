package com.example.caffepopularproject.domain.menu.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.menu.dto.request.MenuCreateRequest;
import com.example.caffepopularproject.domain.menu.dto.response.MenuRankingResponse;
import com.example.caffepopularproject.domain.menu.dto.response.MenuResponse;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 생성 성공")
    void createMene_Success() {

        // given
        MenuCreateRequest request = new MenuCreateRequest(
                "아메리카노",
                2000L,
                100L
        );

        given(menuRepository.save(any(Menu.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        menuService.createMenu(request);

        // then
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository, times(1)).save(menuCaptor.capture());

        Menu savedMenu = menuCaptor.getValue();
        
        assertThat(savedMenu.getName()).isEqualTo("아메리카노");
        assertThat(savedMenu.getPrice()).isEqualTo(2000L);
        assertThat(savedMenu.getStock()).isEqualTo(100L);
    }


    @Test
    @DisplayName("메뉴 생성 실패 - 가격이 음수일 경우 예외 발생")
    void createMenu_Fail_NegativePrice() {

        // given
        MenuCreateRequest request = new MenuCreateRequest(
                "아메리카노",
                -2000L,
                10L
        );

        // when&then
        assertThatThrownBy(() -> menuService.createMenu(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.INVALID_MENU_VALUE.getMessage());

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 생성 실패 - 재고가 음수일 경우 예외 발생")
    void createMenu_Fail_NegativeStock() {

        // given
        MenuCreateRequest request = new MenuCreateRequest(
                "아메리카노",
                2000L,
                -3L
        );

        // when&then
        assertThatThrownBy(() -> menuService.createMenu(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.INVALID_MENU_VALUE.getMessage());

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("인기 메뉴 조회 성공 - 7일치 데이텉 합산해 TOP 3 반환")
    void findTop3MenuLast7Days_Success() {

        // given
        LocalDate standardDate = LocalDate.of(2026, 4, 3);
        String destKey = "menu_rank:last7Days:" + standardDate.toString();

        // opsZest 호출 할 때 zSetOperations 객체 반환
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
        // unionAndStore 동작 설정
        given(zSetOperations.unionAndStore(anyString(),any(java.util.Collection.class),anyString())).willReturn(3L);
        // TTL (expire) 동작 설정
        given(stringRedisTemplate.expire(anyString(), any(java.time.Duration.class))).willReturn(true);

        // Redis에서 꺼내올 가짜 결과 데이터 셋팅
        Set<ZSetOperations.TypedTuple<String>> mockResult = new LinkedHashSet<>();
        mockResult.add(new DefaultTypedTuple<>("아메리카노", 140.0));
        mockResult.add(new DefaultTypedTuple<>("자몽블랙티", 119.0));
        mockResult.add(new DefaultTypedTuple<>("카페라테", 105.0));

        given(zSetOperations.reverseRangeWithScores(destKey, 0L, 2L)).willReturn(mockResult);

        // when
        List<MenuRankingResponse> responses = menuService.findTop3MenuInLast7Days(standardDate);

        // then
        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).getTitle()).isEqualTo("아메리카노");
        assertThat(responses.get(0).getScore()).isEqualTo(140.0);
        assertThat(responses.get(2).getTitle()).isEqualTo("카페라테");

        // Redis 호출 횟수 검증
        verify(zSetOperations, times(1)).unionAndStore(anyString(), anyCollection(), eq(destKey));
        verify(stringRedisTemplate, times(1)).expire(eq(destKey),any());
    }

    @Test
    @DisplayName("인기 메뉴 조회 - 조회된 데이터가 없으면 빈 리스트 반환")
    void findTop3MenuLast7Days_Empty() {

        // given
        LocalDate standardDate = LocalDate.of(2026, 4, 3);
        String destKey = "menu_rank:last7Days:" + standardDate.toString();

        // opsZest 호출 할 때 zSetOperations 객체 반환
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
        // unionAndStore 동작 설정
        given(zSetOperations.unionAndStore(anyString(),any(java.util.Collection.class),anyString())).willReturn(3L);
        // TTL (expire) 동작 설정
        given(stringRedisTemplate.expire(anyString(), any(java.time.Duration.class))).willReturn(true);

        given(zSetOperations.reverseRangeWithScores(destKey, 0L, 2L)).willReturn(null);

        // when
        List<MenuRankingResponse> responses = menuService.findTop3MenuInLast7Days(standardDate);

        // then
        assertThat(responses).isEmpty();
    }
}
