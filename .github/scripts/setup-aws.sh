#!/bin/bash

# Setup AWS Resources for Lingulu Backend CI/CD
# This script creates necessary AWS resources for ECS deployment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
AWS_REGION="ap-southeast-1"
ECR_REPO_NAME="lingulu-backend-core"
APP_NAME="lingulu-backend-core"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Lingulu Backend - AWS Setup Script${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}Error: AWS CLI is not installed${NC}"
    echo "Please install AWS CLI first: https://aws.amazon.com/cli/"
    exit 1
fi

# Check if AWS credentials are configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}Error: AWS credentials not configured${NC}"
    echo "Please run: aws configure"
    exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo -e "${GREEN}✓ AWS Account ID: ${ACCOUNT_ID}${NC}"
echo -e "${GREEN}✓ Region: ${AWS_REGION}${NC}"
echo ""

# Function to create ECR repository
create_ecr_repo() {
    echo -e "${YELLOW}Creating ECR repository...${NC}"

    if aws ecr describe-repositories --repository-names ${ECR_REPO_NAME} --region ${AWS_REGION} &> /dev/null; then
        echo -e "${GREEN}✓ ECR repository already exists${NC}"
    else
        aws ecr create-repository \
            --repository-name ${ECR_REPO_NAME} \
            --region ${AWS_REGION} \
            --image-scanning-configuration scanOnPush=true \
            --encryption-configuration encryptionType=AES256

        echo -e "${GREEN}✓ ECR repository created${NC}"
    fi

    ECR_URI="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}"
    echo -e "${GREEN}  URI: ${ECR_URI}${NC}"
    echo ""
}

# Function to create ECS cluster
create_ecs_cluster() {
    local cluster_name="${APP_NAME}-cluster"

    echo -e "${YELLOW}Creating ECS cluster...${NC}"

    if aws ecs describe-clusters --clusters ${cluster_name} --region ${AWS_REGION} --query 'clusters[0].status' --output text 2>/dev/null | grep -q "ACTIVE"; then
        echo -e "${GREEN}✓ Cluster ${cluster_name} already exists${NC}"
    else
        aws ecs create-cluster \
            --cluster-name ${cluster_name} \
            --region ${AWS_REGION} \
            --capacity-providers FARGATE FARGATE_SPOT \
            --default-capacity-provider-strategy \
                capacityProvider=FARGATE,weight=1 \
                capacityProvider=FARGATE_SPOT,weight=4

        echo -e "${GREEN}✓ Cluster ${cluster_name} created${NC}"
    fi
    echo ""
}

# Function to create CloudWatch log group
create_log_group() {
    # Use double slash to prevent Git Bash path conversion on Windows
    local log_group="//ecs/${APP_NAME}"

    echo -e "${YELLOW}Creating CloudWatch log group...${NC}"

    if aws logs describe-log-groups --log-group-name-prefix ${log_group} --region ${AWS_REGION} --query 'logGroups[0].logGroupName' --output text 2>/dev/null | grep -q "${log_group}"; then
        echo -e "${GREEN}✓ Log group ${log_group} already exists${NC}"
    else
        aws logs create-log-group \
            --log-group-name ${log_group} \
            --region ${AWS_REGION}

        aws logs put-retention-policy \
            --log-group-name ${log_group} \
            --retention-in-days 30 \
            --region ${AWS_REGION}

        echo -e "${GREEN}✓ Log group ${log_group} created (30 days retention)${NC}"
    fi
    echo ""
}

# Function to create IAM roles
create_iam_roles() {
    echo -e "${YELLOW}Creating IAM roles...${NC}"

    # Trust policy for ECS tasks
    TRUST_POLICY='{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ecs-tasks.amazonaws.com"},"Action":"sts:AssumeRole"}]}'

    # ECS Task Execution Role
    if aws iam get-role --role-name ecsTaskExecutionRole &> /dev/null; then
        echo -e "${GREEN}✓ ecsTaskExecutionRole already exists${NC}"
    else
        aws iam create-role \
            --role-name ecsTaskExecutionRole \
            --assume-role-policy-document "${TRUST_POLICY}"

        aws iam attach-role-policy \
            --role-name ecsTaskExecutionRole \
            --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy

        echo -e "${GREEN}✓ ecsTaskExecutionRole created${NC}"
    fi

    # ECS Task Role
    if aws iam get-role --role-name ecsTaskRole &> /dev/null; then
        echo -e "${GREEN}✓ ecsTaskRole already exists${NC}"
    else
        aws iam create-role \
            --role-name ecsTaskRole \
            --assume-role-policy-document "${TRUST_POLICY}"

        echo -e "${GREEN}✓ ecsTaskRole created${NC}"
    fi

    echo ""
}

# Function to create VPC and subnets (simplified - adjust as needed)
create_network() {
    echo -e "${YELLOW}Checking network configuration...${NC}"
    echo -e "${GREEN}✓ Using default VPC and subnets${NC}"
    echo -e "${YELLOW}  Note: For production, consider creating dedicated VPC${NC}"
    echo ""
}

# Main execution
echo -e "${GREEN}Starting setup...${NC}"
echo ""

# Create ECR repository
create_ecr_repo

# Create IAM roles
create_iam_roles

# Create ECS cluster
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Setting up production environment${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

create_ecs_cluster
create_log_group

# Summary
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Setup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${GREEN}Summary:${NC}"
echo -e "  ECR Repository: ${ECR_URI}"
echo -e "  ECS Cluster: ${APP_NAME}-cluster"
echo -e "  CloudWatch Log Group: //ecs/${APP_NAME}"
echo -e "  IAM Roles:"
echo -e "    - ecsTaskExecutionRole"
echo -e "    - ecsTaskRole"
echo ""
echo -e "${YELLOW}Next Steps:${NC}"
echo -e "  1. Create ECS Task Definition"
echo -e "  2. Create ECS Service"
echo -e "  3. Set up Application Load Balancer (if needed)"
echo -e "  4. Configure GitHub Secrets:"
echo -e "     - AWS_ACCESS_KEY_ID"
echo -e "     - AWS_SECRET_ACCESS_KEY"
echo -e "  5. Update workflow files with your resource names"
echo -e "  6. Create Secrets Manager secrets for DB and JWT"
echo ""
echo -e "${GREEN}For task definition examples, check:${NC}"
echo -e "  .github/ecs/task-definition.json"
echo ""





