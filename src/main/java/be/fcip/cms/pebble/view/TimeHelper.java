package be.fcip.cms.pebble.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TimeHelper {

    public LocalDateTime now(){
        return LocalDateTime.now();
    }

    public int currentYear(){
        return now().getYear();
    }
}
