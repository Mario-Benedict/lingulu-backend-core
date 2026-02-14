# 🗄️ AWS Database Setup Guide

Setup PostgreSQL, Redis, dan MongoDB di AWS untuk Lingulu Backend.

## 📋 Overview

Script akan membuat:
- **RDS PostgreSQL** - Database utama (db.t3.micro)
- **ElastiCache Redis** - Caching (cache.t3.micro)
- **DocumentDB** - MongoDB compatible (db.t3.medium)

**⚡ Free Tier Optimized:**
- Backup retention: 0 days (free tier requirement)
- Instance size: db.t3.micro (free tier eligible)
- Perfect untuk development & testing

## 🚀 Quick Setup

### 1. Run Setup Script

```bash
cd .github/scripts
chmod +x setup-databases.sh
./setup-databases.sh
```

**Note:** Proses ini memakan waktu **15-30 menit** karena provisioning database.

### 2. Get Passwords

Script akan menyimpan password di AWS Secrets Manager:

```bash
# Get PostgreSQL password
aws secretsmanager get-secret-value \
  --secret-id lingulu/postgres-password \
  --query SecretString \
  --output text

# Get MongoDB password
aws secretsmanager get-secret-value \
  --secret-id lingulu/mongo-password \
  --query SecretString \
  --output text
```

### 3. Add to GitHub Secrets

Tambahkan credentials berikut ke GitHub Secrets:

```
POSTGRESQL_DB_HOST=<endpoint dari output script>
POSTGRESQL_DB_PORT=5432
POSTGRESQL_DB_NAME=postgres
POSTGRESQL_DB_USER=lingulu_admin
POSTGRESQL_DB_PASSWORD=<dari Secrets Manager>

REDIS_DB_HOST=<endpoint dari output script>
REDIS_DB_PORT=6379
REDIS_DB_USER=
REDIS_DB_PASSWORD=

MONGO_DB_HOST=<endpoint dari output script>
MONGO_DB_PORT=27017
MONGO_DB_USER=lingulu_admin
MONGO_DB_NAME=lingulu
MONGO_DB_PASSWORD=<dari Secrets Manager>
```

---

## 📊 Database Details

### PostgreSQL (RDS)

**Instance Class:** db.t3.micro
**Engine:** PostgreSQL 16.1
**Storage:** 20 GB
**Backup:** Disabled (for Free Tier compatibility)
**Access:** Public (via security group)

**Connection String:**
```
jdbc:postgresql://<endpoint>:5432/postgres
```

**Default Database:** `postgres`
**Username:** `lingulu_admin`

### Redis (ElastiCache)

**Node Type:** cache.t3.micro
**Engine:** Redis
**Nodes:** 1

**Connection:**
```
Host: <endpoint>
Port: 6379
```

**Note:** Redis tidak memerlukan password (default config)

### MongoDB (DocumentDB)

**Instance Class:** db.t3.medium
**Engine:** DocumentDB (MongoDB 5.0 compatible)

**Connection String:**
```
mongodb://lingulu_admin:<password>@<endpoint>:27017/lingulu?authSource=admin
```

**Default Database:** `lingulu`
**Username:** `lingulu_admin`

**Important:** DocumentDB memerlukan TLS/SSL connection!

---

## 🔧 Manual Configuration

Jika tidak ingin menggunakan script, bisa setup manual via AWS Console:

### RDS PostgreSQL

1. Go to **RDS Console**
2. Create Database
3. Choose **PostgreSQL**
4. Template: **Free tier** atau **Production**
5. DB instance: `db.t3.micro`
6. Master username: `lingulu_admin`
7. Auto-generate password atau input manual
8. Public access: **Yes**
9. VPC security group: Allow port 5432

### ElastiCache Redis

1. Go to **ElastiCache Console**
2. Create Redis cluster
3. Node type: `cache.t3.micro`
4. Number of replicas: 0
5. Subnet group: Select subnets
6. Security group: Allow port 6379

### DocumentDB

1. Go to **DocumentDB Console**
2. Create cluster
3. Instance class: `db.t3.medium`
4. Number of instances: 1
5. Username: `lingulu_admin`
6. Password: Auto-generate atau manual
7. VPC security group: Allow port 27017

---

## 🔒 Security Group Rules

Script otomatis membuat security group dengan rules:

```
Inbound Rules:
- Port 5432 (PostgreSQL) - 0.0.0.0/0
- Port 6379 (Redis) - 0.0.0.0/0
- Port 27017 (MongoDB) - 0.0.0.0/0
```

**Production Note:** Untuk production, sebaiknya restrict source ke:
- ECS tasks security group
- Specific IP ranges
- VPN/Bastion host

---

## 📝 Environment Variables

Setelah database ready, environment variables ini akan otomatis di-inject ke ECS container saat deployment:

```
POSTGRESQL_DB_HOST
POSTGRESQL_DB_PORT
POSTGRESQL_DB_NAME
POSTGRESQL_DB_USER
POSTGRESQL_DB_PASSWORD

REDIS_DB_HOST
REDIS_DB_PORT
REDIS_DB_USER
REDIS_DB_PASSWORD

MONGO_DB_HOST
MONGO_DB_PORT
MONGO_DB_USER
MONGO_DB_NAME
MONGO_DB_PASSWORD
```

Backend Spring Boot akan otomatis menggunakan values ini via `application.properties`.

---

## 🧪 Testing Connections

### Test PostgreSQL

```bash
psql -h <endpoint> -p 5432 -U lingulu_admin -d postgres
# Enter password when prompted
```

### Test Redis

```bash
redis-cli -h <endpoint> -p 6379
# Try: PING (should return PONG)
```

### Test MongoDB

```bash
mongosh "mongodb://lingulu_admin:<password>@<endpoint>:27017/lingulu?authSource=admin&tls=true"
```

---

## 💰 Cost Estimation

### Monthly Costs (approximate):

- **RDS PostgreSQL (db.t3.micro):** ~$15-20/month
- **ElastiCache Redis (cache.t3.micro):** ~$12-15/month
- **DocumentDB (db.t3.medium):** ~$50-60/month

**Total:** ~$77-95/month

**Free Tier:** RDS dan ElastiCache eligible untuk free tier (12 months)

---

## 🔄 Database Initialization

### Create Database (PostgreSQL)

Setelah connect ke PostgreSQL, buat database jika perlu:

```sql
CREATE DATABASE lingulu;
```

### Create Database (MongoDB)

DocumentDB akan auto-create database saat first connection.

---

## 📈 Monitoring

### CloudWatch Metrics

Semua database otomatis kirim metrics ke CloudWatch:

- CPU Utilization
- Free Storage Space
- Database Connections
- Read/Write IOPS

### Alerts (Optional)

Setup CloudWatch Alarms untuk:
- CPU > 80%
- Storage < 10%
- Connection count > threshold

---

## 🔐 Backup & Recovery

### RDS PostgreSQL

- **Automated Backups:** Disabled (Free Tier limitation)
- **Manual Snapshots:** Available & Recommended
- **Point-in-time Recovery:** Not available (requires automated backups)

**Recommended:** Take manual snapshots regularly
```bash
aws rds create-db-snapshot \
  --db-instance-identifier lingulu-backend-core-postgres \
  --db-snapshot-identifier lingulu-backup-$(date +%Y%m%d)
```

### DocumentDB

- **Automated Backups:** Enabled by default
- **Retention:** 1-35 days
- **Manual Snapshots:** Available

### ElastiCache Redis

- **Snapshots:** Manual snapshots supported
- **Backup Window:** Configure as needed

---

## 🗑️ Cleanup (Delete Resources)

**Warning:** This will delete all data!

```bash
# Delete RDS
aws rds delete-db-instance \
  --db-instance-identifier lingulu-backend-core-postgres \
  --skip-final-snapshot

# Delete Redis
aws elasticache delete-cache-cluster \
  --cache-cluster-id lingulu-backend-core-redis

# Delete DocumentDB
aws docdb delete-db-cluster \
  --db-cluster-identifier lingulu-backend-core-mongo \
  --skip-final-snapshot
```

---

## ✅ Verification Checklist

- [ ] RDS PostgreSQL created
- [ ] ElastiCache Redis created
- [ ] DocumentDB created
- [ ] Security groups configured
- [ ] Endpoints noted
- [ ] Passwords stored in Secrets Manager
- [ ] GitHub Secrets updated
- [ ] Test connections successful
- [ ] Application deployed and connected

---

## 🆘 Troubleshooting

### Can't connect to database

1. Check security group rules
2. Verify endpoint hostname
3. Check credentials
4. Ensure public accessibility enabled

### Connection timeout

1. Check VPC and subnet configuration
2. Verify security group allows traffic
3. Check if database is in "available" state

### MongoDB TLS errors

DocumentDB requires TLS. Make sure Spring Boot MongoDB config includes:
```
?tls=true&tlsAllowInvalidHostnames=true
```

---

## 📚 References

- [RDS PostgreSQL Documentation](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_PostgreSQL.html)
- [ElastiCache Redis Documentation](https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/)
- [DocumentDB Documentation](https://docs.aws.amazon.com/documentdb/)

---

**Created:** February 14, 2026  
**Version:** 1.0  
**Status:** ✅ Ready to Use




