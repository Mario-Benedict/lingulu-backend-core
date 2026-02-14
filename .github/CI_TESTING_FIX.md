# 🧪 CI Testing Setup - Fixed!

## Problem Solved
❌ **Before:** CI gagal karena mencoba connect ke AWS databases yang belum ada  
✅ **After:** CI menggunakan **in-memory databases** untuk testing

---

## 🎯 Solution

### Test Profile dengan In-Memory Databases

**File:** `src/test/resources/application-test.properties`

- ✅ **PostgreSQL** → H2 In-Memory Database
- ✅ **Redis** → Embedded Redis (port 6370)
- ✅ **MongoDB** → Embedded MongoDB (port 27018)
- ✅ **AWS Services** → Mock values
- ✅ **Email** → Mock SMTP

---

## 📦 Dependencies Added

**File:** `pom.xml`

```xml
<!-- H2 Database for testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Embedded Redis -->
<dependency>
    <groupId>it.ozimov</groupId>
    <artifactId>embedded-redis</artifactId>
    <version>0.7.3</version>
    <scope>test</scope>
</dependency>

<!-- Embedded MongoDB -->
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 🔄 CI Workflow Changes

**File:** `.github/workflows/ci.yml`

### Before (❌ Failed):
```yaml
- name: Create secret.properties
  # Create with AWS database credentials
  
- name: Run tests
  run: mvn clean test
  # Tries to connect to AWS databases → FAILS!
```

### After (✅ Works):
```yaml
- name: Create dummy CloudFront key
  run: echo "dummy" > src/main/resources/private_lingulu_cdn_key_pkcs8.der

- name: Run tests with test profile
  run: mvn clean test -Dspring.profiles.active=test
  env:
    SPRING_PROFILES_ACTIVE: test
  # Uses in-memory databases → SUCCESS!
```

---

## 🧪 How It Works

### 1. Test Profile Active
```bash
mvn clean test -Dspring.profiles.active=test
```

### 2. Spring Loads Test Configuration
- `application-test.properties` is used
- Overrides main `application.properties`

### 3. In-Memory Databases Start
- **H2** starts automatically (in-memory SQL)
- **Embedded Redis** starts on port 6370
- **Embedded MongoDB** starts on port 27018

### 4. Tests Run
- All tests run against in-memory databases
- No external services needed
- Fast & isolated

### 5. Cleanup
- All in-memory data destroyed after tests
- No cleanup needed

---

## 📊 Test vs Production

| Service | Test Profile | Production |
|---------|-------------|------------|
| **PostgreSQL** | H2 (in-memory) | AWS RDS |
| **Redis** | Embedded (6370) | AWS ElastiCache |
| **MongoDB** | Embedded (27018) | MongoDB Atlas |
| **S3** | Mock values | Real AWS S3 |
| **Email** | Mock SMTP | Real Gmail |
| **CloudFront** | Dummy key | Real private key |

---

## ✅ Benefits

### For CI/CD:
- ✅ **Fast Tests** - No network latency
- ✅ **Isolated** - No external dependencies
- ✅ **Reliable** - Not affected by AWS downtime
- ✅ **Free** - No database costs during testing
- ✅ **Parallel** - Can run multiple builds simultaneously

### For Developers:
- ✅ **Local Testing** - Can run tests without AWS
- ✅ **No Setup** - No database installation needed
- ✅ **Consistent** - Same environment for everyone
- ✅ **Faster** - In-memory is much faster

---

## 🚀 Running Tests

### Locally:
```bash
# With test profile (in-memory databases)
mvn clean test -Dspring.profiles.active=test

# Without profile (tries to connect to real databases)
mvn clean test
```

### In CI:
Automatic - workflow uses test profile by default

---

## 🔧 Configuration Details

### H2 Database (PostgreSQL replacement)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

**Features:**
- In-memory only (data gone after tests)
- SQL compliant
- Fast startup (~100ms)

### Embedded Redis
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6370
spring.data.redis.password=
```

**Features:**
- Starts automatically
- Different port (6370 vs 6379) to avoid conflicts
- Full Redis functionality

### Embedded MongoDB
```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27018
spring.data.mongodb.database=test
```

**Features:**
- Real MongoDB instance (embedded)
- Different port (27018 vs 27017)
- Full MongoDB features

---

## 📝 Test Example

```java
@SpringBootTest
@ActiveProfiles("test")  // Uses test profile
public class MyServiceTest {
    
    @Autowired
    private MyRepository repository;
    
    @Test
    public void testSave() {
        // This uses H2 in-memory database
        MyEntity entity = new MyEntity();
        repository.save(entity);
        
        // Data only exists during this test
        assertNotNull(repository.findById(entity.getId()));
    }
}
```

---

## 🔄 Migration Notes

### Tests that need updates:
- Tests that rely on specific PostgreSQL features → May need adjustments for H2
- Tests that use Redis-specific commands → Should work as-is
- Tests that use MongoDB aggregations → Should work as-is

### Tests that work unchanged:
- JPA/Hibernate tests
- Service layer tests
- Controller tests
- Integration tests

---

## 🆘 Troubleshooting

### "Could not start H2 database"
**Solution:** Check if another H2 instance is running

### "Redis connection refused"
**Solution:** Embedded Redis starts on port 6370 (not 6379)

### "MongoDB connection timeout"
**Solution:** Embedded MongoDB takes ~2-3 seconds to start, be patient

### Tests pass locally but fail in CI
**Solution:** Make sure test profile is active in CI

---

## ✨ Summary

**Changes Made:**
1. ✅ Added H2, Embedded Redis, Embedded MongoDB dependencies
2. ✅ Created `application-test.properties` with test configuration
3. ✅ Updated CI workflow to use test profile
4. ✅ No more AWS database connections during tests

**Result:**
- ✅ CI tests run successfully without AWS databases
- ✅ Fast, isolated, reliable testing
- ✅ Local development easier
- ✅ No external dependencies during testing

**CI Now:**
```
Push code → CI runs → Tests with in-memory DBs → ✅ Success!
```

---

**Status:** ✅ Fixed - CI can now run tests without AWS databases!

