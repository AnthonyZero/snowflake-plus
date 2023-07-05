package com.anthonyzero.snowflake.autoconfigure;

import com.anthonyzero.snowflake.registrar.ClusterRegistrar;
import com.anthonyzero.snowflake.registrar.ManualClusterRegistrar;
import com.anthonyzero.snowflake.registrar.RedisClusterRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

@Configuration
public class ClusterAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "snowflake-plus.cluster")
    public ClusterProperties clusterProperties() {
        return new ClusterProperties();
    }

    @Configuration
    @ConditionalOnClass(name = {"org.springframework.data.redis.core.StringRedisTemplate"})
    static class RedisRegistrar {

        //显示调用 保证注册后有值
        @Bean(initMethod = "start")
        @ConditionalOnProperty(prefix = "snowflake-plus.cluster", name = "registrar", havingValue = "redis")
        public ClusterRegistrar redisClusterRegistrar(ClusterProperties cluster,
                                                      StringRedisTemplate redisTemplate) {
            return new RedisClusterRegistrar(cluster, redisTemplate);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "snowflake-plus.cluster",
            name = "registrar",
            havingValue = "manual",
            matchIfMissing = true)
    static class ManualRegistrar {

        @Bean
        @ConditionalOnMissingBean(ClusterRegistrar.class)
        public ClusterRegistrar manualClusterRegistrar(@NonNull ClusterProperties cluster) {
            return new ManualClusterRegistrar(cluster);
        }
    }
}
