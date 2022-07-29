package com.hanghae0705.sbmoney.service.statistic;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsAllUserMonth;
import com.hanghae0705.sbmoney.model.dto.SavedItemForStatisticsAllUserDto;
import com.hanghae0705.sbmoney.repository.statistic.StatisticsAllUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsAllUserMonthService {
    private final StatisticsAllUserRepository statisticsAllUserRepository;

    // 가결별/월별 전체 아낀 내역 저장하기
    public void createAllUserMonthlySave(){
        // 날짜 구하기
        LocalDate yesterday = LocalDate.now().minusDays(1); // 2022-*-31
        LocalDate monthFirst = LocalDate.of(yesterday.getYear(), yesterday.getMonth(), 1);
        LocalDateTime startDateTime = monthFirst.atTime(LocalTime.MIDNIGHT); // 2022-*-01T00:00
        LocalDateTime endDateTime = yesterday.atTime(LocalTime.MAX); // 2022-*-31T23:59:59.999999999

        // userId로 savedItem 일별 리스트를 높은 가격순으로 구해오기
        List<SavedItemForStatisticsAllUserDto> savedItemList = statisticsAllUserRepository.findByMonth(startDateTime, endDateTime);

        // 받아온 savedItem 의 순서대로 price 랭킹을 매겨 List 에 저장
        List<StatisticsAllUserMonth> staticsAllMonthList = savedItemList.stream()
                .map(savedItem -> StatisticsAllUserMonth.builder()
                        .standardDate(yesterday.format(DateTimeFormatter.ofPattern("yyyyMM")))
                        .itemName(savedItem.getItemName())
                        .categoryId(savedItem.getCategoryId())
                        .totalPrice(savedItem.getTotalPrice())
                        .totalCount(savedItem.getTotalCount())
                        .rankPrice(savedItemList.indexOf(savedItem)+1)
                        .build())
                .collect(Collectors.toList());



        // 받아온 savedItem 을 count 기준으로 정렬하여 List 에 추가
        List<SavedItemForStatisticsAllUserDto> savedItemListOrderedByCount = savedItemList.stream().sorted(Comparator.comparing(SavedItemForStatisticsAllUserDto::getTotalCount).reversed())
                .collect(Collectors.toList());
        for(SavedItemForStatisticsAllUserDto savedItemDto : savedItemListOrderedByCount) {
            for(StatisticsAllUserMonth savedItemStatistics : staticsAllMonthList) {
                if (savedItemDto.getItemName().equals(savedItemStatistics.getItemName())) {
                    savedItemStatistics.changeRankCount(savedItemListOrderedByCount.indexOf(savedItemDto)+1);
                    System.out.println(savedItemStatistics.getRankCount()+" "+savedItemStatistics.getRankPrice()+" "+savedItemStatistics.getItemName());
                }
            }
        }

        // List 를 DB에 추가
        for(StatisticsAllUserMonth savedItemStatistics : staticsAllMonthList) {
            statisticsAllUserRepository.saveStatisticsAllMonth(savedItemStatistics);
        }
    }

    public Message getAllUserMonthlySaveByPrice(String month){
        List<StatisticsAllUserMonth> result = statisticsAllUserRepository.findAllMonthlyByPrice(month);
        List<StatisticsAllUserMonth.AllMonthlyByPrice> allMonthlyPriceList = result.stream()
                .map(allMonthly -> StatisticsAllUserMonth.AllMonthlyByPrice.builder()
                        .rankPrice(allMonthly.getRankPrice())
                        .itemName(allMonthly.getItemName())
                        .categoryId(allMonthly.getCategoryId())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("월별 티끌 금액순 통계 조회에 성공하였습니다.")
                .data(allMonthlyPriceList)
                .build();
    }

    public Message getAllUserMonthlySaveByCount(String month){
        List<StatisticsAllUserMonth> result = statisticsAllUserRepository.findAllMonthlyByCount(month);
        List<StatisticsAllUserMonth.AllMonthlyByCount> allMonthlyCountList = result.stream()
                .map(allMonthly -> StatisticsAllUserMonth.AllMonthlyByCount.builder()
                        .rankCount(allMonthly.getRankCount())
                        .itemName(allMonthly.getItemName())
                        .categoryId(allMonthly.getCategoryId())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("월별 티끌 횟수순 통계 조회에 성공하였습니다.")
                .data(allMonthlyCountList)
                .build();
    }
}