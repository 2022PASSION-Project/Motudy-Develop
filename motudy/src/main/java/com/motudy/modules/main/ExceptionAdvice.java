package com.motudy.modules.main;

import com.motudy.modules.account.Account;
import com.motudy.modules.account.CurrentAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler // 정보를 로깅하도록 ExceptionHandler를 만들어 놓음
    public String handlerRuntimeException(@CurrentAccount Account account, HttpServletRequest req, RuntimeException e) {
        if(account != null) {
            // 누가(계정이) 어떤 요청을 보냈다고 출력을 해놓는 것
            log.info("'{}' requested '{}'",account.getNickname(), req.getRequestURI());
        } else { // 어디에서 요청이 온 건지(referer)를 체크
            log.info("requested '{}'", req.getRequestURI());
        }
        log.error("bad request", e);
        return "error";
    }
}
