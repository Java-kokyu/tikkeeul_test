package com.hanghae0705.sbmoney.service.statistic;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsMyMonth;
import com.hanghae0705.sbmoney.model.dto.SavedItemForStatisticsDto;
import com.hanghae0705.sbmoney.repository.item.ItemRepository;
import com.hanghae0705.sbmoney.repository.statistic.StatisticsMyMonthRepository;
import com.hanghae0705.sbmoney.repository.user.UserRepository;
import com.hanghae0705.sbmoney.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsMyMonthlyService {

    private final StatisticsMyMonthRepository myMonthRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommonService commonService;
    private String errorMsg;

    public void updateMyMonthlyStatistics() {
        Long userId = commonService.getUserId();
        try {
            // 로직 순서 - 날짜 구하기 - statistics 테이블 업데이트 - dto에 담기
            // 날짜 구하기 - 서울 시간대 ZoneId
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            // 220701 0100 스케쥴러 작동 시 220601 01:00 ~ 220701 00:59:59 까지 집계한다.
            // expect collectStartTime = 220601T0100
            LocalDateTime collectStartTime = LocalDate.now(zoneId).minusMonths(1).atTime(1, 0);
            // 220601 01:00 ~ 220701 00:59:59
            LocalDateTime collectEndTime = collectStartTime.plusMonths(1).minusSeconds(1);

            // savedItem table 에서 select query 실행
            List<SavedItemForStatisticsDto> tempList = myMonthRepository.findByDate(userId, collectStartTime, collectEndTime);

            // tempList에서 priceRank, cntRank 정렬해서 순위값을 정하고 DB에 넣어야함
            List<StatisticsMyMonth> statisticsMyMonthList = tempList.stream()
                    .map(savedItem -> StatisticsMyMonth.builder()
                            .userId(savedItem.getUserId())
                            .standardDate(collectStartTime.format(DateTimeFormatter.ofPattern("yyyyMM")))
                            .itemName(savedItem.getItemName())
                            .totalPrice(savedItem.getTotalPrice())
                            .totalCnt(savedItem.getTotalCount())
                            .categoryId(savedItem.getCategoryId())
                            .rankPrice(tempList.indexOf(savedItem)+1)
                            .build())
                    .limit(5)
                    .collect(Collectors.toList());

            List<SavedItemForStatisticsDto> savedItemListOrderedByCount = tempList.stream().sorted(Comparator.comparing(SavedItemForStatisticsDto::getTotalCount).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            for(SavedItemForStatisticsDto savedItemDto : savedItemListOrderedByCount) {
                for(StatisticsMyMonth savedItemStatistics : statisticsMyMonthList) {
                    if (savedItemDto.getItemName().equals(savedItemStatistics.getItemName())) {
                        savedItemStatistics.changeRankCount(savedItemListOrderedByCount.indexOf(savedItemDto)+1);
                    }
                }
            }

            for(StatisticsMyMonth savedItemStatistics : statisticsMyMonthList) {
                myMonthRepository.saveMyMonthlyStatistics(savedItemStatistics);
            }
        } catch (Exception e) {
            log.info(e.getMessage() + " 월별 통계 service 에서 에러 발생");
        }
    }

    public Message getMyDailyByUserIdAndPrice(String month){
        Long userId = commonService.getUserId();

        try {
            List<StatisticsMyMonth> result = myMonthRepository.findMyMonthlyStatisticsByUserIdAndPrice(userId, month);
            List<StatisticsMyMonth.StatisticsMonthByPrice> myMonthlyByPriceList = result.stream()
                    .map(myMonthly -> StatisticsMyMonth.StatisticsMonthByPrice.builder()
                            .userId(myMonthly.getUserId())
                            .rankPrice(myMonthly.getRankPrice())
                            .itemName(myMonthly.getItemName())
                            .categoryId(myMonthly.getCategoryId())
                            .build())
                    .collect(Collectors.toList());

            return Message.builder()
                    .result(true)
                    .respMsg("나의 아낀 항목 일일 금액별 통계 조회에 성공했습니다.")
                    .data(myMonthlyByPriceList)
                    .build();
        } catch (Exception e) {
            return new Message(false, errorMsg);
        }
    }

    public Message getMyDailyByUserIdAndCount(String month){
        Long userId = commonService.getUserId();

        try {
            List<StatisticsMyMonth> result = myMonthRepository.findMyMonthlyStatisticsByUserIdAndCount(userId, month);
            List<StatisticsMyMonth.StatisticsMonthByCnt> myMonthlyByCntList = result.stream()
                    .map(myMonthly -> StatisticsMyMonth.StatisticsMonthByCnt.builder()
                            .userId(myMonthly.getUserId())
                            .rankCnt(myMonthly.getRankCnt())
                            .itemName(myMonthly.getItemName())
                            .categoryId(myMonthly.getCategoryId())
                            .build())
                    .collect(Collectors.toList());

            return Message.builder()
                    .result(true)
                    .respMsg("나의 아낀 항목 월별 횟수별 통계 조회에 성공했습니다.")
                    .data(myMonthlyByCntList)
                    .build();
        } catch (Exception e){
            return new Message(false, errorMsg);
        }
    }
}
