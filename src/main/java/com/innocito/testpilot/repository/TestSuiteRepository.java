package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.TestSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuite, Long>, JpaSpecificationExecutor<TestSuite> {

    Optional<TestSuite> findByIdAndActiveStatus(long id, int activeStatus);

    Optional<TestSuite> findByProjectIdAndName(long id, String name);
}