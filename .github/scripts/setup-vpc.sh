#!/bin/bash

# Setup Shared VPC for All Lingulu Services
# This VPC will be used by: Backend, API Gateway, Databases, and other services

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
AWS_REGION="ap-southeast-1"
PROJECT_NAME="lingulu"
VPC_CIDR="10.0.0.0/16"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Lingulu - Shared VPC Setup${NC}"
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

# Check if VPC already exists
echo -e "${YELLOW}Checking for existing VPC...${NC}"
EXISTING_VPC=$(aws ec2 describe-vpcs \
    --filters "Name=tag:Name,Values=${PROJECT_NAME}-vpc" \
    --query 'Vpcs[0].VpcId' \
    --region ${AWS_REGION} \
    --output text 2>/dev/null)

if [ -n "$EXISTING_VPC" ] && [ "$EXISTING_VPC" != "None" ]; then
    echo -e "${GREEN}✓ VPC already exists: ${EXISTING_VPC}${NC}"
    echo ""
    echo -e "${YELLOW}Use cleanup-all.sh to delete existing VPC first if you want to recreate it.${NC}"
    exit 0
fi

echo -e "${YELLOW}Creating VPC...${NC}"

# Create VPC
VPC_ID=$(aws ec2 create-vpc \
    --cidr-block ${VPC_CIDR} \
    --region ${AWS_REGION} \
    --tag-specifications "ResourceType=vpc,Tags=[{Key=Name,Value=${PROJECT_NAME}-vpc},{Key=Project,Value=${PROJECT_NAME}}]" \
    --query 'Vpc.VpcId' \
    --output text)

echo -e "${GREEN}✓ VPC created: ${VPC_ID}${NC}"

# Enable DNS
aws ec2 modify-vpc-attribute --vpc-id ${VPC_ID} --enable-dns-hostnames --region ${AWS_REGION}
aws ec2 modify-vpc-attribute --vpc-id ${VPC_ID} --enable-dns-support --region ${AWS_REGION}
echo -e "${GREEN}✓ DNS enabled${NC}"

# Create Internet Gateway
echo -e "${YELLOW}Creating Internet Gateway...${NC}"
IGW_ID=$(aws ec2 create-internet-gateway \
    --region ${AWS_REGION} \
    --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=${PROJECT_NAME}-igw},{Key=Project,Value=${PROJECT_NAME}}]" \
    --query 'InternetGateway.InternetGatewayId' \
    --output text)

aws ec2 attach-internet-gateway \
    --vpc-id ${VPC_ID} \
    --internet-gateway-id ${IGW_ID} \
    --region ${AWS_REGION}

echo -e "${GREEN}✓ Internet Gateway created and attached: ${IGW_ID}${NC}"

# Create Public Subnets (for NAT, Load Balancers, etc)
echo -e "${YELLOW}Creating public subnets...${NC}"

PUBLIC_SUBNET_1=$(aws ec2 create-subnet \
    --vpc-id ${VPC_ID} \
    --cidr-block 10.0.1.0/24 \
    --availability-zone ${AWS_REGION}a \
    --region ${AWS_REGION} \
    --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=${PROJECT_NAME}-public-1a},{Key=Project,Value=${PROJECT_NAME}},{Key=Type,Value=public}]" \
    --query 'Subnet.SubnetId' \
    --output text)

PUBLIC_SUBNET_2=$(aws ec2 create-subnet \
    --vpc-id ${VPC_ID} \
    --cidr-block 10.0.2.0/24 \
    --availability-zone ${AWS_REGION}b \
    --region ${AWS_REGION} \
    --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=${PROJECT_NAME}-public-2b},{Key=Project,Value=${PROJECT_NAME}},{Key=Type,Value=public}]" \
    --query 'Subnet.SubnetId' \
    --output text)

PUBLIC_SUBNET_3=$(aws ec2 create-subnet \
    --vpc-id ${VPC_ID} \
    --cidr-block 10.0.3.0/24 \
    --availability-zone ${AWS_REGION}c \
    --region ${AWS_REGION} \
    --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=${PROJECT_NAME}-public-3c},{Key=Project,Value=${PROJECT_NAME}},{Key=Type,Value=public}]" \
    --query 'Subnet.SubnetId' \
    --output text)

# Enable auto-assign public IP
aws ec2 modify-subnet-attribute --subnet-id ${PUBLIC_SUBNET_1} --map-public-ip-on-launch --region ${AWS_REGION}
aws ec2 modify-subnet-attribute --subnet-id ${PUBLIC_SUBNET_2} --map-public-ip-on-launch --region ${AWS_REGION}
aws ec2 modify-subnet-attribute --subnet-id ${PUBLIC_SUBNET_3} --map-public-ip-on-launch --region ${AWS_REGION}

echo -e "${GREEN}✓ Public subnets created${NC}"

# Skip private subnets and NAT Gateway for FREE TIER
echo -e "${YELLOW}Skipping NAT Gateway (not free tier - saves ~\$32/month)${NC}"
echo -e "${GREEN}✓ Using public subnets only (100% FREE)${NC}"

# Create Route Tables
echo -e "${YELLOW}Creating route tables...${NC}"

# Public route table
PUBLIC_RT=$(aws ec2 create-route-table \
    --vpc-id ${VPC_ID} \
    --region ${AWS_REGION} \
    --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=${PROJECT_NAME}-public-rt},{Key=Project,Value=${PROJECT_NAME}}]" \
    --query 'RouteTable.RouteTableId' \
    --output text)

# Add route to Internet Gateway
aws ec2 create-route \
    --route-table-id ${PUBLIC_RT} \
    --destination-cidr-block 0.0.0.0/0 \
    --gateway-id ${IGW_ID} \
    --region ${AWS_REGION}

# Associate public subnets
aws ec2 associate-route-table --subnet-id ${PUBLIC_SUBNET_1} --route-table-id ${PUBLIC_RT} --region ${AWS_REGION}
aws ec2 associate-route-table --subnet-id ${PUBLIC_SUBNET_2} --route-table-id ${PUBLIC_RT} --region ${AWS_REGION}
aws ec2 associate-route-table --subnet-id ${PUBLIC_SUBNET_3} --route-table-id ${PUBLIC_RT} --region ${AWS_REGION}

echo -e "${GREEN}✓ Public route table configured${NC}"


# Summary
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}FREE TIER VPC Setup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${GREEN}VPC Details:${NC}"
echo -e "  VPC ID: ${VPC_ID}"
echo -e "  CIDR: ${VPC_CIDR}"
echo -e "  Region: ${AWS_REGION}"
echo ""
echo -e "${GREEN}Public Subnets (Internet access via IGW):${NC}"
echo -e "  ${PUBLIC_SUBNET_1} (${AWS_REGION}a) - 10.0.1.0/24"
echo -e "  ${PUBLIC_SUBNET_2} (${AWS_REGION}b) - 10.0.2.0/24"
echo -e "  ${PUBLIC_SUBNET_3} (${AWS_REGION}c) - 10.0.3.0/24"
echo ""
echo -e "${GREEN}Networking:${NC}"
echo -e "  Internet Gateway: ${IGW_ID}"
echo ""
echo -e "${YELLOW}FREE TIER Configuration:${NC}"
echo -e "  ✓ No NAT Gateway (saves ~\$32/month)"
echo -e "  ✓ No private subnets (all resources in public subnets)"
echo -e "  ✓ Internet access via IGW (FREE)"
echo -e "  ✓ Perfect for development & learning"
echo ""
echo -e "${YELLOW}Next Steps:${NC}"
echo -e "  1. Run setup-databases-free.sh for FREE TIER databases"
echo -e "  2. Deploy ECS services to public subnets"
echo -e "  3. Use security groups to restrict access"
echo -e "  4. MongoDB: Use MongoDB Atlas Free Tier (512MB)"
echo ""
echo -e "${GREEN}Total VPC Cost: \$0/month (100% FREE!)${NC}"
echo ""

