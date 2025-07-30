package com.autonomic.java.service.healing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct; // For @PostConstruct

/**
 * The HealingService orchestrates the remediation process.
 * It maps detected AnomalyTypes to specific RemediationAction implementations
 * and executes them to restore service health.
 */
@Service
@RequiredArgsConstructor // Lombok: Generates constructor for final fields
@Slf4j // Lombok: Provides a logger
public class HealingService {

    // Spring injects all beans that implement RemediationAction interface into this list
    private final List<RemediationAction> remediationActions;

    // A map to quickly look up remediation actions by their associated AnomalyType
    private Map<AnomalyType, RemediationAction> actionMap;

    /**
     * This method is called after dependency injection is complete.
     * It initializes the actionMap by mapping each RemediationAction to its
     * corresponding AnomalyType. This allows for efficient lookup of the correct
     * healing action based on the detected anomaly.
     */
    @PostConstruct
    public void init() {
        actionMap = remediationActions.stream()
                .collect(Collectors.toMap(RemediationAction::getAnomalyType, Function.identity()));
        log.info("HealingService initialized with {} remediation actions.", actionMap.size());
        actionMap.forEach((type, action) -> log.info("- AnomalyType: {}, Action: {}", type, action.getActionName()));
    }

    /**
     * Performs the appropriate healing action based on the detected anomaly type.
     * @param anomalyType The type of anomaly detected by the AnomalyDetector.
     */
    public void performHealing(AnomalyType anomalyType) {
        log.info("HealingService: Attempting to perform healing for anomaly type: {}", anomalyType);

        RemediationAction action = actionMap.get(anomalyType);

        if (action != null) {
            log.info("HealingService: Found remediation action '{}' for {}. Executing...", action.getActionName(), anomalyType);
            try {
                action.remediate(); // Execute the specific healing action
                log.info("HealingService: Remediation for {} completed successfully.", anomalyType);
            } catch (Exception e) {
                log.error("HealingService: Error during remediation for {}: {}", anomalyType, e.getMessage(), e);
                // In a real system, you might trigger alerts or fallback actions here
            }
        } else {
            log.warn("HealingService: No specific remediation action found for anomaly type: {}", anomalyType);
            // Fallback: log, alert, or trigger a generic restart
        }
    }
}