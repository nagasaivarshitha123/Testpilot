package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.TestCaseAssertion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseAssertionRepository extends JpaRepository<TestCaseAssertion, Long> {

    Optional<TestCaseAssertion> findByIdAndActiveStatus(long id, int activeStatus);

    List<TestCaseAssertion> findAllByTestCaseIdAndActiveStatus(Long testCaseId, int activeStatus, Sort sort);
}