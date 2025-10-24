# Authentication Setup Guide

## 🔐 **Secure Token Management**

This guide shows you how to securely handle Google Cloud access tokens for the Artifact Registry without exposing them in your repository.

## 📋 **Available Methods**

### **Method 1: Gradle Properties (Recommended for Local Development)**

1. **Copy the template**:
   ```bash
   cp gradle.properties.template gradle.properties
   ```

2. **Get your access token**:
   ```bash
   gcloud auth application-default print-access-token
   ```

3. **Update `gradle.properties`**:
   ```properties
   GOOGLE_ACCESS_TOKEN=ya29.your_actual_token_here
   ```

4. **Build normally**:
   ```bash
   ./gradlew build
   ```

### **Method 2: Environment Variables**

1. **Set environment variable**:
   ```bash
   # Linux/Mac
   export GOOGLE_ACCESS_TOKEN="ya29.your_actual_token_here"
   
   # Windows
   set GOOGLE_ACCESS_TOKEN=ya29.your_actual_token_here
   ```

2. **Build normally**:
   ```bash
   ./gradlew build
   ```

### **Method 3: .env File (Advanced)**

1. **Create `.env` file**:
   ```bash
   echo "GOOGLE_ACCESS_TOKEN=ya29.your_actual_token_here" > .env
   ```

2. **Load in your shell**:
   ```bash
   # Linux/Mac
   source .env
   
   # Or use a tool like direnv
   ```

## 🔄 **Token Refresh Automation**

### **Option A: Shell Script**

Use the provided `refresh-token.sh`:
```bash
# Get fresh token and update gradle.properties
./refresh-token.sh
```

### **Option B: Manual Refresh**

```bash
# Get fresh token
gcloud auth application-default print-access-token

# Update gradle.properties manually
# Or set environment variable
export GOOGLE_ACCESS_TOKEN="new_token_here"
```

## 🚀 **CI/CD Integration**

### **GitHub Actions**

Create `.github/workflows/build.yml`:
```yaml
name: Build
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: google-github-actions/auth@v1
        with:
          credentials_json: ${{ secrets.GOOGLE_CREDENTIALS }}
      - name: Setup JDK
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Build
        run: ./gradlew build
```

### **Jenkins**

In your Jenkins pipeline:
```groovy
pipeline {
    agent any
    environment {
        GOOGLE_ACCESS_TOKEN = credentials('google-access-token')
    }
    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
    }
}
```

## 🔧 **Development Workflow**

### **Daily Development**

1. **Start your day**:
   ```bash
   # Refresh token if needed
   ./refresh-token.sh
   
   # Build and run
   ./gradlew assembleDebug
   ```

2. **Commit changes**:
   ```bash
   # Only commit source code, never gradle.properties
   git add app/src/ docs/ build.gradle settings.gradle
   git commit -m "Your changes"
   git push
   ```

### **Team Collaboration**

1. **Share setup instructions**:
   - Include `setup-auth.md` in your README
   - Share `gradle.properties.template` with team
   - Never commit actual tokens

2. **Onboarding new developers**:
   ```bash
   # Clone repository
   git clone https://github.com/sandsiv/digi-android-demo.git
   cd digi-android-demo
   
   # Setup authentication
   cp gradle.properties.template gradle.properties
   # Edit gradle.properties with your token
   
   # Build
   ./gradlew build
   ```

## 🛡️ **Security Best Practices**

### **Do's**:
- ✅ Use `gradle.properties` for local development
- ✅ Use environment variables for CI/CD
- ✅ Keep tokens in `.gitignore`
- ✅ Rotate tokens regularly
- ✅ Use service accounts for CI/CD

### **Don'ts**:
- ❌ Never commit tokens to git
- ❌ Don't hardcode tokens in source code
- ❌ Don't share tokens in chat/email
- ❌ Don't use personal tokens for CI/CD

## 🔍 **Troubleshooting**

### **Token Issues**

1. **"Could not resolve dependency"**:
   ```bash
   # Check if token is set
   echo $GOOGLE_ACCESS_TOKEN
   
   # Refresh token
   gcloud auth application-default print-access-token
   ```

2. **"401 Unauthorized"**:
   ```bash
   # Re-authenticate
   gcloud auth application-default login
   ```

3. **"Token expired"**:
   ```bash
   # Refresh token
   ./refresh-token.sh
   ```

### **Build Issues**

1. **Check token source**:
   ```bash
   # Debug which token is being used
   ./gradlew build --info | grep GOOGLE_ACCESS_TOKEN
   ```

2. **Verify repository access**:
   ```bash
   # Test token manually
   curl -H "Authorization: Bearer $GOOGLE_ACCESS_TOKEN" \
        "https://europe-maven.pkg.dev/sandsiv-infrastructure/digi-module/"
   ```

## 📚 **Additional Resources**

- [Google Cloud Authentication](https://cloud.google.com/docs/authentication)
- [Gradle Properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_properties_and_system_properties)
- [Environment Variables](https://docs.gradle.org/current/userguide/build_environment.html#sec:environment_variables)

---

*This setup ensures your tokens are secure while allowing seamless development and CI/CD integration.*