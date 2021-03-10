package com.example.pocshedlock;

import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Created by DIPU on 3/9/21
 */

@Component
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
@Log4j2
public class Schedular {


    @Scheduled(initialDelayString = "${initial.delay}", fixedDelayString = "${fixed.delay}")
    @SchedulerLock(name = "lock-user-name")
    public void scheduledJob()
    {
        LockAssert.assertLocked();
        log.info("*** my schedular goes here...: {}", Calendar.getInstance().getTime());
    }

}
