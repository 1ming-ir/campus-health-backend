DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS consultation_records;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS health_articles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  real_name VARCHAR(64),
  role VARCHAR(20) NOT NULL,
  phone VARCHAR(30),
  status VARCHAR(20) DEFAULT 'ENABLED',
  college VARCHAR(64),
  class_name VARCHAR(64),
  gender VARCHAR(20),
  age INT,
  allergy_history VARCHAR(255),
  medical_history VARCHAR(255),
  emergency_contact VARCHAR(64),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE doctors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  department VARCHAR(64),
  title VARCHAR(64),
  specialty VARCHAR(255),
  schedule_text VARCHAR(255),
  phone VARCHAR(30),
  introduction TEXT,
  status VARCHAR(20) DEFAULT 'ENABLED'
);

CREATE TABLE consultation_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  doctor_id BIGINT,
  symptom TEXT,
  duration VARCHAR(64),
  severity VARCHAR(20),
  medicine_used VARCHAR(64),
  medicine_name VARCHAR(128),
  ai_advice TEXT,
  doctor_reply TEXT,
  status VARCHAR(20) DEFAULT 'CREATED',
  deleted BOOLEAN DEFAULT FALSE,
  archived BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  replied_at DATETIME
);

CREATE TABLE appointments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  appointment_date DATE,
  time_slot VARCHAR(64),
  reason TEXT,
  status VARCHAR(20) DEFAULT 'PENDING',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE health_articles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(128),
  category VARCHAR(64),
  summary VARCHAR(255),
  content TEXT,
  status VARCHAR(20) DEFAULT 'PUBLISHED',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE medicines (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128),
  category VARCHAR(64),
  usage_info TEXT,
  caution TEXT,
  prescription_required VARCHAR(20),
  status VARCHAR(20) DEFAULT 'PUBLISHED',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE announcements (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(128),
  type VARCHAR(64),
  content TEXT,
  status VARCHAR(20) DEFAULT 'PUBLISHED',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users(username,password,real_name,role,phone,status,college,class_name,gender,age,allergy_history,medical_history,emergency_contact) VALUES
('student','123456','张同学','STUDENT','13800000001','ENABLED','信息工程学院','软件2301','女',20,'无','无','家属 13800000000'),
('doctor','123456','李医生','DOCTOR','13800000002','ENABLED',NULL,NULL,'男',36,NULL,NULL,NULL),
('admin','123456','管理员','ADMIN','13800000003','ENABLED',NULL,NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO doctors(user_id,department,title,specialty,schedule_text,phone,introduction,status) VALUES
(2,'全科门诊','主治医师','感冒发热、肠胃不适、常见皮肤问题','周一至周五 09:00-11:30，14:00-17:00','13800000002','负责校医院常见病咨询、预约复核和健康指导。','ENABLED');

INSERT INTO consultation_records(student_id,doctor_id,symptom,duration,severity,medicine_used,medicine_name,ai_advice,doctor_reply,status,deleted,archived,replied_at) VALUES
(1,1,'咽痛咳嗽，体温37.8℃，无明显胸闷','2天','轻度','未用药',NULL,'初步判断：可能属于常见轻度上呼吸道不适。\n日常护理建议：注意休息，适量饮水，避免熬夜。\n需要观察的风险信号：若高热不退、呼吸困难或胸痛，应及时线下就医。\n是否建议预约校医院：如 48 小时无缓解，建议预约校医院。\n免责声明：仅供健康咨询参考。','建议继续观察体温变化，若发热超过38.5℃或咳嗽明显加重，请线下就医。','REPLIED',false,false,CURRENT_TIMESTAMP);

INSERT INTO appointments(student_id,doctor_id,appointment_date,time_slot,reason,status) VALUES
(1,1,CURRENT_DATE,'上午 09:00-10:00','咳嗽发热，希望医生进一步评估','PENDING');

INSERT INTO health_articles(title,category,summary,content,status) VALUES
('感冒发热的日常护理','常见症状','了解普通感冒、低热时的观察和护理方法。','普通感冒多与病毒感染有关。建议保持休息，适量饮水，避免熬夜。若出现高热不退、呼吸困难、胸痛或症状持续加重，应及时线下就医。','PUBLISHED'),
('肠胃不适时如何饮食','肠胃不适','腹泻、胃胀、恶心时的饮食注意事项。','肠胃不适时建议少量多餐，选择清淡易消化食物，避免生冷、辛辣和油腻食物。若频繁呕吐、腹痛明显或脱水，应及时就医。','PUBLISHED'),
('运动损伤后的初步处理','运动损伤','扭伤、拉伤后的初步处理方法。','运动损伤后应停止运动，早期可进行冷敷并抬高患处。若疼痛明显、活动受限或出现肿胀加重，应到校医院或正规医疗机构评估。','PUBLISHED');

INSERT INTO medicines(name,category,usage_info,caution,prescription_required,status) VALUES
('对乙酰氨基酚片','解热镇痛','用于普通发热或轻中度疼痛，需按说明书或医生建议使用。','避免与其他含同类成分的复方感冒药重复使用，肝功能异常者慎用。','非处方药','PUBLISHED'),
('蒙脱石散','肠胃用药','用于腹泻时的辅助处理，按说明书冲服。','若腹泻伴高热、便血、明显脱水，应及时就医。','非处方药','PUBLISHED');

INSERT INTO announcements(title,type,content,status) VALUES
('校医院门诊预约提醒','门诊通知','校医院工作日开放线上预约，请按预约时间段到院就诊。','PUBLISHED'),
('近期呼吸道疾病健康提醒','健康提醒','近期气温变化较大，请注意保暖、规律作息，出现持续发热或呼吸困难应及时线下就医。','PUBLISHED');