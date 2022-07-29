package com.hanghae0705.sbmoney.repository.board;

import com.hanghae0705.sbmoney.model.domain.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUserId(Long userId);
    List<Comment> findByBoard_Id(Long boardId);
}
