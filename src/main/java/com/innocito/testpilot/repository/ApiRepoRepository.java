package com.innocito.testpilot.repository;

import com.innocito.testpilot.entity.ApiRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiRepoRepository extends JpaRepository<ApiRepository, Long> {

    Optional<ApiRepository> findByIdAndActiveStatus(long id, int activeStatus);

    Optional<ApiRepository> findByProjectIdAndName(long projectId, String name);

    List<ApiRepository> findByProjectIdAndActiveStatus(long projectId, int activeStatus, Sort sort);

    List<ApiRepository> findByProjectIdAndActiveStatusAndRepositoryType(long projectId, int activeStatus, String repositoryType, Sort sort);
    List<ApiRepository> findByActiveStatusAndRepositoryType(int activeStatus, String repositoryType, Sort sort);
    List<ApiRepository> findByActiveStatus(int activeStatus, Sort sort);


//
//    @Query("""
//    SELECT r FROM ApiRepository r
//    WHERE r.project.id = :projectId
//      AND r.activeStatus = :activeStatus
//      AND (
//        LOWER(r.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
//        LOWER(r.description) LIKE LOWER(CONCAT('%', :searchText, '%'))
//      )
//""")
//    Page<ApiRepository> findByProjectIdAndActiveStatusAndSearch(
//            @Param("projectId") Long projectId,
//            @Param("activeStatus") int activeStatus,
//            @Param("searchText") String searchText,
//            Pageable pageable);
//
//}

    //    @Query("""
//                SELECT r FROM ApiRepository r
//                WHERE r.project.id = :id
//                  AND r.activeStatus = :activeStatus
//                  AND (
//                    LOWER(r.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
//                    LOWER(r.description) LIKE LOWER(CONCAT('%', :searchText, '%'))
//                  )
//            """)
//    Page<ApiRepository> findByProjectIdAndActiveStatusAndSearch(
//            @Param("id") Long id,
//            @Param("activeStatus") int activeStatus,
//            @Param("searchText") String searchText,
//            Pageable pageable);
//}
//    @Query("SELECT DISTINCT ar FROM ApiRepository ar " +
//            "LEFT JOIN ar.requests r " +
//            "WHERE ar.project.id = :projectId " +
//            "AND ar.activeStatus = 1 " +
//            "AND (:search IS NULL OR LOWER(ar.name) LIKE LOWER(:search) " +
//            "OR LOWER(r.name) LIKE LOWER(:search) " +
//            "OR LOWER(r.description) LIKE LOWER(:search))")
//    Page<ApiRepository> findActiveRepositoriesByProjectId(@Param("projectId") Long projectId,
//                                                          @Param("search") String search,
//                                                          Pageable pageable);

    @Query("SELECT r FROM ApiRepository r WHERE r.project.id = :projectId AND r.activeStatus = 1 " +
            "AND (:searchText IS NULL OR LOWER(r.name) LIKE :searchText)")
    Page<ApiRepository> findActiveRepositoriesByProjectId(@Param("projectId") Long projectId,
                                                          @Param("searchText") String searchText,
                                                          Pageable pageable);

}
