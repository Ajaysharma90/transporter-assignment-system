
package com.ajay.transporter.service;

import com.ajay.transporter.dto.InputRequest;
import com.ajay.transporter.dto.LaneInput;
import com.ajay.transporter.dto.TransporterInput;
import com.ajay.transporter.entity.Lane;
import com.ajay.transporter.entity.LaneQuote;
import com.ajay.transporter.entity.Transporter;
import com.ajay.transporter.repository.LaneQuoteRepository;
import com.ajay.transporter.repository.LaneRepository;
import com.ajay.transporter.repository.TransporterRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class InputService {

    private final LaneRepository laneRepository;
    private final TransporterRepository transporterRepository;
    private final LaneQuoteRepository laneQuoteRepository;

    public InputService(
            LaneRepository laneRepository,
            TransporterRepository transporterRepository,
            LaneQuoteRepository laneQuoteRepository) {

        this.laneRepository = laneRepository;
        this.transporterRepository = transporterRepository;
        this.laneQuoteRepository = laneQuoteRepository;
    }

    @Transactional
    public void saveInput(InputRequest request) {

        // Check for duplicate lane IDs
        Set<Long> laneIds = new HashSet<>();

        for (LaneInput laneInput : request.getLanes()) {
            if (!laneIds.add(laneInput.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate lane ID: " + laneInput.getId());
            }
        }

        // Check for duplicate transporter IDs
        Set<Long> transporterIds = new HashSet<>();

        for (TransporterInput transporterInput : request.getTransporters()) {
            if (!transporterIds.add(transporterInput.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate transporter ID: " + transporterInput.getId());
            }
        }

     // Remove previous dataset
laneQuoteRepository.deleteAllInBatch();
laneRepository.deleteAllInBatch();
transporterRepository.deleteAllInBatch();
        // Save lanes
        List<Lane> lanes = new ArrayList<>();
        Map<Long, Lane> laneMap = new HashMap<>();

        for (LaneInput input : request.getLanes()) {
            Lane lane = new Lane(
                    input.getId(),
                    input.getOrigin(),
                    input.getDestination()
            );

            lanes.add(lane);
            laneMap.put(lane.getId(), lane);
        }

        laneRepository.saveAll(lanes);

        // Save transporters
        List<Transporter> transporters = new ArrayList<>();
        Map<Long, Transporter> transporterMap = new HashMap<>();

        for (TransporterInput input : request.getTransporters()) {
            Transporter transporter = new Transporter(
                    input.getId(),
                    input.getName()
            );

            transporters.add(transporter);
            transporterMap.put(transporter.getId(), transporter);
        }

        transporterRepository.saveAll(transporters);

        

        // Create and save new lane quotes
        List<LaneQuote> quotes = new ArrayList<>();
        Set<String> quoteKeys = new HashSet<>();

        for (TransporterInput transporterInput : request.getTransporters()) {

            Transporter transporter =
                    transporterMap.get(transporterInput.getId());

            for (TransporterInput.LaneQuoteInput quoteInput
                    : transporterInput.getLaneQuotes()) {

                Lane lane = laneMap.get(quoteInput.getLaneId());

                if (lane == null) {
                    throw new IllegalArgumentException(
                            "Unknown lane ID: " + quoteInput.getLaneId());
                }

                String quoteKey =
                        transporter.getId() + ":" + lane.getId();

                if (!quoteKeys.add(quoteKey)) {
                    throw new IllegalArgumentException(
                            "Duplicate quote for transporter "
                                    + transporter.getId()
                                    + " and lane "
                                    + lane.getId());
                }

                LaneQuote quote = new LaneQuote(
                        lane,
                        transporter,
                        quoteInput.getQuote()
                );

                quotes.add(quote);
            }
        }

        laneQuoteRepository.saveAll(quotes);
    }
}
