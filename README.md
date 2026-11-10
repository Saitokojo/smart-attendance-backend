# Smart & Secure University Attendance System — Spring Boot Backend
## គម្រោងស្រាវជ្រាវ និងអភិវឌ្ឍន៍សារណាជំនាន់ទី ២ (Build Bright University)
### សាកលវិទ្យាល័យបៀលប្រាយ — មហាវិទ្យាល័យវិទ្យាសាស្ត្រ និងបច្ចេកវិទ្យា

ប្រព័ន្ធគ្រប់គ្រងវត្តមានឆ្លាតវៃ និងមានសុវត្ថិភាពខ្ពស់ដោយប្រើ **Multi-Factor Physical Presence Verification** (Spring Boot 3.2.5, Java 21 LTS Virtual Threads, PostgreSQL 16 & Redis 7)។

---

### 🌟 វឌ្ឍនភាពការងារសប្តាហ៍ទី ២ (Week 2 Update): វេនថ្ងៃអង្គារ (ខី វណ្ណារ៉ុង - Team Leader & DevOps Architect)
- ✅ **Java 21 Virtual Threads (Project Loom):** បើកដំណើរការ `spring.threads.virtual.enabled: true` ទ្រទ្រង់ 10,000+ Concurrent Requests ដោយមិនបារម្ភរឿង OS Thread Starvation។
- ✅ **HikariCP Connection Pool Optimization:** កំណត់ទំហំ Pool 25 Connections, `leak-detection-threshold: 2000ms`, និង Hibernate JDBC Batching (`batch_size: 50`)។
- ✅ **Asynchronous Audit Logging Module (`com.bbu.attendance.audit`):**
  - Entity `AuditLog` ចង Indexes លើ `user_id`, `action`, `timestamp` ក្នុង PostgreSQL។
  - `@Async` Logging ដំណើរការលើ Virtual Threads ដោយមិនរំខាន ឬពន្យឺតល្បឿន Request របស់ User (0ms latency penalty)។
  - Endpoints: `GET /api/admin/audit-logs` (៥០ ជួរចុងក្រោយ) និង `GET /api/admin/audit-logs/user/{userId}` (ការពារដោយ Role ADMIN)។
- ✅ **DevOps Observability & System Telemetry (`com.bbu.attendance.devops`):**
  - Endpoint: `GET /api/admin/system/metrics` ផ្តល់ទិន្នន័យ JVM Heap, Virtual Thread Capability, HikariCP Active/Idle Connections, និង Database Ping Latency (ms)។
  - `PerformanceLoggingFilter`: HTTP Filter កត់ត្រារយៈពេលដំណើរការនៃ API Calls នីមួយៗ និងបន្ថែម Header `X-Response-Time-Millis`។

---

### 🚀 របៀបបញ្ឆេះដំណើរការ (How to Run):
1. **បញ្ឆេះ Database ជាមួយ Docker:**
   ```bash
   docker compose up -d
   ```
   - PostgreSQL 16 នឹងដំណើរការលើ Port `5432` ព្រមទាំងបង្កើត Table ទាំង ១៣ ស្វ័យប្រវត្តិតាមរយៈ `schema.sql`។
   - pgAdmin 4 ដំណើរការលើ Port `5050` (Email: `admin@bbu.edu.kh`, Password: `admin`)។
2. **បើក Project ក្នុង IntelliJ IDEA:**
   - ជ្រើសរើស JDK: **Microsoft OpenJDK 21 LTS** ឬ Java 21+
   - Run `AttendanceApplication.java`
   - ប្រព័ន្ធនឹង **Auto-Seed Roles (STUDENT, TEACHER, ADMIN)** ចូលក្នុង Database ស្វ័យប្រវត្តិ!

---

### 🧪 API Endpoints ដែលអាចតេស្តបាន (Current Endpoints):

#### 🔐 Auth & User Management (Week 1):
* `POST /api/auth/register` — ចុះឈ្មោះគណនីថ្មី
* `POST /api/auth/login` — ចូលប្រើប្រាស់ប្រព័ន្ធ (ទទួលបាន JWT Bearer Token)
* `GET  /api/users/me` — មើលព័ត៌មាន Profile ផ្ទាល់ខ្លួន
* `GET  /api/users/{id}` — Admin មើលព័ត៌មាន User ណាមួយ
* `POST /api/users` — Admin បង្កើត User ថ្មីដោយផ្ទាល់
* `GET  /api/roles` — មើលបញ្ជី Roles ទាំងអស់ (Admin, Teacher)
* `POST /api/roles` — Admin បង្កើត Role ថ្មី

#### ⚡ Concurrency, DevOps & Audit Logs (Week 2 - វណ្ណារ៉ុង):
* `GET /api/admin/system/metrics` — ទាញយកទិន្នន័យ Telemetry (JVM Heap, Virtual Threads, DB Pool, Latency Ping) [Role: ADMIN]
* `GET /api/admin/audit-logs` — មើលកំណត់ត្រាសវនកម្ម ៥០ ជួរចុងក្រោយ [Role: ADMIN]
* `GET /api/admin/audit-logs/user/{userId}` — មើលកំណត់ត្រាសវនកម្មតាម User ID [Role: ADMIN]

---

### 📜 សារបញ្ជូនដំបងបណ្តាក់ប្រចាំថ្ងៃ (Handoff Note for Wednesday):
> **ថ្ងៃនេះ ខី វណ្ណារ៉ុង (Leader & DevOps Architect) បានបញ្ចប់៖**
> 1. Setup Java 21 Virtual Threads & HikariCP Pool រួចរាល់
> 2. បង្កើត Entity, Repository, DTO, Service, Controller សម្រាប់ Audit Logging រួចរាល់
> 3. បង្កើត System Metrics Telemetry & Performance Logging Filter រួចរាល់
> 4. Test Compile ជាមួយ Maven (`BUILD SUCCESS` គ្មាន Error)
> 5. Commit & Push ឡើង GitHub Repository រួចរាល់!
>
> 👉 **ការងារថ្ងៃស្អែក (ថ្ងៃពុធ):** និស្សិត **ស្រិល ម៉េងទី** អាច `git pull` យកកូដទៅធ្វើបន្តលើ **Module 1: Room & Geofence GPS CRUD (`com.bbu.attendance.room.*`)** បានដោយរលូន!
