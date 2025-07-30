package com.autonomic.java.service.healing;

/**
 * Interface for all remediation actions.
 * Any class that implements this interface can be considered a self-healing action.
 * This promotes a clear contract for how healing actions should be defined and executed.
 */
public interface RemediationAction {

    /**
     * Executes the specific healing logic for an identified anomaly.
     * This method should contain the steps required to mitigate or resolve the issue.
     */
    void remediate();

    /**
     * Returns a human-readable name for the action, useful for logging and identification.
     * @return The name of the remediation action.
     */
    String getActionName();

    /**
     * Returns the AnomalyType this remediation action is designed to address.
     * This is used by the HealingService to map anomalies to their specific actions.
     * @return The AnomalyType associated with this action.
     */
    AnomalyType getAnomalyType();
}