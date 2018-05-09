package com.memory7734.rpc.master;


public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);

}
