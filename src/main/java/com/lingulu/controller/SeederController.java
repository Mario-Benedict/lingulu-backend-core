package com.lingulu.controller;

import com.lingulu.seeder.SeederService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Development-only controller untuk trigger seeding via REST API
 * Hanya aktif di profile 'dev' atau 'seeder'
 *
 * Endpoints:
 * - POST /api/dev/seeder/seed - Seed example data
 * - POST /api/dev/seeder/clear - Clear all data
 * - POST /api/dev/seeder/force-reseed - Clear and re-seed
 * - GET /api/dev/seeder/status - Check if data exists
 */
@Slf4j
@RestController
@RequestMapping("/api/dev/seeder")
@Profile({"dev", "seeder"}) // Only active in dev/seeder profiles
@RequiredArgsConstructor
public class SeederController {

    private final SeederService seederService;

    /**
     * Seed example data
     * POST /api/dev/seeder/seed
     */
    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seedData() {
        log.info("Seeding triggered via API");

        try {
            if (seederService.hasData()) {
                return ResponseEntity.ok(createResponse(false,
                    "Data already exists. Use /force-reseed to override.", null));
            }

            seederService.seedExampleData();
            return ResponseEntity.ok(createResponse(true,
                "Data seeded successfully", null));

        } catch (Exception e) {
            log.error("Error seeding data", e);
            return ResponseEntity.internalServerError()
                .body(createResponse(false, "Error seeding data", e.getMessage()));
        }
    }

    /**
     * Clear all course data
     * POST /api/dev/seeder/clear
     */
    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearData() {
        log.warn("Clear data triggered via API");

        try {
            seederService.clearAllData();
            return ResponseEntity.ok(createResponse(true,
                "All data cleared successfully", null));

        } catch (Exception e) {
            log.error("Error clearing data", e);
            return ResponseEntity.internalServerError()
                .body(createResponse(false, "Error clearing data", e.getMessage()));
        }
    }

    /**
     * Force re-seed: Clear and seed
     * POST /api/dev/seeder/force-reseed
     */
    @PostMapping("/force-reseed")
    public ResponseEntity<Map<String, Object>> forceReseed() {
        log.warn("Force re-seed triggered via API");

        try {
            seederService.forceReseed();
            return ResponseEntity.ok(createResponse(true,
                "Data cleared and re-seeded successfully", null));

        } catch (Exception e) {
            log.error("Error during force re-seed", e);
            return ResponseEntity.internalServerError()
                .body(createResponse(false, "Error during force re-seed", e.getMessage()));
        }
    }

    /**
     * Check seeder status
     * GET /api/dev/seeder/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean hasData = seederService.hasData();

        Map<String, Object> response = new HashMap<>();
        response.put("hasData", hasData);
        response.put("message", hasData ? "Database has course data" : "Database is empty");

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to create response
     */
    private Map<String, Object> createResponse(boolean success, String message, String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        if (error != null) {
            response.put("error", error);
        }
        return response;
    }
}

