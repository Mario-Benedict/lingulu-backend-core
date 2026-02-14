# Course Data Seeder

Sistem otomatis untuk generate data course, lesson, dan section dengan berbagai tipe konten (Vocabulary, Grammar, Speaking, MCQ).

## 📋 Struktur Data

```
Course (3 courses)
└── Lesson (4 lessons per course)
    └── Section (4 sections per lesson)
        ├── Section 1: Vocabulary (Material)
        ├── Section 2: Grammar (Material)
        ├── Section 3: Speaking (Exercise)
        └── Section 4: MCQ (Exercise)
```

## 🚀 Cara Penggunaan

### 1. Otomatis Seeding (Recommended)

Seeder akan otomatis running saat aplikasi start jika profile `dev` atau `seeder` aktif.

#### Setting di `application.properties`:
```properties
spring.profiles.active=dev
```

#### Atau via command line:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=seeder
```

### 2. Force Re-seed (Hapus Data Lama)

Jika ingin menghapus data lama dan seed ulang:

**Windows (PowerShell):**
```powershell
$env:FORCE_SEED="true"; mvn spring-boot:run -Dspring-boot.run.profiles=seeder
```

**Linux/Mac:**
```bash
FORCE_SEED=true mvn spring-boot:run -Dspring-boot.run.profiles=seeder
```

### 3. Manual Seeding (Via Code)

```java
@Autowired
private CourseDataSeeder courseDataSeeder;

public void seedManually() {
    // Gunakan config example
    CourseSeederConfig config = CourseSeederConfigExample.getExampleConfig();
    courseDataSeeder.seedData(config);
    
    // Atau buat config custom
    CourseSeederConfig customConfig = CourseSeederConfig.builder()
        .courses(List.of(
            CourseData.builder()
                .courseTitle("My Custom Course")
                .description("Custom description")
                // ... configuration lainnya
                .build()
        ))
        .build();
    courseDataSeeder.seedData(customConfig);
}
```

### 4. Via REST API (Dev Only)

Controller hanya aktif di profile `dev` atau `seeder`:

```bash
# Check status - apakah data sudah ada?
curl http://localhost:8080/api/dev/seeder/status

# Seed data (skip jika sudah ada)
curl -X POST http://localhost:8080/api/dev/seeder/seed

# Clear semua data
curl -X POST http://localhost:8080/api/dev/seeder/clear

# Force re-seed (clear + seed)
curl -X POST http://localhost:8080/api/dev/seeder/force-reseed
```

### 5. Via SeederService

```java
@Autowired
private SeederService seederService;

public void example() {
    // Seed example data
    seederService.seedExampleData();
    
    // Seed hanya jika database kosong
    seederService.seedIfEmpty();
    
    // Force re-seed (clear + seed)
    seederService.forceReseed();
    
    // Check if has data
    boolean hasData = seederService.hasData();
    
    // Clear all data
    seederService.clearAllData();
}
```

## 🛠️ Kustomisasi Data

### Cara 1: Modifikasi Example Config

Edit file `CourseSeederConfigExample.java` sesuai kebutuhan:

```java
private static VocabularyData createCustomVocab() {
    return VocabularyData.builder()
        .word("Custom Word")
        .translation("Kata Custom")
        .audioPath("/audio/custom.mp3")
        .build();
}
```

### Cara 2: Buat Config Baru

```java
public class MyCustomSeederConfig {
    
    public static CourseSeederConfig getMyConfig() {
        return CourseSeederConfig.builder()
            .courses(Arrays.asList(
                CourseData.builder()
                    .courseTitle("Kursus Saya")
                    .description("Deskripsi kursus saya")
                    .difficultyLevel("Beginner")
                    .languageFrom("Indonesian")
                    .languageTo("English")
                    .position(1)
                    .published(true)
                    .lessons(Arrays.asList(
                        LessonData.builder()
                            .lessonTitle("Lesson 1")
                            .position(1)
                            .sections(Arrays.asList(
                                // Vocabulary Section
                                SectionData.builder()
                                    .sectionTitle("Kosakata Lesson 1")
                                    .sectionType("VOCABULARY")
                                    .vocabularies(Arrays.asList(
                                        VocabularyData.builder()
                                            .word("Apple")
                                            .translation("Apel")
                                            .audioPath("/audio/apple.mp3")
                                            .build()
                                    ))
                                    .build(),
                                // Grammar Section
                                SectionData.builder()
                                    .sectionTitle("Grammar Lesson 1")
                                    .sectionType("GRAMMAR")
                                    .grammar(GrammarData.builder()
                                        .title("Present Tense")
                                        .filePath("/grammar/present_tense.pdf")
                                        .build())
                                    .build(),
                                // Speaking Section
                                SectionData.builder()
                                    .sectionTitle("Speaking Lesson 1")
                                    .sectionType("SPEAKING")
                                    .speakings(Arrays.asList(
                                        SpeakingData.builder()
                                            .sentence("I like apples")
                                            .audioPath("/audio/i_like_apples.mp3")
                                            .build()
                                    ))
                                    .build(),
                                // MCQ Section
                                SectionData.builder()
                                    .sectionTitle("Quiz Lesson 1")
                                    .sectionType("MCQ")
                                    .mcqQuestions(Arrays.asList(
                                        MCQQuestionData.builder()
                                            .questionText("What is 'Apel' in English?")
                                            .options(Arrays.asList(
                                                MCQOptionData.builder()
                                                    .optionText("Apple")
                                                    .isCorrect(true)
                                                    .build(),
                                                MCQOptionData.builder()
                                                    .optionText("Orange")
                                                    .isCorrect(false)
                                                    .build()
                                            ))
                                            .build()
                                    ))
                                    .build()
                            ))
                            .build()
                    ))
                    .build()
            ))
            .build();
    }
}
```

Kemudian gunakan di `CourseDataSeederRunner.java`:

```java
// Ganti baris ini:
CourseSeederConfig config = CourseSeederConfigExample.getExampleConfig();

// Dengan:
CourseSeederConfig config = MyCustomSeederConfig.getMyConfig();
```

## 📊 Tipe Section

### 1. VOCABULARY (Material)
- Berisi list kata-kata dengan terjemahan
- Setiap vocabulary memiliki: word, translation, audioPath

### 2. GRAMMAR (Material)
- Berisi 1 grammar material per section
- Grammar memiliki: title, filePath (PDF/dokumen)

### 3. SPEAKING (Exercise)
- Berisi list kalimat untuk latihan berbicara
- Setiap speaking memiliki: sentence, audioPath

### 4. MCQ (Exercise)
- Berisi list pertanyaan pilihan ganda
- Setiap question memiliki beberapa options
- Setiap option punya flag `isCorrect`

## 🔧 Utility Methods

### Check apakah data sudah ada
```java
boolean hasData = courseDataSeeder.hasData();
```

### Clear semua data
```java
courseDataSeeder.clearAllData(); // WARNING: Hapus semua course!
```

### Seed dengan config tertentu
```java
courseDataSeeder.seedData(config);
```

## 📝 Catatan Penting

1. **Profile**: Seeder hanya running di profile `dev` atau `seeder`
2. **Auto-skip**: Jika data sudah ada, seeder akan skip otomatis
3. **Force Seed**: Gunakan `FORCE_SEED=true` untuk hapus data lama
4. **Transaction**: Seeder menggunakan `@Transactional` untuk data consistency
5. **Cascade Delete**: Hapus course akan otomatis hapus lesson dan section terkait

## 🎯 Example Data Default

Default config (`CourseSeederConfigExample`) akan generate:

- **3 Courses**: Beginner, Intermediate, Advanced
- **12 Lessons**: 4 per course
- **48 Sections**: 4 per lesson (Vocabulary, Grammar, Speaking, MCQ)
- **~60 Vocabularies**: 5 per vocabulary section
- **12 Grammar materials**: 1 per grammar section
- **~48 Speaking exercises**: 4 per speaking section
- **~36 MCQ questions**: 3 per MCQ section
- **~144 MCQ options**: 4 per question

## 🐛 Troubleshooting

### Seeder tidak running
- Pastikan profile `dev` atau `seeder` aktif
- Check log untuk error messages

### Data tidak ter-seed
- Check apakah data sudah ada (auto-skip)
- Gunakan `FORCE_SEED=true` untuk force re-seed
- Check database connection

### Error saat seeding
- Check foreign key constraints
- Pastikan semua repository beans tersedia
- Check log untuk detail error

## 📚 File Structure

```
com.lingulu.seeder/
├── CourseDataSeeder.java              # Main seeder class
├── CourseDataSeederRunner.java        # Auto-run on startup
└── config/
    ├── CourseSeederConfig.java        # Configuration model
    └── CourseSeederConfigExample.java # Example configuration
```

## 🎨 Custom Configuration Template

Lihat file `CourseSeederConfigExample.java` untuk template lengkap.
Copy dan modifikasi sesuai kebutuhan Anda!

