package com.memory7734.rpc.master;

public interface SlaveInfo {
    String getMaxMemory(String memory);

    String getUsedMemory(String memory);

    String getFreeMemory(String memory);

    String getProcessors(String processors);

}
