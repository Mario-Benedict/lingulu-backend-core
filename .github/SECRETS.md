# GitHub Secrets Configuration

Untuk CI/CD berjalan dengan baik, Anda perlu menambahkan secrets berikut di GitHub Repository.

## 📍 Cara Menambahkan Secrets

1. Buka repository di GitHub
2. Go to: **Settings** → **Secrets and variables** → **Actions**
3. Click: **New repository secret**
4. Tambahkan satu per satu secrets di bawah ini

**💡 Tip:** Untuk database credentials, gunakan script `setup-databases.sh` untuk otomatis setup database di AWS!
See: [DATABASE_SETUP.md](DATABASE_SETUP.md)

---

## 🔐 Required Secrets

### AWS Credentials (untuk ECR & ECS)
```
AWS_ACCESS_KEY_ID         = your-aws-access-key-id
AWS_SECRET_ACCESS_KEY     = your-aws-secret-access-key
```

### Database - PostgreSQL
```
POSTGRESQL_DB_HOST        = your-postgres-host
POSTGRESQL_DB_PORT        = 5432
POSTGRESQL_DB_NAME        = lingulu_db
POSTGRESQL_DB_USER        = your-db-username
POSTGRESQL_DB_PASSWORD    = your-db-password
```

### Database - Redis
```
REDIS_DB_HOST            = your-redis-host
REDIS_DB_PORT            = 6379
REDIS_DB_USER            = your-redis-username (optional, bisa kosong)
REDIS_DB_PASSWORD        = your-redis-password
```

### Email (Gmail SMTP)
```
SPRING_MAIL_USERNAME     = your-email@gmail.com
SPRING_MAIL_PASSWORD     = your-app-password
```

**Note:** Untuk Gmail, gunakan App Password, bukan password biasa.
Cara membuat: https://support.google.com/accounts/answer/185833

### OAuth2 - Google
```
GOOGLE_CLIENT_ID         = your-google-client-id
GOOGLE_CLIENT_SECRET     = your-google-client-secret
```

### JWT
```
JWT_SECRET               = your-jwt-secret-key-minimum-256-bits
```

**Generate random JWT secret:**
```bash
openssl rand -base64 64
```

### AWS S3
```
AWS_REGION_S3            = ap-southeast-1
AWS_ENDPOINT             = https://s3.ap-southeast-1.amazonaws.com
AWS_S3_CHAT_BUCKET_NAME  = your-chat-bucket-name
AWS_S3_PROFILE_BUCKET_NAME = your-profile-bucket-name
AWS_ACCESS_KEY           = your-s3-access-key
AWS_SECRET_KEY           = your-s3-secret-key
```

### AWS CloudFront
```
AWS_CLOUDFRONT_KEYPAIR   = your-cloudfront-key-pair-id
AWS_CLOUDFRONT_PRIVATE_KEY_BASE64 = base64-encoded-private-key
CDN_DOMAIN               = your-cloudfront-domain.cloudfront.net
```

**How to encode private key to base64:**
```bash
# Linux/Mac/Git Bash
base64 -w 0 private_lingulu_cdn_key_pkcs8.der

# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("private_lingulu_cdn_key_pkcs8.der"))
```

### API Keys
```
GROQ_API_KEY             = your-groq-api-key
```

### MongoDB
```
MONGO_DB_USER            = your-mongo-username
MONGO_DB_PASSWORD        = your-mongo-password
MONGO_DB_HOST            = your-mongo-host
MONGO_DB_PORT            = 27017
MONGO_DB_NAME            = lingulu_db
```

---

## ✅ Verification Checklist

Setelah menambahkan semua secrets, verify dengan checklist ini:

### AWS Secrets
- [ ] `AWS_ACCESS_KEY_ID`
- [ ] `AWS_SECRET_ACCESS_KEY`

### Database Secrets
- [ ] `POSTGRESQL_DB_HOST`
- [ ] `POSTGRESQL_DB_PORT`
- [ ] `POSTGRESQL_DB_NAME`
- [ ] `POSTGRESQL_DB_USER`
- [ ] `POSTGRESQL_DB_PASSWORD`
- [ ] `REDIS_DB_HOST`
- [ ] `REDIS_DB_PORT`
- [ ] `REDIS_DB_USER`
- [ ] `REDIS_DB_PASSWORD`

### Email & OAuth Secrets
- [ ] `SPRING_MAIL_USERNAME`
- [ ] `SPRING_MAIL_PASSWORD`
- [ ] `GOOGLE_CLIENT_ID`
- [ ] `GOOGLE_CLIENT_SECRET`

### Security & Storage Secrets
- [ ] `JWT_SECRET`
- [ ] `AWS_REGION_S3`
- [ ] `AWS_ENDPOINT`
- [ ] `AWS_S3_CHAT_BUCKET_NAME`
- [ ] `AWS_S3_PROFILE_BUCKET_NAME`
- [ ] `AWS_ACCESS_KEY`
- [ ] `AWS_SECRET_KEY`

### CloudFront Secrets
- [ ] `AWS_CLOUDFRONT_KEYPAIR`
- [ ] `AWS_CLOUDFRONT_PRIVATE_KEY_BASE64`
- [ ] `CDN_DOMAIN`

### API Keys
- [ ] `GROQ_API_KEY`

### MongoDB Secrets
- [ ] `MONGO_DB_USER`
- [ ] `MONGO_DB_PASSWORD`
- [ ] `MONGO_DB_HOST`
- [ ] `MONGO_DB_PORT`
- [ ] `MONGO_DB_NAME`

---

## 🧪 Testing Secrets

Untuk test apakah secrets sudah benar:

1. Push code ke branch `main`
2. Check GitHub Actions workflow
3. Lihat apakah job "Run Tests" berhasil
4. Jika gagal, check logs untuk error message

---

## 💡 Tips

### For Development/Testing
Anda bisa menggunakan dummy values untuk testing:
- Database: Gunakan database testing
- Redis: Bisa skip jika tidak mandatory
- Email: Gunakan test SMTP atau mailtrap.io
- S3: Gunakan bucket development

### For Production
- Gunakan credentials production yang sebenarnya
- Rotate secrets secara berkala
- Jangan share secrets dengan siapapun
- Backup credentials di password manager

---

## 🔄 Update Secrets

Jika perlu update secrets:
1. Go to Settings → Secrets → Actions
2. Click pada secret yang ingin diupdate
3. Click **Update secret**
4. Enter new value
5. Save

---

## ⚠️ Important Notes

1. **Never commit secrets to Git!**
2. File `secret.properties` sudah ada di `.gitignore`
3. CI/CD akan auto-generate `secret.properties` dari GitHub Secrets
4. Secrets tidak akan terlihat di logs (GitHub otomatis mask)
5. Hanya collaborators dengan write access yang bisa manage secrets

---

## 📚 Reference

- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
- [Google OAuth Setup](https://console.cloud.google.com/apis/credentials)



