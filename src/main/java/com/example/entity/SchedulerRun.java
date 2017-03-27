package com.example.entity;

import com.example.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduler_runs")
public class SchedulerRun implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(JsonViews.MetaView.class)

    private Integer id;

    private LocalDateTime startedAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    @Column(nullable = true)
    private Integer startId;

    @Column(nullable = true)
    private Integer endId;

    private Integer newUploadCount = 0;

    private LocalDateTime created = startedAt;

    private LocalDateTime upadated;

    private boolean completed = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getStartId() {
        return startId;
    }

    public void setStartId(Integer startId) {
        this.startId = startId;
    }

    public Integer getEndId() {
        return endId;
    }

    public void setEndId(Integer endId) {
        this.endId = endId;
    }

    public Integer getNewUploadCount() {
        return newUploadCount;
    }

    public void setNewUploadCount(Integer newUploadCount) {
        this.newUploadCount = newUploadCount;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpadated() {
        return upadated;
    }

    public void setUpadated(LocalDateTime upadated) {
        this.upadated = upadated;
    }
}
