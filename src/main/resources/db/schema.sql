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
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE doctors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  department VARCHAR(64),
  title VARCHAR(64),
  specialty VARCHAR(255),
  schedule_text VARCHAR(255)
);

CREATE TABLE consultation_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  symptom TEXT,
  duration VARCHAR(64),
  severity VARCHAR(20),
  medicine_used VARCHAR(255),
  ai_advice TEXT,
  doctor_reply TEXT,
  status VARCHAR(20) DEFAULT 'CREATED',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
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

INSERT INTO users(username,password,real_name,role,phone,status) VALUES
('student','123456','张同学','STUDENT','13800000001','ENABLED'),
('doctor','123456','李医生','DOCTOR','13800000002','ENABLED'),
('admin','123456','管理员','ADMIN','13800000003','ENABLED');

INSERT INTO doctors(user_id,department,title,specialty,schedule_text) VALUES
(2,'全科门诊','主治医师','感冒发热、肠胃不适、常见皮肤问题','周一至周五 09:00-11:30，14:00-17:00');

INSERT INTO consultation_records(student_id,symptom,duration,severity,medicine_used,ai_advice,doctor_reply,status) VALUES
(1,'咽痛、咳嗽两天，轻微发热','2天','轻度','未用药','建议多饮水、休息，观察体温变化；如持续高热或呼吸困难，请及时就医。本建议仅供健康咨询参考，不构成医学诊断。','可到校医院全科门诊测量体温并评估是否需要用药。','REPLIED');

INSERT INTO appointments(student_id,doctor_id,appointment_date,time_slot,reason,status) VALUES
(1,1,CURRENT_DATE,'上午 09:00-10:00','咳嗽发热，希望医生进一步评估','PENDING');

INSERT INTO health_articles(title,category,summary,content,status) VALUES
('普通感冒护理','呼吸道','休息、补水、观察体温，警惕持续高热。','普通感冒多数为病毒感染，建议保证睡眠、补充水分，避免剧烈运动。如出现持续高热、胸闷气短、意识异常等情况，应及时到校医院或正规医疗机构就诊。','PUBLISHED'),
('腹泻期间饮食建议','肠胃','清淡饮食并警惕脱水。','腹泻期间建议少量多次饮水，选择粥、面条等清淡食物，避免油腻和刺激性饮食。如出现明显脱水、便血、持续腹痛，应及时就医。','PUBLISHED'),
('考试周压力调节','心理健康','规律作息，必要时寻求帮助。','考试周应尽量保持规律睡眠，拆分复习任务，避免连续熬夜。若焦虑、失眠或情绪低落持续影响学习生活，应主动联系辅导员或心理咨询中心。','PUBLISHED');
