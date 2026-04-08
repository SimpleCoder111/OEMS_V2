package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.CreateNewClassRequest;
import org.demo.oems.service.OpenAPIService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/openapi")
public class OpenAPIRest {

    private static final Logger logger = LogManager.getLogger(OpenAPIRest.class);

    private final OpenAPIService openAPIService;

    @Operation(summary = "Admin Classes Service - Create Classes Info", description = "Create Classes Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/horoscope/fortune")
    public ResponseEntity<Map<String, Object>> horoscopeFortune() {
        try {
            logger.info("Start - horoscopeFortune controller");
            Map<String, Object> finalResponse = openAPIService.horoscopeFortune();
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error("Exception in horoscopeFortune controller :: {}", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

}
