package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.Request;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByIdAndActiveStatus(long id, int activeStatus);

    List<Request> findByProjectIdAndActiveStatus(long projectId, int activeStatus, Sort sort);

    Optional<Request> findByApiRepositoryIdAndName(long apiRepoId, String name);
}