# 🚀 Quick Start Guide - Course Data Seeder

## Langkah Cepat untuk Mulai

### 1️⃣ Setup Profile (Pilih salah satu)

**Option A: Via application.properties**
```properties
# Tambahkan di src/main/resources/application.properties
spring.profiles.active=dev
```

**Option B: Via Command Line**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=seeder
```

### 2️⃣ Run Aplikasi

```bash
mvn spring-boot:run
```

Seeder akan otomatis running dan seed data default!

### 3️⃣ Lihat Output

Cek console log, akan muncul:
```
============================================================
Course Data Seeder Runner
============================================================
Starting data seeding process...
Starting course data seeding...
Seeding course: Bahasa Inggris Dasar
Created course: Bahasa Inggris Dasar with ID: ...
  Seeding lesson: Perkenalan
  Created lesson: Perkenalan with ID: ...
    Seeding section: Kosakata Perkenalan (Type: VOCABULARY)
    Created section: Kosakata Perkenalan with ID: ...
      Created vocabulary: Hello -> Halo
      Created vocabulary: Goodbye -> Selamat tinggal
      ...
Data seeding completed successfully!
============================================================
```

## 🎯 Data Default yang Ter-seed

Setelah running, database akan terisi:
- ✅ **3 Courses** (Beginner, Intermediate, Advanced)
- ✅ **12 Lessons** (4 per course)
- ✅ **48 Sections** (4 per lesson)
- ✅ **~60 Vocabulary items**
- ✅ **12 Grammar materials**
- ✅ **~48 Speaking exercises**
- ✅ **~36 MCQ questions** dengan ~144 options

## 🔄 Force Re-seed (Hapus Data Lama)

Jika ingin hapus data lama dan seed ulang:

**Windows PowerShell:**
```powershell
$env:FORCE_SEED="true"
mvn spring-boot:run -Dspring-boot.run.profiles=seeder
```

**Linux/Mac:**
```bash
FORCE_SEED=true mvn spring-boot:run -Dspring-boot.run.profiles=seeder
```

## ✏️ Kustomisasi Data (Quick)

### Cara 1: Edit File Example

Edit `CourseSeederConfigExample.java` dan ganti data yang ada

### Cara 2: Gunakan Template

1. Copy file `CourseSeederConfigTemplate.java`
2. Rename jadi `MyCourseSeederConfig.java`
3. Ganti semua data dengan data Anda
4. Update `CourseDataSeederRunner.java`:

```java
// Ganti baris ini:
CourseSeederConfig config = CourseSeederConfigExample.getExampleConfig();

// Dengan:
CourseSeederConfig config = MyCourseSeederConfig.getCustomConfig();
```

### Cara 3: Buat Config Programmatically

```java
@Autowired
private CourseDataSeeder seeder;

public void seedMyData() {
    CourseSeederConfig config = CourseSeederConfig.builder()
        .courses(List.of(
            CourseData.builder()
                .courseTitle("My Course")
                .difficultyLevel("Beginner")
                .languageFrom("Indonesian")
                .languageTo("English")
                .published(true)
                .lessons(List.of(
                    LessonData.builder()
                        .lessonTitle("Lesson 1")
                        .position(1)
                        .sections(List.of(
                            // ... sections
                        ))
                        .build()
                ))
                .build()
        ))
        .build();
    
    seeder.seedData(config);
}
```

## 📋 Checklist Setup

- [ ] Set profile ke `dev` atau `seeder`
- [ ] Database sudah tersetup dan running
- [ ] Run aplikasi
- [ ] Cek log untuk konfirmasi seeding berhasil
- [ ] Verify data di database

## ⚠️ Troubleshooting

**Seeder tidak running?**
- ✅ Cek profile sudah `dev` atau `seeder`
- ✅ Cek log untuk error messages

**Data sudah ada dan di-skip?**
- ✅ Normal behavior! Gunakan `FORCE_SEED=true` untuk re-seed

**Error database connection?**
- ✅ Pastikan database running
- ✅ Cek credentials di `application.properties`

## 📚 Next Steps

1. ✅ Run aplikasi dan verify data ter-seed
2. ✅ Baca `README.md` untuk detail lengkap
3. ✅ Customize data sesuai kebutuhan
4. ✅ Explore template di `CourseSeederConfigTemplate.java`

## 🆘 Need Help?

Lihat dokumentasi lengkap di:
- `README.md` - Full documentation
- `CourseSeederConfigExample.java` - Contoh lengkap
- `CourseSeederConfigTemplate.java` - Template untuk customize

---

**Happy Seeding! 🌱**

