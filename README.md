# 简易图书管理系统 (Simple Library System)

一个基于 Spring Boot 3.5 + JPA + MySQL 的现代化图书管理系统，为项目管理实践课程开发。

## 📋 项目简介

本系统实现了图书馆日常管理的核心业务流程，包括用户管理、图书信息管理、借阅管理和图书捐赠等功能。系统采用前后端分离架构，提供 RESTful API 接口，支持管理员和读者两种角色。

### 主要特性

- 🔐 **用户认证与授权**：基于 Token 的身份验证，支持管理员和读者角色
- 📚 **图书信息管理**：图书的增删改查、分类管理、封面上传
- 📖 **借阅管理**：借书、还书、借阅记录查询，事务保证数据一致性
- 🎁 **图书捐赠**：读者可提交捐赠申请，管理员可处理捐赠流程
- 🔍 **多维度查询**：支持分页、模糊搜索、多条件筛选
- 📊 **数据统计**：用户数、图书数、借阅记录统计

## 🛠️ 技术栈

### 后端技术

- **框架**：Spring Boot 3.5.11
- **ORM**：Spring Data JPA
- **数据库**：MySQL 8.0
- **Java 版本**：JDK 21
- **构建工具**：Maven
- **其他依赖**：
  - Lombok（简化代码）
  - Spring Validation（参数校验）
  - H2 Database（测试环境）

### 架构设计

- **分层架构**：Controller - Service - Repository
- **RESTful API**：统一的接口设计规范
- **统一响应格式**：ApiResponse、PageResponse
- **拦截器**：Token 验证、管理员权限验证
- **全局异常处理**：统一的异常处理机制

## 📦 项目结构

```
Simple_Library_System/
├── doc/                          # 项目文档
│   ├── 需求规格说明书.md
│   ├── 后端接口文档实现.md
│   ├── 原型设计.md
│   ├── 项目范围说明.md
│   ├── commit-convention.md
│   ├── env.md
│   └── api-fox/                  # API 接口文档
├── src/
│   ├── main/
│   │   ├── java/edu/cupk/simple_library_system/
│   │   │   ├── common/           # 通用类（响应封装、异常处理）
│   │   │   ├── config/           # 配置类（拦截器、CORS）
│   │   │   ├── controller/       # 控制器层
│   │   │   ├── dto/              # 数据传输对象
│   │   │   ├── entity/           # 实体类
│   │   │   ├── repository/       # 数据访问层
│   │   │   ├── service/          # 业务逻辑层
│   │   │   └── util/             # 工具类
│   │   └── resources/
│   │       ├── application.properties  # 应用配置
│   │       └── static/           # 静态资源
│   └── test/                     # 测试代码
├── simple_library_system.sql     # 数据库初始化脚本
├── pom.xml                       # Maven 配置
└── README.md                     # 项目说明文档
```

## 🚀 快速开始

### 环境要求

- JDK 21 或更高版本
- MySQL 8.0 或更高版本
- Maven 3.6+

### 安装步骤

1. **克隆项目**

```bash
git clone https://github.com/gua7gua/Simple_Library_System.git
cd Simple_Library_System
```

2. **创建数据库**

```bash
mysql -u root -p
CREATE DATABASE simple_library_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **导入数据库脚本**

```bash
mysql -u root -p simple_library_system < simple_library_system.sql
```

4. **配置数据库连接**

编辑 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/simple_library_system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

5. **构建并运行项目**

```bash
# 使用 Maven 构建
mvn clean install

# 运行项目
mvn spring-boot:run
```

或者使用 Maven Wrapper：

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

6. **访问应用**

服务启动后，访问：`http://localhost:8092/BookManager`

## 📖 API 文档

### 基础信息

- **基础 URL**：`http://localhost:8092/BookManager`
- **数据格式**：JSON
- **字符编码**：UTF-8
- **认证方式**：Token（登录后获取）

### 主要接口模块

| 模块 | 路径前缀 | 接口数量 | 说明 |
|------|---------|---------|------|
| 用户管理 | `/user` | 13 | 登录、注册、用户信息管理 |
| 图书类型 | `/bookType` | 8 | 图书分类管理 |
| 图书信息 | `/bookInfo` | 7 | 图书增删改查 |
| 借阅管理 | `/borrow` | 10 | 借书、还书、记录查询 |
| 图书捐赠 | `/donation`、`/admin/donation` | 6 | 捐赠申请与管理 |
| 文件上传 | `/update` | 1 | 图书封面上传 |

详细的 API 文档请参考：[后端接口文档实现.md](doc/后端接口文档实现.md)

### 快速示例

**用户登录**

```bash
curl -X POST "http://localhost:8092/BookManager/user/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","userpassword":"admin123","isadmin":1}'
```

**查询图书**

```bash
curl "http://localhost:8092/BookManager/bookInfo/queryBookInfosByPage?page=1&limit=10&token=YOUR_TOKEN"
```

**借书**

```bash
curl "http://localhost:8092/BookManager/borrow/borrowBook?userid=1&bookid=5&token=YOUR_TOKEN"
```

## 🗄️ 数据库设计

### 核心数据表

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| `user` | 用户表 | userid, username, userpassword, isadmin |
| `book_type` | 图书类型表 | booktypeid, booktypename, booktypedesc |
| `book_info` | 图书信息表 | bookid, bookname, bookauthor, bookprice, booktypeid, isborrowed |
| `borrow` | 借阅记录表 | borrowid, userid, bookid, borrowtime, returntime |
| `book_donation` | 图书捐赠表 | donationid, donorname, donorphone, bookisbn, status |

### 实体关系

```
user ──┬─> borrow <─┬── book_info <── book_type
       │            │
       └─> 1:N ────┘
```

详细的数据库设计请参考：[需求规格说明书.md](doc/需求规格说明书.md)

## 👥 用户角色

### 管理员 (isadmin=1)

- 用户管理：添加、修改、删除用户
- 图书管理：添加、修改、删除图书和类型
- 借阅管理：查看所有借阅记录，手动添加/修改记录
- 捐赠管理：查看、处理捐赠申请，更新状态和备注
- 文件上传：上传图书封面

### 读者 (isadmin=0)

- 浏览图书：查询图书信息和类型
- 借阅图书：借书、还书
- 查看记录：查看自己的借阅记录
- 捐赠图书：提交捐赠申请，查询捐赠记录

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |

## 🔧 配置说明

### 应用配置 (application.properties)

```properties
# 服务端口
server.port=8092

# 应用上下文路径
server.servlet.context-path=/BookManager

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/simple_library_system
spring.datasource.username=root
spring.datasource.password=your_password

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Token 有效期（秒）
app.token.expire-seconds=3600

# 文件上传路径
app.upload.path=uploads/
```

## 🧪 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest
```

### 测试覆盖

项目包含以下测试：

- 单元测试：Service 层业务逻辑测试
- 集成测试：Controller 层接口测试
- 测试数据库：使用 H2 内存数据库

## 📝 开发规范

### Git 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

详细规范请参考：[commit-convention.md](doc/commit-convention.md)

### 代码规范

- 遵循 Java 编码规范
- 使用 Lombok 简化代码
- Controller 层只负责参数接收和响应
- Service 层实现业务逻辑
- Repository 层负责数据访问

## 🐛 常见问题

### 1. 数据库连接失败

**问题**：`Communications link failure`

**解决**：
- 检查 MySQL 服务是否启动
- 确认数据库连接配置正确
- 检查防火墙设置

### 2. Token 验证失败

**问题**：`Token无效或已过期`

**解决**：
- 重新登录获取新 Token
- 检查 Token 是否正确传递
- 确认 Token 未超过有效期（默认 1 小时）

### 3. 文件上传失败

**问题**：`文件大小超过限制`

**解决**：
- 检查文件大小是否超过 10MB
- 确认文件格式为 JPG 或 PNG
- 检查上传目录是否有写入权限

### 4. 端口被占用

**问题**：`Port 8092 is already in use`

**解决**：
- 修改 `application.properties` 中的 `server.port`
- 或者关闭占用 8092 端口的进程

## 📄 许可证

本项目仅用于教学和学习目的。

## 👨‍💻 贡献者

本项目由项目管理实践课程团队开发。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue：[GitHub Issues](https://github.com/gua7gua/Simple_Library_System/issues)
- Pull Request：欢迎提交改进建议

## 🙏 致谢

感谢所有为本项目做出贡献的团队成员。

---

**开发时间**：2026 年 3-5 月  
**课程**：项目管理实践  
**版本**：v1.0
