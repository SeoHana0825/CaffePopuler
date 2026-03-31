package com.example.caffepopularproject.domain.menu.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.menu.dto.request.MenuCreateRequest;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

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
}
