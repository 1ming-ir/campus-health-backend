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
    @Insert("insert into consultation_records(student_id,doctor_id,symptom,duration,severity,medicine_used,medicine_name,ai_advice,status,deleted,archived) values(#{studentId},#{doctorId},#{symptom},#{duration},#{severity},#{medicineUsed},#{medicineName},#{aiAdvice},#{status},false,false)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ConsultationRecord record);

    @Select("select id, student_id as studentId, doctor_id as doctorId, symptom, duration, severity, medicine_used as medicineUsed, medicine_name as medicineName, ai_advice as aiAdvice, doctor_reply as doctorReply, status, deleted, archived, created_at as createdAt, replied_at as repliedAt from consultation_records where student_id=#{studentId} and deleted=false and archived=false order by id desc")
    List<ConsultationRecord> findByStudentId(Long studentId);

    @Select("select c.id, c.student_id as studentId, c.doctor_id as doctorId, u.real_name as studentName, c.symptom, c.duration, c.severity, c.medicine_used as medicineUsed, c.medicine_name as medicineName, c.ai_advice as aiAdvice, c.doctor_reply as doctorReply, c.status, c.created_at as createdAt, c.replied_at as repliedAt from consultation_records c left join users u on c.student_id=u.id where c.deleted=false order by c.id desc")
    List<Map<String, Object>> findAllWithStudent();

    @Select("select id, student_id as studentId, doctor_id as doctorId, symptom, duration, severity, medicine_used as medicineUsed, medicine_name as medicineName, ai_advice as aiAdvice, doctor_reply as doctorReply, status, deleted, archived, created_at as createdAt, replied_at as repliedAt from consultation_records where id=#{id}")
    ConsultationRecord findById(Long id);

    @Update("update consultation_records set doctor_reply=#{doctorReply}, doctor_id=#{doctorId}, status='REPLIED', replied_at=CURRENT_TIMESTAMP where id=#{id} and deleted=false")
    int updateDoctorReply(@Param("id") Long id, @Param("doctorId") Long doctorId, @Param("doctorReply") String doctorReply);

    @Update("update consultation_records set deleted=true, status='DELETED' where id=#{id} and student_id=#{studentId} and doctor_reply is null")
    int deleteUnreplied(@Param("id") Long id, @Param("studentId") Long studentId);

    @Update("update consultation_records set archived=true, status='ARCHIVED' where id=#{id} and student_id=#{studentId} and doctor_reply is not null")
    int archiveReplied(@Param("id") Long id, @Param("studentId") Long studentId);
}