package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, MissionCustomRepository {
}
