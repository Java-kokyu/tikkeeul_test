package com.hanghae0705.sbmoney.repository.statistic;

import com.hanghae0705.sbmoney.model.domain.item.QSavedItem;
import com.hanghae0705.sbmoney.model.domain.statistic.QStatisticsAllUserMonth;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsAllUserMonth;
import com.hanghae0705.sbmoney.model.dto.SavedItemForStatisticsAllUserDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;



@Repository
@Slf4j
public class StatisticsAllUserRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public StatisticsAllUserRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 시작일, 종료일로 끊어서 데이터 받아오기
    public List<SavedItemForStatisticsAllUserDto> findByMonth(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QSavedItem savedItem = QSavedItem.savedItem;
        NumberPath<Integer> aliasOrderType = Expressions.numberPath(Integer.class, "totalPrice");

        List<SavedItemForStatisticsAllUserDto> result = queryFactory
                .select(Projections.fields(SavedItemForStatisticsAllUserDto.class,
                        savedItem.item.name.as("itemName"),
                        savedItem.item.category.id.as("categoryId"),
                        savedItem.price.sum().as("totalPrice"),
                        savedItem.count().as("totalCount")
                ))
                .from(savedItem)
                .where(savedItem.createdAt.between(startDateTime, endDateTime))
                .groupBy(savedItem.item.id)
                .orderBy(aliasOrderType.desc())
                .limit(10)
                .fetch();

        // log
        result.forEach(SavedItemForStatisticsAllUserDto -> {
            log.info("itemName: "+ SavedItemForStatisticsAllUserDto.getItemName());
            log.info("totalPrice: "+ SavedItemForStatisticsAllUserDto.getTotalPrice());
            log.info("totalCount: "+ SavedItemForStatisticsAllUserDto.getTotalCount());
        });
        return result;
    }

    @Transactional
    public void saveStatisticsAllMonth(StatisticsAllUserMonth statisticsAllUserMonth) {
        em.persist(statisticsAllUserMonth);
    }

    public List<StatisticsAllUserMonth> findAllMonthlyByPrice(String standardDate) {
        QStatisticsAllUserMonth AllMonthPrice = QStatisticsAllUserMonth.statisticsAllUserMonth;

        List<StatisticsAllUserMonth> result = queryFactory.select(Projections.fields(StatisticsAllUserMonth.class,
                        AllMonthPrice.standardDate,
                        AllMonthPrice.rankPrice,
                        AllMonthPrice.itemName,
                        AllMonthPrice.categoryId
                ))
                .from(AllMonthPrice)
                .where(AllMonthPrice.standardDate.eq(standardDate))
                .orderBy(AllMonthPrice.rankPrice.asc())
                .fetch();

        //log
        result.forEach(statisticsAllUserMonth -> {
            log.info(statisticsAllUserMonth.getStandardDate() + " | "
                    + statisticsAllUserMonth.getRankPrice() + " | "
                    + statisticsAllUserMonth.getItemName() + " | "
                    + statisticsAllUserMonth.getCategoryId());
        });

        return result;
    }

    public List<StatisticsAllUserMonth> findAllMonthlyByCount(String standardDate) {
        QStatisticsAllUserMonth AllMonthCount = QStatisticsAllUserMonth.statisticsAllUserMonth;

        List<StatisticsAllUserMonth> result = queryFactory.select(Projections.fields(StatisticsAllUserMonth.class,
                        AllMonthCount.standardDate,
                        AllMonthCount.rankCount,
                        AllMonthCount.itemName,
                        AllMonthCount.categoryId
                ))
                .from(AllMonthCount)
                .where(AllMonthCount.standardDate.eq(standardDate))
                .orderBy(AllMonthCount.rankCount.asc())
                .fetch();

        //log
        result.forEach(statisticsAllUserMonth -> {
            log.info(statisticsAllUserMonth.getStandardDate() + " | "
                    + statisticsAllUserMonth.getRankCount() + " | "
                    + statisticsAllUserMonth.getItemName() + " | "
                    + statisticsAllUserMonth.getCategoryId());
        });

        return result;
    }
}

