package com.jobtracker.repository;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    @Query("""
            select j from JobApplication j
            where j.user.email = :email
              and (lower(j.company) like lower(concat('%', :q, '%'))
                   or lower(j.role) like lower(concat('%', :q, '%')))
            """)
    Page<JobApplication> search(@Param("email") String email,
                                @Param("q") String q,
                                Pageable pageable);

    @Query("""
            select j from JobApplication j
            where j.user.email = :email
              and j.status = :status
              and (lower(j.company) like lower(concat('%', :q, '%'))
                   or lower(j.role) like lower(concat('%', :q, '%')))
            """)
    Page<JobApplication> searchByStatus(@Param("email") String email,
                                        @Param("status") Status status,
                                        @Param("q") String q,
                                        Pageable pageable);

    Optional<JobApplication> findByIdAndUserEmail(Long id, String email);

    @Query("select j.status, count(j) from JobApplication j where j.user.email = :email group by j.status")
    List<Object[]> countByStatus(@Param("email") String email);
}
