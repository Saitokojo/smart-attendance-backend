package com.bbu.attendance.clazz.repository;

/**
 * ម៉ូឌុលទី ២៖ Class Management & Dynamic Invite Code
 * សមាជិកទទួលខុសត្រូវ៖ មុត ភិរាក់នាន់ (វេនថ្ងៃព្រហស្បតិ៍)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/clazz/repository/ClassRepository.java
 *
 * ការណែនាំ៖
 * - Extend: JpaRepository<ClassEntity, Long>
 * - បន្ថែម Methods: findByInviteCode(String inviteCode), findByTeacherId(Long teacherId)
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
public interface ClassRepository {
    // TODO [មុត ភិរាក់នាន់]: សរសេរ JPA Queries សម្រាប់ ClassEntity នៅទីនេះ
}
