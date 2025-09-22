package com.example.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class DefaultHandlerInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final Object handler,
            ModelAndView modelAndView) {

        // デフォルトレスポンスヘッダ設定
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.getMethodAnnotation(DefaultResponseHeaders.class) != null
                    || AnnotationUtils.findAnnotation(
                                    handlerMethod.getBeanType(), DefaultResponseHeaders.class)
                            != null) {
                response.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store");
                response.addHeader(HttpHeaders.PRAGMA, "no-cache");
                response.addHeader(HttpHeaders.EXPIRES, "0");
            }
        }
    }
}
