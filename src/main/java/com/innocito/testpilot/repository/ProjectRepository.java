package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.ApiRepository;
import com.innocito.testpilot.entity.Project;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndActiveStatus(long id, int activeStatus);

    List<Project> findAllByActiveStatus(int  activeStatus);

    Optional<Project> findByIdAndActiveStatusAndCreatedBy(Long id, Integer activeStatus, String createdBy);


    List<Project> findAllByActiveStatusAndCreatedBy(Integer activeStatus, String createdBy);
}