package com.example.caffepopularproject.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.NOT_FOUND,"중복된 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // MENU
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 메뉴입니다."),
    MENU_SOLD_OUT(HttpStatus.BAD_REQUEST,"품절된 메뉴입니다."),
    MENU_DUPLICATE_NAME(HttpStatus.BAD_REQUEST,"이미 등록된 메뉴입니다."),
    INVALID_MENU_VALUE(HttpStatus.BAD_REQUEST, "가격이나 재고가 0 미만일 수 없습니다."),

    // ORDER
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND,"주문을 찾을 수 없습니다."),
    ORDER_DUPLICATE_NAME(HttpStatus.BAD_REQUEST,"이미 등록된 주문입니다."),
    ORDER_CANCEL_FORBIDDEN(HttpStatus.FORBIDDEN,"해당 아이디의 주문만 취소가 가능합니다."),
    ORDER_BELOW_MINIMUM(HttpStatus.BAD_REQUEST,"최소 주문금액을 충족하지 않습니다."),
    ORDER_NOT_CHANGEABLE(HttpStatus.BAD_REQUEST,"이미 완료되거나 취소된 주문입니다."),

    // POINT
    POINT_NOT_ENOUGH(HttpStatus.BAD_REQUEST,"결제 가능한 포인트가 부족합니다."),
    ALREADY_POINT_USED(HttpStatus.BAD_REQUEST,"이미 포인트 결제가 완료된 주문입니다.");

    // PAYMENT


    private final HttpStatus status;
    private final String message;
}
