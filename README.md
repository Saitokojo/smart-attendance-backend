# Smart & Secure University Attendance System — Spring Boot Backend
## គម្រោងស្រាវជ្រាវ និងអភិវឌ្ឍន៍សារណាជំនាន់ទី ២ (Build Bright University)

គម្រោងនេះជា Spring Boot 3 RESTful API ពេញលេញសម្រាប់សប្តាហ៍ទី ១ (Auth API & PostgreSQL Database)។

### 🚀 របៀបបញ្ឆេះដំណើរការ (How to Run):
1. **បញ្ឆេះ Database ជាមួយ Docker (ងាយស្រួលបំផុត):**
   ```bash
   docker compose up -d
   ```
   - PostgreSQL 16 នឹងដំណើរការលើ Port `5432` ព្រមទាំងបង្កើត Table ទាំង ១៣ ស្វ័យប្រវត្តិតាមរយៈ `schema.sql`។
   - pgAdmin 4 ដំណើរការលើ Port `5050` (Email: `admin@bbu.edu.kh`, Password: `admin`)។
2. បើក Folder នេះក្នុង **IntelliJ IDEA** (Open Project)។
3. ចុច Run `AttendanceApplication.java`។
   - ប្រព័ន្ធនឹង **Auto-Seed Roles (STUDENT, TEACHER, ADMIN)** ចូលក្នុង Database ស្វ័យប្រវត្តិ!

---

### 🧪 របៀបតេស្តជាមួយ Postman (1-Click Import Postman Collection):

នៅក្នុង Folder នេះមានឯកសារ **`smart-attendance-api.postman_collection.json`** ស្រាប់៖
1. បើកកម្មវិធី **Postman**។
2. ចុចប៊ូតុង **Import** (នៅជ្រុងខាងឆ្វេងខាងលើ)។
3. Drag & Drop ឬជ្រើសរើសឯកសារ **`smart-attendance-api.postman_collection.json`**។
4. អ្នកនឹងទទួលបានសំណុំ Requests ទាំង ៥ រួមមាន Test Scripts ផ្ទៀងផ្ទាត់ស្វ័យប្រវត្តិ៖
   - `01. ចុះឈ្មោះសិស្សថ្មី (Register Student)` -> `POST /api/auth/register` (201 Created)
   - `02. ចុះឈ្មោះគ្រូថ្មី (Register Teacher)` -> `POST /api/auth/register` (201 Created)
   - `03. ចូលប្រើប្រាស់ជោគជ័យ (Login Success)` -> `POST /api/auth/login` (200 OK + Auto Save JWT)
   - `04. ចូលប្រើប្រាស់បរាជ័យ (Login Fail - Bad Password)` -> `POST /api/auth/login` (401 Unauthorized)
   - `05. កំហុស Validation (Register Validation Error)` -> `POST /api/auth/register` (400 Bad Request)
