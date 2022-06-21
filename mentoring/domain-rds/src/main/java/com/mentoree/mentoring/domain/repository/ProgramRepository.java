package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long>, ProgramCustomRepository {

}
