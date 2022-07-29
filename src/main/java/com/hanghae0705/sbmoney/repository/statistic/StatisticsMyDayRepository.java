package com.hanghae0705.sbmoney.repository.statistic;


import com.hanghae0705.sbmoney.model.domain.item.QSavedItem;
import com.hanghae0705.sbmoney.model.domain.statistic.QStatisticsMyDay;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsMyDay;
import com.hanghae0705.sbmoney.model.dto.SavedItemForStatisticsDto;
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
public class StatisticsMyDayRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public StatisticsMyDayRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 시작일, 종료일로 끊어서 데이터 받아오기
    public List<SavedItemForStatisticsDto> findByDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QSavedItem savedItem = QSavedItem.savedItem;
        NumberPath<Integer> aliasOrderType = Expressions.numberPath(Integer.class, "totalPrice");
        // select sum(price), item_id, user_id, count(item_id)
        // from saved_item group by item_id, user_id order by user_id, sum(price) desc;

        List<SavedItemForStatisticsDto> result = queryFactory
                .select(Projections.fields(SavedItemForStatisticsDto.class,
                        savedItem.item.id.as("itemId"),
                        savedItem.user.id.as("userId"),
                        savedItem.item.category.id.as("categoryId"),
                        savedItem.item.name.as("itemName"),
                        savedItem.price.sum().as("totalPrice"),
                        savedItem.item.id.count().as("totalCount")
                ))
                .from(savedItem)
                .where(savedItem.createdAt.between(startDateTime, endDateTime))
                .groupBy(savedItem.item.id, savedItem.user.id)
                .orderBy(savedItem.user.id.desc(), aliasOrderType.desc())
                .fetch();

        // log
        result.forEach(SavedItemForStatisticsDto -> {
                    log.info("userId: "+SavedItemForStatisticsDto.getUserId() + " | "
                            + "categoryId: "+SavedItemForStatisticsDto.getCategoryId() + " | "
                            + "itemName: "+SavedItemForStatisticsDto.getItemName() + " | "
                            + "totalPrice: "+SavedItemForStatisticsDto.getTotalPrice() + " | "
                            + "totalCount: "+SavedItemForStatisticsDto.getTotalCount());
                });
        return result;
    }

    @Transactional
    public void saveStatisticsMyDay(StatisticsMyDay statisticsMyDay) {
        em.persist(statisticsMyDay);
    }

    public List<StatisticsMyDay> findMyDailyByUserIdAndPrice(Long userId, String standardDate) {
        QStatisticsMyDay myDayPrice = QStatisticsMyDay.statisticsMyDay;

        List<StatisticsMyDay> result = queryFactory.select(Projections.fields(StatisticsMyDay.class,
                        myDayPrice.standardDate,
                        myDayPrice.rankPrice,
                        myDayPrice.itemName,
                        myDayPrice.totalPrice,
                        myDayPrice.userId
                        ))
                .from(myDayPrice)
                .where(myDayPrice.userId.eq(userId),
                        myDayPrice.standardDate.eq(standardDate))
                .orderBy(myDayPrice.rankPrice.asc())
                .fetch();

        //log
        result.forEach(StatisticsMyDay -> {
            log.info(StatisticsMyDay.getStandardDate() + " | "
                    + StatisticsMyDay.getRankPrice() + " | "
                    + StatisticsMyDay.getItemName() + " | "
                    + StatisticsMyDay.getTotalPrice() + " | "
                    + StatisticsMyDay.getUserId());
        });

        return result;
    }

    public List<StatisticsMyDay> findMyDailyByUserIdAndCount(Long userId, String standardDate) {
        QStatisticsMyDay myDayCount = QStatisticsMyDay.statisticsMyDay;

        List<StatisticsMyDay> result = queryFactory.select(Projections.fields(StatisticsMyDay.class,
                        myDayCount.standardDate,
                        myDayCount.rankCount,
                        myDayCount.itemName,
                        myDayCount.totalCount,
                        myDayCount.userId
                ))
                .from(myDayCount)
                .where(myDayCount.userId.eq(userId),
                        myDayCount.standardDate.eq(standardDate))
                .orderBy(myDayCount.rankCount.asc())
                .fetch();

        //log
        result.forEach(StatisticsMyDay -> {
            log.info(StatisticsMyDay.getStandardDate() + " | "
                    + StatisticsMyDay.getRankCount() + " | "
                    + StatisticsMyDay.getItemName() + " | "
                    + StatisticsMyDay.getRankCount() + " | "
                    + StatisticsMyDay.getUserId());
        });

        return result;
    }
}
