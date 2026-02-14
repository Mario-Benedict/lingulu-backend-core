# 🆓 Complete FREE TIER Setup Guide

Setup **SEMUA services yang Anda butuhkan** dengan **100% AWS Free Tier + External Free Services**!

## ✅ Services yang Didapat (100% GRATIS!)

### AWS Free Tier:
1. ✅ **PostgreSQL** → RDS db.t3.micro (750 hours/month)
2. ✅ **Redis** → ElastiCache cache.t3.micro (750 hours/month)
3. ✅ **VPC** → Internet Gateway, Subnets (FREE)
4. ✅ **ECS** → Fargate 20GB storage, 10GB transfer/month (FREE)
5. ✅ **ECR** → 500MB storage/month (FREE)
6. ✅ **API Gateway** → 1 million requests/month (FREE)
7. ✅ **CloudWatch** → 5GB logs, 10 metrics (FREE)

### External Free Services:
8. ✅ **MongoDB** → MongoDB Atlas 512MB (FREE FOREVER)

---

## 🚀 Step-by-Step Setup

### Step 1: Setup VPC (2 menit)
```bash
cd .github/scripts
chmod +x setup-vpc.sh
./setup-vpc.sh
```

**Creates:**
- VPC dengan 3 public subnets (multi-AZ)
- Internet Gateway
- **NO NAT Gateway** (saves $32/month)

**Cost: $0/month** ✅

---

### Step 2: Setup Databases (20-30 menit)
```bash
chmod +x setup-databases-free.sh
./setup-databases-free.sh
```

**Creates:**
- ✅ RDS PostgreSQL (db.t3.micro)
- ✅ ElastiCache Redis (cache.t3.micro)

**Cost: $0/month** (under free tier) ✅

---

### Step 3: Setup MongoDB Atlas (5 menit)

#### 3.1 Sign Up
1. Go to https://www.mongodb.com/cloud/atlas/register
2. Sign up (FREE, no credit card needed)

#### 3.2 Create FREE Cluster
1. Click "Build a Database"
2. Choose **FREE** tier (M0 - 512MB)
3. Select **AWS** & **ap-southeast-1** (Singapore)
4. Click "Create"

#### 3.3 Create Database User
1. Security → Database Access
2. Add New User
3. Username: `lingulu_admin`
4. Password: Auto-generate (save it!)
5. Database User Privileges: **Read & Write**

#### 3.4 Whitelist IP
1. Security → Network Access
2. Add IP Address
3. **Allow access from anywhere**: `0.0.0.0/0`
4. (Production: gunakan specific IP)

#### 3.5 Get Connection String
1. Database → Connect
2. Connect your application
3. Copy connection string:
   ```
   mongodb+srv://lingulu_admin:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
   ```
4. Replace `<password>` dengan password Anda

**Cost: $0/month (FREE FOREVER)** ✅

---

### Step 4: Setup ECS & ECR (2 menit)
```bash
chmod +x setup-aws.sh
./setup-aws.sh
```

**Creates:**
- ECR Repository
- ECS Cluster  
- IAM Roles
- CloudWatch Logs

**Cost: $0/month** (under free tier) ✅

---

### Step 5: Add GitHub Secrets

Get database credentials:

```bash
# Get PostgreSQL password
POSTGRES_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id lingulu/postgres-password \
  --query SecretString \
  --output text)

echo "PostgreSQL Password: $POSTGRES_PASSWORD"
```

Add to **GitHub Secrets**:

```
# PostgreSQL (RDS)
POSTGRESQL_DB_HOST=<from setup-databases-free.sh output>
POSTGRESQL_DB_PORT=5432
POSTGRESQL_DB_NAME=postgres
POSTGRESQL_DB_USER=lingulu_admin
POSTGRESQL_DB_PASSWORD=<from command above>

# Redis (ElastiCache)
REDIS_DB_HOST=<from setup-databases-free.sh output>
REDIS_DB_PORT=6379
REDIS_DB_USER=
REDIS_DB_PASSWORD=

# MongoDB (Atlas)
MONGO_DB_HOST=cluster0.xxxxx.mongodb.net
MONGO_DB_PORT=27017
MONGO_DB_USER=lingulu_admin
MONGO_DB_NAME=lingulu
MONGO_DB_PASSWORD=<from MongoDB Atlas>

# Plus all other secrets from .github/SECRETS.md
```

---

## 🎯 Architecture (FREE TIER)

```
┌──────────────────────────────────────────────┐
│              Internet                        │
└──────────────┬───────────────────────────────┘
               │
       ┌───────▼────────┐
       │  API Gateway   │ (1M requests/month FREE)
       │   (Future)     │
       └───────┬────────┘
               │
       ┌───────▼────────────────────────────────┐
       │         Lingulu VPC (FREE)             │
       │                                        │
       │  ┌──────────────────────────────────┐ │
       │  │  Public Subnets (3 AZs) - FREE  │ │
       │  │                                  │ │
       │  │  ┌────────────────────────────┐ │ │
       │  │  │  ECS Fargate Tasks        │ │ │
       │  │  │  (Backend Services)       │ │ │
       │  │  │  FREE: 20GB, 10GB/month   │ │ │
       │  │  └────────────────────────────┘ │ │
       │  │                                  │ │
       │  │  ┌────────────────────────────┐ │ │
       │  │  │  RDS PostgreSQL           │ │ │
       │  │  │  db.t3.micro              │ │ │
       │  │  │  FREE: 750 hours/month    │ │ │
       │  │  └────────────────────────────┘ │ │
       │  │                                  │ │
       │  │  ┌────────────────────────────┐ │ │
       │  │  │  ElastiCache Redis        │ │ │
       │  │  │  cache.t3.micro           │ │ │
       │  │  │  FREE: 750 hours/month    │ │ │
       │  │  └────────────────────────────┘ │ │
       │  │                                  │ │
       │  └──────────────────────────────────┘ │
       │                                        │
       └────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│         MongoDB Atlas (External)              │
│         FREE: 512MB Forever                   │
└──────────────────────────────────────────────┘
```

**Security:**
- Security groups restrict access ke VPC CIDR only
- RDS publicly accessible (tapi restricted by SG)
- MongoDB Atlas dari cloud (always available)

---

## 💰 Cost Breakdown

### Per Month (Free Tier):

| Service | Free Tier Limit | Cost |
|---------|----------------|------|
| **RDS PostgreSQL** | 750 hours (db.t3.micro) | $0 |
| **ElastiCache Redis** | 750 hours (cache.t3.micro) | $0 |
| **MongoDB Atlas** | 512MB forever | $0 |
| **VPC** | Unlimited | $0 |
| **Internet Gateway** | Unlimited | $0 |
| **ECS Fargate** | 20GB storage, 10GB transfer | $0 |
| **ECR** | 500MB storage | $0 |
| **API Gateway** | 1M requests | $0 |
| **CloudWatch** | 5GB logs, 10 metrics | $0 |

**Total: $0/month** (under free tier limits) 🎉

### After Free Tier (12 months):
- RDS: ~$15/month
- Redis: ~$12/month  
- MongoDB Atlas: Still FREE!
- Others: ~$5-10/month

**Total after free tier:** ~$32-37/month

---

## ⚠️ Free Tier Limits & Tips

### RDS PostgreSQL (750 hours/month)
- **1 instance** running **24/7** = 720 hours ✅
- db.t3.micro only
- 20GB storage (FREE)
- **Tip:** Stop instance when not in use to save hours

### ElastiCache Redis (750 hours/month)
- **1 node** running **24/7** = 720 hours ✅
- cache.t3.micro only
- **Tip:** Use for caching only, not as primary DB

### MongoDB Atlas (512MB)
- **FREE FOREVER!** 🎉
- No time limit
- Perfect for development
- **Upgrade** when you need more space

### ECS Fargate (20GB storage, 10GB transfer)
- **1 task** with 512MB RAM, 1 vCPU ✅
- **Tip:** Use smallest task size possible

### API Gateway (1M requests/month)
- **33,333 requests/day** ✅
- Perfect for development
- **Tip:** Enable caching to reduce requests

---

## 🔧 API Gateway Setup (Future)

Ketika siap untuk API Gateway:

### Step 1: Create REST API
```bash
# Via AWS Console or CLI
aws apigateway create-rest-api \
  --name lingulu-api \
  --description "Lingulu API Gateway" \
  --region ap-southeast-1
```

### Step 2: Create VPC Link (optional)
```bash
# For private VPC resources
aws apigatewayv2 create-vpc-link \
  --name lingulu-vpc-link \
  --subnet-ids <subnet-ids> \
  --security-group-ids <sg-ids>
```

### Step 3: Integrate with Backend
- **HTTP Integration** → ECS Task Public IP
- **VPC Link** → Private ECS (jika pakai private subnet)

### Cost:
- **1M requests/month:** FREE
- **REST API:** FREE
- **VPC Link:** $0.025/hour (~$18/month) - optional

**Recommended:** Use HTTP integration to public ECS (FREE!)

---

## 📊 Monitoring (FREE)

### CloudWatch (Included)
```bash
# View logs
aws logs tail //ecs/lingulu --follow

# View metrics  
aws cloudwatch get-metric-statistics ...
```

**Free Tier:**
- 5GB log ingestion
- 10 custom metrics
- 1M API requests

---

## 🆘 Troubleshooting

### "Exceeded free tier"
**Check usage:**
```bash
# RDS hours
aws rds describe-db-instances --query 'DBInstances[*].[DBInstanceIdentifier,InstanceCreateTime]'

# Stop instance when not in use
aws rds stop-db-instance --db-instance-identifier lingulu-postgres
```

### MongoDB Atlas connection fails
**Check:**
1. IP Whitelist: Must have `0.0.0.0/0`
2. Database user created
3. Connection string correct
4. Replace `<password>` in connection string

### ECS task won't start
**Check:**
1. Task in public subnet
2. Auto-assign public IP enabled
3. Security group allows port 8080
4. ECR image exists

---

## ✅ Complete Setup Checklist

### Infrastructure
- [ ] VPC created (setup-vpc.sh)
- [ ] PostgreSQL created (setup-databases-free.sh)
- [ ] Redis created (setup-databases-free.sh)
- [ ] MongoDB Atlas account created
- [ ] MongoDB cluster created (FREE tier)
- [ ] ECS cluster created (setup-aws.sh)
- [ ] ECR repository created

### Configuration
- [ ] MongoDB user created
- [ ] MongoDB IP whitelisted
- [ ] Connection strings obtained
- [ ] All GitHub Secrets added (30+ secrets)
- [ ] Database credentials tested

### Deployment
- [ ] First push to main
- [ ] CI/CD runs successfully
- [ ] ECS task running
- [ ] Application accessible
- [ ] Database connections work

---

## 🎯 Summary

### Yang Anda Dapat (GRATIS):
✅ **PostgreSQL** - RDS Free Tier  
✅ **Redis** - ElastiCache Free Tier  
✅ **MongoDB** - Atlas Free Forever  
✅ **Backend** - ECS Fargate Free Tier  
✅ **Container Registry** - ECR Free Tier  
✅ **API Gateway** - 1M requests/month  
✅ **Networking** - VPC, IGW Free  
✅ **Monitoring** - CloudWatch Free Tier  

### Cost:
- **First 12 months:** $0/month
- **After free tier:** ~$32-37/month
- **MongoDB:** FREE FOREVER

### Perfect untuk:
- ✅ Development
- ✅ Learning
- ✅ MVP/Prototype
- ✅ Small projects
- ✅ Side projects

---

## 🚀 Quick Commands

```bash
# Full setup
./setup-vpc.sh
./setup-databases-free.sh
./setup-aws.sh

# Get PostgreSQL password
aws secretsmanager get-secret-value \
  --secret-id lingulu/postgres-password \
  --query SecretString --output text

# Deploy
git push origin main

# Monitor
aws logs tail //ecs/lingulu --follow

# Stop RDS (save free hours)
aws rds stop-db-instance --db-instance-identifier lingulu-postgres

# Start RDS
aws rds start-db-instance --db-instance-identifier lingulu-postgres
```

---

**Everything is FREE! Siap untuk production (scale saat perlu)! 🎉**

