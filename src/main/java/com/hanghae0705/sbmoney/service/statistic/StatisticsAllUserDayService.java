package com.hanghae0705.sbmoney.service.statistic;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsAllUserDay;
import com.hanghae0705.sbmoney.model.dto.SavedItemForStatisticsAllUserDto;
import com.hanghae0705.sbmoney.repository.statistic.StatisticsAllUserDayRepository;
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
public class StatisticsAllUserDayService {


    private final StatisticsAllUserDayRepository statisticsAllUserDayRepository;

    // 가결별/일별 나의 아낀 내역 저장하기
    public void createAllUserDailySave(){
        // username, userId 받아오기


        // 날짜 구하기
        LocalDate yesterday = LocalDate.now().minusDays(1); // 2022-07-12
        LocalDateTime startDateTime = yesterday.atTime(LocalTime.MIDNIGHT); // 2022-07-12T00:00
        LocalDateTime endDateTime = yesterday.atTime(LocalTime.MAX); // 2022-07-12T23:59:59.999999999

        // userId로 savedItem 일별 리스트를 높은 가격순으로 구해오기
        List<SavedItemForStatisticsAllUserDto> savedItemList = statisticsAllUserDayRepository.findByDate( startDateTime, endDateTime);

        // 받아온 savedItem 의 순서대로 price 랭킹을 매겨 List 에 저장
        List<StatisticsAllUserDay> statisticsAllUserDayList = savedItemList.stream()
                .map(savedItem -> StatisticsAllUserDay.builder()
                        .standardDate(yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
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
            for(StatisticsAllUserDay savedItemStatistics : statisticsAllUserDayList) {
                if (savedItemDto.getItemName().equals(savedItemStatistics.getItemName())) {
                    savedItemStatistics.changeRankCount(savedItemListOrderedByCount.indexOf(savedItemDto)+1);
                    System.out.println(savedItemStatistics.getRankCount()+" "+savedItemStatistics.getRankPrice()+" "+savedItemStatistics.getItemName());
                }
            }
        }

        // List 를 DB에 추가
        for(StatisticsAllUserDay savedItemStatistics : statisticsAllUserDayList) {
            statisticsAllUserDayRepository.saveStatisticsAllUserDay(savedItemStatistics);
        }
    }

    // 나의 일일 가격순 코드 불러오기
    public Message getAllUserDailyByPrice(String day){


        List<StatisticsAllUserDay> result = statisticsAllUserDayRepository.findAllUserDailyByPrice( day);
        List<StatisticsAllUserDay.AllUserDailyByPrice> AllUserDailyByPriceList = result.stream()
                .map(AllUserDaily -> StatisticsAllUserDay.AllUserDailyByPrice.builder()
                        .rankPrice(AllUserDaily.getRankPrice())
                        .itemName(AllUserDaily.getItemName())
                        .categoryId(AllUserDaily.getCategoryId())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("전체 아낀 항목 일일 금액별 통계 조회에 성공했습니다.")
                .data(AllUserDailyByPriceList)
                .build();
    }

    public Message getAllUserDailyByCount(String day){


        List<StatisticsAllUserDay> result = statisticsAllUserDayRepository.findAllUserDailyByCount( day);
        List<StatisticsAllUserDay.AllUserDailyByCount> AllUserDailyByCountList = result.stream()
                .map(AllUserDaily -> StatisticsAllUserDay.AllUserDailyByCount.builder()
                        .rankCount(AllUserDaily.getRankCount())
                        .itemName(AllUserDaily.getItemName())
                        .categoryId(AllUserDaily.getCategoryId())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("전체 아낀 항목 일일 횟수별 통계 조회에 성공했습니다.")
                .data(AllUserDailyByCountList)
                .build();
    }


}