# 🏗️ Complete Infrastructure Setup Guide

Setup order yang benar untuk semua AWS resources.

## 📋 Recommended Setup Order

### Step 1: Setup Shared VPC (Recommended)
```bash
cd .github/scripts
chmod +x setup-vpc.sh
./setup-vpc.sh
```

**Creates:**
- ✅ VPC dengan public & private subnets
- ✅ Internet Gateway untuk public access
- ✅ NAT Gateway untuk private subnet internet access
- ✅ Proper routing tables
- ✅ Ready untuk multiple services (Backend, API Gateway, dll)

**Time:** ~5 minutes

**Cost:** ~$32/month (NAT Gateway)

---

### Step 2: Setup Databases
```bash
chmod +x setup-databases.sh
./setup-databases.sh
```

**Creates:**
- ✅ RDS PostgreSQL (private subnet)
- ✅ ElastiCache Redis (private subnet)
- ✅ DocumentDB MongoDB (private subnet)

**Uses:** VPC from Step 1

**Time:** ~20-30 minutes

---

### Step 3: Setup ECS & ECR
```bash
chmod +x setup-aws.sh
./setup-aws.sh
```

**Creates:**
- ✅ ECR Repository
- ✅ ECS Cluster
- ✅ CloudWatch Logs
- ✅ IAM Roles

**Time:** ~2 minutes

---

## 🎯 Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Lingulu VPC                          │
│                   (10.0.0.0/16)                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────────────────────────────────────┐          │
│  │        Public Subnets (3 AZs)           │          │
│  │  - 10.0.1.0/24 (AZ-a)                   │          │
│  │  - 10.0.2.0/24 (AZ-b)                   │          │
│  │  - 10.0.3.0/24 (AZ-c)                   │          │
│  │                                          │          │
│  │  Contains:                               │          │
│  │  • Load Balancer (ALB)                  │          │
│  │  • NAT Gateway                          │          │
│  │  • API Gateway VPC Link (future)        │          │
│  └─────────────┬───────────────────────────┘          │
│                │                                        │
│                │ Internet Gateway                       │
│                ↓                                        │
│           Internet                                      │
│                                                         │
│  ┌─────────────────────────────────────────┐          │
│  │       Private Subnets (3 AZs)           │          │
│  │  - 10.0.11.0/24 (AZ-a)                  │          │
│  │  - 10.0.12.0/24 (AZ-b)                  │          │
│  │  - 10.0.13.0/24 (AZ-c)                  │          │
│  │                                          │          │
│  │  Contains:                               │          │
│  │  • ECS Tasks (Backend)                  │          │
│  │  • RDS PostgreSQL                       │          │
│  │  • ElastiCache Redis                    │          │
│  │  • DocumentDB (MongoDB)                 │          │
│  │  • Future microservices                 │          │
│  └─────────────┬───────────────────────────┘          │
│                │                                        │
│                │ NAT Gateway                            │
│                ↓                                        │
│           Internet (outbound only)                     │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 🔐 Security Architecture

### Public Subnets
- **Purpose:** Load Balancers, NAT Gateway, Bastion (optional)
- **Internet:** Bidirectional (via Internet Gateway)
- **Access:** From internet to LB, LB to private subnets

### Private Subnets
- **Purpose:** Application servers, Databases
- **Internet:** Outbound only (via NAT Gateway)
- **Access:** Only from within VPC
- **Security:** No direct internet exposure

### Security Groups
```
Database SG:
  Inbound: Port 5432, 6379, 27017 from VPC CIDR only

ECS Tasks SG:
  Inbound: Port 8080 from ALB SG only
  Outbound: All (to databases, external APIs)

ALB SG:
  Inbound: Port 80, 443 from 0.0.0.0/0
  Outbound: Port 8080 to ECS Tasks SG
```

---

## 🚀 Quick Setup (All-in-One)

```bash
cd .github/scripts

# 1. VPC
chmod +x setup-vpc.sh
./setup-vpc.sh

# 2. Databases
chmod +x setup-databases.sh
./setup-databases.sh

# 3. ECS
chmod +x setup-aws.sh
./setup-aws.sh

# Done! Total time: ~30-40 minutes
```

---

## 💡 Alternative Setup (No VPC Script)

Jika tidak ingin run `setup-vpc.sh`:

```bash
# setup-databases.sh will auto-detect:
# 1. Existing lingulu-vpc (from setup-vpc.sh)
# 2. Existing lingulu-backend-core-vpc
# 3. Default VPC
# 4. Or create simple VPC if none found

./setup-databases.sh
# Will prompt: Continue creating simple VPC? (yes/no)
```

**Simple VPC limitations:**
- ❌ No NAT Gateway (private subnets can't access internet)
- ❌ Not suitable for multiple services
- ✅ OK for testing with publicly accessible databases only

---

## 🏷️ VPC Naming Convention

### Shared Project VPC (Recommended)
```
Name: lingulu-vpc
Tag: Project=lingulu
Created by: setup-vpc.sh
```

### Database-Only VPC (Legacy)
```
Name: lingulu-backend-core-vpc
Created by: setup-databases.sh (if no VPC found)
```

---

## 🔄 Migration Path

### If you have old VPC (lingulu-backend-core-vpc):

**Option 1: Keep using it**
```bash
# No changes needed
# setup-databases.sh will auto-detect and use it
```

**Option 2: Migrate to new shared VPC**
```bash
# 1. Cleanup old resources
./cleanup-all.sh

# 2. Setup new shared VPC
./setup-vpc.sh

# 3. Re-setup databases
./setup-databases.sh

# 4. Re-deploy application
```

---

## 💰 Cost Breakdown

### VPC (setup-vpc.sh)
- VPC: Free
- Subnets: Free
- Internet Gateway: Free
- **NAT Gateway: ~$32/month** ⚠️
- Elastic IP (NAT): ~$3.6/month (if not attached)

### Databases
- RDS: ~$15-20/month (Free Tier eligible)
- Redis: ~$12-15/month (Free Tier eligible)
- MongoDB: ~$50-60/month

### ECS
- Fargate: ~$15-20/month (1 task)
- CloudWatch: ~$1-2/month

**Total: ~$125-150/month** (with NAT)  
**Total: ~$93-118/month** (without NAT)

### Cost Optimization

**Remove NAT Gateway if:**
- Private subnets don't need internet access
- All external API calls can go through ALB/API Gateway
- Not using package managers in ECS tasks

```bash
# Delete NAT Gateway
aws ec2 delete-nat-gateway --nat-gateway-id <nat-id>

# Release Elastic IP
aws ec2 release-address --allocation-id <eip-id>

# Saves: ~$32/month
```

---

## 📝 Subnet Usage Guide

### Public Subnets - Use for:
- ✅ Application Load Balancer
- ✅ NAT Gateway
- ✅ Bastion Host (if needed)
- ✅ API Gateway VPC Link endpoint
- ❌ Don't use for: Databases, application servers

### Private Subnets - Use for:
- ✅ ECS Tasks (Backend services)
- ✅ RDS PostgreSQL
- ✅ ElastiCache Redis
- ✅ DocumentDB
- ✅ Future microservices
- ❌ Don't use for: Resources needing direct internet access

---

## ✅ Verification Checklist

After setup, verify:

### VPC
- [ ] VPC exists with tag `lingulu-vpc`
- [ ] 3 public subnets in different AZs
- [ ] 3 private subnets in different AZs
- [ ] Internet Gateway attached
- [ ] NAT Gateway in public subnet
- [ ] Public route table points to IGW
- [ ] Private route table points to NAT

### Databases
- [ ] RDS in private subnet
- [ ] Redis in private subnet
- [ ] MongoDB in private subnet
- [ ] Security groups allow VPC CIDR only

### ECS
- [ ] Cluster exists
- [ ] ECR repository exists
- [ ] IAM roles created

---

## 🆘 Troubleshooting

### "No internet gateway attached" error
**Solution:** Run `setup-vpc.sh` first, or use default VPC

### Databases can't be accessed from ECS
**Check:**
1. ECS tasks in same VPC as databases
2. Security groups allow VPC CIDR
3. Database endpoints correct

### ECS tasks can't pull Docker images
**Check:**
1. Tasks in private subnet with NAT Gateway
2. Or tasks in public subnet with public IP

---

## 📚 Next Steps After Setup

1. **Setup Application Load Balancer**
   - Place in public subnets
   - Target ECS tasks in private subnets

2. **Setup API Gateway** (future)
   - VPC Link to ALB
   - Or direct integration

3. **Setup CloudFront** (optional)
   - Origin: ALB
   - Distribute globally

4. **Setup Auto Scaling**
   - ECS Service auto-scaling
   - Database read replicas

---

**Recommended:** Always use `setup-vpc.sh` first for proper multi-service architecture!

