package com.anthonyzero.snowflake.registrar;

import com.anthonyzero.snowflake.autoconfigure.ClusterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RedisClusterRegistrar extends AbstractClusterRegistrar {

    public static final String SNOW_DC_CACHE_KEY = "snowflake-plus:dc";
    public static final String SNOW_MACHINE_CACHE_KEY = "snowflake-plus:machine";
    private static final String randomInstanceId = UUID.randomUUID().toString();

    private final StringRedisTemplate redisTemplate;

    public RedisClusterRegistrar(ClusterProperties properties, StringRedisTemplate redisTemplate) {
        super(properties);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void register() {
        if (cluster.getDataCenterId() == null || cluster.getDataCenterId() < 0) {
            int dc = regDC();
            log.info("The sequence of current data center is {}", dc);
            cluster.setDataCenterId(dc);
        }
        if (cluster.getMachineId() == null || cluster.getMachineId() < 0) {
            int machine = regMachine(3);
            log.info("The sequence of current machine is {}", machine);
            cluster.setMachineId(machine);
        }
    }

    @Override
    public void unregister() {
        ZSetOperations<String, String> dcCache = redisTemplate.opsForZSet();
        HashOperations<String, String, String> machineCache = redisTemplate.opsForHash();
        String machineKey = getMachineKey(), instance = getInstanceName(),
                serviceName = cluster.getServiceName();
        try {
            for(Map.Entry<String,String> entry : machineCache.entries(machineKey).entrySet()) {
                if(entry.getValue().equals(instance)) {
                    machineCache.delete(machineKey, entry.getKey());
                    log.info("Unregister machine of instance '{}' successfully", instance);
                }
            }
        } catch (Exception ex) {
            log.error("Fail to unregister machine from redis", ex);
        }
        try {
            if (StringUtils.hasText(serviceName)) {
                Long size = machineCache.size(machineKey);
                if (Long.valueOf(0).equals(size)) { //全部pod 已stop
                    dcCache.remove(SNOW_DC_CACHE_KEY, serviceName);
                    log.info("Unregister datacenter of service '{}' successfully", serviceName);
                }
            }
        } catch (Exception ex) {
            log.error("Fail to unregister datacenter from redis", ex);
        }
    }

    private String getInstanceName() {
        return StringUtils.hasText(cluster.getInstanceName()) ?
                cluster.getInstanceName() :
                randomInstanceId;
    }

    @NonNull
    private String getMachineKey() {
        return cluster.getArgs().getOrDefault("machine-key", SNOW_MACHINE_CACHE_KEY) +
                ":" + cluster.getServiceName();
    }

    private String getDatacenterKey() {
        return cluster.getArgs().getOrDefault("dc-key", SNOW_DC_CACHE_KEY);
    }

    private int regDC() {
        try {
            String key = getDatacenterKey();
            ZSetOperations<String, String> cache = redisTemplate.opsForZSet();
            String name = cluster.getServiceName();
            if (!StringUtils.hasText(name)) return 0;
            double score = OffsetDateTime.now(Clock.systemUTC()).toEpochSecond() / Math.pow(10, 8);
            Boolean done = cache.addIfAbsent(key, name, score);
            if (Boolean.TRUE.equals(done)) {
                log.info("Register dc '{}' to redis successfully", name);
            }
            Long rank = cache.rank(key, name);
            if (rank != null) return (int) (rank % 32);
            return 0;
        } catch (Exception ex) {
            log.error("Fail to register data center from redis", ex);
        }
        return 0;
    }

    private int regMachine(int maxRetry) {
        if (maxRetry < 0)
            return ThreadLocalRandom.current().nextInt(0, 32);
        try {
            String key = getMachineKey(), instance = getInstanceName();
            HashOperations<String, String, String> cache = redisTemplate.opsForHash();
            for(int i = 0; i < 32; i++) {
                Boolean done = cache.putIfAbsent(key, String.valueOf(i), instance); //slot -> instanceName
                if(Boolean.TRUE.equals(done)) {
                    log.info("Register instance '{}' of machine '{}'  to redis successfully", instance, i);
                    return i;
                }
            }
            return regMachine(--maxRetry);
        } catch (Exception ex) {
            log.error("Fail to register machine from redis", ex);
        }
        return 0;
    }
}
