package com.jobtracker.service;

import com.jobtracker.dto.JobRequest;
import com.jobtracker.dto.JobResponse;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.Status;
import com.jobtracker.entity.User;
import com.jobtracker.exception.NotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobApplicationRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;

    private JobRequest request(Status status) {
        return new JobRequest("Virtusa", "Associate Engineer", "Chennai", status,
                "https://example.com/job", "Referred by a friend", LocalDate.of(2026, 11, 24));
    }

    @Test
    void createSavesJobForTheLoggedInUser() {
        User user = new User();
        user.setEmail("ari@example.com");
        when(userRepository.findByEmail("ari@example.com")).thenReturn(Optional.of(user));
        when(jobRepository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        JobResponse response = jobService.create("ari@example.com", request(Status.APPLIED));

        assertEquals("Virtusa", response.company());
        assertEquals(Status.APPLIED, response.status());
        verify(jobRepository).save(argThat(job -> job.getUser() == user));
    }

    @Test
    void getThrowsNotFoundWhenJobBelongsToSomeoneElse() {
        when(jobRepository.findByIdAndUserEmail(5L, "other@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> jobService.get("other@example.com", 5L));
    }

    @Test
    void updateStatusChangesOnlyTheStatus() {
        JobApplication job = new JobApplication();
        job.setCompany("Virtusa");
        job.setStatus(Status.APPLIED);
        when(jobRepository.findByIdAndUserEmail(1L, "ari@example.com")).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);

        JobResponse response = jobService.updateStatus("ari@example.com", 1L, Status.INTERVIEW);

        assertEquals(Status.INTERVIEW, response.status());
        assertEquals("Virtusa", response.company());
    }

    @Test
    void statsIncludesEveryStatusEvenWhenCountIsZero() {
        when(jobRepository.countByStatus("ari@example.com"))
                .thenReturn(java.util.List.<Object[]>of(new Object[]{Status.APPLIED, 3L}));

        Map<Status, Long> stats = jobService.stats("ari@example.com");

        assertEquals(3L, stats.get(Status.APPLIED));
        assertEquals(0L, stats.get(Status.OFFER));
        assertEquals(Status.values().length, stats.size());
    }
}
