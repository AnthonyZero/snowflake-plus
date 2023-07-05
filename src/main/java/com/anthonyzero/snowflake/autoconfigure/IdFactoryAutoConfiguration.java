package com.anthonyzero.snowflake.autoconfigure;

import com.anthonyzero.snowflake.SnowflakeIdFactory;
import com.anthonyzero.snowflake.registrar.ClusterRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class IdFactoryAutoConfiguration {

    @Bean
    @ConditionalOnBean(ClusterRegistrar.class)
    public SnowflakeIdFactory snowflakeIdFactory(@NonNull ClusterRegistrar clusterRegistrar) {
        return new SnowflakeIdFactory(clusterRegistrar.cluster().getMachineId(),
                clusterRegistrar.cluster().getDataCenterId(),
                clusterRegistrar.cluster().getArg("epoch", 1609459200000L));
    }
}
