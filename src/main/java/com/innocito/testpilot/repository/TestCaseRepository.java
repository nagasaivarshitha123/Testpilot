package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long>, JpaSpecificationExecutor<TestCase> {
    Optional<TestCase> findByIdAndActiveStatus(long id, int activeStatus);

    Optional<TestCase> findByProjectIdAndName(long id, String name);

    Optional<TestCase> findByNameAndActiveStatusAndProjectId(String name, int activeStatus, long id);
}