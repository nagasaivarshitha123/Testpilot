package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.TestSuiteTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSuiteTestCaseRepository extends JpaRepository<TestSuiteTestCase, Long> {

    List<TestSuiteTestCase> findAllByTestSuiteIdAndActiveStatusAndTestCaseActiveStatus(long testSuiteId,
                                                                                       int activeStatus,
                                                                                       int testCaseActiveStatus);

    Optional<TestSuiteTestCase> findByTestSuiteIdAndTestCaseId(long testSuiteId, long testCaseId);

    List<TestSuiteTestCase> findAllByTestSuiteIdAndTestCaseIdIn(long testSuiteId, List<Long> testCaseIds);

    List<TestSuiteTestCase> findAllByTestSuiteIdAndActiveStatusAndEnabledAndTestCaseActiveStatus(long testSuiteId,
                                                                                                 int activeStatus,
                                                                                                 int enabled,
                                                                                                 int testCaseActiveStatus);

    boolean existsByTestCaseIdAndActiveStatusAndTestSuiteActiveStatus(long testCaseId,
                                                                      int activeStatus,
                                                                      int testSuiteActiveStatus);
}