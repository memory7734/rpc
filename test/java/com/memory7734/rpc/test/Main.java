package com.memory7734.rpc.test;

import com.memory7734.rpc.master.SlaveInfo;

public class Main {
    public static void main(String[] args) {
        try {
            Class clazz = Class.forName("com.memory7734.rpc.master.SlaveInfo");
            System.out.println(clazz);
            System.out.println(SlaveInfo.class == clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
