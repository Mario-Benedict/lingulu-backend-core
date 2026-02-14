package com.lingulu.seeder;

import com.lingulu.seeder.config.CourseSeederConfig;
import com.lingulu.seeder.config.CourseSeederConfigExample;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service untuk manual seeding via API/Controller
 * Berguna untuk trigger seeding dari REST API atau admin panel
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeederService {

    private final CourseDataSeeder courseDataSeeder;

    /**
     * Seed dengan example configuration
     */
    public void seedExampleData() {
        log.info("Seeding example data...");
        CourseSeederConfig config = CourseSeederConfigExample.getExampleConfig();
        courseDataSeeder.seedData(config);
    }

    /**
     * Seed dengan custom configuration
     */
    public void seedCustomData(CourseSeederConfig config) {
        log.info("Seeding custom data...");
        courseDataSeeder.seedData(config);
    }

    /**
     * Clear all course data
     * WARNING: This will delete ALL courses and related data!
     */
    public void clearAllData() {
        log.warn("Clearing all course data via SeederService...");
        courseDataSeeder.clearAllData();
    }

    /**
     * Check if data exists
     */
    public boolean hasData() {
        return courseDataSeeder.hasData();
    }

    /**
     * Force re-seed: Clear old data and seed new data
     */
    public void forceReseed() {
        log.info("Force re-seeding...");
        clearAllData();
        seedExampleData();
    }

    /**
     * Seed only if no data exists
     */
    public void seedIfEmpty() {
        if (!hasData()) {
            log.info("Database is empty. Seeding data...");
            seedExampleData();
        } else {
            log.info("Database already has data. Skipping seeding.");
        }
    }
}

