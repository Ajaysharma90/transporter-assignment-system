package com.ajay.transporter.repository;

import com.ajay.transporter.entity.LaneQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaneQuoteRepository extends JpaRepository<LaneQuote, Long> {

    List<LaneQuote> findByLane_Id(Long laneId);

    List<LaneQuote> findByTransporter_Id(Long transporterId);
}