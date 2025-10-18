# JPA vs MyBatis 代码对比示例

## 1. 实体类定义

### JPA方式
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "email")
    private String email;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
    
    // getters, setters...
}
```

### MyBatis方式
```java
public class User {
    private Long id;
    private String username;
    private String email;
    private List<Order> orders;
    
    // getters, setters...
}
```

## 2. Repository/DAO层

### JPA方式
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 方法名自动生成SQL
    List<User> findByUsernameContaining(String username);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain%")
    List<User> findByEmailDomain(@Param("domain") String domain);
    
    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :id")
    int updateEmail(@Param("id") Long id, @Param("email") String email);
}
```

### MyBatis方式
```java
@Mapper
public interface UserMapper {
    List<User> findByUsernameContaining(String username);
    List<User> findByEmailDomain(String domain);
    int updateEmail(@Param("id") Long id, @Param("email") String email);
}
```

## 3. SQL映射文件

### JPA - 无需XML（注解方式）
```java
@Query("SELECT u FROM User u " +
       "LEFT JOIN FETCH u.orders o " +
       "WHERE u.id = :id")
User findUserWithOrders(@Param("id") Long id);
```

### MyBatis - XML映射文件
```xml
<!-- UserMapper.xml -->
<mapper namespace="com.example.mapper.UserMapper">
    <select id="findByUsernameContaining" resultType="User">
        SELECT * FROM users 
        WHERE username LIKE CONCAT('%', #{username}, '%')
    </select>
    
    <select id="findByEmailDomain" resultType="User">
        SELECT * FROM users 
        WHERE email LIKE CONCAT('%', #{domain})
    </select>
    
    <update id="updateEmail">
        UPDATE users 
        SET email = #{email} 
        WHERE id = #{id}
    </update>
    
    <select id="findUserWithOrders" resultMap="UserWithOrdersMap">
        SELECT u.id, u.username, u.email,
               o.id as order_id, o.order_date, o.total_amount
        FROM users u
        LEFT JOIN orders o ON u.id = o.user_id
        WHERE u.id = #{id}
    </select>
    
    <resultMap id="UserWithOrdersMap" type="User">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="email" column="email"/>
        <collection property="orders" ofType="Order">
            <id property="id" column="order_id"/>
            <result property="orderDate" column="order_date"/>
            <result property="totalAmount" column="total_amount"/>
        </collection>
    </resultMap>
</mapper>
```

## 4. 复杂查询对比

### 场景：查询用户及其订单统计

#### JPA方式
```java
@Query("SELECT u, COUNT(o) as orderCount, SUM(o.totalAmount) as totalAmount " +
       "FROM User u LEFT JOIN u.orders o " +
       "GROUP BY u.id, u.username, u.email")
List<Object[]> findUsersWithOrderStats();

// 或者使用DTO
@Query("SELECT new com.example.dto.UserOrderStatsDTO(" +
       "u.id, u.username, u.email, COUNT(o), SUM(o.totalAmount)) " +
       "FROM User u LEFT JOIN u.orders o " +
       "GROUP BY u.id, u.username, u.email")
List<UserOrderStatsDTO> findUsersWithOrderStatsDTO();
```

#### MyBatis方式
```xml
<select id="findUsersWithOrderStats" resultType="UserOrderStatsDTO">
    SELECT u.id, u.username, u.email,
           COUNT(o.id) as orderCount,
           COALESCE(SUM(o.total_amount), 0) as totalAmount
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    GROUP BY u.id, u.username, u.email
    ORDER BY totalAmount DESC
</select>
```

## 5. 性能优化对比

### JPA性能优化
```java
// 使用@BatchSize优化N+1问题
@Entity
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private List<Order> orders;
}

// 使用@NamedEntityGraph
@Entity
@NamedEntityGraph(
    name = "User.withOrders",
    attributeNodes = @NamedAttributeNode("orders")
)
public class User { ... }

// 查询时使用
@Query("SELECT u FROM User u")
@EntityGraph("User.withOrders")
List<User> findAllWithOrders();
```

### MyBatis性能优化
```xml
<!-- 使用延迟加载 -->
<resultMap id="UserMap" type="User">
    <collection property="orders" 
                select="selectOrdersByUserId" 
                column="id"
                fetchType="lazy"/>
</resultMap>

<!-- 使用缓存 -->
<cache eviction="LRU" flushInterval="60000" size="512" readOnly="true"/>

<!-- 批量操作 -->
<insert id="batchInsert" parameterType="java.util.List">
    INSERT INTO users (username, email) VALUES
    <foreach collection="list" item="user" separator=",">
        (#{user.username}, #{user.email})
    </foreach>
</insert>
```