package com.example;

import com.example.entity.FileRepository;
import com.example.entity.SchedulerRepository;
import com.example.entity.SchedulerRun;
import com.example.entity.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Configuration
@EnableScheduling
public class SendEmailsTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailsTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static final int SECONDS = 1 * 1000;

    public static final int MINUTES = 60 * SECONDS;

    public static final int HOURS = 60 * MINUTES;


    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private SchedulerRepository schedulerRepository;

    @Scheduled(fixedRate = 30 * SECONDS)
//    @Scheduled(fixedRate = 1 * HOURS)
    public void findNewUploadsAndSendEmail() {

        LOGGER.info("Scheduler start at " + LocalDateTime.now());

        final List<SchedulerRun> recentRuns = schedulerRepository.findLastRun(new PageRequest(0, 1));
        final SchedulerRun run = new SchedulerRun();
        schedulerRepository.save(run);
        final Integer runId = run.getId();
        LOGGER.info("Scheduler Run Id : " + runId);

        int newUploadCount = 0;

        SchedulerRun lastRun;


        lastRun = recentRuns.size() > 0 ? recentRuns.get(0) : null;
        Integer lastId = 0;

        if (lastRun != null && lastRun.getEndId() != null)
            lastId = lastRun.getEndId();

        final List<Upload> lastHourUploads = fileRepository.findByIdGreaterThanEqual(lastId);
        newUploadCount = lastHourUploads.size();

        this.sendEmails(lastHourUploads);
        final LocalDateTime completedAt = LocalDateTime.now();

        LOGGER.info("Scheduler completed at " + completedAt);

        //Update the scheduler run details into the database
        final SchedulerRun run2;
//        run2 = schedulerRepository.findOne(runId);
        run2 = run;

/*
        final Comparator<Upload> idComparator = (a, b) -> a.getId().compareTo(b.getId());
        final Optional<Upload> max = lastHourUploads.stream().max(idComparator);
        final Optional<Upload> min = lastHourUploads.stream().min(idComparator);

        if (min.isPresent()) {
            run2.setStartId(min.get().getId());
        }
        if (max.isPresent()) {
            run2.setEndId(max.get().getId());
        }*/

        run2.setNewUploadCount(newUploadCount);
        run2.setCompletedAt(completedAt);

        schedulerRepository.save(run2);

        LOGGER.info(String.format("Found New Uploads %d", newUploadCount));
    }

    public void sendEmails(List<Upload> lastHourUploads) {
        //TODO: Core for sending the mails
    }

    public FileRepository getFileRepository() {
        return fileRepository;
    }

    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public SchedulerRepository getSchedulerRepository() {
        return schedulerRepository;
    }

    public void setSchedulerRepository(SchedulerRepository schedulerRepository) {
        this.schedulerRepository = schedulerRepository;
    }
}