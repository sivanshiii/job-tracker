package com.jobtracker.controller;

import com.jobtracker.dto.JobRequest;
import com.jobtracker.dto.JobResponse;
import com.jobtracker.dto.PageResponse;
import com.jobtracker.dto.StatusUpdateRequest;
import com.jobtracker.entity.Status;
import com.jobtracker.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public PageResponse<JobResponse> list(Authentication auth,
                                          @RequestParam(required = false) Status status,
                                          @RequestParam(required = false) String q,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return jobService.list(auth.getName(), status, q, page, size);
    }

    @GetMapping("/stats")
    public Map<Status, Long> stats(Authentication auth) {
        return jobService.stats(auth.getName());
    }

    @GetMapping("/{id}")
    public JobResponse get(Authentication auth, @PathVariable Long id) {
        return jobService.get(auth.getName(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(Authentication auth, @Valid @RequestBody JobRequest request) {
        return jobService.create(auth.getName(), request);
    }

    @PutMapping("/{id}")
    public JobResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody JobRequest request) {
        return jobService.update(auth.getName(), id, request);
    }

    @PatchMapping("/{id}/status")
    public JobResponse updateStatus(Authentication auth, @PathVariable Long id,
                                    @Valid @RequestBody StatusUpdateRequest request) {
        return jobService.updateStatus(auth.getName(), id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable Long id) {
        jobService.delete(auth.getName(), id);
    }
}
