# Spring Boot 多数据源配置示例

这个项目演示了如何在Spring Boot应用中配置和使用多个数据源。

## 项目结构

```
src/main/java/com/example/
├── MultiDataSourceApplication.java          # 主应用类
├── config/
│   ├── PrimaryDataSourceConfig.java        # 主数据源配置 (MySQL)
│   └── SecondaryDataSourceConfig.java      # 辅助数据源配置 (H2)
├── entity/
│   ├── primary/
│   │   └── User.java                       # 主数据源实体类
│   └── secondary/
│       └── Product.java                    # 辅助数据源实体类
├── repository/
│   ├── primary/
│   │   └── UserRepository.java             # 主数据源Repository
│   └── secondary/
│       └── ProductRepository.java          # 辅助数据源Repository
└── controller/
    ├── UserController.java                 # 用户API控制器
    ├── ProductController.java              # 产品API控制器
    └── MultiDataSourceController.java      # 多数据源综合控制器
```

## 数据源配置

### 主数据源 (MySQL)
- 数据库：`primary_db`
- 实体包：`com.example.entity.primary`
- Repository包：`com.example.repository.primary`

### 辅助数据源 (H2)
- 数据库：内存数据库 `secondary_db`
- 实体包：`com.example.entity.secondary`
- Repository包：`com.example.repository.secondary`

## 配置说明

### 1. 数据源配置类
- `PrimaryDataSourceConfig`: 配置主数据源，使用`@Primary`注解
- `SecondaryDataSourceConfig`: 配置辅助数据源

### 2. 关键配置点
- 使用`@EnableJpaRepositories`指定不同的Repository包
- 为每个数据源配置独立的EntityManagerFactory
- 为每个数据源配置独立的事务管理器

### 3. 配置文件
在`application.yml`中配置两个数据源的连接信息：
- 主数据源：MySQL数据库
- 辅助数据源：H2内存数据库

## 运行项目

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 访问H2控制台
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:secondary_db`
- 用户名: `sa`
- 密码: (空)

## API接口

### 用户相关接口 (主数据源)
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 根据ID获取用户
- `GET /api/users/username/{username}` - 根据用户名获取用户
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 产品相关接口 (辅助数据源)
- `GET /api/products` - 获取所有产品
- `GET /api/products/{id}` - 根据ID获取产品
- `GET /api/products/category/{category}` - 根据分类获取产品
- `POST /api/products` - 创建产品
- `PUT /api/products/{id}` - 更新产品
- `DELETE /api/products/{id}` - 删除产品

### 多数据源综合接口
- `GET /api/multi/stats` - 获取两个数据源的统计信息
- `POST /api/multi/create-sample-data` - 创建示例数据
- `GET /api/multi/users-by-domain/{domain}` - 根据邮箱域名查询用户
- `GET /api/multi/products-by-category/{category}` - 根据分类查询产品

## 测试示例

### 1. 创建示例数据
```bash
curl -X POST http://localhost:8080/api/multi/create-sample-data
```

### 2. 查询用户
```bash
curl http://localhost:8080/api/users
```

### 3. 查询产品
```bash
curl http://localhost:8080/api/products
```

### 4. 获取统计信息
```bash
curl http://localhost:8080/api/multi/stats
```

## 注意事项

1. **事务管理**: 每个数据源都有独立的事务管理器，跨数据源操作需要特别注意事务边界
2. **数据源隔离**: 不同数据源的实体类和Repository必须放在不同的包中
3. **配置优先级**: 主数据源使用`@Primary`注解，避免自动配置冲突
4. **连接池配置**: 为每个数据源配置了独立的HikariCP连接池参数

## 扩展说明

如果需要添加更多数据源，可以按照相同的模式：
1. 创建新的数据源配置类
2. 创建对应的实体类和Repository
3. 在配置文件中添加数据源配置
4. 确保包结构清晰分离