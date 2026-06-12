package com.campus.health.mapper;

import com.campus.health.entity.Appointment;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AppointmentMapper {
    @Insert("insert into appointments(student_id,doctor_id,appointment_date,time_slot,reason,status) values(#{studentId},#{doctorId},#{appointmentDate},#{timeSlot},#{reason},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Appointment appointment);

    @Select("select a.id, a.student_id as studentId, a.doctor_id as doctorId, du.real_name as doctorName, a.appointment_date as appointmentDate, a.time_slot as timeSlot, a.reason, a.status from appointments a left join doctors d on a.doctor_id=d.id left join users du on d.user_id=du.id where a.student_id=#{studentId} order by a.id desc")
    List<Map<String, Object>> findByStudentId(Long studentId);

    @Select("select a.id, a.student_id as studentId, su.real_name as studentName, a.doctor_id as doctorId, a.appointment_date as appointmentDate, a.time_slot as timeSlot, a.reason, a.status from appointments a left join users su on a.student_id=su.id where a.doctor_id=#{doctorId} order by a.id desc")
    List<Map<String, Object>> findForDoctor(Long doctorId);

    @Select("select a.id, a.student_id as studentId, su.real_name as studentName, a.doctor_id as doctorId, du.real_name as doctorName, a.appointment_date as appointmentDate, a.time_slot as timeSlot, a.reason, a.status from appointments a left join users su on a.student_id=su.id left join doctors d on a.doctor_id=d.id left join users du on d.user_id=du.id order by a.id desc")
    List<Map<String, Object>> findAll();

    @Update("update appointments set status=#{status} where id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}