package com.hanghae0705.sbmoney.repository.statistic;

import com.hanghae0705.sbmoney.model.domain.item.QSavedItem;
import com.hanghae0705.sbmoney.model.domain.statistic.QStatisticsMyMonth;
import com.hanghae0705.sbmoney.model.domain.statistic.StatisticsMyMonth;
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
public class StatisticsMyMonthRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public StatisticsMyMonthRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    // 월별, 일별 Data 생성
    // savedItem에서 entity들을 뽑아와 날짜로 월별, 일별 통계 테이블에 저장하는 용도
    // 얘는 controller의 스케쥴러에서 사용한다.
    public List<SavedItemForStatisticsDto> findByDate(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        QSavedItem savedItem = QSavedItem.savedItem;
        NumberPath<Integer> aliasOrderType = Expressions.numberPath(Integer.class, "totalPrice");
        List<SavedItemForStatisticsDto> tempList = jpaQueryFactory.select(
                Projections.fields(SavedItemForStatisticsDto.class,
                    savedItem.user.id.as("userId"),
                    savedItem.item.id.as("itemId"),
                    savedItem.item.name.as("itemName"),
                    savedItem.item.id.count().as("totalCount"),
                    savedItem.price.sum().as("totalPrice"),
                    savedItem.item.category.id.as("categoryId")))
                .from(savedItem)
                .where(savedItem.user.id.eq(userId), savedItem.createdAt.between(startDate, endDate))
                .orderBy(aliasOrderType.desc(), savedItem.user.id.desc())
                .groupBy(savedItem.item.id, savedItem.user.id)
                .limit(5)
                .fetch();
        tempList.forEach(SavedItemForStatisticsDto -> {
            log.info("userId: "+SavedItemForStatisticsDto.getUserId() + " | "
                    + "categoryId: "+SavedItemForStatisticsDto.getCategoryId() + " | "
                    + "itemName: "+SavedItemForStatisticsDto.getItemName() + " | "
                    + "totalPrice: "+SavedItemForStatisticsDto.getTotalPrice() + " | "
                    + "totalCount: "+SavedItemForStatisticsDto.getTotalCount());
        });
        return tempList;
    }

    public List<StatisticsMyMonth> findMyMonthlyStatisticsByUserIdAndPrice(Long userId, String standardDate) {
        QStatisticsMyMonth monthlyPrice = QStatisticsMyMonth.statisticsMyMonth;
        List<StatisticsMyMonth> result = jpaQueryFactory.select(Projections.fields(StatisticsMyMonth.class,
                        monthlyPrice.standardDate,
                        monthlyPrice.rankPrice,
                        monthlyPrice.itemName,
                        monthlyPrice.totalPrice,
                        monthlyPrice.userId,
                        monthlyPrice.categoryId
                ))
                .from(monthlyPrice)
                .where(monthlyPrice.userId.eq(userId),
                        monthlyPrice.standardDate.eq(standardDate))
                .orderBy(monthlyPrice.rankPrice.asc())
                .fetch();

        return result;
    }

    // 횟수 오름차순
    public List<StatisticsMyMonth> findMyMonthlyStatisticsByUserIdAndCount(Long userId, String standardDate) {
        QStatisticsMyMonth monthlyCnt = QStatisticsMyMonth.statisticsMyMonth;

        List<StatisticsMyMonth> result = jpaQueryFactory.select(Projections.fields(StatisticsMyMonth.class,
                        monthlyCnt.standardDate,
                        monthlyCnt.rankCnt,
                        monthlyCnt.itemName,
                        monthlyCnt.totalCnt,
                        monthlyCnt.userId,
                        monthlyCnt.categoryId
                ))
                .from(monthlyCnt)
                .where(monthlyCnt.userId.eq(userId),
                        monthlyCnt.standardDate.eq(standardDate))
                .orderBy(monthlyCnt.rankCnt.asc())
                .fetch();

        return result;
    }

    @Transactional
    public void saveMyMonthlyStatistics(StatisticsMyMonth statisticsMymonth) {
        entityManager.persist(statisticsMymonth);
    }
}
