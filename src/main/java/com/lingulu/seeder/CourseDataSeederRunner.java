package com.lingulu.seeder;

import com.lingulu.seeder.config.CourseSeederConfig;
import com.lingulu.seeder.config.CourseSeederConfigExample;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Runner untuk otomatis seed data saat aplikasi start
 * Hanya akan running di profile 'dev' atau 'seeder'
 *
 * Cara pakai:
 * 1. Set profile: spring.profiles.active=dev atau seeder
 * 2. Jalankan aplikasi
 * 3. Data akan otomatis ter-seed jika database masih kosong
 *
 * Untuk force re-seed (hapus data lama):
 * Set environment variable: FORCE_SEED=true
 */
@Slf4j
@Component
@Profile({"dev", "seeder"}) // Hanya running di profile dev atau seeder
@RequiredArgsConstructor
public class CourseDataSeederRunner implements CommandLineRunner {

    private final CourseDataSeeder courseDataSeeder;

    @Override
    public void run(String... args) {
        log.info("=".repeat(60));
        log.info("Course Data Seeder Runner");
        log.info("=".repeat(60));

        // Check environment variable untuk force seed
        boolean forceSeed = Boolean.parseBoolean(System.getenv().getOrDefault("FORCE_SEED", "false"));

        if (forceSeed) {
            log.warn("FORCE_SEED is enabled. Clearing existing data...");
            courseDataSeeder.clearAllData();
        } else {
            // Check if data already exists
            if (courseDataSeeder.hasData()) {
                log.info("Course data already exists. Skipping seeding.");
                log.info("To force re-seed, set environment variable: FORCE_SEED=true");
                log.info("=".repeat(60));
                return;
            }
        }

        try {
            log.info("Starting data seeding process...");

            // Get configuration
            CourseSeederConfig config = CourseSeederConfigExample.getExampleConfig();

            // Seed the data
            courseDataSeeder.seedData(config);

            log.info("Data seeding completed successfully!");
        } catch (Exception e) {
            log.error("Error during data seeding: {}", e.getMessage(), e);
        }

        log.info("=".repeat(60));
    }
}

