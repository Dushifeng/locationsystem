package cn.lovezsm.locationsystem.base.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class ExceptionHandlingControllerAdvice {


    /**
     * SQLException
     * @param exception
     * @return
     */
    @ExceptionHandler({ SQLException.class })
    public String databaseError(Exception exception) {
        log.error("Request raised " + exception.getClass().getSimpleName());
        return "Global_databaseError";
    }

}
