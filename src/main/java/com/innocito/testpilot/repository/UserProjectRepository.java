package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
}