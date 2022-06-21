package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {

}
