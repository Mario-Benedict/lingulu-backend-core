# 🚨 EMERGENCY: Deployment Stuck & CPU 100%

## Masalah Anda Sekarang
```
✗ Deployment stuck - lama banget
✗ CPU usage 100%
✗ VPC tidak bisa diakses
✗ Service tidak stabil
```

## 🔥 QUICK FIX - Jalankan Sekarang!

### Step 1: Stop Deployment yang Stuck
```bash
cd .github/scripts
chmod +x emergency-cleanup.sh
./emergency-cleanup.sh
```

**Ketik `yes` untuk confirm**

Script ini akan:
1. ✅ Stop semua task yang running
2. ✅ Scale service to 0
3. ✅ Register task definition BARU (1024 CPU, 2048 Memory)
4. ✅ Deploy dengan konfigurasi yang diperbaiki
5. ✅ Monitor sampai stable

**Waktu: ~5-10 menit**

---

### Step 2: Monitor Logs
```bash
# Buka terminal kedua, watch logs live
aws logs tail //ecs/lingulu-backend-core --follow
```

**Cari di logs:**
- ❌ `Connection refused` - Database tidak reachable
- ❌ `OutOfMemoryError` - Memory habis
- ❌ `Timeout` - Database connection timeout
- ✅ `Started LinguluApplication` - App berhasil start

---

## 🔍 Kenapa Ini Terjadi?

### Penyebab Utama (BERDASARKAN GEJALA ANDA):

1. **Task Size Terlalu Kecil** ✅ FIXED!
   ```
   Old: 512 CPU, 1024 Memory
   New: 1024 CPU, 2048 Memory (DOUBLED!)
   ```

2. **Database Connection Gagal** (Kemungkinan besar!)
   - PostgreSQL tidak reachable
   - Redis tidak reachable  
   - MongoDB Atlas connection string salah
   - **Result:** App crash loop → CPU 100%

3. **Health Check Terlalu Cepat** ✅ FIXED!
   ```
   Old: 60s start period
   New: 180s start period (3 minutes!)
   ```

---

## 🎯 Fixes Yang Sudah Diterapkan

### 1. Doubled Resources
```json
"cpu": "1024",     // Was: 512
"memory": "2048"   // Was: 1024
```

### 2. JVM Optimization
```json
"JAVA_OPTS": "-Xmx1536m -Xms512m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"
```
- Max heap: 1.5GB (dari 2GB total)
- Better garbage collector
- Prevent OutOfMemory

### 3. Extended Health Check
```json
"startPeriod": 180,  // Was: 60 (3 minutes grace period!)
"interval": 60,      // Was: 30
"timeout": 10,       // Was: 5
"retries": 5         // Was: 3
```

### 4. Reduced Logging (Less CPU)
```json
"LOGGING_LEVEL_ROOT": "WARN",
"SPRING_JPA_SHOW_SQL": "false"
```

---

## ⚠️ CRITICAL: Check Database Connectivity

**Kemungkinan besar masalahnya di sini!**

### PostgreSQL - Apakah Ready?
```bash
aws rds describe-db-instances \
  --db-instance-identifier lingulu-postgres \
  --region ap-southeast-1 \
  --query 'DBInstances[0].[DBInstanceStatus,Endpoint.Address]'
```

**Harus:** `["available", "lingulu-postgres.xxx.rds.amazonaws.com"]`

### Redis - Apakah Ready?
```bash
aws elasticache describe-cache-clusters \
  --cache-cluster-id lingulu-redis \
  --show-cache-node-info \
  --region ap-southeast-1 \
  --query 'CacheClusters[0].[CacheClusterStatus,CacheNodes[0].Endpoint.Address]'
```

**Harus:** `["available", "lingulu-redis.xxx.cache.amazonaws.com"]`

### MongoDB Atlas - Connection String Benar?
```bash
# Check GitHub Secret
# Format harus:
mongodb+srv://username:password@cluster.mongodb.net/dbname
```

**Common mistakes:**
- ❌ Password belum di-encode
- ❌ IP tidak di-whitelist (`0.0.0.0/0`)
- ❌ Database user belum dibuat

---

## 📊 Timeline Expected (After Fix)

```
0:00 - Task starting
0:30 - Container pulled & running
1:00 - Java application booting
1:30 - Connecting to databases... (CRITICAL MOMENT!)
     └─ PostgreSQL connect
     └─ Redis connect
     └─ MongoDB Atlas connect
2:00 - Application ready
2:30 - Health check starting (grace period)
3:00 - First health check
3:30 - Service STABLE ✅
```

**Jika stuck di step 1:30 → Database problem!**

---

## 🔧 Manual Troubleshooting

### Jika Emergency Script Gagal:

#### 1. Force Delete Service & Recreate
```bash
# Delete service
aws ecs delete-service \
  --cluster lingulu-cluster \
  --service lingulu-backend-service \
  --force \
  --region ap-southeast-1

# Wait for deletion
sleep 30

# Recreate with new task def
cd .github/scripts
./setup-ecs-service.sh
```

#### 2. Check VPC & Security Groups
```bash
# Get VPC
VPC_ID=$(aws ec2 describe-vpcs \
  --filters "Name=tag:Name,Values=lingulu-vpc" \
  --query 'Vpcs[0].VpcId' \
  --output text)

echo "VPC: $VPC_ID"

# Check subnets
aws ec2 describe-subnets \
  --filters "Name=vpc-id,Values=$VPC_ID" \
  --query 'Subnets[*].[SubnetId,AvailabilityZone,State]'

# Should show 3 subnets, all "available"
```

#### 3. Check Security Group Rules
```bash
# Database SG
aws ec2 describe-security-groups \
  --filters "Name=group-name,Values=lingulu-db-sg" \
  --query 'SecurityGroups[0].IpPermissions'

# Should allow ports: 5432, 6379, 27017 from VPC CIDR

# App SG
aws ec2 describe-security-groups \
  --filters "Name=group-name,Values=lingulu-backend-sg" \
  --query 'SecurityGroups[0].IpPermissions'

# Should allow port 8080
```

---

## 🆘 Worst Case: Start Fresh

**Jika semua gagal, cleanup dan setup ulang:**

```bash
# 1. Cleanup semua
./cleanup-all.sh

# 2. Setup ulang dari awal
./setup-vpc.sh
./setup-databases-free.sh
./setup-aws.sh
./setup-ecs-service.sh

# 3. Deploy
git push origin main
```

**Time: ~40 minutes total**

---

## ✅ Success Indicators

### Healthy Deployment:
```
✓ Service: 1/1 tasks running
✓ Task status: RUNNING
✓ Health status: HEALTHY
✓ CPU: <50% (not 100%!)
✓ Logs: "Started LinguluApplication in XX seconds"
✓ curl http://IP:8080/actuator/health → {"status":"UP"}
```

### Still Failing:
```
✗ Task keeps restarting
✗ CPU spikes to 100%
✗ Health: UNHEALTHY
✗ Logs show database errors
```

**→ CHECK DATABASE CONNECTIONS!**

---

## 📝 Common Error Messages & Fixes

### "Connection refused" in logs
**Problem:** Database tidak reachable  
**Fix:** 
1. Check security group allows VPC CIDR
2. Check database actually running
3. Check endpoint correct in GitHub Secrets

### "OutOfMemoryError"
**Problem:** Memory habis  
**Fix:** ✅ Already fixed with 2GB memory

### "Health check timeout"
**Problem:** App too slow to start  
**Fix:** ✅ Already fixed with 180s grace period

### "Task failed to start"
**Problem:** Container can't pull image  
**Fix:** Check ECR permissions, image exists

---

## 🎯 NEXT STEPS - RIGHT NOW:

1. **STOP deployment:**
   ```bash
   ./emergency-cleanup.sh
   ```

2. **WATCH logs:**
   ```bash
   aws logs tail //ecs/lingulu-backend-core --follow
   ```

3. **CHECK databases are READY:**
   ```bash
   # PostgreSQL status
   aws rds describe-db-instances --db-instance-identifier lingulu-postgres
   
   # Must be "available"
   ```

4. **WAIT for stability:**
   - Takes 3-5 minutes with new config
   - Monitor logs untuk errors
   - Check health check passes

5. **IF still fails:**
   - Read logs carefully
   - Probably database connection issue
   - Fix database, then re-run emergency-cleanup.sh

---

## 📞 Quick Reference

| Command | Purpose |
|---------|---------|
| `./emergency-cleanup.sh` | Stop stuck deployment |
| `./debug-ecs-task.sh` | See what's wrong |
| `aws logs tail //ecs/lingulu-backend-core --follow` | Live logs |
| `./cleanup-all.sh` | Delete everything |
| `./setup-ecs-service.sh` | Redeploy |

---

**JALANKAN SEKARANG:**
```bash
cd .github/scripts
./emergency-cleanup.sh
```

**KEMUNGKINAN BESAR:** Database connection problem setelah CPU/memory di-fix!

**CHECK LOGS untuk confirm!** 🔍

