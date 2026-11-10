package com.bbu.attendance.clazz.entity;

/**
 * ម៉ូឌុលទី ២៖ Class Management & Dynamic Invite Code
 * សមាជិកទទួលខុសត្រូវ៖ មុត ភិរាក់នាន់ (វេនថ្ងៃព្រហស្បតិ៍)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/clazz/entity/ClassStudent.java
 *
 * ការណែនាំ៖
 * - បន្ថែម UniqueConstraint លើ (class_id, student_id) ការពារ Duplicate Enrollment
 * - បំពេញ Attributes: id, classId, studentId, joinedAt, status (ACTIVE, DROPPED)
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
public class ClassStudent {
    // TODO [មុត ភិរាក់នាន់]: សរសេរកូដ JPA Entity សម្រាប់ Table class_students នៅទីនេះ
}
