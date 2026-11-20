
package com.ajay.transporter.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class InputRequest {

    @NotEmpty(message = "At least one lane is required")
    @Valid
    private List<LaneInput> lanes;

    @NotEmpty(message = "At least one transporter is required")
    @Valid
    private List<TransporterInput> transporters;

    public InputRequest() {
    }

    public InputRequest(List<LaneInput> lanes, List<TransporterInput> transporters) {
        this.lanes = lanes;
        this.transporters = transporters;
    }

    public List<LaneInput> getLanes() {
        return lanes;
    }

    public void setLanes(List<LaneInput> lanes) {
        this.lanes = lanes;
    }

    public List<TransporterInput> getTransporters() {
        return transporters;
    }

    public void setTransporters(List<TransporterInput> transporters) {
        this.transporters = transporters;
    }
}
