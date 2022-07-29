package com.hanghae0705.sbmoney.controller.statistic;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.service.statistic.StatisticsAllUserMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticsAllUserMonthController {

    private final StatisticsAllUserMonthService statisticsAllUserMonthService;

    // 프론트에서 요청하는 API

    @GetMapping("/api/statistics/allUser/month/{month}/price")
    public Message getAllUserMonthlySaveByPrice(@PathVariable String month){
        return statisticsAllUserMonthService.getAllUserMonthlySaveByPrice(month);
    }

    @GetMapping("/api/statistics/allUser/month/{month}/count")
    public Message getAllUserMonthlySaveByCount(@PathVariable String month){
        return statisticsAllUserMonthService.getAllUserMonthlySaveByCount(month);
    }

    @Scheduled(cron = "0 0 0 1 * *") // 초, 분, 시, 일, 월, 요일 / 매달 1일 자정에 실행
    @GetMapping("/api/statistics/allUser/month/price")
    public void createAllUserMonthlySave(){
        statisticsAllUserMonthService.createAllUserMonthlySave();
        log.info("createAllUserMonthlySave() 실행");
    }
}