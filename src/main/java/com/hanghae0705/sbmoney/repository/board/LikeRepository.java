package com.hanghae0705.sbmoney.repository.board;

import com.hanghae0705.sbmoney.model.domain.board.Board;
import com.hanghae0705.sbmoney.model.domain.board.BoardLike;
import com.hanghae0705.sbmoney.model.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<BoardLike, Long> {
    List<BoardLike> findByBoardAndLikeIsTrue(Board board);

    BoardLike findByBoardAndUser(Board board, User user);
}
