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
('student','123456','寮犲悓瀛?,'STUDENT','13800000001','ENABLED','淇℃伅宸ョ▼瀛﹂櫌','杞欢 2301','濂?,20,'鏃?,'鏃?,'瀹堕暱 13800000000'),
('doctor','123456','鏉庡尰鐢?,'DOCTOR','13800000002','ENABLED',NULL,NULL,'鐢?,36,NULL,NULL,NULL),
('admin','123456','绠＄悊鍛?,'ADMIN','13800000003','ENABLED',NULL,NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO doctors(user_id,department,title,specialty,schedule_text,phone,introduction,status) VALUES
(2,'鍏ㄧ闂ㄨ瘖','涓绘不鍖诲笀','鎰熷啋鍙戠儹銆佽偁鑳冧笉閫傘€佸父瑙佺毊鑲ら棶棰?,'鍛ㄤ竴鑷冲懆浜?09:00-11:30锛?4:00-17:00','13800000002','璐熻矗鏍″洯甯歌杞荤棁鍜ㄨ銆侀绾﹁瘎浼板拰鍋ュ悍绉戞櫘銆?,'ENABLED');

INSERT INTO consultation_records(student_id,doctor_id,symptom,duration,severity,medicine_used,medicine_name,ai_advice,doctor_reply,status,deleted,archived) VALUES
(1,1,'鍜界棝銆佸挸鍡戒袱澶╋紝杞诲井鍙戠儹','2澶?,'杞诲害','鏈敤鑽?,'鏃?,'鍒濇鍒ゆ柇锛氬彲鑳戒负鏅€氫笂鍛煎惛閬撲笉閫傦紝璇疯瀵熶綋娓╁拰鐥囩姸鍙樺寲銆俓n鏃ュ父鎶ょ悊寤鸿锛氬楗按銆佷繚璇佷紤鎭€侀伩鍏嶇啲澶溿€俓n闇€瑕佽瀵熺殑椋庨櫓淇″彿锛氬鎸佺画楂樼儹鎴栧懠鍚稿洶闅撅紝璇峰強鏃剁嚎涓嬪氨鍖汇€俓n鏄惁寤鸿棰勭害鏍″尰闄細濡?48 灏忔椂鏈紦瑙ｏ紝寤鸿棰勭害鏍″尰闄€俓n鍏嶈矗澹版槑锛氭湰寤鸿浠呬緵鍋ュ悍鍜ㄨ鍙傝€冿紝涓嶆瀯鎴愭寮忓尰瀛﹁瘖鏂€?,'鍙埌鏍″尰闄㈠叏绉戦棬璇婃祴閲忎綋娓╁苟璇勪及鏄惁闇€瑕佺敤鑽€?,'REPLIED',false,false);

INSERT INTO appointments(student_id,doctor_id,appointment_date,time_slot,reason,status) VALUES
(1,1,CURRENT_DATE,'涓婂崍 09:00-10:00','鍜冲椊鍙戠儹锛屽笇鏈涘尰鐢熻繘涓€姝ヨ瘎浼?,'PENDING');

INSERT INTO health_articles(title,category,summary,content,status) VALUES
('鏅€氭劅鍐掓姢鐞?,'鍛煎惛閬?,'浼戞伅銆佽ˉ姘淬€佽瀵熶綋娓╋紝璀︽儠鎸佺画楂樼儹銆?,'鏅€氭劅鍐掑鏁颁负鐥呮瘨鎰熸煋锛屽缓璁繚璇佺潯鐪犮€佽ˉ鍏呮按鍒嗭紝閬垮厤鍓х儓杩愬姩銆傚鍑虹幇鎸佺画楂樼儹銆佽兏闂锋皵鐭€佹剰璇嗗紓甯哥瓑鎯呭喌锛屽簲鍙婃椂鍒版牎鍖婚櫌鎴栨瑙勫尰鐤楁満鏋勫氨璇娿€?,'PUBLISHED'),
('鑵规郴鏈熼棿楗寤鸿','鑲犺儍','娓呮贰楗骞惰鎯曡劚姘淬€?,'鑵规郴鏈熼棿寤鸿灏戦噺澶氭楗按锛岄€夋嫨绮ャ€侀潰鏉＄瓑娓呮贰椋熺墿锛岄伩鍏嶆补鑵诲拰鍒烘縺鎬чギ椋熴€傚鍑虹幇鏄庢樉鑴辨按銆佷究琛€銆佹寔缁吂鐥涳紝搴斿強鏃跺氨鍖汇€?,'PUBLISHED'),
('鑰冭瘯鍛ㄥ帇鍔涜皟鑺?,'蹇冪悊鍋ュ悍','瑙勫緥浣滄伅锛屽繀瑕佹椂瀵绘眰甯姪銆?,'鑰冭瘯鍛ㄥ簲灏介噺淇濇寔瑙勫緥鐫＄湢锛屾媶鍒嗗涔犱换鍔★紝閬垮厤杩炵画鐔銆傝嫢鐒﹁檻銆佸け鐪犳垨鎯呯华浣庤惤鎸佺画褰卞搷瀛︿範鐢熸椿锛屽簲涓诲姩鑱旂郴杈呭鍛樻垨蹇冪悊鍜ㄨ涓績銆?,'PUBLISHED');

INSERT INTO medicines(name,category,usage_info,caution,prescription_required,status) VALUES
('瀵逛箼閰版皑鍩洪厷','鎰熷啋鍙戠儹','鐢ㄤ簬鍙戠儹銆佽交涓害鐤肩棝鐨勫父瑙侀€€鐑晣鐥涚鏅€?,'閬垮厤閲嶅鏈嶇敤鍚悓绫绘垚鍒嗚嵂鐗╋紝鑲濆姛鑳藉紓甯歌€呴伒鍖诲槺銆?,'鍚?,'PUBLISHED'),
('鍙ｆ湇琛ユ恫鐩?,'鑲犺儍鐢ㄨ嵂','鑵规郴鎴栧嚭姹楄緝澶氭椂鐢ㄤ簬琛ュ厖姘村垎鍜岀數瑙ｈ川銆?,'涓ラ噸鑴辨按銆佹寔缁憰鍚愭垨渚胯搴斿強鏃跺氨鍖汇€?,'鍚?,'PUBLISHED');

INSERT INTO announcements(title,type,content,status) VALUES
('鏍″尰闄㈤绾︽彁閱?,'鏍″尰闄㈤€氱煡','璇锋寜棰勭害鏃堕棿娈靛埌鏍″尰闄㈠氨璇婏紝濡傞渶鍙栨秷璇锋彁鍓嶅湪绯荤粺涓搷浣溿€?,'PUBLISHED'),
('鑰冭瘯鍛ㄥ仴搴锋彁閱?,'鍋ュ悍鎻愰啋','鑰冭瘯鍛ㄦ敞鎰忚寰嬩綔鎭紝鍑虹幇鎸佺画澶辩湢銆佺劍铏戞垨韬綋涓嶉€傛椂璇峰強鏃跺姹傚府鍔┿€?,'PUBLISHED');