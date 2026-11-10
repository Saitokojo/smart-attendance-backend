package com.bbu.attendance.device.entity;

/**
 * ម៉ូឌុលទី ៣៖ Anti-Proxy Device UUID Binding Guard
 * សមាជិកទទួលខុសត្រូវ៖ សេង ស៊ីមផា (វេនថ្ងៃសុក្រ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/device/entity/Device.java
 *
 * ការណែនាំ៖
 * - បន្ថែម Unique Constraint លើ device_uuid និង student_id (One Student = One Trusted Phone)
 * - បំពេញ Attributes: id, studentId, deviceUuid, deviceModel, osVersion, isBlocked, boundAt, lastSeenAt
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
public class Device {
    // TODO [សេង ស៊ីមផា]: សរសេរកូដ JPA Entity សម្រាប់ Table devices នៅទីនេះ
}
