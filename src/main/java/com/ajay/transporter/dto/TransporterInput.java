package com.ajay.transporter.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public class TransporterInput {

    @NotNull(message = "Transporter ID is required")
    private Long id;

    @NotBlank(message = "Transporter name is required")
    private String name;

    @NotEmpty(message = "At least one lane quote is required")
    @Valid
    private List<LaneQuoteInput> laneQuotes;

    public TransporterInput() {
    }

    public TransporterInput(Long id, String name, List<LaneQuoteInput> laneQuotes) {
        this.id = id;
        this.name = name;
        this.laneQuotes = laneQuotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LaneQuoteInput> getLaneQuotes() {
        return laneQuotes;
    }

    public void setLaneQuotes(List<LaneQuoteInput> laneQuotes) {
        this.laneQuotes = laneQuotes;
    }

    public static class LaneQuoteInput {

        @NotNull(message = "Lane ID is required")
        private Long laneId;

        @NotNull(message = "Quote is required")
        private BigDecimal quote;

        public LaneQuoteInput() {
        }

        public LaneQuoteInput(Long laneId, BigDecimal quote) {
            this.laneId = laneId;
            this.quote = quote;
        }

        public Long getLaneId() {
            return laneId;
        }

        public void setLaneId(Long laneId) {
            this.laneId = laneId;
        }

        public BigDecimal getQuote() {
            return quote;
        }

        public void setQuote(BigDecimal quote) {
            this.quote = quote;
        }
    }
}