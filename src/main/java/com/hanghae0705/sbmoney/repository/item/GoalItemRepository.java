package com.hanghae0705.sbmoney.repository.item;

import com.hanghae0705.sbmoney.model.domain.item.GoalItem;
import com.hanghae0705.sbmoney.model.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalItemRepository extends JpaRepository<GoalItem, Long> {
    List<GoalItem> findAllByCheckReachedOrderByCreatedAtDesc(Boolean checkReached);
    GoalItem findByCheckReached(Boolean checkedReached);
    GoalItem findAllById(Long goalId);

    @Modifying
    @Query("delete from GoalItem g where g.user.id = ?1")
    void deleteAllByUserId(Long userId);
    GoalItem findByUserAndAndCheckReachedIsFalse(User user);

}