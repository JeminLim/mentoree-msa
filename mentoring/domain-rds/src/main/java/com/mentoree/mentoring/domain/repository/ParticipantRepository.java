package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantCustomRepository {

    @Query("select pm.program.id from Participant pm "
            + "join Program p on pm.program.id = p.id "
            + "where pm.memberId = :memberId and pm.approval = true")
    List<Long> findProgramIdByMemberId(@Param("memberId") Long memberId);

    @Query("select count(pm) from Participant pm " +
            "where pm.memberId = :memberId and pm.program.id = :programId")
    Long isApplicant(@Param("memberId") Long memberId, @Param("programId") Long programId);

    @Query("select pm from Participant pm " +
            "where pm.memberId = :memberId and pm.program.id = :programId")
    Optional<Participant> findParticipantByProgramIdAndMemberId(@Param("memberId") Long memberId, @Param("programId") Long programId);

    @Query("select count(pm) from Participant pm " +
            "where pm.program.id = :programId and pm.approval = true")
    Long countCurrentMember(@Param("programId") Long programId);

    @Query("select count(pm) from Participant pm " +
            "where pm.program.id = :programId and pm.memberId = :memberId and pm.isHost = true")
    Long isHost(@Param("programId") Long programId, @Param("memberId") Long memberId);

}
