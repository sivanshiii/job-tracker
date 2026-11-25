package com.jobtracker.service;

import com.jobtracker.dto.JobRequest;
import com.jobtracker.dto.JobResponse;
import com.jobtracker.dto.PageResponse;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.Status;
import com.jobtracker.entity.User;
import com.jobtracker.exception.NotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
public class JobService {

    private static final int MAX_PAGE_SIZE = 50;

    private final JobApplicationRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobApplicationRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> list(String email, Status status, String q, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        String term = q == null ? "" : q.trim();
        Page<JobApplication> result = status == null
                ? jobRepository.search(email, term, pageable)
                : jobRepository.searchByStatus(email, status, term, pageable);
        return PageResponse.from(result.map(JobResponse::from));
    }

    @Transactional(readOnly = true)
    public JobResponse get(String email, Long id) {
        return JobResponse.from(findOwned(email, id));
    }

    @Transactional
    public JobResponse create(String email, JobRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        JobApplication job = new JobApplication();
        job.setUser(user);
        apply(job, request);
        return JobResponse.from(jobRepository.save(job));
    }

    @Transactional
    public JobResponse update(String email, Long id, JobRequest request) {
        JobApplication job = findOwned(email, id);
        apply(job, request);
        return JobResponse.from(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateStatus(String email, Long id, Status status) {
        JobApplication job = findOwned(email, id);
        job.setStatus(status);
        return JobResponse.from(jobRepository.save(job));
    }

    @Transactional
    public void delete(String email, Long id) {
        jobRepository.delete(findOwned(email, id));
    }

    @Transactional(readOnly = true)
    public Map<Status, Long> stats(String email) {
        Map<Status, Long> counts = new EnumMap<>(Status.class);
        for (Status s : Status.values()) {
            counts.put(s, 0L);
        }
        for (Object[] row : jobRepository.countByStatus(email)) {
            counts.put((Status) row[0], (Long) row[1]);
        }
        return counts;
    }

    /** Looks up a job only if it belongs to this user, so users can never touch each other's data. */
    private JobApplication findOwned(String email, Long id) {
        return jobRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    private void apply(JobApplication job, JobRequest r) {
        job.setCompany(r.company().trim());
        job.setRole(r.role().trim());
        job.setLocation(r.location());
        job.setStatus(r.status());
        job.setJobUrl(r.jobUrl());
        job.setNotes(r.notes());
        job.setAppliedDate(r.appliedDate());
    }
}
