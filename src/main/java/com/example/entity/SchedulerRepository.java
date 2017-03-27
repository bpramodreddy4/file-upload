package com.example.entity;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SchedulerRepository extends CrudRepository<SchedulerRun, Integer> {

    @Query("SELECT run FROM SchedulerRun run ORDER BY run.id DESC")
    List<SchedulerRun> findLastRun(Pageable pageRequest);
}
