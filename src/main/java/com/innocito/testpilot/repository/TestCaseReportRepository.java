package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.TestCaseReport;
import com.innocito.testpilot.model.TestCaseOverallReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TestCaseReportRepository extends JpaRepository<TestCaseReport, Long>, JpaSpecificationExecutor<TestCaseReport> {
    
    /*@Query("select tcr.testCase.id as testCaseId, tcr.testCase.name as testCaseName, tcr.testCase.creationDate as creationDate," +
            " count(tcr.testCase.id) as total, " +
            "sum(case when tcr.status = 'Passed' then 1 else 0 end) as passed," +
            " sum(case when tcr.status = 'Failed' then 1 else 0 end) as failed " +
            "from TestCaseReport tcr " +
            "where tcr.projectId = :projectId " +
            "and tcr.testCase.activeStatus = 1 " +
            "and ((:searchText is null or tcr.testCase.name like :searchText) or " +
            "(:searchText is null or tcr.testCase.description like :searchText)) " +
            "and (tcr.executionDate between :startDate and :endDate) " +
            "and tcr.testCase.request.apiRepository.repositoryType = :repositoryType " +
            "group by tcr.testCase.id " +
            "order by testCaseId asc")
    Page<TestCaseOverallReport> getTestCaseOverallReport(long projectId, String searchText,
                                                         Date startDate, Date endDate, String status,
                                                         String repositoryType, Pageable pageable);*/

    @Query("select tcr.testCase.id as testCaseId, tcr.testCase.name as testCaseName, tcr.testCase.creationDate as creationDate," +
            " count(tcr.testCase.id) as total, " +
            "MAX(tcr.executionDate) as lastExecutionDate, " +
            "sum(case when tcr.status = 'Passed' then 1 else 0 end) as passed," +
            " sum(case when tcr.status = 'Failed' then 1 else 0 end) as failed " +
            "from TestCaseReport tcr " +
            "where tcr.projectId = :projectId " +
            "and tcr.testCase.activeStatus = 1 " +
            "and ((:searchText is null or tcr.testCase.name like :searchText) or " +
            "(:searchText is null or tcr.testCase.description like :searchText)) " +
            "and (:status is null or tcr.status = :status) " +
            "and (tcr.executionDate between :startDate and :endDate) " +
            "and tcr.testCase.request.apiRepository.repositoryType = :repositoryType " +
            "group by tcr.testCase.id " +
            "order by lastExecutionDate desc")
    Page<TestCaseOverallReport> getTestCaseOverallReport(long projectId, String searchText,
                                                         Date startDate, Date endDate, String status,
                                                         String repositoryType, Pageable pageable);
}