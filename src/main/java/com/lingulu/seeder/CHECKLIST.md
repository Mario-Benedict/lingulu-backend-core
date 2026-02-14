# ✅ Course Data Seeder - Setup Checklist

## Pre-requisites
- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] Database (PostgreSQL/MySQL) running
- [ ] Database credentials configured di `application.properties`

## Setup Steps

### 1. Configuration Setup
- [ ] Set profile ke `dev` atau `seeder` di `application.properties`:
  ```properties
  spring.profiles.active=dev
  ```
- [ ] Atau gunakan command line:
  ```bash
  mvn spring-boot:run -Dspring-boot.run.profiles=seeder
  ```

### 2. Database Setup
- [ ] Database sudah dibuat
- [ ] Connection string benar
- [ ] Username/password benar
- [ ] Test connection berhasil

### 3. Dependencies Check
- [ ] Semua dependencies ter-download (run `mvn clean install`)
- [ ] Tidak ada compilation errors
- [ ] Semua repositories tersedia

## Running Seeder

### Auto Seeding (On Startup)
- [ ] Run aplikasi: `mvn spring-boot:run`
- [ ] Check console log untuk output seeder
- [ ] Verify "Data seeding completed successfully!"

### Manual Seeding (REST API)
- [ ] Aplikasi running
- [ ] Profile `dev` atau `seeder` aktif
- [ ] Test endpoint:
  ```bash
  curl http://localhost:8080/api/dev/seeder/status
  ```
- [ ] Seed data:
  ```bash
  curl -X POST http://localhost:8080/api/dev/seeder/seed
  ```

## Verification

### 1. Check Logs
- [ ] No errors in console
- [ ] Seeding messages present
- [ ] Success message shown
- [ ] All courses, lessons, sections logged

### 2. Check Database
- [ ] `courses` table populated
- [ ] `lesson` table populated
- [ ] `section` table populated
- [ ] `vocabulary` table populated
- [ ] `grammar` table populated
- [ ] `speaking` table populated
- [ ] `mcq_question` table populated
- [ ] `mcq_option` table populated

### 3. Count Records
```sql
-- Expected with example config:
SELECT 'courses' as table_name, COUNT(*) as count FROM courses
UNION ALL
SELECT 'lesson', COUNT(*) FROM lesson
UNION ALL
SELECT 'section', COUNT(*) FROM section
UNION ALL
SELECT 'vocabulary', COUNT(*) FROM vocabulary
UNION ALL
SELECT 'grammar', COUNT(*) FROM grammar
UNION ALL
SELECT 'speaking', COUNT(*) FROM speaking
UNION ALL
SELECT 'mcq_question', COUNT(*) FROM mcq_question
UNION ALL
SELECT 'mcq_option', COUNT(*) FROM mcq_option;
```

Expected counts (with CourseSeederConfigExample):
- courses: 3
- lesson: 12 (4 per course)
- section: 48 (4 per lesson)
- vocabulary: ~60 (5 per vocabulary section)
- grammar: 12 (1 per grammar section)
- speaking: ~48 (4 per speaking section)
- mcq_question: ~36 (3 per MCQ section)
- mcq_option: ~144 (4 per question)

## Customization

### Option 1: Edit Example Config
- [ ] Open `CourseSeederConfigExample.java`
- [ ] Modify data as needed
- [ ] Save file
- [ ] Re-run seeder (with FORCE_SEED if needed)

### Option 2: Use Template
- [ ] Copy `CourseSeederConfigTemplate.java`
- [ ] Rename to your config name
- [ ] Fill in your data
- [ ] Update `CourseDataSeederRunner.java` to use your config
- [ ] Run seeder

### Option 3: Use Minimal Example
- [ ] Update `CourseDataSeederRunner.java`:
  ```java
  CourseSeederConfig config = MinimalSeederExample.getMinimalConfig();
  ```
- [ ] Run seeder
- [ ] Verify smaller dataset

## Troubleshooting

### Seeder Not Running?
- [ ] Check profile is `dev` or `seeder`
- [ ] Check `@Profile` annotation di `CourseDataSeederRunner`
- [ ] Look for errors in console
- [ ] Verify database connection

### Data Already Exists?
- [ ] Normal behavior - seeder auto-skips
- [ ] Use FORCE_SEED to override:
  ```powershell
  $env:FORCE_SEED="true"
  mvn spring-boot:run -Dspring-boot.run.profiles=seeder
  ```
- [ ] Or use REST API: `/api/dev/seeder/force-reseed`

### Foreign Key Errors?
- [ ] Check cascade settings
- [ ] Verify entity relationships
- [ ] Check database schema matches entities
- [ ] Try dropping and recreating tables

### Memory Issues?
- [ ] Reduce data size (use MinimalSeederExample)
- [ ] Increase JVM heap: `-Xmx2g`
- [ ] Check database connection pool settings

## Post-Setup

### Testing
- [ ] Query courses from API
- [ ] Verify relationships work
- [ ] Test progress tracking
- [ ] Check all section types accessible

### Development
- [ ] Create custom config for your needs
- [ ] Add more data as needed
- [ ] Setup different configs for different environments
- [ ] Document your custom configurations

### Production
- [ ] DISABLE seeder in production!
- [ ] Remove `dev`/`seeder` from production profiles
- [ ] Backup production data before any seeding
- [ ] Use migration scripts instead of seeder

## Files Created

- [x] `GrammarRepository.java`
- [x] `CourseDataSeeder.java`
- [x] `CourseDataSeederRunner.java`
- [x] `SeederService.java`
- [x] `SeederController.java`
- [x] `CourseSeederConfig.java`
- [x] `CourseSeederConfigExample.java`
- [x] `CourseSeederConfigTemplate.java`
- [x] `MinimalSeederExample.java`
- [x] `README.md`
- [x] `QUICKSTART.md`
- [x] `SUMMARY.md`
- [x] `CHECKLIST.md` (this file)

## Quick Commands Reference

```bash
# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Force re-seed (Windows)
$env:FORCE_SEED="true"; mvn spring-boot:run -Dspring-boot.run.profiles=seeder

# Force re-seed (Linux/Mac)
FORCE_SEED=true mvn spring-boot:run -Dspring-boot.run.profiles=seeder

# Check status via API
curl http://localhost:8080/api/dev/seeder/status

# Seed via API
curl -X POST http://localhost:8080/api/dev/seeder/seed

# Clear via API
curl -X POST http://localhost:8080/api/dev/seeder/clear

# Force re-seed via API
curl -X POST http://localhost:8080/api/dev/seeder/force-reseed
```

---

**Status**: Ready to Use ✅
**Last Updated**: February 14, 2026

