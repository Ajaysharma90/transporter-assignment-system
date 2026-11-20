
package com.ajay.transporter.dto;

import java.math.BigDecimal;
import java.util.List;

public class AssignmentResponse {

    private String status;
    private BigDecimal totalCost;
    private List<LaneAssignment> assignments;
    private List<SelectedTransporter> selectedTransporters;

    public AssignmentResponse() {
    }

    public AssignmentResponse(
            String status,
            BigDecimal totalCost,
            List<LaneAssignment> assignments,
            List<SelectedTransporter> selectedTransporters) {

        this.status = status;
        this.totalCost = totalCost;
        this.assignments = assignments;
        this.selectedTransporters = selectedTransporters;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public List<LaneAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<LaneAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<SelectedTransporter> getSelectedTransporters() {
        return selectedTransporters;
    }

    public void setSelectedTransporters(List<SelectedTransporter> selectedTransporters) {
        this.selectedTransporters = selectedTransporters;
    }
}
