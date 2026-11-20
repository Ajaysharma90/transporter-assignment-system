
package com.ajay.transporter.controller;

import com.ajay.transporter.dto.ApiResponse;
import com.ajay.transporter.dto.AssignmentRequest;
import com.ajay.transporter.dto.AssignmentResponse;
import com.ajay.transporter.dto.InputRequest;
import com.ajay.transporter.service.AssignmentService;
import com.ajay.transporter.service.InputService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transporters")
public class TransporterController {

    private final InputService inputService;
    private final AssignmentService assignmentService;

    public TransporterController(
            InputService inputService,
            AssignmentService assignmentService) {

        this.inputService = inputService;
        this.assignmentService = assignmentService;
    }

    // API 1: Save lanes, transporters and quotes
    @PostMapping("/input")
    public ResponseEntity<ApiResponse> saveInput(
            @Valid @RequestBody InputRequest request) {

        inputService.saveInput(request);

        return ResponseEntity.ok(
                new ApiResponse(
                        "success",
                        "Input data saved successfully."
                )
        );
    }

    // API 2: Generate optimal transporter assignments
    @PostMapping("/assignment")
    public ResponseEntity<AssignmentResponse> assignTransporters(
            @Valid @RequestBody AssignmentRequest request) {

        AssignmentResponse response =
                assignmentService.assignTransporters(
                        request.getMaxTransporters()
                );

        return ResponseEntity.ok(response);
    }
}
