package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.TestCaseAssertionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseAssertionResponseRepository extends JpaRepository<TestCaseAssertionResponse, Long> {

}