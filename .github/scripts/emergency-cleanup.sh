#!/bin/bash

# Emergency Stop & Cleanup Stuck ECS Deployment
# Use this when deployment is stuck/hanging

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

echo -e "${RED}========================================${NC}"
echo -e "${RED}Emergency Deployment Cleanup${NC}"
echo -e "${RED}========================================${NC}"
echo ""
echo -e "${YELLOW}This will:${NC}"
echo -e "  1. Stop all running tasks"
echo -e "  2. Scale service to 0"
echo -e "  3. Wait for cleanup"
echo -e "  4. Register new task definition with fixes"
echo -e "  5. Scale service back to 1"
echo ""
read -p "Continue? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo -e "${GREEN}Cancelled${NC}"
    exit 0
fi

echo ""
echo -e "${YELLOW}Step 1: Scaling service to 0...${NC}"
aws ecs update-service \
    --cluster ${CLUSTER_NAME} \
    --service ${SERVICE_NAME} \
    --desired-count 0 \
    --region ${AWS_REGION} > /dev/null

echo -e "${GREEN}✓ Service scaled to 0${NC}"
echo ""

echo -e "${YELLOW}Step 2: Waiting for all tasks to stop...${NC}"
sleep 10

# Force stop any remaining tasks
TASK_ARNS=$(aws ecs list-tasks \
    --cluster ${CLUSTER_NAME} \
    --service-name ${SERVICE_NAME} \
    --region ${AWS_REGION} \
    --query 'taskArns[]' \
    --output text)

if [ -n "$TASK_ARNS" ]; then
    echo -e "${YELLOW}Stopping running tasks...${NC}"
    for TASK_ARN in $TASK_ARNS; do
        aws ecs stop-task \
            --cluster ${CLUSTER_NAME} \
            --task $TASK_ARN \
            --reason "Emergency cleanup" \
            --region ${AWS_REGION} > /dev/null
        echo -e "  Stopped: ${TASK_ARN##*/}"
    done
fi

echo -e "${GREEN}✓ All tasks stopped${NC}"
echo ""

echo -e "${YELLOW}Step 3: Registering new task definition with fixes...${NC}"

# Get account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# Copy and update task definition
cp ../../.github/ecs/task-definition.json task-definition-temp.json
sed -i "s/{ACCOUNT_ID}/${ACCOUNT_ID}/g" task-definition-temp.json

# Register new task definition
NEW_REVISION=$(aws ecs register-task-definition \
    --cli-input-json file://task-definition-temp.json \
    --region ${AWS_REGION} \
    --query 'taskDefinition.revision' \
    --output text)

rm task-definition-temp.json

echo -e "${GREEN}✓ New task definition registered: lingulu-backend-core:${NEW_REVISION}${NC}"
echo -e "${GREEN}  CPU: 1024 (2x increase)${NC}"
echo -e "${GREEN}  Memory: 2048 MB (2x increase)${NC}"
echo -e "${GREEN}  Health check: 180s start period${NC}"
echo ""

echo -e "${YELLOW}Step 4: Updating service with new task definition...${NC}"
aws ecs update-service \
    --cluster ${CLUSTER_NAME} \
    --service ${SERVICE_NAME} \
    --task-definition lingulu-backend-core:${NEW_REVISION} \
    --desired-count 0 \
    --force-new-deployment \
    --region ${AWS_REGION} > /dev/null

echo -e "${GREEN}✓ Service updated${NC}"
echo ""

echo -e "${YELLOW}Waiting 15 seconds for cleanup...${NC}"
sleep 15

echo ""
echo -e "${YELLOW}Step 5: Scaling service back to 1...${NC}"
aws ecs update-service \
    --cluster ${CLUSTER_NAME} \
    --service ${SERVICE_NAME} \
    --desired-count 1 \
    --region ${AWS_REGION} > /dev/null

echo -e "${GREEN}✓ Service scaled to 1${NC}"
echo ""

echo -e "${YELLOW}Monitoring deployment...${NC}"
echo -e "${YELLOW}(Will wait up to 10 minutes for service to stabilize)${NC}"
echo ""

# Monitor deployment
START_TIME=$(date +%s)
TIMEOUT=600  # 10 minutes

while true; do
    CURRENT_TIME=$(date +%s)
    ELAPSED=$((CURRENT_TIME - START_TIME))

    if [ $ELAPSED -gt $TIMEOUT ]; then
        echo -e "${RED}Timeout reached (10 minutes)${NC}"
        break
    fi

    SERVICE_STATUS=$(aws ecs describe-services \
        --cluster ${CLUSTER_NAME} \
        --services ${SERVICE_NAME} \
        --region ${AWS_REGION} \
        --query 'services[0]')

    RUNNING=$(echo $SERVICE_STATUS | jq -r '.runningCount')
    DESIRED=$(echo $SERVICE_STATUS | jq -r '.desiredCount')
    PENDING=$(echo $SERVICE_STATUS | jq -r '.pendingCount')

    # Get latest event
    LATEST_EVENT=$(echo $SERVICE_STATUS | jq -r '.events[0].message')

    echo -e "  Running: ${RUNNING}/${DESIRED}, Pending: ${PENDING}"
    echo -e "  ${LATEST_EVENT}"
    echo ""

    if [ "$RUNNING" == "$DESIRED" ] && [ "$DESIRED" == "1" ] && [ "$PENDING" == "0" ]; then
        echo -e "${GREEN}✓ Service is stable!${NC}"
        break
    fi

    sleep 15
done

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Cleanup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Get task IP
TASK_ARN=$(aws ecs list-tasks \
    --cluster ${CLUSTER_NAME} \
    --service-name ${SERVICE_NAME} \
    --region ${AWS_REGION} \
    --query 'taskArns[0]' \
    --output text)

if [ -n "$TASK_ARN" ] && [ "$TASK_ARN" != "None" ]; then
    echo -e "${YELLOW}Getting task details...${NC}"

    TASK_DETAILS=$(aws ecs describe-tasks \
        --cluster ${CLUSTER_NAME} \
        --tasks $TASK_ARN \
        --region ${AWS_REGION} \
        --query 'tasks[0]')

    TASK_STATUS=$(echo $TASK_DETAILS | jq -r '.lastStatus')
    HEALTH_STATUS=$(echo $TASK_DETAILS | jq -r '.healthStatus // "UNKNOWN"')

    echo -e "${GREEN}Task Status: ${TASK_STATUS}${NC}"
    echo -e "${GREEN}Health Status: ${HEALTH_STATUS}${NC}"

    # Try to get public IP
    ENI_ID=$(echo $TASK_DETAILS | jq -r '.attachments[0].details[] | select(.name=="networkInterfaceId") | .value')

    if [ -n "$ENI_ID" ]; then
        PUBLIC_IP=$(aws ec2 describe-network-interfaces \
            --network-interface-ids ${ENI_ID} \
            --query 'NetworkInterfaces[0].Association.PublicIp' \
            --region ${AWS_REGION} \
            --output text 2>/dev/null)

        if [ -n "$PUBLIC_IP" ] && [ "$PUBLIC_IP" != "None" ]; then
            echo -e "${GREEN}Public IP: ${PUBLIC_IP}${NC}"
            echo ""
            echo -e "${GREEN}Test application:${NC}"
            echo -e "  curl http://${PUBLIC_IP}:8080/actuator/health"
            echo ""
        fi
    fi
fi

echo -e "${YELLOW}View logs:${NC}"
echo -e "  aws logs tail //ecs/lingulu-backend-core --follow"
echo ""
echo -e "${YELLOW}Monitor in console:${NC}"
echo -e "  https://console.aws.amazon.com/ecs/home?region=${AWS_REGION}#/clusters/${CLUSTER_NAME}/services/${SERVICE_NAME}"
echo ""

