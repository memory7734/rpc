package com.memory7734.rpc.slave;

import com.memory7734.rpc.master.SlaveInfo;

@SlaveService(SlaveInfo.class)
public class SlaveInfoImpl implements SlaveInfo {

    public SlaveInfoImpl() {
    }

    @Override
    public String getMaxMemory(String memory) {
        Runtime context = Runtime.getRuntime();
        return String.format("%.2f", 1.0 * context.totalMemory() / 1024 / 1024 / 1024) + "GB";
    }

    @Override
    public String getUsedMemory(String memory) {
        Runtime context = Runtime.getRuntime();
        return String.format("%.2f", 100.0 * context.totalMemory() / context.maxMemory()) + "%";
    }

    @Override
    public String getFreeMemory(String memory) {
        Runtime context = Runtime.getRuntime();
        return String.format("%.2f", 100 - 100.0 * context.totalMemory() / context.maxMemory()) + "%";
    }

    @Override
    public String getProcessors(String processors) {
        Runtime context = Runtime.getRuntime();
        return String.valueOf(context.availableProcessors());
    }
}
