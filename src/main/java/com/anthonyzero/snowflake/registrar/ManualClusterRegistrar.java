package com.anthonyzero.snowflake.registrar;

import com.anthonyzero.snowflake.autoconfigure.ClusterProperties;

public class ManualClusterRegistrar extends AbstractClusterRegistrar {

    public ManualClusterRegistrar(ClusterProperties cluster) {
        super(cluster);
        if (cluster.getDataCenterId() == null || cluster.getDataCenterId() < 0) {
            cluster.setDataCenterId(0);
        }
        if (cluster.getMachineId() == null || cluster.getMachineId() < 0) {
            cluster.setMachineId(0);
        }
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }
}
