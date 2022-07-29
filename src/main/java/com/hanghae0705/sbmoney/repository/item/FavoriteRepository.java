package com.hanghae0705.sbmoney.repository.item;

import com.hanghae0705.sbmoney.model.domain.user.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser_Id(Long userId);
    List<Favorite> findByUserId(Long userId);

    @Modifying
    @Query("delete from Favorite f where f.user.id = ?1")
    void deleteAllByUserId(Long userId);
}
