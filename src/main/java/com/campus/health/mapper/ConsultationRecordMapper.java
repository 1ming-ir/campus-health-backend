package com.campus.health.mapper;

import com.campus.health.entity.ConsultationRecord;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ConsultationRecordMapper {
    @Insert("insert into consultation_records(student_id,symptom,duration,severity,medicine_used,ai_advice,status) values(#{studentId},#{symptom},#{duration},#{severity},#{medicineUsed},#{aiAdvice},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ConsultationRecord record);

    @Select("select id, student_id as studentId, symptom, duration, severity, medicine_used as medicineUsed, ai_advice as aiAdvice, doctor_reply as doctorReply, status from consultation_records where student_id=#{studentId} order by id desc")
    List<ConsultationRecord> findByStudentId(Long studentId);

    @Select("select c.id, c.student_id as studentId, u.real_name as studentName, c.symptom, c.duration, c.severity, c.medicine_used as medicineUsed, c.ai_advice as aiAdvice, c.doctor_reply as doctorReply, c.status, c.created_at as createdAt from consultation_records c left join users u on c.student_id=u.id order by c.id desc")
    List<Map<String, Object>> findAllWithStudent();

    @Update("update consultation_records set doctor_reply=#{doctorReply}, status='REPLIED' where id=#{id}")
    int updateDoctorReply(@Param("id") Long id, @Param("doctorReply") String doctorReply);
}
