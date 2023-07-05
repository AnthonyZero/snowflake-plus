package com.anthonyzero.snowflake.registrar;

import com.anthonyzero.snowflake.autoconfigure.ClusterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public abstract class AbstractClusterRegistrar implements ClusterRegistrar {
    protected final ClusterProperties cluster;

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public boolean isLeader() {
        int id = cluster.getMachineId() == null ? 0 : cluster.getMachineId();
        return id == 0;
    }

    @NonNull
    @Override
    public ClusterProperties cluster() {
        return cluster;
    }

    @Override
    public void stop() {
        unregister();
        running.set(false);
    }

    @Override
    public void start() {
        // 保证初始化一次
        if (!running.compareAndSet(false, true)) {
            return;
        }
        register();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}
