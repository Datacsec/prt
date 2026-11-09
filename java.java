package com.bnpp.irb.datalake.portail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bnpp.irb.datalake.portail.models.ControleHistory;

public interface ControleHistoryRepository extends JpaRepository<ControleHistory, Long> {

    boolean existsByControleIdAndDepartmentIdentityAndMoisControle(
            String controleId, String departmentIdentity, String moisControle);
}
