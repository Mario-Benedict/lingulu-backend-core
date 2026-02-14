# 🎯 Complete Deployment Guide

Setup lengkap AWS infrastructure + CI/CD untuk Lingulu Backend.

## 📋 Prerequisites

- AWS CLI installed & configured
- GitHub account dengan repository
- Git Bash (Windows) atau terminal (Linux/Mac)

---

## 🚀 Step-by-Step Setup

### Step 1: Setup AWS Infrastructure (15-30 menit)

#### 1.1 Setup ECS & ECR
```bash
cd .github/scripts
chmod +x setup-aws.sh
./setup-aws.sh
```

Creates:
- ✅ ECR Repository
- ✅ ECS Cluster
- ✅ CloudWatch Log Group
- ✅ IAM Roles

#### 1.2 Setup Databases
```bash
chmod +x setup-databases.sh
./setup-databases.sh
```

Creates (takes 15-30 minutes):
- ✅ RDS PostgreSQL
- ✅ ElastiCache Redis
- ✅ DocumentDB (MongoDB)

**Note:** Save the endpoints and passwords!

---

### Step 2: Get Database Credentials (2 menit)

```bash
# Get PostgreSQL password
POSTGRES_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id lingulu/postgres-password \
  --query SecretString \
  --output text)

# Get MongoDB password
MONGO_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id lingulu/mongo-password \
  --query SecretString \
  --output text)

# Display
echo "PostgreSQL Password: $POSTGRES_PASSWORD"
echo "MongoDB Password: $MONGO_PASSWORD"
```

---

### Step 3: Setup GitHub Secrets (10 menit)

Go to: **GitHub → Settings → Secrets → Actions**

Add all 30 secrets (see `.github/SECRETS.md`):

#### AWS Credentials
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

#### Database Credentials (dari Step 1.2 output)
```
POSTGRESQL_DB_HOST=<dari setup-databases.sh>
POSTGRESQL_DB_PORT=5432
POSTGRESQL_DB_NAME=postgres
POSTGRESQL_DB_USER=lingulu_admin
POSTGRESQL_DB_PASSWORD=<dari Secrets Manager>

REDIS_DB_HOST=<dari setup-databases.sh>
REDIS_DB_PORT=6379
REDIS_DB_USER=
REDIS_DB_PASSWORD=

MONGO_DB_HOST=<dari setup-databases.sh>
MONGO_DB_PORT=27017
MONGO_DB_USER=lingulu_admin
MONGO_DB_NAME=lingulu
MONGO_DB_PASSWORD=<dari Secrets Manager>
```

#### Other Secrets
```
SPRING_MAIL_USERNAME
SPRING_MAIL_PASSWORD
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
JWT_SECRET
AWS_REGION_S3
AWS_ENDPOINT
AWS_S3_CHAT_BUCKET_NAME
AWS_S3_PROFILE_BUCKET_NAME
AWS_ACCESS_KEY
AWS_SECRET_KEY
GROQ_API_KEY
AWS_CLOUDFRONT_KEYPAIR
AWS_CLOUDFRONT_PRIVATE_KEY_BASE64
CDN_DOMAIN
```

**Full list:** See `.github/SECRETS.md`

---

### Step 4: Create ECS Task Definition (3 menit)

```bash
# Get AWS Account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# Update task definition
cd .github/ecs
sed -i "s/{ACCOUNT_ID}/${ACCOUNT_ID}/g" task-definition.json

# Register
aws ecs register-task-definition \
  --cli-input-json file://task-definition.json \
  --region ap-southeast-1
```

---

### Step 5: Create ECS Service (5 menit)

```bash
# Get VPC
VPC_ID=$(aws ec2 describe-vpcs \
  --filters "Name=isDefault,Values=true" \
  --query 'Vpcs[0].VpcId' \
  --output text)

# Get Subnets
SUBNET_IDS=$(aws ec2 describe-subnets \
  --filters "Name=vpc-id,Values=${VPC_ID}" \
  --query 'Subnets[*].SubnetId' \
  --output text | tr '\t' ',')

# Create Security Group
SG_ID=$(aws ec2 create-security-group \
  --group-name lingulu-backend-core-sg \
  --description "Lingulu backend security group" \
  --vpc-id ${VPC_ID} \
  --query 'GroupId' \
  --output text)

# Allow port 8080
aws ec2 authorize-security-group-ingress \
  --group-id ${SG_ID} \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0

# Create ECS Service
aws ecs create-service \
  --cluster lingulu-cluster \
  --service-name lingulu-backend-service \
  --task-definition lingulu-backend-core \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[${SUBNET_IDS}],securityGroups=[${SG_ID}],assignPublicIp=ENABLED}" \
  --region ap-southeast-1
```

---

### Step 6: Deploy! (1 menit)

```bash
# Commit & push
git add .
git commit -m "ci: setup complete infrastructure"
git push origin main
```

**Watch it deploy:**
- GitHub Actions → CI workflow runs
- Build & push Docker image to ECR
- CD workflow deploys to ECS
- Application starts with database connections!

---

## ✅ Verification

### 1. Check CI/CD
```bash
# Via GitHub CLI
gh run list --limit 5

# Or via GitHub UI
# Go to: Actions tab
```

### 2. Check ECS Service
```bash
aws ecs describe-services \
  --cluster lingulu-cluster \
  --services lingulu-backend-service \
  --region ap-southeast-1
```

### 3. Get Application URL
```bash
# Get task ARN
TASK_ARN=$(aws ecs list-tasks \
  --cluster lingulu-cluster \
  --service-name lingulu-backend-service \
  --query 'taskArns[0]' \
  --output text \
  --region ap-southeast-1)

# Get task details (includes public IP)
aws ecs describe-tasks \
  --cluster lingulu-cluster \
  --tasks ${TASK_ARN} \
  --region ap-southeast-1
```

### 4. Test Application
```bash
# Health check
curl http://<PUBLIC_IP>:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

---

## 🔍 View Logs

```bash
# Via AWS CLI
aws logs tail //ecs/lingulu-backend-core --follow --region ap-southeast-1

# Via AWS Console
# CloudWatch → Log Groups → //ecs/lingulu-backend-core
```

---

## 📊 What's Running

After complete setup:

```
AWS Resources:
├── ECR
│   └── lingulu-backend-core (Docker images)
├── ECS
│   ├── Cluster: lingulu-cluster
│   └── Service: lingulu-backend-service (1 task)
├── Databases
│   ├── RDS PostgreSQL
│   ├── ElastiCache Redis
│   └── DocumentDB (MongoDB)
├── CloudWatch
│   └── Log Group: //ecs/lingulu-backend-core
└── Secrets Manager
    ├── lingulu/postgres-password
    └── lingulu/mongo-password
```

---

## 🔄 Deployment Flow

```
Developer                  GitHub                 AWS
    |                        |                     |
    | git push main          |                     |
    |----------------------->|                     |
                             |                     |
                             | CI Workflow         |
                             |  - Run tests        |
                             |  - Build image      |
                             |  - Push to ECR ---->|
                             |                     |
                             | CD Workflow         |
                             |  - Update task def  |
                             |  - Deploy to ECS -->|
                             |                     |
                             |                     | ECS pulls image
                             |                     | Start container
                             |                     | Connect to databases
                             |                     | ✅ Application running!
```

---

## 💰 Monthly Cost Estimate

- ECR: ~$1 (storage)
- ECS Fargate: ~$15-20 (1 task, t3.micro equivalent)
- RDS PostgreSQL: ~$15-20 (db.t3.micro)
- ElastiCache Redis: ~$12-15 (cache.t3.micro)
- DocumentDB: ~$50-60 (db.t3.medium)
- CloudWatch Logs: ~$1-2

**Total: ~$94-118/month**

**Note:** RDS & ElastiCache eligible for AWS Free Tier (12 months)

---

## 🎯 Next Steps

1. ✅ Setup Application Load Balancer (optional)
2. ✅ Configure custom domain & HTTPS
3. ✅ Setup CloudWatch alarms
4. ✅ Configure auto-scaling
5. ✅ Setup backup policies
6. ✅ Configure monitoring & alerts

---

## 📚 Documentation Reference

- **[SIMPLE_QUICKSTART.md](SIMPLE_QUICKSTART.md)** - Quick setup ECS
- **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - Database details
- **[SECRETS.md](SECRETS.md)** - All required secrets
- **[workflows/ci.yml](workflows/ci.yml)** - CI configuration
- **[workflows/cd.yml](workflows/cd.yml)** - CD configuration

---

## 🆘 Troubleshooting

### ECS task won't start
```bash
# Check logs
aws logs tail //ecs/lingulu-backend-core --follow --region ap-southeast-1

# Check task stopped reason
aws ecs describe-tasks --cluster lingulu-cluster --tasks <TASK_ID> --region ap-southeast-1
```

### Can't connect to database
1. Check security group allows traffic
2. Verify database is "available" state
3. Check credentials in GitHub Secrets
4. Test connection manually

### CI/CD fails
1. Check GitHub Secrets are all set
2. Review GitHub Actions logs
3. Verify AWS credentials have correct permissions

---

**Setup Complete! 🎉**

Your backend is now:
- ✅ Auto-tested on every push
- ✅ Auto-deployed to production
- ✅ Connected to managed databases
- ✅ Monitored via CloudWatch
- ✅ Scalable & reliable

**Happy Deploying! 🚀**

