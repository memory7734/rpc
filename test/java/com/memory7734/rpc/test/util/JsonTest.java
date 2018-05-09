package com.memory7734.rpc.test.util;

import com.memory7734.rpc.test.master.Person;
import com.memory7734.rpc.test.slave.HelloServiceImpl;
import com.memory7734.protocol.JsonUtil;
import com.memory7734.protocol.Request;
import com.memory7734.protocol.Response;
import com.memory7734.protocol.SerializationUtil;

import java.util.UUID;

/**
 * Created by jsc on 2016-03-10.
 */
public class JsonTest {
    public static void main(String[] args){
        Response response = new Response();
        response.setRequestId(UUID.randomUUID().toString());
        response.setError("Error msg");
        System.out.println(response.getRequestId());

        byte[] datas = JsonUtil.serialize(response);
        System.out.println("Json byte length: " + datas.length);

        byte[] datas2 = SerializationUtil.serialize(response);
        System.out.println("Protobuf byte length: " + datas2.length);

        Response resp = (Response)JsonUtil.deserialize(datas,Response.class);
        System.out.println(resp.getRequestId());
    }


    private static void TestJsonSerialize(){
        Request request = new Request();
        request.setClassName(HelloServiceImpl.class.getName());
        request.setMethodName(HelloServiceImpl.class.getDeclaredMethods()[0].getName());
        Person person = new Person("lu","xiaoxun");
        request.setParameters(new Object[]{person});
        request.setRequestId(UUID.randomUUID().toString());
        System.out.println(request.getRequestId());

        byte[] datas = JsonUtil.serialize(request);
        System.out.println("Json byte length: " + datas.length);

        byte[] datas2 = SerializationUtil.serialize(request);
        System.out.println("Protobuf byte length: " + datas2.length);

        Request req = (Request)JsonUtil.deserialize(datas,Request.class);
        System.out.println(req.getRequestId());
    }

}
