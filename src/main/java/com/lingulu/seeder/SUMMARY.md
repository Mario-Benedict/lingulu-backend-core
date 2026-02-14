# 📦 Course Data Seeder - Implementation Summary

## ✅ Files Created

### 1. Repository
- `GrammarRepository.java` - Repository untuk Grammar entity

### 2. Seeder Core
- `CourseDataSeeder.java` - Main seeder logic class
- `CourseDataSeederRunner.java` - Auto-run seeder on startup
- `SeederService.java` - Service for manual seeding operations

### 3. Configuration
- `CourseSeederConfig.java` - Data configuration model
- `CourseSeederConfigExample.java` - Example configuration dengan sample data
- `CourseSeederConfigTemplate.java` - Template untuk custom configuration

### 4. Optional REST API (Dev Only)
- `SeederController.java` - REST API endpoints untuk trigger seeding (profile: dev/seeder)

### 5. Documentation
- `README.md` - Dokumentasi lengkap
- `QUICKSTART.md` - Quick start guide
- `SUMMARY.md` - File ini

## 🏗️ Struktur Data yang Di-seed

```
Course (3 courses)
├── Lesson 1 (Position 1)
│   ├── Section 1: Vocabulary (MATERIAL)
│   │   └── Multiple Vocabulary items (word, translation, audio)
│   ├── Section 2: Grammar (MATERIAL)
│   │   └── Grammar content (title, file path)
│   ├── Section 3: Speaking (EXERCISE)
│   │   └── Multiple Speaking exercises (sentence, audio)
│   └── Section 4: MCQ (EXERCISE)
│       └── Multiple MCQ Questions
│           └── Multiple Options (with isCorrect flag)
├── Lesson 2 (Position 2)
│   └── (Same structure)
├── Lesson 3 (Position 3)
│   └── (Same structure)
└── Lesson 4 (Position 4)
    └── (Same structure)
```

## 🎯 Features

### Auto Seeding
- ✅ Otomatis running saat aplikasi start (profile: dev/seeder)
- ✅ Auto-skip jika data sudah ada
- ✅ Support FORCE_SEED untuk re-seed

### Flexible Configuration
- ✅ Builder pattern untuk easy configuration
- ✅ Support semua tipe section (Vocabulary, Grammar, Speaking, MCQ)
- ✅ Template-based configuration

### Data Integrity
- ✅ Transactional seeding
- ✅ Cascade relationships
- ✅ Proper foreign key handling

### Logging
- ✅ Detailed logging untuk setiap step
- ✅ Success/error messages
- ✅ Progress tracking

## 📊 Default Data

Example configuration generates:
- **3 Courses**: Beginner, Intermediate, Advanced
- **12 Lessons**: 4 per course
- **48 Sections**: 4 per lesson
  - 12 Vocabulary sections (~60 items total)
  - 12 Grammar sections
  - 12 Speaking sections (~48 exercises total)
  - 12 MCQ sections (~36 questions, ~144 options total)

## 🚀 Usage

### Quick Start
```bash
# Set profile
spring.profiles.active=dev

# Run aplikasi
mvn spring-boot:run
```

### Force Re-seed
```powershell
# Windows PowerShell
$env:FORCE_SEED="true"
mvn spring-boot:run -Dspring-boot.run.profiles=seeder
```

### Custom Configuration
```java
CourseSeederConfig config = CourseSeederConfig.builder()
    .courses(List.of(
        CourseData.builder()
            .courseTitle("My Course")
            .lessons(List.of(
                LessonData.builder()
                    .sections(List.of(
                        // Vocabulary section
                        SectionData.builder()
                            .sectionType("VOCABULARY")
                            .vocabularies(...)
                            .build(),
                        // Grammar section
                        SectionData.builder()
                            .sectionType("GRAMMAR")
                            .grammar(...)
                            .build(),
                        // Speaking section
                        SectionData.builder()
                            .sectionType("SPEAKING")
                            .speakings(...)
                            .build(),
                        // MCQ section
                        SectionData.builder()
                            .sectionType("MCQ")
                            .mcqQuestions(...)
                            .build()
                    ))
                    .build()
            ))
            .build()
    ))
    .build();

seeder.seedData(config);
```

### Via REST API (Dev Only)
```bash
# Check status
curl http://localhost:8080/api/dev/seeder/status

# Seed data
curl -X POST http://localhost:8080/api/dev/seeder/seed

# Clear data
curl -X POST http://localhost:8080/api/dev/seeder/clear

# Force re-seed
curl -X POST http://localhost:8080/api/dev/seeder/force-reseed
```

### Via Service (Programmatic)
```java
@Autowired
private SeederService seederService;

// Seed example data
seederService.seedExampleData();

// Seed only if empty
seederService.seedIfEmpty();

// Force re-seed
seederService.forceReseed();

// Check if has data
boolean hasData = seederService.hasData();
```

## 🔧 API Methods

### CourseDataSeeder

```java
// Seed data dari config
public void seedData(CourseSeederConfig config)

// Clear semua data (WARNING!)
public void clearAllData()

// Check apakah data sudah ada
public boolean hasData()
```

## 📁 File Structure

```
com.lingulu/
├── controller/
│   └── SeederController.java          # REST API (dev only)
├── repository/
│   └── GrammarRepository.java
└── seeder/
    ├── CourseDataSeeder.java
    ├── CourseDataSeederRunner.java
    ├── SeederService.java
    ├── README.md
    ├── QUICKSTART.md
    ├── SUMMARY.md
    └── config/
        ├── CourseSeederConfig.java
        ├── CourseSeederConfigExample.java
        └── CourseSeederConfigTemplate.java
```

## 🎨 Customization Options

### 1. Edit Example
Langsung edit `CourseSeederConfigExample.java`

### 2. Use Template
Copy `CourseSeederConfigTemplate.java` dan customize

### 3. Programmatic
Create config via code dan panggil `seeder.seedData(config)`

## ⚙️ Configuration Properties

### Course Properties
- `courseTitle` - Judul course
- `description` - Deskripsi course
- `difficultyLevel` - Level kesulitan (Beginner/Intermediate/Advanced)
- `languageFrom` - Bahasa asal
- `languageTo` - Bahasa target
- `position` - Urutan course
- `published` - Status publish

### Lesson Properties
- `lessonTitle` - Judul lesson
- `position` - Urutan lesson

### Section Properties
- `sectionTitle` - Judul section
- `sectionType` - Tipe section (VOCABULARY/GRAMMAR/SPEAKING/MCQ)

### Content Properties

**Vocabulary:**
- `word` - Kata
- `translation` - Terjemahan
- `audioPath` - Path file audio

**Grammar:**
- `title` - Judul grammar
- `filePath` - Path file material

**Speaking:**
- `sentence` - Kalimat
- `audioPath` - Path file audio

**MCQ:**
- `questionText` - Teks pertanyaan
- `optionText` - Teks pilihan
- `isCorrect` - Flag jawaban benar

## 🔒 Best Practices

1. **Always use profiles** - Jangan running seeder di production
2. **Backup data** - Sebelum force re-seed
3. **Test config** - Test dengan data kecil dulu
4. **Use transactions** - Sudah built-in di seeder
5. **Check logs** - Monitor seeding process

## 🐛 Troubleshooting

### Seeder tidak running
- Check profile setting (harus `dev` atau `seeder`)
- Check logs untuk error

### Data tidak ter-seed
- Check apakah data sudah ada (auto-skip)
- Gunakan `FORCE_SEED=true` untuk override

### Error saat seeding
- Check database connection
- Check foreign key constraints
- Review error logs

## 📚 Documentation

- `QUICKSTART.md` - Quick start guide
- `README.md` - Full documentation
- `CourseSeederConfigExample.java` - Working example
- `CourseSeederConfigTemplate.java` - Customization template

## ✨ Next Steps

1. ✅ Set profile ke `dev` atau `seeder`
2. ✅ Run aplikasi
3. ✅ Verify data di database
4. ✅ Customize sesuai kebutuhan
5. ✅ Create custom config
6. ✅ Deploy dan test

---

**Created:** February 2026
**Version:** 1.0
**Status:** ✅ Ready to Use





