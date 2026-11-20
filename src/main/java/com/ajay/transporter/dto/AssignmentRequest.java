
package com.ajay.transporter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AssignmentRequest {

    @NotNull(message = "Maximum transporters is required")
    @Min(value = 1, message = "Maximum transporters must be at least 1")
    private Integer maxTransporters;

    public AssignmentRequest() {
    }

    public AssignmentRequest(Integer maxTransporters) {
        this.maxTransporters = maxTransporters;
    }

    public Integer getMaxTransporters() {
        return maxTransporters;
    }

    public void setMaxTransporters(Integer maxTransporters) {
        this.maxTransporters = maxTransporters;
    }
}
