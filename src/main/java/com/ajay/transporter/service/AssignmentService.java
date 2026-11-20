
package com.ajay.transporter.service;

import com.ajay.transporter.dto.AssignmentResponse;
import com.ajay.transporter.dto.LaneAssignment;
import com.ajay.transporter.dto.SelectedTransporter;
import com.ajay.transporter.entity.Lane;
import com.ajay.transporter.entity.LaneQuote;
import com.ajay.transporter.entity.Transporter;
import com.ajay.transporter.repository.LaneQuoteRepository;
import com.ajay.transporter.repository.LaneRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssignmentService {

    private final LaneRepository laneRepository;
    private final LaneQuoteRepository laneQuoteRepository;

    public AssignmentService(
            LaneRepository laneRepository,
            LaneQuoteRepository laneQuoteRepository) {

        this.laneRepository = laneRepository;
        this.laneQuoteRepository = laneQuoteRepository;
    }

    public AssignmentResponse assignTransporters(int maxTransporters) {

        List<Lane> lanes = laneRepository.findAll();
        List<LaneQuote> quotes = laneQuoteRepository.findAll();

        if (lanes.isEmpty()) {
            throw new IllegalArgumentException("No lanes available.");
        }

        if (quotes.isEmpty()) {
            throw new IllegalArgumentException("No transporter quotes available.");
        }

        Map<Long, List<LaneQuote>> quotesByLane = new HashMap<>();
        Map<Long, Transporter> transporterMap = new HashMap<>();

        for (LaneQuote quote : quotes) {

            Long laneId = quote.getLane().getId();
            Long transporterId = quote.getTransporter().getId();

            quotesByLane
                    .computeIfAbsent(laneId, key -> new ArrayList<>())
                    .add(quote);

            transporterMap.put(transporterId, quote.getTransporter());
        }

        // Verify that every lane has at least one quote.
        for (Lane lane : lanes) {

            if (!quotesByLane.containsKey(lane.getId())) {

                throw new IllegalArgumentException(
                        "No transporter quote available for lane: "
                                + lane.getId());
            }
        }

        List<Transporter> transporters =
                new ArrayList<>(transporterMap.values());

        transporters.sort(Comparator.comparing(Transporter::getId));

        int maximum = Math.min(maxTransporters, transporters.size());

        // Try the largest possible transporter group first.
        for (int size = maximum; size >= 1; size--) {

            SearchResult bestResult = new SearchResult();

            generateCombinations(
                    transporters,
                    size,
                    0,
                    new ArrayList<>(),
                    lanes,
                    quotesByLane,
                    bestResult
            );

            if (bestResult.cost != null) {
                return buildResponse(bestResult);
            }
        }

        throw new IllegalArgumentException(
                "Unable to assign all lanes within the transporter limit.");
    }

    private void generateCombinations(
            List<Transporter> transporters,
            int targetSize,
            int start,
            List<Transporter> selected,
            List<Lane> lanes,
            Map<Long, List<LaneQuote>> quotesByLane,
            SearchResult bestResult) {

        if (selected.size() == targetSize) {

            evaluateCombination(
                    selected,
                    lanes,
                    quotesByLane,
                    bestResult
            );

            return;
        }

        for (int i = start;
             i <= transporters.size() - (targetSize - selected.size());
             i++) {

            selected.add(transporters.get(i));

            generateCombinations(
                    transporters,
                    targetSize,
                    i + 1,
                    selected,
                    lanes,
                    quotesByLane,
                    bestResult
            );

            selected.remove(selected.size() - 1);
        }
    }

    // Dynamic programming: find the minimum cost while using
    // every transporter in the selected combination.
    private void evaluateCombination(
            List<Transporter> selected,
            List<Lane> lanes,
            Map<Long, List<LaneQuote>> quotesByLane,
            SearchResult bestResult) {

        Map<Long, Integer> transporterIndexes = new HashMap<>();

        for (int i = 0; i < selected.size(); i++) {

            transporterIndexes.put(
                    selected.get(i).getId(),
                    i
            );
        }

        // A bit represents whether a transporter has been used.
        BigInteger fullMask = BigInteger.ONE
                .shiftLeft(selected.size())
                .subtract(BigInteger.ONE);

        // Map: transporter usage mask -> minimum-cost assignment.
        Map<BigInteger, AssignmentState> dp = new HashMap<>();

        dp.put(
                BigInteger.ZERO,
                new AssignmentState(
                        BigDecimal.ZERO,
                        new ArrayList<>()
                )
        );

        // Process each lane.
        for (Lane lane : lanes) {

            Map<BigInteger, AssignmentState> nextDp = new HashMap<>();

            List<LaneQuote> laneQuotes =
                    quotesByLane.get(lane.getId());

            for (Map.Entry<BigInteger, AssignmentState> entry
                    : dp.entrySet()) {

                BigInteger currentMask = entry.getKey();

                AssignmentState currentState = entry.getValue();

                for (LaneQuote quote : laneQuotes) {

                    Long transporterId =
                            quote.getTransporter().getId();

                    Integer index =
                            transporterIndexes.get(transporterId);

                    // Ignore transporters outside this combination.
                    if (index == null) {
                        continue;
                    }

                    // Mark this transporter as used.
                    BigInteger newMask = currentMask.setBit(index);

                    BigDecimal newCost =
                            currentState.cost.add(quote.getQuote());

                    List<LaneAssignment> newAssignments =
                            new ArrayList<>(currentState.assignments);

                    newAssignments.add(
                            new LaneAssignment(
                                    lane.getId(),
                                    transporterId
                            )
                    );

                    AssignmentState existing =
                            nextDp.get(newMask);

                    // Retain the cheapest assignment for this mask.
                    if (existing == null
                            || newCost.compareTo(existing.cost) < 0) {

                        nextDp.put(
                                newMask,
                                new AssignmentState(
                                        newCost,
                                        newAssignments
                                )
                        );
                    }
                }
            }

            dp = nextDp;

            // This transporter combination cannot cover all lanes.
            if (dp.isEmpty()) {
                return;
            }
        }

        // A valid solution must use every selected transporter.
        AssignmentState finalState = dp.get(fullMask);

        if (finalState == null) {
            return;
        }

        // Update the best solution.
        if (bestResult.cost == null
                || finalState.cost.compareTo(bestResult.cost) < 0) {

            bestResult.cost = finalState.cost;

            bestResult.assignments =
                    finalState.assignments;

            bestResult.selectedTransporters =
                    new ArrayList<>(selected);
        }
    }

    private AssignmentResponse buildResponse(SearchResult result) {

        List<SelectedTransporter> selected = new ArrayList<>();

        for (Transporter transporter : result.selectedTransporters) {

            selected.add(
                    new SelectedTransporter(
                            transporter.getId(),
                            transporter.getName()
                    )
            );
        }

        return new AssignmentResponse(
                "success",
                result.cost,
                result.assignments,
                selected
        );
    }

    private static class SearchResult {

        private BigDecimal cost;

        private List<LaneAssignment> assignments;

        private List<Transporter> selectedTransporters;
    }

    private static class AssignmentState {

        private BigDecimal cost;

        private List<LaneAssignment> assignments;

        private AssignmentState(
                BigDecimal cost,
                List<LaneAssignment> assignments) {

            this.cost = cost;
            this.assignments = assignments;
        }
    }
}
