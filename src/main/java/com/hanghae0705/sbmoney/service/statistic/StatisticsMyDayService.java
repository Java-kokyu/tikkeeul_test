package com.hanghae0705.sbmoney.service.statistic;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsMyDay;
import com.hanghae0705.sbmoney.model.dto.SavedItemForStatisticsDto;
import com.hanghae0705.sbmoney.repository.statistic.StatisticsMyDayRepository;
import com.hanghae0705.sbmoney.service.CommonService;
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
public class StatisticsMyDayService {

    private final StatisticsMyDayRepository statisticsMyDayRepository;
    private final CommonService commonService;

    // 가결별/일별 나의 아낀 내역 저장하기
    public void createMyDailySave(){
        // 전체 유저의 아낀 내역 저장

        // 날짜 구하기
        LocalDate yesterday = LocalDate.now().minusDays(1); // 2022-07-12
        LocalDateTime startDateTime = yesterday.atTime(LocalTime.MIDNIGHT); // 2022-07-12T00:00
        LocalDateTime endDateTime = yesterday.atTime(LocalTime.MAX); // 2022-07-12T23:59:59.999999999

        // savedItem 일별 리스트를 구해오기
        List<SavedItemForStatisticsDto> savedItemList = statisticsMyDayRepository.findByDate(startDateTime, endDateTime);

        // 받아온 savedItem 의 순서대로 price 랭킹을 매겨 List 에 저장
        List<StatisticsMyDay> statisticsMyDayList = savedItemList.stream()
                .map(savedItem -> StatisticsMyDay.builder()
                        .userId(savedItem.getUserId())
                        .categoryId(savedItem.getCategoryId())
                        .standardDate(yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .itemName(savedItem.getItemName())
                        .totalPrice(savedItem.getTotalPrice())
                        .totalCount(savedItem.getTotalCount())
                        .rankPrice(savedItemList.indexOf(savedItem)+1)
                        .build())
                .limit(5)
                .collect(Collectors.toList());

        // 받아온 savedItem 을 count 기준으로 정렬하여 List 에 추가
        List<SavedItemForStatisticsDto> savedItemListOrderedByCount = savedItemList.stream().sorted(Comparator.comparing(SavedItemForStatisticsDto::getTotalCount).reversed())
                .limit(5)
                .collect(Collectors.toList());
        for(SavedItemForStatisticsDto savedItemDto : savedItemListOrderedByCount) {
            for(StatisticsMyDay savedItemStatistics : statisticsMyDayList) {
                if (savedItemDto.getItemName().equals(savedItemStatistics.getItemName())) {
                    savedItemStatistics.changeRankCount(savedItemListOrderedByCount.indexOf(savedItemDto)+1);
                    System.out.println(savedItemStatistics.getRankCount()+" "+savedItemStatistics.getRankPrice()+" "+savedItemStatistics.getItemName());
                }
            }
        }

        // List 를 DB에 추가
        for(StatisticsMyDay savedItemStatistics : statisticsMyDayList) {
            statisticsMyDayRepository.saveStatisticsMyDay(savedItemStatistics);
        }
    }

    // 나의 일일 가격순 코드 불러오기
    public Message getMyDailyByUserIdAndPrice(String day){
        Long userId = commonService.getUserId();
        //Long userId = 76L;

        List<StatisticsMyDay> result = statisticsMyDayRepository.findMyDailyByUserIdAndPrice(userId, day);
        List<StatisticsMyDay.MyDailyByPrice> myDailyByPriceList = result.stream()
                .map(myDaily -> StatisticsMyDay.MyDailyByPrice.builder()
                        .userId(myDaily.getUserId())
                        .rankPrice(myDaily.getRankPrice())
                        .categoryId(myDaily.getCategoryId())
                        .itemName(myDaily.getItemName())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("나의 아낀 항목 일일 금액별 통계 조회에 성공했습니다.")
                .data(myDailyByPriceList)
                .build();
    }

    public Message getMyDailyByUserIdAndCount(String day){
        Long userId = commonService.getUserId();
        //Long userId = 76L;

        List<StatisticsMyDay> result = statisticsMyDayRepository.findMyDailyByUserIdAndCount(userId, day);
        List<StatisticsMyDay.MyDailyByCount> myDailyByCountList = result.stream()
                .map(myDaily -> StatisticsMyDay.MyDailyByCount.builder()
                        .userId(myDaily.getUserId())
                        .rankCount(myDaily.getRankCount())
                        .categoryId(myDaily.getCategoryId())
                        .itemName(myDaily.getItemName())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("나의 아낀 항목 일일 횟수별 통계 조회에 성공했습니다.")
                .data(myDailyByCountList)
                .build();
    }
}
