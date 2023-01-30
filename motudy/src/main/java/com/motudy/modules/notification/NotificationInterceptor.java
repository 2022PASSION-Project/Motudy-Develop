package com.motudy.modules.notification;

import com.motudy.modules.account.Account;
import com.motudy.modules.account.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * model에 정보가 들어가는 Interceptor
 */
@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

    private final NotificationRepository notificationRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        /*
         1. modelAndView를 쓰는 경우에만 모델에 넣어줌
         2. view 이름이 redirect:로 시작하거나 view의 타입이 RedirectView면 안 됨
         3. authentication이 있어야 함
         4. authentication.getPrincipal() 이 UserAccount 타입이어야 함 (anonymous 안 됨)
         */
        if(modelAndView != null && !isRedirectView(modelAndView) &&
                authentication != null && authentication.getPrincipal() instanceof UserAccount) {
            Account account = ((UserAccount)authentication.getPrincipal()).getAccount();
            long count = notificationRepository.countByAccountAndChecked(account, false);
            modelAndView.addObject("hasNotification", count > 0);
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}
