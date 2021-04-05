package com.zhenyi.remoting.test;


public class HelloServiceImpl implements HelloService 
{


    @Override
    public String sayHello(TestEntity somebody) 
    {
        return "hello " + somebody.test2 + "!";
    }


}
