package com.hanghae0705.sbmoney.repository.board;

import com.hanghae0705.sbmoney.model.domain.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    //lastBoardId보다 작은 값의 id의 게시물을 수정일자 순으로 가져온다.
    Page<Board> findByIdLessThanOrderByIdDesc(Long lastBoardId, Pageable pageable);
    Board findAllById(Long BoardId);
    List<Board> findAllByUserId(Long userId);
}
