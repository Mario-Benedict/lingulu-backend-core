# 📚 Course Data Seeder - Documentation Index

Selamat datang di **Course Data Seeder**! Sistem otomatis untuk generate data Course, Lesson, dan Section untuk aplikasi Lingulu.

## 🚀 Getting Started

**Baru pertama kali?** Mulai dari sini:

1. **[QUICKSTART.md](QUICKSTART.md)** ⭐ **START HERE!**
   - Setup dalam 3 langkah
   - Quick commands
   - Troubleshooting dasar

2. **[CHECKLIST.md](CHECKLIST.md)** ✅
   - Step-by-step setup checklist
   - Verification steps
   - Testing guide

## 📖 Documentation

### Main Documentation
- **[README.md](README.md)** - Complete documentation
  - Cara penggunaan lengkap
  - Kustomisasi data
  - Utility methods
  - Troubleshooting detail

- **[SUMMARY.md](SUMMARY.md)** - Implementation summary
  - Files created
  - Features overview
  - API reference
  - Best practices

### Code Examples
- **`CourseSeederConfigExample.java`** - Working example
  - 3 Courses dengan data lengkap
  - Semua tipe section
  - Production-ready template

- **`CourseSeederConfigTemplate.java`** - Customization template
  - Template kosong untuk copy
  - Helper methods
  - Comment guides

- **`MinimalSeederExample.java`** - Minimal example
  - 1 Course, 2 Lessons
  - Cocok untuk testing
  - Lightweight data

## 🏗️ Architecture

### Core Components

```
CourseDataSeeder (Main Logic)
    ↓
CourseDataSeederRunner (Auto-run on startup)
    ↓
SeederService (Manual operations)
    ↓
SeederController (REST API - Dev only)
```

### Configuration

```
CourseSeederConfig (Model)
    ├── CourseData
    │   └── LessonData
    │       └── SectionData
    │           ├── VocabularyData
    │           ├── GrammarData
    │           ├── SpeakingData
    │           └── MCQQuestionData
    │               └── MCQOptionData
```

## 🎯 Use Cases

### 1. Development - Auto Seeding
**File**: `CourseDataSeederRunner.java`
- Auto-run saat aplikasi start
- Skip jika data sudah ada
- Support FORCE_SEED

**How to use**:
```properties
spring.profiles.active=dev
```
```bash
mvn spring-boot:run
```

### 2. Testing - Minimal Data
**File**: `MinimalSeederExample.java`
- Data kecil untuk testing
- 1 Course, 2 Lessons
- Fast seeding

**How to use**:
```java
CourseSeederConfig config = MinimalSeederExample.getMinimalConfig();
seeder.seedData(config);
```

### 3. Production - Custom Data
**File**: `CourseSeederConfigTemplate.java`
- Template untuk custom data
- Copy dan customize
- Full control

**How to use**:
1. Copy template
2. Rename & customize
3. Update runner
4. Run seeder

### 4. Manual Trigger - REST API
**File**: `SeederController.java`
- REST endpoints
- Dev/test only
- Manual control

**How to use**:
```bash
curl -X POST http://localhost:8080/api/dev/seeder/seed
```

## 📁 File Structure

```
seeder/
├── 📄 Core Files
│   ├── CourseDataSeeder.java           # Main seeder logic
│   ├── CourseDataSeederRunner.java     # Auto-runner
│   └── SeederService.java              # Manual operations
│
├── 📋 Configuration
│   └── config/
│       ├── CourseSeederConfig.java     # Config model
│       ├── CourseSeederConfigExample.java   # Example (3 courses)
│       ├── CourseSeederConfigTemplate.java  # Template
│       └── MinimalSeederExample.java   # Minimal (1 course)
│
├── 🌐 API (Optional)
│   └── ../controller/SeederController.java  # REST API
│
└── 📚 Documentation
    ├── INDEX.md (this file)            # Documentation index
    ├── QUICKSTART.md                   # Quick start guide
    ├── README.md                       # Full documentation
    ├── SUMMARY.md                      # Implementation summary
    └── CHECKLIST.md                    # Setup checklist
```

## 🎨 Configuration Examples

### Example 1: Full Dataset (Default)
**File**: `CourseSeederConfigExample.java`
- 3 Courses (Beginner, Intermediate, Advanced)
- 12 Lessons (4 per course)
- 48 Sections (4 per lesson)
- ~300+ total records

**Use case**: Production, complete learning path

### Example 2: Minimal Dataset
**File**: `MinimalSeederExample.java`
- 1 Course
- 2 Lessons
- 8 Sections
- ~30 total records

**Use case**: Testing, development, CI/CD

### Example 3: Custom Dataset
**File**: Your custom config file
- Your own structure
- Your own data
- Your own rules

**Use case**: Specific requirements

## 🔧 Common Tasks

### Task: Run Auto Seeder
1. Set profile to `dev`
2. Run application
3. Check logs

**See**: [QUICKSTART.md](QUICKSTART.md#1️⃣-setup-profile)

### Task: Customize Data
1. Copy template file
2. Modify data
3. Update runner
4. Re-run

**See**: [README.md](README.md#🛠️-kustomisasi-data)

### Task: Clear Data
1. Use FORCE_SEED env var
   OR
2. Call REST API `/api/dev/seeder/clear`

**See**: [QUICKSTART.md](QUICKSTART.md#🔄-force-re-seed)

### Task: Verify Seeding
1. Check console logs
2. Query database
3. Count records

**See**: [CHECKLIST.md](CHECKLIST.md#verification)

## 🛠️ API Reference

### CourseDataSeeder
```java
void seedData(CourseSeederConfig config)  // Seed dari config
void clearAllData()                        // Clear semua data
boolean hasData()                          // Check if data exists
```

### SeederService
```java
void seedExampleData()                     // Seed example config
void seedCustomData(CourseSeederConfig)    // Seed custom config
void clearAllData()                        // Clear semua data
boolean hasData()                          // Check if data exists
void forceReseed()                         // Clear + re-seed
void seedIfEmpty()                         // Seed hanya jika kosong
```

### REST API (Dev Only)
```
GET  /api/dev/seeder/status        # Check status
POST /api/dev/seeder/seed          # Seed data
POST /api/dev/seeder/clear         # Clear data
POST /api/dev/seeder/force-reseed  # Force re-seed
```

## 🔒 Safety & Best Practices

### ✅ DO
- Use profiles (`dev`, `seeder`) for seeding
- Backup data before force re-seed
- Test with minimal config first
- Check logs after seeding
- Verify data in database

### ❌ DON'T
- Run seeder in production
- Seed without backup
- Ignore error logs
- Force re-seed without checking
- Skip verification

## 📞 Need Help?

### Quick Issues

**Seeder not running?**
→ Check [QUICKSTART.md - Troubleshooting](QUICKSTART.md#⚠️-troubleshooting)

**Data not seeding?**
→ Check [README.md - Troubleshooting](README.md#🐛-troubleshooting)

**Want to customize?**
→ See [README.md - Kustomisasi](README.md#🛠️-kustomisasi-data)

### Step-by-Step Help

**Complete setup guide**
→ [CHECKLIST.md](CHECKLIST.md)

**API reference**
→ [SUMMARY.md - API Methods](SUMMARY.md#🔧-api-methods)

**Examples**
→ See code files: `*Example.java`

## 📊 Data Structure Reference

### Course → Lesson → Section → Content

```
Course
├── courseTitle: String
├── description: String
├── difficultyLevel: String (Beginner/Intermediate/Advanced)
├── languageFrom: String
├── languageTo: String
├── position: Integer
├── published: Boolean
└── lessons: List<Lesson>
    └── Lesson
        ├── lessonTitle: String
        ├── position: Integer
        └── sections: List<Section>
            └── Section
                ├── sectionTitle: String
                ├── sectionType: Enum (VOCABULARY/GRAMMAR/SPEAKING/MCQ)
                └── content: (depends on type)
                    ├── VOCABULARY → List<Vocabulary>
                    ├── GRAMMAR → Grammar
                    ├── SPEAKING → List<Speaking>
                    └── MCQ → List<MCQQuestion>
```

**See**: [SUMMARY.md - Struktur Data](SUMMARY.md#🏗️-struktur-data-yang-di-seed)

## 🎓 Learning Path

### Beginner
1. Read [QUICKSTART.md](QUICKSTART.md)
2. Run auto seeder
3. Verify data

### Intermediate
1. Read [README.md](README.md)
2. Use `MinimalSeederExample`
3. Customize simple data

### Advanced
1. Copy `CourseSeederConfigTemplate`
2. Create custom config
3. Build production data

## ✨ Features Highlight

- ✅ **Auto-seeding** on startup
- ✅ **Skip existing** data
- ✅ **Force re-seed** option
- ✅ **REST API** for manual trigger
- ✅ **Multiple examples** (full, minimal, template)
- ✅ **Transactional** seeding
- ✅ **Detailed logging**
- ✅ **Type-safe** configuration
- ✅ **Builder pattern**
- ✅ **Profile-based** activation

---

## 🚀 Quick Start Command

```bash
# Set profile in application.properties
echo "spring.profiles.active=dev" >> src/main/resources/application.properties

# Run application
mvn spring-boot:run

# Data will be seeded automatically!
```

---

**Version**: 1.0  
**Status**: ✅ Ready to Use  
**Last Updated**: February 14, 2026

**Happy Seeding! 🌱**

