package com.memory7734.rpc.master.proxy;

import com.memory7734.rpc.master.MasterFuture;


public interface IAsyncObjectProxy {
    public MasterFuture call(String funcName, Object... args);
}