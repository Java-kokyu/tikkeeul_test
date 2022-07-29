package com.hanghae0705.sbmoney.repository.statistic;

import com.hanghae0705.sbmoney.model.domain.item.QGoalItem;
import com.hanghae0705.sbmoney.model.domain.statistic.QStatisticsAllUserGoal;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsAllUserGoal;
import com.hanghae0705.sbmoney.model.dto.GoalItemForStatisticsAllUserDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class StatisticsAllUserGolItemRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public StatisticsAllUserGolItemRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 시작일, 종료일로 끊어서 데이터 받아오기
    public List<GoalItemForStatisticsAllUserDto> findByDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QGoalItem goalItem = QGoalItem.goalItem;

        List<GoalItemForStatisticsAllUserDto> result = queryFactory
                .select(Projections.fields(GoalItemForStatisticsAllUserDto.class,
                        goalItem.item.category.id.as("categoryId"),
                        goalItem.item.name.as("itemName"),
                        goalItem.total.sum().as("totalPrice"),
                        goalItem.count().as("totalCount")
                ))
                .from(goalItem)
                .where(goalItem.createdAt.between(startDateTime, endDateTime),
                        goalItem.item.name.notIn("이름 없음"))
                .groupBy(goalItem.item.id)
                .orderBy(goalItem.total.desc())
                .limit(10)
                .fetch();

        // log
        result.forEach(SavedItemForStatisticsDto -> {
                    log.info("categoryId: "+SavedItemForStatisticsDto.getCategoryId() + " | "
                            + "itemName: "+SavedItemForStatisticsDto.getItemName() + " | "
                            + "totalPrice: "+SavedItemForStatisticsDto.getTotalPrice() + " | "
                            + "totalCount: "+SavedItemForStatisticsDto.getTotalCount());
                });
        return result;
    }

    @Transactional
    public void saveStatisticsAllUserGoal(StatisticsAllUserGoal statisticsAllUserGoal) {
        em.persist(statisticsAllUserGoal);
    }

    public List<StatisticsAllUserGoal> findGoalItemByPrice(String standardDate) {
        QStatisticsAllUserGoal goalByPrice = QStatisticsAllUserGoal.statisticsAllUserGoal;

        List<StatisticsAllUserGoal> result = queryFactory.select(Projections.fields(StatisticsAllUserGoal.class,
                        goalByPrice.standardDate,
                        goalByPrice.rankPrice,
                        goalByPrice.categoryId,
                        goalByPrice.itemName,
                        goalByPrice.totalPrice
                        ))
                .from(goalByPrice)
                .where(goalByPrice.standardDate.eq(standardDate))
                .orderBy(goalByPrice.rankPrice.asc())
                .limit(10)
                .fetch();

        //log
        result.forEach(StatisticsAllUserGoal -> {
            log.info(StatisticsAllUserGoal.getStandardDate() + " | "
                    + StatisticsAllUserGoal.getRankPrice() + " | "
                    + StatisticsAllUserGoal.getCategoryId() + " | "
                    + StatisticsAllUserGoal.getItemName() + " | "
                    + StatisticsAllUserGoal.getTotalPrice() + " | ");
        });

        return result;
    }

    public List<StatisticsAllUserGoal> findGoalItemByCount(String standardDate) {
        QStatisticsAllUserGoal goalByCount = QStatisticsAllUserGoal.statisticsAllUserGoal;

        List<StatisticsAllUserGoal> result = queryFactory.select(Projections.fields(StatisticsAllUserGoal.class,
                        goalByCount.standardDate,
                        goalByCount.rankCount,
                        goalByCount.categoryId,
                        goalByCount.itemName,
                        goalByCount.totalCount
                ))
                .from(goalByCount)
                .where(goalByCount.standardDate.eq(standardDate))
                .orderBy(goalByCount.rankCount.asc())
                .limit(10)
                .fetch();

        //log
        result.forEach(StatisticsAllUserGoal -> {
            log.info(StatisticsAllUserGoal.getStandardDate() + " | "
                    + StatisticsAllUserGoal.getRankCount() + " | "
                    + StatisticsAllUserGoal.getCategoryId() + " | "
                    + StatisticsAllUserGoal.getItemName() + " | "
                    + StatisticsAllUserGoal.getRankCount() + " | ");
        });

        return result;
    }
}
