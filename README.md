# campus-health-backend

校园健康问诊与咨询系统后端，采用 SpringBoot 3 + MyBatis 架构，提供学生、医生、管理员三类角色所需 REST API。

## 核心功能

- 认证：学生、医生、管理员角色登录。
- AI 问诊：学生提交症状，后端调用大语言模型接口生成初步健康建议，并保存问诊记录。
- 预约管理：学生提交校医院预约，医生查看并更新预约状态。
- 医生工作台：医生查看问诊记录并填写补充建议。
- 科普管理：学生查看疾病科普，管理员查看和维护文章数据。
- 管理员后台：用户、医生、科普文章基础数据管理。

## 技术栈

- SpringBoot 3.3.5
- MyBatis 3.0.3
- H2 本地演示数据库
- MySQL 生产数据库
- OpenAI 兼容 Chat Completions 大语言模型接口

## 本地运行

需要 JDK 17 和 Maven。

```powershell
cd D:\实训\项目源码\campus-health-backend
mvn spring-boot:run
```

默认启动地址：`http://localhost:8080`。

健康检查：`GET http://localhost:8080/api/health`。

## 演示账号

- 学生：student / 123456
- 医生：doctor / 123456
- 管理员：admin / 123456

## 数据库

默认使用 H2 内存数据库，启动时自动执行：

`src/main/resources/db/schema.sql`

生产环境启用 `prod` profile 后连接 MySQL：

```powershell
$env:SPRING_PROFILES_ACTIVE='prod'
$env:MYSQLHOST='你的数据库地址'
$env:MYSQLPORT='3306'
$env:MYSQLDATABASE='campus_health'
$env:MYSQLUSER='用户名'
$env:MYSQLPASSWORD='密码'
mvn spring-boot:run
```

## 大语言模型配置

后端 AI 服务使用 OpenAI 兼容 Chat Completions 请求格式。可配置：

```powershell
$env:AI_API_KEY='你的模型密钥'
$env:AI_API_ENDPOINT='https://api.openai.com/v1/chat/completions'
$env:AI_MODEL='gpt-4o-mini'
```

未配置接口或调用失败时，系统会自动使用本地安全兜底建议，保证实训演示流程可用。

## 主要接口

- `POST /api/auth/login`
- `POST /api/consultations`
- `GET /api/consultations/my`
- `GET /api/consultations`
- `PUT /api/consultations/{id}/reply`
- `POST /api/appointments`
- `GET /api/appointments/my`
- `GET /api/appointments/doctor`
- `PUT /api/appointments/{id}/status`
- `GET /api/articles`
- `GET /api/admin/users`
- `GET /api/admin/doctors`
- `GET /api/admin/articles`

## Railway 启动配置

项目包含 `Procfile`：

```text
web: java -jar target/*.jar
```

Railway 构建 Maven 项目后会使用该命令启动 SpringBoot 后端。

项目同时包含 `railway.json`，显式配置 Railway 使用 Nixpacks 构建、`mvn -DskipTests package` 打包、`java -jar target/*.jar` 启动，并使用 `/api/health` 作为健康检查路径。

## Docker / Railway 容器构建

项目包含 `Dockerfile`，可使用 Maven 17 镜像构建 jar，再用 JRE 17 运行。Railway 如果检测到 Dockerfile，可以按容器方式部署，降低本地没有 Maven/JDK 时的部署风险。

```powershell
docker build -t campus-health-backend .
docker run -p 8080:8080 campus-health-backend
```
