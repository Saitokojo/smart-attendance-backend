package com.bbu.attendance.device.repository;

/**
 * ម៉ូឌុលទី ៣៖ Anti-Proxy Device UUID Binding Guard
 * សមាជិកទទួលខុសត្រូវ៖ សេង ស៊ីមផា (វេនថ្ងៃសុក្រ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/device/repository/DeviceRepository.java
 *
 * ការណែនាំ៖
 * - Extend: JpaRepository<Device, Long>
 * - បន្ថែម Methods: findByStudentId(Long studentId), findByDeviceUuid(String deviceUuid), existsByDeviceUuid(String deviceUuid)
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
public interface DeviceRepository {
    // TODO [សេង ស៊ីមផា]: សរសេរ JPA Queries សម្រាប់ Device នៅទីនេះ
}
