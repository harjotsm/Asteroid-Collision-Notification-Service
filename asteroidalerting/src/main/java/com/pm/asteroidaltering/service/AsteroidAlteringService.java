package com.pm.asteroidaltering.service;

import com.pm.asteroidaltering.client.NasaClient;
import com.pm.asteroidaltering.dto.Asteroid;
import com.pm.asteroidaltering.event.AsteroidCollisionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AsteroidAlteringService {

    private final NasaClient nasaClient;
    private final KafkaTemplate<String, AsteroidCollisionEvent> kafkaTemplate;

    @Autowired
    public AsteroidAlteringService(NasaClient nasaClient, KafkaTemplate<String, AsteroidCollisionEvent> kafkaTemplate) {
        this.nasaClient = nasaClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void alert() {
        log.info("Alerting service called");

        final LocalDate fromDate = LocalDate.now();
        final LocalDate toDate = LocalDate.now().plusDays(7);

        log.info("Getting asteroid list for dates: {} to {}", fromDate, toDate);
        final List<Asteroid> asteroidList = nasaClient.getNeoAsteroids(fromDate, toDate);
        log.info("Retrieved Asteroid list of size: {}", asteroidList.size());

        final List<Asteroid> dangerousAsteroids = asteroidList.stream()
                .filter(Asteroid::isPotentiallyHazardousAsteroid)
                .toList();
        log.info("Found {} hazardous asteroids", dangerousAsteroids.size());

        // create kafka alert
        final List<AsteroidCollisionEvent> asteroidCollisionEventList = createEventLisOfDangerousAsteroids(dangerousAsteroids);
        log.info("Sending {} asteroid alerts to Kafka", asteroidCollisionEventList.size());

        asteroidCollisionEventList.forEach(event -> {
            kafkaTemplate.send("asteroid-alerts", event);
            log.info("Sent alert for asteroid: {} | Close Approach Date: {} | Miss Distance: {} km | Estimated Diameter Avg: {} meters", 
                    event.getAsteroidName(), 
                    event.getCloseApproachDate(), 
                    event.getMissDistanceKilometers(), 
                    event.getEstimatedDiameterAvgMeters());
        });
    }

    private List<AsteroidCollisionEvent> createEventLisOfDangerousAsteroids(final List<Asteroid> dangerousAsteroids) {
        return dangerousAsteroids.stream()
                .map(asteroid -> {
                    if (asteroid.isPotentiallyHazardousAsteroid()) {
                        return AsteroidCollisionEvent.builder()
                                .asteroidName(asteroid.getName())
                                .closeApproachDate(asteroid.getCloseApproachData().get(0).getCloseApproachDate().toString())
                                .missDistanceKilometers(asteroid.getCloseApproachData().get(0).getMissDistance().getKilometers())
                                .estimatedDiameterAvgMeters((asteroid.getEstimatedDiameter().getMeters().getMinDiametre() +
                                        asteroid.getEstimatedDiameter().getMeters().getMaxDiametre()) / 2)
                                .build();
                    }
                    return null;
                }).toList();
    }
}
