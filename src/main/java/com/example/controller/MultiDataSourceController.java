package com.example.controller;

import com.example.entity.primary.User;
import com.example.entity.secondary.Product;
import com.example.repository.primary.UserRepository;
import com.example.repository.secondary.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/multi")
public class MultiDataSourceController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/stats")
    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 主数据源统计
        stats.put("userCount", userRepository.count());
        stats.put("users", userRepository.findAll());
        
        // 辅助数据源统计
        stats.put("productCount", productRepository.count());
        stats.put("products", productRepository.findAll());
        stats.put("averagePrice", productRepository.getAveragePrice());
        
        return stats;
    }

    @PostMapping("/create-sample-data")
    public Map<String, Object> createSampleData() {
        Map<String, Object> result = new HashMap<>();
        
        // 创建示例用户数据（主数据源）
        User user1 = new User("john_doe", "john@example.com", "John Doe");
        User user2 = new User("jane_smith", "jane@example.com", "Jane Smith");
        User user3 = new User("bob_wilson", "bob@test.com", "Bob Wilson");
        
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        
        // 创建示例产品数据（辅助数据源）
        Product product1 = new Product("笔记本电脑", "高性能游戏本", 8999.99, "电子产品");
        Product product2 = new Product("无线鼠标", "蓝牙无线鼠标", 199.99, "电子产品");
        Product product3 = new Product("机械键盘", "青轴机械键盘", 599.99, "电子产品");
        Product product4 = new Product("咖啡杯", "陶瓷咖啡杯", 39.99, "生活用品");
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
        
        result.put("message", "示例数据创建成功");
        result.put("userCount", userRepository.count());
        result.put("productCount", productRepository.count());
        
        return result;
    }

    @GetMapping("/users-by-domain/{domain}")
    public List<User> getUsersByDomain(@PathVariable String domain) {
        return userRepository.findByEmailDomain(domain);
    }

    @GetMapping("/products-by-category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productRepository.findByCategory(category);
    }

    @GetMapping("/expensive-products")
    public List<Product> getExpensiveProducts(@RequestParam(defaultValue = "500.0") Double minPrice) {
        return productRepository.findExpensiveProducts(minPrice);
    }

    @GetMapping("/category-distribution")
    public List<Object[]> getCategoryDistribution() {
        return productRepository.countProductsByCategory();
    }
}