package com.anthonyzero.snowflake.registrar;

import com.anthonyzero.snowflake.autoconfigure.ClusterProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.NonNull;

public interface ClusterRegistrar extends SmartLifecycle {

    boolean isLeader();

    @NonNull
    ClusterProperties cluster();

    void register();

    void unregister();
}
