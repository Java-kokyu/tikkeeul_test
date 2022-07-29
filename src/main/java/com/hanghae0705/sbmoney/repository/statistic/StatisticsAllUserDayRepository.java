package com.hanghae0705.sbmoney.repository.statistic;

import com.hanghae0705.sbmoney.model.domain.item.QSavedItem;
import com.hanghae0705.sbmoney.model.domain.statistic.QStatisticsAllUserDay;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsAllUserDay;
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
public class StatisticsAllUserDayRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public StatisticsAllUserDayRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // userId 기반으로 시작일, 종료일로 끊어서 데이터 받아오기
    public List<SavedItemForStatisticsAllUserDto> findByDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
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
            log.info("itemName: " + SavedItemForStatisticsAllUserDto.getItemName());
            log.info("totalPrice: " + SavedItemForStatisticsAllUserDto.getTotalPrice());
            log.info("totalCount: " + SavedItemForStatisticsAllUserDto.getTotalCount());
        });
        return result;
    }

    @Transactional
    public void saveStatisticsAllUserDay(StatisticsAllUserDay statisticsAllUserDay) {
        em.persist(statisticsAllUserDay);
    }

    public List<StatisticsAllUserDay> findAllUserDailyByPrice( String standardDate) {
        QStatisticsAllUserDay AllUserDayPrice = QStatisticsAllUserDay.statisticsAllUserDay;


        List<StatisticsAllUserDay> result = queryFactory.select(Projections.fields(StatisticsAllUserDay.class,
                        AllUserDayPrice.standardDate,
                        AllUserDayPrice.rankPrice,
                        AllUserDayPrice.itemName,
                        AllUserDayPrice.categoryId

                ))
                .from(AllUserDayPrice)
                .where(AllUserDayPrice.standardDate.eq(standardDate))
                .orderBy(AllUserDayPrice.rankPrice.asc())
                .fetch();

        //log
        result.forEach(StatisticsAllUserDay -> {
            log.info(StatisticsAllUserDay.getStandardDate() + " | "
                    + StatisticsAllUserDay.getRankPrice() + " | "
                    + StatisticsAllUserDay.getItemName() + " | "
                    + StatisticsAllUserDay.getCategoryId());
        });

        return result;
    }

    public List<StatisticsAllUserDay> findAllUserDailyByCount( String standardDate) {
        QStatisticsAllUserDay AllUserDayCount = QStatisticsAllUserDay.statisticsAllUserDay;

        List<StatisticsAllUserDay> result = queryFactory.select(Projections.fields(StatisticsAllUserDay.class,
                        AllUserDayCount.standardDate,
                        AllUserDayCount.rankCount,
                        AllUserDayCount.itemName,
                        AllUserDayCount.categoryId

                ))
                .from(AllUserDayCount)
                .where(AllUserDayCount.standardDate.eq(standardDate))
                .orderBy(AllUserDayCount.rankCount.asc())
                .fetch();

        //log
        result.forEach(StatisticsAllUserDay -> {
            log.info(StatisticsAllUserDay.getStandardDate() + " | "
                    + StatisticsAllUserDay.getRankCount() + " | "
                    + StatisticsAllUserDay.getItemName() + " | "
                    + StatisticsAllUserDay.getCategoryId());
        });

        return result;
    }
}
