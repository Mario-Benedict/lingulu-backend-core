# 🔧 VPC Auto-Creation Fix

## Problem
Error: `No default VPC found` saat menjalankan `setup-databases.sh`

## Solution
Script sekarang **otomatis membuat VPC** jika tidak ada default VPC.

## What Gets Created

Jika tidak ada default VPC, script akan create:

### VPC
- **CIDR:** 10.0.0.0/16
- **DNS Hostname:** Enabled
- **DNS Support:** Enabled

### Subnets (3 subnets di 3 AZs)
- **Subnet 1:** 10.0.1.0/24 (ap-southeast-1a)
- **Subnet 2:** 10.0.2.0/24 (ap-southeast-1b)
- **Subnet 3:** 10.0.3.0/24 (ap-southeast-1c)
- **Public IP:** Auto-assign enabled

### Networking
- **Internet Gateway:** Created & attached
- **Route Table:** 0.0.0.0/0 → Internet Gateway

## Usage

Sama seperti sebelumnya, tidak ada perubahan:

```bash
cd .github/scripts
chmod +x setup-databases.sh
./setup-databases.sh
```

Script akan:
1. ✅ Check default VPC
2. ✅ Jika tidak ada → **Auto-create VPC + Subnets + IGW**
3. ✅ Jika ada → Use existing VPC
4. ✅ Continue dengan database setup

## Resources Created

```
VPC: lingulu-backend-core-vpc (10.0.0.0/16)
├── Internet Gateway: lingulu-backend-core-igw
├── Subnet 1: lingulu-backend-core-subnet-1 (10.0.1.0/24, AZ-a)
├── Subnet 2: lingulu-backend-core-subnet-2 (10.0.2.0/24, AZ-b)
└── Subnet 3: lingulu-backend-core-subnet-3 (10.0.3.0/24, AZ-c)
```

## Cleanup

Jika ingin delete VPC yang dibuat:

```bash
# Get VPC ID
VPC_ID=$(aws ec2 describe-vpcs \
  --filters "Name=tag:Name,Values=lingulu-backend-core-vpc" \
  --query 'Vpcs[0].VpcId' \
  --output text)

# Delete dependencies first (RDS, ElastiCache, DocumentDB, etc)
# Then delete VPC resources

# Delete subnets
aws ec2 describe-subnets \
  --filters "Name=vpc-id,Values=${VPC_ID}" \
  --query 'Subnets[*].SubnetId' \
  --output text | xargs -n1 aws ec2 delete-subnet --subnet-id

# Detach and delete IGW
IGW_ID=$(aws ec2 describe-internet-gateways \
  --filters "Name=attachment.vpc-id,Values=${VPC_ID}" \
  --query 'InternetGateways[0].InternetGatewayId' \
  --output text)
aws ec2 detach-internet-gateway --internet-gateway-id ${IGW_ID} --vpc-id ${VPC_ID}
aws ec2 delete-internet-gateway --internet-gateway-id ${IGW_ID}

# Delete VPC
aws ec2 delete-vpc --vpc-id ${VPC_ID}
```

## Notes

- VPC creation adds ~10-20 seconds to script execution
- All subnets are public (auto-assign public IP)
- Perfect untuk development/testing
- Untuk production, consider private subnets dengan NAT Gateway

---

**Status:** ✅ Fixed - Script now handles missing default VPC

