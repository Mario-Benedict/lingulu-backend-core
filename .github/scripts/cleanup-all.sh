#!/bin/bash

# Cleanup Script - Delete ALL AWS Resources
# WARNING: This will delete ALL resources created by setup scripts!

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
AWS_REGION="ap-southeast-1"
APP_NAME="lingulu-backend-core"

echo -e "${RED}========================================${NC}"
echo -e "${RED}AWS Resources Cleanup Script${NC}"
echo -e "${RED}========================================${NC}"
echo ""
echo -e "${YELLOW}WARNING: This will DELETE ALL resources!${NC}"
echo -e "${YELLOW}Including:${NC}"
echo -e "  - RDS PostgreSQL"
echo -e "  - ElastiCache Redis"
echo -e "  - DocumentDB (MongoDB)"
echo -e "  - ECS Cluster & Services"
echo -e "  - ECR Repository & Images"
echo -e "  - VPC & Networking"
echo -e "  - Security Groups"
echo -e "  - CloudWatch Logs"
echo -e "  - Secrets Manager Secrets"
echo ""
read -p "Are you sure you want to continue? (type 'yes' to confirm): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo -e "${GREEN}Cleanup cancelled.${NC}"
    exit 0
fi

echo ""
echo -e "${RED}Starting cleanup...${NC}"
echo ""

# Check AWS CLI
if ! command -v aws &> /dev/null; then
    echo -e "${RED}Error: AWS CLI is not installed${NC}"
    exit 1
fi

if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}Error: AWS credentials not configured${NC}"
    exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo -e "${GREEN}AWS Account: ${ACCOUNT_ID}${NC}"
echo ""

# Function to delete ECS resources
delete_ecs_resources() {
    echo -e "${YELLOW}Deleting ECS resources...${NC}"

    local cluster_name="${APP_NAME}-cluster"
    local service_name="${APP_NAME}-service"

    # Check if cluster exists
    if aws ecs describe-clusters --clusters ${cluster_name} --region ${AWS_REGION} --query 'clusters[0].status' --output text 2>/dev/null | grep -q "ACTIVE"; then

        # Scale service to 0
        if aws ecs describe-services --cluster ${cluster_name} --services ${service_name} --region ${AWS_REGION} &> /dev/null; then
            echo -e "  Scaling service to 0..."
            aws ecs update-service \
                --cluster ${cluster_name} \
                --service ${service_name} \
                --desired-count 0 \
                --region ${AWS_REGION} &> /dev/null || true

            sleep 5

            # Delete service
            echo -e "  Deleting service..."
            aws ecs delete-service \
                --cluster ${cluster_name} \
                --service ${service_name} \
                --force \
                --region ${AWS_REGION} &> /dev/null || true
        fi

        # Delete cluster
        echo -e "  Deleting cluster..."
        aws ecs delete-cluster \
            --cluster ${cluster_name} \
            --region ${AWS_REGION} &> /dev/null || true

        echo -e "${GREEN}✓ ECS resources deleted${NC}"
    else
        echo -e "${GREEN}✓ No ECS cluster found${NC}"
    fi
    echo ""
}

# Function to delete ECR repository
delete_ecr_repository() {
    echo -e "${YELLOW}Deleting ECR repository...${NC}"

    local repo_name="${APP_NAME}"

    if aws ecr describe-repositories --repository-names ${repo_name} --region ${AWS_REGION} &> /dev/null; then
        aws ecr delete-repository \
            --repository-name ${repo_name} \
            --force \
            --region ${AWS_REGION} &> /dev/null
        echo -e "${GREEN}✓ ECR repository deleted${NC}"
    else
        echo -e "${GREEN}✓ No ECR repository found${NC}"
    fi
    echo ""
}

# Function to delete RDS
delete_rds() {
    echo -e "${YELLOW}Deleting RDS PostgreSQL...${NC}"

    local db_instance_id="${APP_NAME}-postgres"

    if aws rds describe-db-instances --db-instance-identifier ${db_instance_id} --region ${AWS_REGION} &> /dev/null; then
        echo -e "  Deleting DB instance (this may take 5-10 minutes)..."
        aws rds delete-db-instance \
            --db-instance-identifier ${db_instance_id} \
            --skip-final-snapshot \
            --delete-automated-backups \
            --region ${AWS_REGION} &> /dev/null

        echo -e "  Waiting for deletion..."
        aws rds wait db-instance-deleted \
            --db-instance-identifier ${db_instance_id} \
            --region ${AWS_REGION} || true

        echo -e "${GREEN}✓ RDS deleted${NC}"
    else
        echo -e "${GREEN}✓ No RDS instance found${NC}"
    fi
    echo ""
}

# Function to delete ElastiCache Redis
delete_elasticache() {
    echo -e "${YELLOW}Deleting ElastiCache Redis...${NC}"

    local cache_cluster_id="${APP_NAME}-redis"

    if aws elasticache describe-cache-clusters --cache-cluster-id ${cache_cluster_id} --region ${AWS_REGION} &> /dev/null; then
        echo -e "  Deleting cache cluster (this may take 5-10 minutes)..."
        aws elasticache delete-cache-cluster \
            --cache-cluster-id ${cache_cluster_id} \
            --region ${AWS_REGION} &> /dev/null

        echo -e "  Waiting for deletion..."
        sleep 30  # ElastiCache doesn't have a wait command

        echo -e "${GREEN}✓ ElastiCache deleted${NC}"
    else
        echo -e "${GREEN}✓ No ElastiCache cluster found${NC}"
    fi

    # Delete subnet group
    local subnet_group_name="${cache_cluster_id}-subnet-group"
    if aws elasticache describe-cache-subnet-groups --cache-subnet-group-name ${subnet_group_name} --region ${AWS_REGION} &> /dev/null; then
        aws elasticache delete-cache-subnet-group \
            --cache-subnet-group-name ${subnet_group_name} \
            --region ${AWS_REGION} &> /dev/null || true
    fi

    echo ""
}

# Function to delete DocumentDB
delete_documentdb() {
    echo -e "${YELLOW}Deleting DocumentDB...${NC}"

    local cluster_id="${APP_NAME}-mongo"
    local instance_id="${cluster_id}-instance"

    # Delete instance first
    if aws docdb describe-db-instances --db-instance-identifier ${instance_id} --region ${AWS_REGION} &> /dev/null; then
        echo -e "  Deleting DB instance..."
        aws docdb delete-db-instance \
            --db-instance-identifier ${instance_id} \
            --region ${AWS_REGION} &> /dev/null

        echo -e "  Waiting for instance deletion..."
        sleep 60
    fi

    # Delete cluster
    if aws docdb describe-db-clusters --db-cluster-identifier ${cluster_id} --region ${AWS_REGION} &> /dev/null; then
        echo -e "  Deleting DB cluster (this may take 5-10 minutes)..."
        aws docdb delete-db-cluster \
            --db-cluster-identifier ${cluster_id} \
            --skip-final-snapshot \
            --region ${AWS_REGION} &> /dev/null

        echo -e "  Waiting for deletion..."
        sleep 120

        echo -e "${GREEN}✓ DocumentDB deleted${NC}"
    else
        echo -e "${GREEN}✓ No DocumentDB cluster found${NC}"
    fi
    echo ""
}

# Function to delete DB subnet group
delete_db_subnet_group() {
    echo -e "${YELLOW}Deleting DB subnet group...${NC}"

    local subnet_group_name="${APP_NAME}-db-subnet-group"

    if aws rds describe-db-subnet-groups --db-subnet-group-name ${subnet_group_name} --region ${AWS_REGION} &> /dev/null; then
        aws rds delete-db-subnet-group \
            --db-subnet-group-name ${subnet_group_name} \
            --region ${AWS_REGION} &> /dev/null
        echo -e "${GREEN}✓ DB subnet group deleted${NC}"
    else
        echo -e "${GREEN}✓ No DB subnet group found${NC}"
    fi
    echo ""
}

# Function to delete security groups
delete_security_groups() {
    echo -e "${YELLOW}Deleting security groups...${NC}"

    # Get VPC
    VPC_ID=$(aws ec2 describe-vpcs \
        --filters "Name=tag:Name,Values=${APP_NAME}-vpc" \
        --query 'Vpcs[0].VpcId' \
        --region ${AWS_REGION} \
        --output text 2>/dev/null)

    if [ -z "$VPC_ID" ] || [ "$VPC_ID" = "None" ]; then
        VPC_ID=$(aws ec2 describe-vpcs \
            --filters "Name=isDefault,Values=true" \
            --query 'Vpcs[0].VpcId' \
            --region ${AWS_REGION} \
            --output text 2>/dev/null)
    fi

    if [ -n "$VPC_ID" ] && [ "$VPC_ID" != "None" ]; then
        # Delete app security group
        SG_ID=$(aws ec2 describe-security-groups \
            --filters "Name=group-name,Values=${APP_NAME}-sg" "Name=vpc-id,Values=${VPC_ID}" \
            --query 'SecurityGroups[0].GroupId' \
            --region ${AWS_REGION} \
            --output text 2>/dev/null)

        if [ -n "$SG_ID" ] && [ "$SG_ID" != "None" ]; then
            aws ec2 delete-security-group \
                --group-id ${SG_ID} \
                --region ${AWS_REGION} &> /dev/null || true
            echo -e "  Deleted ${APP_NAME}-sg"
        fi

        # Delete database security group
        DB_SG_ID=$(aws ec2 describe-security-groups \
            --filters "Name=group-name,Values=${APP_NAME}-db-sg" "Name=vpc-id,Values=${VPC_ID}" \
            --query 'SecurityGroups[0].GroupId' \
            --region ${AWS_REGION} \
            --output text 2>/dev/null)

        if [ -n "$DB_SG_ID" ] && [ "$DB_SG_ID" != "None" ]; then
            aws ec2 delete-security-group \
                --group-id ${DB_SG_ID} \
                --region ${AWS_REGION} &> /dev/null || true
            echo -e "  Deleted ${APP_NAME}-db-sg"
        fi

        echo -e "${GREEN}✓ Security groups deleted${NC}"
    else
        echo -e "${GREEN}✓ No security groups found${NC}"
    fi
    echo ""
}

# Function to delete VPC
delete_vpc() {
    echo -e "${YELLOW}Deleting VPC...${NC}"

    VPC_ID=$(aws ec2 describe-vpcs \
        --filters "Name=tag:Name,Values=${APP_NAME}-vpc" \
        --query 'Vpcs[0].VpcId' \
        --region ${AWS_REGION} \
        --output text 2>/dev/null)

    if [ -z "$VPC_ID" ] || [ "$VPC_ID" = "None" ]; then
        echo -e "${GREEN}✓ No custom VPC found${NC}"
        echo ""
        return
    fi

    # Delete subnets
    echo -e "  Deleting subnets..."
    aws ec2 describe-subnets \
        --filters "Name=vpc-id,Values=${VPC_ID}" \
        --query 'Subnets[*].SubnetId' \
        --region ${AWS_REGION} \
        --output text 2>/dev/null | tr '\t' '\n' | while read subnet; do
        [ -n "$subnet" ] && aws ec2 delete-subnet --subnet-id ${subnet} --region ${AWS_REGION} &> /dev/null || true
    done

    # Detach and delete internet gateway
    echo -e "  Deleting internet gateway..."
    IGW_ID=$(aws ec2 describe-internet-gateways \
        --filters "Name=attachment.vpc-id,Values=${VPC_ID}" \
        --query 'InternetGateways[0].InternetGatewayId' \
        --region ${AWS_REGION} \
        --output text 2>/dev/null)

    if [ -n "$IGW_ID" ] && [ "$IGW_ID" != "None" ]; then
        aws ec2 detach-internet-gateway \
            --internet-gateway-id ${IGW_ID} \
            --vpc-id ${VPC_ID} \
            --region ${AWS_REGION} &> /dev/null || true
        aws ec2 delete-internet-gateway \
            --internet-gateway-id ${IGW_ID} \
            --region ${AWS_REGION} &> /dev/null || true
    fi

    # Delete VPC
    echo -e "  Deleting VPC..."
    aws ec2 delete-vpc \
        --vpc-id ${VPC_ID} \
        --region ${AWS_REGION} &> /dev/null || true

    echo -e "${GREEN}✓ VPC deleted${NC}"
    echo ""
}

# Function to delete CloudWatch logs
delete_cloudwatch_logs() {
    echo -e "${YELLOW}Deleting CloudWatch log groups...${NC}"

    local log_group="//ecs/${APP_NAME}"

    if aws logs describe-log-groups --log-group-name-prefix ${log_group} --region ${AWS_REGION} &> /dev/null; then
        aws logs delete-log-group \
            --log-group-name ${log_group} \
            --region ${AWS_REGION} &> /dev/null || true
        echo -e "${GREEN}✓ CloudWatch logs deleted${NC}"
    else
        echo -e "${GREEN}✓ No log groups found${NC}"
    fi
    echo ""
}

# Function to delete IAM roles
delete_iam_roles() {
    echo -e "${YELLOW}Deleting IAM roles...${NC}"

    # ecsTaskExecutionRole
    if aws iam get-role --role-name ecsTaskExecutionRole &> /dev/null; then
        # Detach policies
        aws iam detach-role-policy \
            --role-name ecsTaskExecutionRole \
            --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy &> /dev/null || true

        # Delete role
        aws iam delete-role --role-name ecsTaskExecutionRole &> /dev/null || true
        echo -e "  Deleted ecsTaskExecutionRole"
    fi

    # ecsTaskRole
    if aws iam get-role --role-name ecsTaskRole &> /dev/null; then
        aws iam delete-role --role-name ecsTaskRole &> /dev/null || true
        echo -e "  Deleted ecsTaskRole"
    fi

    echo -e "${GREEN}✓ IAM roles deleted${NC}"
    echo ""
}

# Function to delete Secrets Manager secrets
delete_secrets() {
    echo -e "${YELLOW}Deleting Secrets Manager secrets...${NC}"

    # List and delete secrets
    for secret in "lingulu/postgres-password" "lingulu/mongo-password"; do
        if aws secretsmanager describe-secret --secret-id ${secret} --region ${AWS_REGION} &> /dev/null; then
            aws secretsmanager delete-secret \
                --secret-id ${secret} \
                --force-delete-without-recovery \
                --region ${AWS_REGION} &> /dev/null || true
            echo -e "  Deleted ${secret}"
        fi
    done

    echo -e "${GREEN}✓ Secrets deleted${NC}"
    echo ""
}

# Main execution
echo -e "${RED}========================================${NC}"
echo -e "${RED}Starting Resource Deletion${NC}"
echo -e "${RED}========================================${NC}"
echo ""

# Delete in correct order (dependencies first)
delete_ecs_resources
delete_rds
delete_elasticache
delete_documentdb
delete_db_subnet_group
delete_security_groups
delete_vpc
delete_ecr_repository
delete_cloudwatch_logs
delete_iam_roles
delete_secrets

# Summary
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Cleanup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${GREEN}All resources have been deleted:${NC}"
echo -e "  ✓ ECS Cluster & Services"
echo -e "  ✓ ECR Repository"
echo -e "  ✓ RDS PostgreSQL"
echo -e "  ✓ ElastiCache Redis"
echo -e "  ✓ DocumentDB (MongoDB)"
echo -e "  ✓ VPC & Networking"
echo -e "  ✓ Security Groups"
echo -e "  ✓ CloudWatch Logs"
echo -e "  ✓ IAM Roles"
echo -e "  ✓ Secrets Manager"
echo ""
echo -e "${YELLOW}Note: Some resources may take a few minutes to fully delete.${NC}"
echo -e "${YELLOW}Check AWS Console to confirm all deletions.${NC}"
echo ""

