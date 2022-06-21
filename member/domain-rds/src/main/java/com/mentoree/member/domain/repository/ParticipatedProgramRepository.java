package com.mentoree.member.domain.repository;

import com.mentoree.member.domain.entity.ParticipatedProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipatedProgramRepository extends JpaRepository<ParticipatedProgram, Long> {

}
