package com.novel.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
public class DatasourceHealthConfig implements HealthIndicator {

    @Resource
    private HikariDataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            int activeConnections = dataSource.getHikariPoolMXBean().getActiveConnections();
            int idleConnections = dataSource.getHikariPoolMXBean().getIdleConnections();
            int totalConnections = dataSource.getHikariPoolMXBean().getTotalConnections();

            log.debug("连接池状态 - 活跃连接: {}, 空闲连接: {}, 总连接: {}",
                    activeConnections, idleConnections, totalConnections);

            if (activeConnections >= dataSource.getMaximumPoolSize() - 1) {
                log.warn("连接池接近饱和 - 活跃连接: {}, 最大连接: {}", activeConnections, dataSource.getMaximumPoolSize());
            }

            return Health.up()
                    .withDetail("activeConnections", activeConnections)
                    .withDetail("idleConnections", idleConnections)
                    .withDetail("totalConnections", totalConnections)
                    .withDetail("maxPoolSize", dataSource.getMaximumPoolSize())
                    .withDetail("minIdle", dataSource.getMinimumIdle())
                    .build();
        } catch (SQLException e) {
            log.error("数据库连接健康检查失败", e);
            return Health.down(e).build();
        }
    }
}