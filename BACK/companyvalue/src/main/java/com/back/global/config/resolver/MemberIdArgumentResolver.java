package com.back.global.config.resolver;

import com.back.global.annotation.CurrentMemberId;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 매개변수에 @CurrentMemberId 어노테이션이 있고, 타입이 Long인지 확인
        return parameter.hasParameterAnnotation(CurrentMemberId.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 정보가 없거나, 인증되지 않은 사용자(anonymousUser 등)인 경우 예외 발생
        if(authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        Object principal = authentication.getPrincipal();
        if(principal instanceof User user) {
            try {
                return Long.parseLong(user.getUsername());
            } catch (NumberFormatException e) {
                // 혹시 모를 파싱 에러 방어 (일어나면 안 되는 상황)
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
    }
}
