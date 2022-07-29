package com.hanghae0705.sbmoney.service.statistic;

import com.hanghae0705.sbmoney.data.Message;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsAllUserGoal;
import com.hanghae0705.sbmoney.model.dto.GoalItemForStatisticsAllUserDto;
import com.hanghae0705.sbmoney.repository.statistic.StatisticsAllUserGolItemRepository;
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
public class StatisticsAllUserGoalItemService {

    private final StatisticsAllUserGolItemRepository statisticsAllUserGolItemRepository;

    // 가결별/일별 남의 골아이템 일주일간 저장하기
    public void createGoalSave(){

        // 날짜 구하기
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate minusDays7 = LocalDate.now().minusDays(7); // 2022-07-12
        LocalDateTime startDateTime = minusDays7.atTime(LocalTime.MIDNIGHT); // 2022-07-12T00:00
        LocalDateTime endDateTime = yesterday.atTime(LocalTime.MAX); // 2022-07-12T23:59:59.999999999

        // userId로 savedItem 일별 리스트를 높은 가격순으로 구해오기
        List<GoalItemForStatisticsAllUserDto> goalItemList = statisticsAllUserGolItemRepository.findByDate(startDateTime, endDateTime);

        // 받아온 savedItem 의 순서대로 price 랭킹을 매겨 List 에 저장
        List<StatisticsAllUserGoal> statisticsAllUserGoalList = goalItemList.stream()
                .map(goalItem -> StatisticsAllUserGoal.builder()
                        .categoryId(goalItem.getCategoryId())
                        .standardDate(minusDays7.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .itemName(goalItem.getItemName())
                        .totalPrice(goalItem.getTotalPrice())
                        .totalCount(goalItem.getTotalCount())
                        .rankPrice(goalItemList.indexOf(goalItem)+1)
                        .build())
                .collect(Collectors.toList());

        // 받아온 goalItem 을 count 기준으로 정렬하여 List 에 추가
        List<GoalItemForStatisticsAllUserDto> goalItemListOrderedByCount = goalItemList.stream().sorted(Comparator.comparing(GoalItemForStatisticsAllUserDto::getTotalCount).reversed())
                .collect(Collectors.toList());
        for(GoalItemForStatisticsAllUserDto goalItemDto : goalItemListOrderedByCount) {
            for(StatisticsAllUserGoal statisticsAllUserGoal : statisticsAllUserGoalList) {
                if (goalItemDto.getItemName().equals(statisticsAllUserGoal.getItemName())) {
                    statisticsAllUserGoal.changeRankCount(goalItemListOrderedByCount.indexOf(goalItemDto)+1);
                    System.out.println(statisticsAllUserGoal.getRankCount()+" "+statisticsAllUserGoal.getRankPrice()+" "+statisticsAllUserGoal.getItemName());
                }
            }
        }

        // List 를 DB에 추가
        for(StatisticsAllUserGoal statisticsAllUserGoal : statisticsAllUserGoalList) {
            statisticsAllUserGolItemRepository.saveStatisticsAllUserGoal(statisticsAllUserGoal);
        }
    }

    // 골아이템 가격별 코드 불러오기
    public Message getGoalItemByPrice(){

        String standardDate = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        List<StatisticsAllUserGoal> result = statisticsAllUserGolItemRepository.findGoalItemByPrice(standardDate);
        List<StatisticsAllUserGoal.GoalByPrice> goalByPriceList = result.stream()
                .map(goal -> StatisticsAllUserGoal.GoalByPrice.builder()
                        .rankPrice(goal.getRankPrice())
                        .categoryId(goal.getCategoryId())
                        .itemName(goal.getItemName())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("골 아이템의 최근 일주일 금액별 통계 조회에 성공했습니다.")
                .data(goalByPriceList)
                .build();
    }

    public Message getGoalItemByCount(){

        String standardDate = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        List<StatisticsAllUserGoal> result = statisticsAllUserGolItemRepository.findGoalItemByCount(standardDate);
        List<StatisticsAllUserGoal.GoalByCount> goalByCountList = result.stream()
                .map(goal -> StatisticsAllUserGoal.GoalByCount.builder()
                        .rankCount(goal.getRankCount())
                        .categoryId(goal.getCategoryId())
                        .itemName(goal.getItemName())
                        .build())
                .collect(Collectors.toList());

        return Message.builder()
                .result(true)
                .respMsg("골 아이템의 최근 일주일 횟수별 통계 조회에 성공했습니다.")
                .data(goalByCountList)
                .build();
    }
}
