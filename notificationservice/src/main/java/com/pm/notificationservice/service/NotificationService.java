package com.pm.notificationservice.service;

import com.pm.asteroidalerting.event.AsteroidCollisionEvent;
import com.pm.notificationservice.entity.Notification;
import com.pm.notificationservice.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }


    @KafkaListener(topics = "asteroid-alert", groupId = "notification-service")
    public void alterEvent(AsteroidCollisionEvent notificationEvent) {
        log.info("Received Asteroid Collision Event {}", notificationEvent);

        final Notification notification = Notification.builder()
                .asteroidName(notificationEvent.getAsteroidName())
                .closeApproachDate(LocalDate.parse(notificationEvent.getCloseApproachDate()))
                .estimatedDiameterAvgMeters(notificationEvent.getEstimatedDiameterAvgMeters())
                .missDistanceKilometers(new BigDecimal(notificationEvent.getMissDistanceKilometers()))
                .emailSent(false)
                .build();

        final Notification savedNotification = notificationRepository.save(notification);
        log.info("Saved Notification {}", savedNotification);
    }

    @Scheduled(fixedRate = 10000)
    public void sendEmail() {
        log.info("Sending email");
        emailService.sendAsteroidAlterEmail();
    }
}
