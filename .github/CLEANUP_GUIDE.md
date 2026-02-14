# 🗑️ Cleanup Script - Delete All AWS Resources

Script untuk menghapus **SEMUA** resources AWS yang dibuat oleh setup scripts.

## ⚠️ WARNING

**Script ini akan menghapus SEMUA resources termasuk DATA!**

Resources yang akan dihapus:
- ✅ ECS Cluster & Services
- ✅ ECR Repository & Docker Images
- ✅ RDS PostgreSQL (+ DATA!)
- ✅ ElastiCache Redis
- ✅ DocumentDB MongoDB (+ DATA!)
- ✅ VPC & Subnets
- ✅ Security Groups
- ✅ CloudWatch Log Groups
- ✅ IAM Roles
- ✅ Secrets Manager Secrets

---

## 🚀 Usage

```bash
cd .github/scripts
chmod +x cleanup-all.sh
./cleanup-all.sh
```

**Confirmation required:**
```
WARNING: This will DELETE ALL resources!
Are you sure you want to continue? (type 'yes' to confirm): yes
```

---

## ⏱️ Estimated Time

Total cleanup time: **15-25 minutes**

- ECS: ~1 minute
- RDS: ~5-10 minutes
- ElastiCache: ~5-10 minutes
- DocumentDB: ~5-10 minutes
- VPC & Others: ~1-2 minutes

---

## 📋 Deletion Order

Script menghapus resources dalam urutan yang benar (dependencies first):

```
1. ECS Services & Cluster
2. RDS PostgreSQL
3. ElastiCache Redis
4. DocumentDB MongoDB
5. DB Subnet Groups
6. Security Groups
7. VPC & Networking
8. ECR Repository
9. CloudWatch Logs
10. IAM Roles
11. Secrets Manager
```

---

## 🔍 What Gets Deleted

### Compute & Containers
```
ECS Cluster:     lingulu-backend-core-cluster
ECS Service:     lingulu-backend-core-service
ECR Repository:  lingulu-backend-core (+ all images)
```

### Databases
```
PostgreSQL:      lingulu-backend-core-postgres
Redis:           lingulu-backend-core-redis
MongoDB:         lingulu-backend-core-mongo
```

### Networking
```
VPC:             lingulu-backend-core-vpc (if created)
Subnets:         lingulu-backend-core-subnet-1/2/3
Internet Gateway: lingulu-backend-core-igw
Security Groups: lingulu-backend-core-sg, lingulu-backend-core-db-sg
```

### Logs & Secrets
```
CloudWatch:      //ecs/lingulu-backend-core
Secrets:         lingulu/postgres-password, lingulu/mongo-password
```

### IAM
```
Roles:           ecsTaskExecutionRole, ecsTaskRole
```

---

## 💰 Stop Billing

Setelah cleanup, billing akan berhenti untuk:

- ✅ ECS Fargate tasks
- ✅ RDS instance (~$15-20/month)
- ✅ ElastiCache (~$12-15/month)
- ✅ DocumentDB (~$50-60/month)
- ✅ Data transfer
- ✅ CloudWatch logs

**Total savings:** ~$77-95/month

---

## 🔒 Safety Features

1. **Confirmation Required** - Must type 'yes' to proceed
2. **Ordered Deletion** - Dependencies deleted first
3. **Skip Missing Resources** - Won't error if resource doesn't exist
4. **Force Delete** - Removes all dependencies
5. **No Snapshots** - Skip final snapshots (faster deletion)

---

## 📊 Example Output

```bash
./cleanup-all.sh

========================================
AWS Resources Cleanup Script
========================================

WARNING: This will DELETE ALL resources!
Are you sure you want to continue? (type 'yes' to confirm): yes

Starting cleanup...

AWS Account: 123456789012

Deleting ECS resources...
  Scaling service to 0...
  Deleting service...
  Deleting cluster...
✓ ECS resources deleted

Deleting RDS PostgreSQL...
  Deleting DB instance (this may take 5-10 minutes)...
  Waiting for deletion...
✓ RDS deleted

Deleting ElastiCache Redis...
  Deleting cache cluster (this may take 5-10 minutes)...
  Waiting for deletion...
✓ ElastiCache deleted

Deleting DocumentDB...
  Deleting DB instance...
  Deleting DB cluster (this may take 5-10 minutes)...
  Waiting for deletion...
✓ DocumentDB deleted

...

========================================
Cleanup Complete!
========================================

All resources have been deleted:
  ✓ ECS Cluster & Services
  ✓ ECR Repository
  ✓ RDS PostgreSQL
  ✓ ElastiCache Redis
  ✓ DocumentDB (MongoDB)
  ✓ VPC & Networking
  ✓ Security Groups
  ✓ CloudWatch Logs
  ✓ IAM Roles
  ✓ Secrets Manager
```

---

## 🔄 Partial Cleanup

Jika hanya ingin delete specific resources:

### Delete Databases Only
```bash
# Edit cleanup-all.sh, comment out unwanted deletions
# Or use AWS CLI directly:

# Delete RDS
aws rds delete-db-instance \
  --db-instance-identifier lingulu-backend-core-postgres \
  --skip-final-snapshot

# Delete Redis
aws elasticache delete-cache-cluster \
  --cache-cluster-id lingulu-backend-core-redis

# Delete MongoDB
aws docdb delete-db-cluster \
  --db-cluster-identifier lingulu-backend-core-mongo \
  --skip-final-snapshot
```

### Delete ECS Only
```bash
aws ecs delete-service \
  --cluster lingulu-backend-core-cluster \
  --service lingulu-backend-core-service \
  --force

aws ecs delete-cluster \
  --cluster lingulu-backend-core-cluster
```

### Delete ECR Images Only (keep repository)
```bash
# List images
aws ecr list-images \
  --repository-name lingulu-backend-core

# Delete all images
aws ecr batch-delete-image \
  --repository-name lingulu-backend-core \
  --image-ids imageTag=latest
```

---

## ⚠️ Before Running

### Backup Data (if needed)
```bash
# PostgreSQL
pg_dump -h <endpoint> -U lingulu_admin -d postgres > backup.sql

# MongoDB
mongodump --uri="mongodb://..." --out=backup/
```

### Export Environment Variables
```bash
# Save from GitHub Secrets
# Or export from AWS Console
```

### Take Snapshots
```bash
# RDS Snapshot
aws rds create-db-snapshot \
  --db-instance-identifier lingulu-backend-core-postgres \
  --db-snapshot-identifier lingulu-backup-$(date +%Y%m%d)

# DocumentDB Snapshot
aws docdb create-db-cluster-snapshot \
  --db-cluster-identifier lingulu-backend-core-mongo \
  --db-cluster-snapshot-identifier lingulu-mongo-backup-$(date +%Y%m%d)
```

---

## 🔧 Troubleshooting

### "Resource is in use"
Some resources may have dependencies. Wait a few minutes and retry.

### "Access Denied"
Ensure AWS credentials have delete permissions.

### "Resource not found"
Safe to ignore - resource already deleted or never created.

### Script hangs
Press Ctrl+C to cancel. Some deletions take 10+ minutes (normal).

---

## ✅ Verification

After cleanup, verify all resources deleted:

```bash
# Check ECS
aws ecs list-clusters --region ap-southeast-1

# Check RDS
aws rds describe-db-instances --region ap-southeast-1

# Check ElastiCache
aws elasticache describe-cache-clusters --region ap-southeast-1

# Check DocumentDB
aws docdb describe-db-clusters --region ap-southeast-1

# Check ECR
aws ecr describe-repositories --region ap-southeast-1

# Check VPC
aws ec2 describe-vpcs \
  --filters "Name=tag:Name,Values=lingulu-backend-core-vpc" \
  --region ap-southeast-1
```

Expected: Empty results or "not found" errors

---

## 📝 Notes

- **Irreversible** - Data cannot be recovered after deletion
- **No Snapshots** - Script uses `--skip-final-snapshot` for faster deletion
- **Free Tier Safe** - Safe to use with AWS Free Tier
- **Idempotent** - Safe to run multiple times
- **Async Deletion** - Some resources delete in background

---

## 🔁 Re-setup After Cleanup

To recreate everything:

```bash
# Setup infrastructure
./setup-aws.sh

# Setup databases
./setup-databases.sh

# Update GitHub Secrets with new endpoints
# Deploy application
```

---

**Use with caution!** ⚠️

This script permanently deletes all resources and data.

