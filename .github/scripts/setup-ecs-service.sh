#!/bin/bash

# Setup ECS Service for Lingulu Backend
# Run this AFTER: setup-vpc.sh, setup-databases-free.sh, setup-aws.sh

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
AWS_REGION="ap-southeast-1"
CLUSTER_NAME="lingulu-cluster"
SERVICE_NAME="lingulu-backend-service"
TASK_FAMILY="lingulu-backend-core"
DESIRED_COUNT=1

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}ECS Service Setup${NC}"
echo -e "${GREEN}========================================${NC}"
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

# Check if cluster exists
echo -e "${YELLOW}Checking ECS cluster...${NC}"
if ! aws ecs describe-clusters --clusters ${CLUSTER_NAME} --region ${AWS_REGION} --query 'clusters[0].status' --output text 2>/dev/null | grep -q "ACTIVE"; then
    echo -e "${RED}Error: Cluster ${CLUSTER_NAME} not found${NC}"
    echo -e "${YELLOW}Please run setup-aws.sh first${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Cluster exists: ${CLUSTER_NAME}${NC}"
echo ""

# Get VPC
echo -e "${YELLOW}Getting VPC...${NC}"
VPC_ID=$(aws ec2 describe-vpcs \
    --filters "Name=tag:Name,Values=lingulu-vpc" \
    --query 'Vpcs[0].VpcId' \
    --region ${AWS_REGION} \
    --output text 2>/dev/null)

if [ -z "$VPC_ID" ] || [ "$VPC_ID" = "None" ]; then
    echo -e "${RED}Error: VPC not found${NC}"
    echo -e "${YELLOW}Please run setup-vpc.sh first${NC}"
    exit 1
fi
echo -e "${GREEN}✓ VPC: ${VPC_ID}${NC}"
echo ""

# Get public subnets
echo -e "${YELLOW}Getting subnets...${NC}"
SUBNET_IDS=$(aws ec2 describe-subnets \
    --filters "Name=vpc-id,Values=${VPC_ID}" "Name=tag:Type,Values=public" \
    --query 'Subnets[*].SubnetId' \
    --region ${AWS_REGION} \
    --output text | tr '\t' ',')

if [ -z "$SUBNET_IDS" ]; then
    echo -e "${RED}Error: No public subnets found${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Subnets: ${SUBNET_IDS}${NC}"
echo ""

# Create security group for ECS service
echo -e "${YELLOW}Creating security group...${NC}"
SG_NAME="lingulu-backend-sg"

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
        --description "Lingulu backend service security group" \
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

    # Allow port 8080 from VPC
    aws ec2 authorize-security-group-ingress \
        --group-id ${SG_ID} \
        --protocol tcp \
        --port 8080 \
        --cidr ${VPC_CIDR} \
        --region ${AWS_REGION} 2>/dev/null || true

    # Allow port 8080 from anywhere (for testing - restrict in production!)
    aws ec2 authorize-security-group-ingress \
        --group-id ${SG_ID} \
        --protocol tcp \
        --port 8080 \
        --cidr 0.0.0.0/0 \
        --region ${AWS_REGION} 2>/dev/null || true

    echo -e "${GREEN}✓ Security group created: ${SG_ID}${NC}"
fi
echo ""

# Register task definition
echo -e "${YELLOW}Registering task definition...${NC}"

# Copy template
cp ../.github/ecs/task-definition.json task-definition-temp.json

# Replace placeholder
sed -i "s/{ACCOUNT_ID}/${ACCOUNT_ID}/g" task-definition-temp.json

# Register
aws ecs register-task-definition \
    --cli-input-json file://task-definition-temp.json \
    --region ${AWS_REGION} > /dev/null

rm task-definition-temp.json

echo -e "${GREEN}✓ Task definition registered: ${TASK_FAMILY}${NC}"
echo ""

# Check if service already exists
echo -e "${YELLOW}Checking if service exists...${NC}"
if aws ecs describe-services \
    --cluster ${CLUSTER_NAME} \
    --services ${SERVICE_NAME} \
    --region ${AWS_REGION} \
    --query 'services[0].status' \
    --output text 2>/dev/null | grep -q "ACTIVE"; then

    echo -e "${GREEN}✓ Service already exists: ${SERVICE_NAME}${NC}"
    echo -e "${YELLOW}Updating service with new task definition...${NC}"

    aws ecs update-service \
        --cluster ${CLUSTER_NAME} \
        --service ${SERVICE_NAME} \
        --task-definition ${TASK_FAMILY} \
        --desired-count ${DESIRED_COUNT} \
        --force-new-deployment \
        --region ${AWS_REGION} > /dev/null

    echo -e "${GREEN}✓ Service updated${NC}"
else
    echo -e "${YELLOW}Creating ECS service...${NC}"

    aws ecs create-service \
        --cluster ${CLUSTER_NAME} \
        --service-name ${SERVICE_NAME} \
        --task-definition ${TASK_FAMILY} \
        --desired-count ${DESIRED_COUNT} \
        --launch-type FARGATE \
        --network-configuration "awsvpcConfiguration={subnets=[${SUBNET_IDS}],securityGroups=[${SG_ID}],assignPublicIp=ENABLED}" \
        --region ${AWS_REGION} > /dev/null

    echo -e "${GREEN}✓ Service created: ${SERVICE_NAME}${NC}"
fi

echo ""

# Wait for service to stabilize
echo -e "${YELLOW}Waiting for service to stabilize (this may take 2-3 minutes)...${NC}"
aws ecs wait services-stable \
    --cluster ${CLUSTER_NAME} \
    --services ${SERVICE_NAME} \
    --region ${AWS_REGION}

echo -e "${GREEN}✓ Service is stable${NC}"
echo ""

# Get task public IP
echo -e "${YELLOW}Getting task public IP...${NC}"
TASK_ARN=$(aws ecs list-tasks \
    --cluster ${CLUSTER_NAME} \
    --service-name ${SERVICE_NAME} \
    --query 'taskArns[0]' \
    --region ${AWS_REGION} \
    --output text)

if [ -n "$TASK_ARN" ] && [ "$TASK_ARN" != "None" ]; then
    ENI_ID=$(aws ecs describe-tasks \
        --cluster ${CLUSTER_NAME} \
        --tasks ${TASK_ARN} \
        --query 'tasks[0].attachments[0].details[?name==`networkInterfaceId`].value' \
        --region ${AWS_REGION} \
        --output text)

    PUBLIC_IP=$(aws ec2 describe-network-interfaces \
        --network-interface-ids ${ENI_ID} \
        --query 'NetworkInterfaces[0].Association.PublicIp' \
        --region ${AWS_REGION} \
        --output text)

    echo -e "${GREEN}✓ Task Public IP: ${PUBLIC_IP}${NC}"
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}ECS Service Setup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${GREEN}Service Details:${NC}"
echo -e "  Cluster: ${CLUSTER_NAME}"
echo -e "  Service: ${SERVICE_NAME}"
echo -e "  Task Definition: ${TASK_FAMILY}"
echo -e "  Desired Count: ${DESIRED_COUNT}"
echo -e "  VPC: ${VPC_ID}"
echo -e "  Security Group: ${SG_ID}"
echo ""
if [ -n "$PUBLIC_IP" ] && [ "$PUBLIC_IP" != "None" ]; then
    echo -e "${GREEN}Access your application:${NC}"
    echo -e "  http://${PUBLIC_IP}:8080"
    echo -e "  http://${PUBLIC_IP}:8080/actuator/health"
    echo ""
fi
echo -e "${YELLOW}Next Steps:${NC}"
echo -e "  1. Test the application via public IP"
echo -e "  2. Setup Application Load Balancer (optional)"
echo -e "  3. Configure domain name (when ready)"
echo -e "  4. Run CI/CD pipeline: git push origin main"
echo ""

