#!/bin/bash

# Setup FREE TIER Databases ONLY
# PostgreSQL (RDS) + Redis (ElastiCache)
# MongoDB → Use MongoDB Atlas Free Tier instead

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
AWS_REGION="ap-southeast-1"
APP_NAME="lingulu"
DB_USERNAME="lingulu_admin"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}FREE TIER Database Setup${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${YELLOW}This script creates FREE TIER eligible databases only:${NC}"
echo -e "  ✓ RDS PostgreSQL (db.t3.micro - 750 hours/month FREE)"
echo -e "  ✓ ElastiCache Redis (cache.t3.micro - 750 hours/month FREE)"
echo -e "  ✗ MongoDB → Use MongoDB Atlas Free Tier (512MB)"
echo ""

# Check AWS CLI
if ! command -v aws &> /dev/null; then
    echo -e "${RED}Error: AWS CLI not installed${NC}"
    exit 1
fi

if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}Error: AWS credentials not configured${NC}"
    exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo -e "${GREEN}✓ AWS Account: ${ACCOUNT_ID}${NC}"
echo ""

# Get VPC
echo -e "${YELLOW}Finding VPC...${NC}"
VPC_ID=$(aws ec2 describe-vpcs \
    --filters "Name=tag:Name,Values=lingulu-vpc" \
    --query 'Vpcs[0].VpcId' \
    --region ${AWS_REGION} \
    --output text 2>/dev/null)

if [ -z "$VPC_ID" ] || [ "$VPC_ID" = "None" ]; then
    echo -e "${RED}Error: VPC not found!${NC}"
    echo -e "${YELLOW}Please run setup-vpc.sh first${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Using VPC: ${VPC_ID}${NC}"
echo ""

# Get subnets
echo -e "${YELLOW}Getting subnets...${NC}"
SUBNET_IDS=$(aws ec2 describe-subnets \
    --filters "Name=vpc-id,Values=${VPC_ID}" "Name=tag:Type,Values=public" \
    --query 'Subnets[*].SubnetId' \
    --region ${AWS_REGION} \
    --output text)

if [ -z "$SUBNET_IDS" ]; then
    echo -e "${RED}Error: No subnets found${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Found subnets${NC}"
echo ""

# Create DB subnet group
echo -e "${YELLOW}Creating DB subnet group...${NC}"
DB_SUBNET_GROUP="${APP_NAME}-db-subnet-group"

if aws rds describe-db-subnet-groups --db-subnet-group-name ${DB_SUBNET_GROUP} --region ${AWS_REGION} &> /dev/null; then
    echo -e "${GREEN}✓ DB subnet group already exists${NC}"
else
    aws rds create-db-subnet-group \
        --db-subnet-group-name ${DB_SUBNET_GROUP} \
        --db-subnet-group-description "Lingulu DB subnet group" \
        --subnet-ids ${SUBNET_IDS} \
        --region ${AWS_REGION}
    echo -e "${GREEN}✓ DB subnet group created${NC}"
fi
echo ""

# Create security group
echo -e "${YELLOW}Creating security group...${NC}"
SG_NAME="${APP_NAME}-db-sg"

SG_ID=$(aws ec2 describe-security-groups \
    --filters "Name=group-name,Values=${SG_NAME}" "Name=vpc-id,Values=${VPC_ID}" \
    --query 'SecurityGroups[0].GroupId' \
    --region ${AWS_REGION} \
    --output text 2>/dev/null)

if [ -n "$SG_ID" ] && [ "$SG_ID" != "None" ]; then
    echo -e "${GREEN}✓ Security group exists: ${SG_ID}${NC}"
else
    SG_ID=$(aws ec2 create-security-group \
        --group-name ${SG_NAME} \
        --description "Lingulu databases security group" \
        --vpc-id ${VPC_ID} \
        --region ${AWS_REGION} \
        --query 'GroupId' \
        --output text)

    # Get VPC CIDR
    VPC_CIDR=$(aws ec2 describe-vpcs \
        --vpc-ids ${VPC_ID} \
        --query 'Vpcs[0].CidrBlock' \
        --region ${AWS_REGION} \
        --output text)

    # Allow PostgreSQL from VPC
    aws ec2 authorize-security-group-ingress \
        --group-id ${SG_ID} \
        --protocol tcp \
        --port 5432 \
        --cidr ${VPC_CIDR} \
        --region ${AWS_REGION} 2>/dev/null || true

    # Allow Redis from VPC
    aws ec2 authorize-security-group-ingress \
        --group-id ${SG_ID} \
        --protocol tcp \
        --port 6379 \
        --cidr ${VPC_CIDR} \
        --region ${AWS_REGION} 2>/dev/null || true

    echo -e "${GREEN}✓ Security group created: ${SG_ID}${NC}"
fi
echo ""

# Function to generate password
generate_password() {
    openssl rand -base64 32 | tr -d "=+/" | cut -c1-25
}

# Function to store secret
store_secret() {
    local secret_name=$1
    local secret_value=$2

    if aws secretsmanager describe-secret --secret-id ${secret_name} --region ${AWS_REGION} &> /dev/null; then
        aws secretsmanager update-secret \
            --secret-id ${secret_name} \
            --secret-string "${secret_value}" \
            --region ${AWS_REGION} &> /dev/null
    else
        aws secretsmanager create-secret \
            --name ${secret_name} \
            --secret-string "${secret_value}" \
            --region ${AWS_REGION} &> /dev/null
    fi
}

# Create RDS PostgreSQL
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Creating RDS PostgreSQL (FREE TIER)${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

DB_INSTANCE_ID="${APP_NAME}-postgres"

if aws rds describe-db-instances --db-instance-identifier ${DB_INSTANCE_ID} --region ${AWS_REGION} &> /dev/null; then
    echo -e "${GREEN}✓ PostgreSQL already exists${NC}"
    POSTGRES_ENDPOINT=$(aws rds describe-db-instances \
        --db-instance-identifier ${DB_INSTANCE_ID} \
        --query 'DBInstances[0].Endpoint.Address' \
        --region ${AWS_REGION} \
        --output text)
else
    DB_PASSWORD=$(generate_password)

    echo -e "${YELLOW}Detecting PostgreSQL version...${NC}"
    PG_VERSION=$(aws rds describe-db-engine-versions \
        --engine postgres \
        --region ${AWS_REGION} \
        --query 'reverse(sort_by(DBEngineVersions[*], &EngineVersion))[0].EngineVersion' \
        --output text 2>/dev/null || echo "")

    if [ -z "$PG_VERSION" ] || [ "$PG_VERSION" = "None" ]; then
        PG_VERSION="15.3"
    fi

    echo -e "${GREEN}  Using PostgreSQL ${PG_VERSION}${NC}"
    echo -e "${YELLOW}Creating DB instance (5-10 minutes)...${NC}"

    aws rds create-db-instance \
        --db-instance-identifier ${DB_INSTANCE_ID} \
        --db-instance-class db.t3.micro \
        --engine postgres \
        --engine-version ${PG_VERSION} \
        --master-username ${DB_USERNAME} \
        --master-user-password "${DB_PASSWORD}" \
        --allocated-storage 20 \
        --db-subnet-group-name ${DB_SUBNET_GROUP} \
        --vpc-security-group-ids ${SG_ID} \
        --backup-retention-period 0 \
        --publicly-accessible \
        --region ${AWS_REGION}

    aws rds wait db-instance-available \
        --db-instance-identifier ${DB_INSTANCE_ID} \
        --region ${AWS_REGION}

    POSTGRES_ENDPOINT=$(aws rds describe-db-instances \
        --db-instance-identifier ${DB_INSTANCE_ID} \
        --query 'DBInstances[0].Endpoint.Address' \
        --region ${AWS_REGION} \
        --output text)

    store_secret "lingulu/postgres-password" "${DB_PASSWORD}"

    echo -e "${GREEN}✓ PostgreSQL created${NC}"
    echo -e "${GREEN}  Endpoint: ${POSTGRES_ENDPOINT}${NC}"
    echo -e "${GREEN}  Username: ${DB_USERNAME}${NC}"
    echo -e "${GREEN}  Password saved to Secrets Manager${NC}"
fi
echo ""

# Create ElastiCache Redis
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Creating ElastiCache Redis (FREE TIER)${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

CACHE_CLUSTER_ID="${APP_NAME}-redis"

if aws elasticache describe-cache-clusters --cache-cluster-id ${CACHE_CLUSTER_ID} --region ${AWS_REGION} &> /dev/null; then
    echo -e "${GREEN}✓ Redis already exists${NC}"
    REDIS_ENDPOINT=$(aws elasticache describe-cache-clusters \
        --cache-cluster-id ${CACHE_CLUSTER_ID} \
        --show-cache-node-info \
        --query 'CacheClusters[0].CacheNodes[0].Endpoint.Address' \
        --region ${AWS_REGION} \
        --output text)
else
    # Create cache subnet group
    CACHE_SUBNET_GROUP="${CACHE_CLUSTER_ID}-subnet-group"

    if ! aws elasticache describe-cache-subnet-groups --cache-subnet-group-name ${CACHE_SUBNET_GROUP} --region ${AWS_REGION} &> /dev/null; then
        aws elasticache create-cache-subnet-group \
            --cache-subnet-group-name ${CACHE_SUBNET_GROUP} \
            --cache-subnet-group-description "Redis subnet group" \
            --subnet-ids ${SUBNET_IDS} \
            --region ${AWS_REGION}
    fi

    echo -e "${YELLOW}Creating Redis cluster (5-10 minutes)...${NC}"

    aws elasticache create-cache-cluster \
        --cache-cluster-id ${CACHE_CLUSTER_ID} \
        --cache-node-type cache.t3.micro \
        --engine redis \
        --num-cache-nodes 1 \
        --cache-subnet-group-name ${CACHE_SUBNET_GROUP} \
        --security-group-ids ${SG_ID} \
        --region ${AWS_REGION}

    aws elasticache wait cache-cluster-available \
        --cache-cluster-id ${CACHE_CLUSTER_ID} \
        --region ${AWS_REGION}

    REDIS_ENDPOINT=$(aws elasticache describe-cache-clusters \
        --cache-cluster-id ${CACHE_CLUSTER_ID} \
        --show-cache-node-info \
        --query 'CacheClusters[0].CacheNodes[0].Endpoint.Address' \
        --region ${AWS_REGION} \
        --output text)

    echo -e "${GREEN}✓ Redis created${NC}"
    echo -e "${GREEN}  Endpoint: ${REDIS_ENDPOINT}${NC}"
fi
echo ""

# Summary
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}FREE TIER Database Setup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${GREEN}PostgreSQL (RDS - FREE TIER):${NC}"
echo -e "  Host: ${POSTGRES_ENDPOINT}"
echo -e "  Port: 5432"
echo -e "  Username: ${DB_USERNAME}"
echo -e "  Database: postgres"
echo -e "  Cost: \$0/month (750 hours FREE)"
echo ""
echo -e "${GREEN}Redis (ElastiCache - FREE TIER):${NC}"
echo -e "  Host: ${REDIS_ENDPOINT}"
echo -e "  Port: 6379"
echo -e "  Cost: \$0/month (750 hours FREE)"
echo ""
echo -e "${YELLOW}MongoDB (Use MongoDB Atlas):${NC}"
echo -e "  ✓ Sign up: https://www.mongodb.com/cloud/atlas/register"
echo -e "  ✓ Create FREE cluster (512MB)"
echo -e "  ✓ Whitelist IP: 0.0.0.0/0"
echo -e "  ✓ Get connection string"
echo -e "  Cost: \$0/month (FREE FOREVER)"
echo ""
echo -e "${YELLOW}Add to GitHub Secrets:${NC}"
echo -e "  POSTGRESQL_DB_HOST=${POSTGRES_ENDPOINT}"
echo -e "  POSTGRESQL_DB_PORT=5432"
echo -e "  POSTGRESQL_DB_NAME=postgres"
echo -e "  POSTGRESQL_DB_USER=${DB_USERNAME}"
echo -e "  POSTGRESQL_DB_PASSWORD=<get from: aws secretsmanager get-secret-value --secret-id lingulu/postgres-password>"
echo ""
echo -e "  REDIS_DB_HOST=${REDIS_ENDPOINT}"
echo -e "  REDIS_DB_PORT=6379"
echo ""
echo -e "  MONGO_DB_HOST=<from MongoDB Atlas>"
echo -e "  MONGO_DB_USER=<from MongoDB Atlas>"
echo -e "  MONGO_DB_PASSWORD=<from MongoDB Atlas>"
echo ""
echo -e "${GREEN}Total Cost: \$0/month (100% FREE!)${NC}"
echo ""

