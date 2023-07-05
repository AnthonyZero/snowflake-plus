package com.anthonyzero.snowflake.autoconfigure;

import lombok.Data;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ClusterProperties implements EnvironmentAware {

    private final static String APP_ID = "app.id";
    private final static String APP_NAME = "app-settings.name";
    private final static String FULL_NAME = "spring.application.name";
    /**
     * 其它参数
     */
    private final Map<String, String> args = new LinkedHashMap<>();
    /**
     * 服务名称（数据中心）
     */
    private String serviceName;
    /**
     * 实例名称
     */
    private String instanceName;
    /**
     * 数据中心ID（0-31）
     */
    private Integer dataCenterId;
    /**
     * 机器码（0-31）
     */
    private Integer machineId;
    /**
     * 集群注册类型
     */
    private RegisterType registrar = RegisterType.MANUAL;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        setDefaultServiceName(environment);
    }

    public <T> T getArg(String name) {
        return (T) args.get(name);
    }

    public <T> T getArg(String name, T defaultArg) {
        Object arg = args.get(name);
        return arg == null ? defaultArg : (T) arg;
    }

    private void setDefaultServiceName(@NonNull Environment env) {
        String name = env.getProperty(APP_ID, env.getProperty(APP_NAME, env.getProperty(FULL_NAME, "")));
        if (!StringUtils.hasText(getServiceName())) {
            setServiceName(name);
        }
    }
}
