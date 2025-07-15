package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.TestSuiteReport;
import com.innocito.testpilot.model.TestSuiteOverallReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TestSuiteReportRepository extends JpaRepository<TestSuiteReport, Long>, JpaSpecificationExecutor<TestSuiteReport> {

    @Query("select tsr.testSuite.id as testSuiteId, tsr.testSuite.name as testSuiteName, tsr.testSuite.creationDate as creationDate," +
            " count(tsr.testSuite.id) as total, " +
            "MAX(tsr.executionDate) as lastExecutionDate, " +
            "sum(case when tsr.status = 'Passed' then 1 else 0 end) as passed," +
            " sum(case when tsr.status = 'Failed' then 1 else 0 end) as failed " +
            "from TestSuiteReport tsr " +
            "where tsr.projectId = :projectId " +
            "and tsr.testSuite.activeStatus = 1 " +
            "and ((:searchText is null or tsr.testSuite.name like :searchText) or " +
            "(:searchText is null or tsr.testSuite.description like :searchText)) " +
            "and (:status is null or tsr.status = :status) " +
            "and (tsr.executionDate between :startDate and :endDate) " +
            "group by tsr.testSuite.id " +
            "order by lastExecutionDate desc")
    Page<TestSuiteOverallReport> getTestSuiteOverallReport(long projectId, String searchText,
                                                           Date startDate, Date endDate,
                                                           String status, Pageable pageable);
}