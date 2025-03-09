package com.lwc.domain.exception;/**
* ClassName: ConditionException
* Description:数据层的共做就包括了数据类的封装，万事万物皆对象，异常类也是对象，所以也封装到dao模块里里面
 * 本文件封装了一个条件异常类
* @Author 林伟朝
* @Create 2024/10/9 20:40
*/
/**/

    //父类RuntimeException是非受检异常，也即运行时异常,这类异常可以不用事先在代码中捕获或抛出
public class ConditionException extends RuntimeException{
    private static final long serialVersionUID = 1L;//序列化UID,java对象的序列化反序列化传输时要用到

    private String code;//区别于父类，这个异常类多了一个属性，即状态码



    public ConditionException(String code, String name) {
        super(name);//所有的异常共有的属性，异常的描述信息，或者说是异常的名字
        this.code = code;//指定的异常状态码
    }


    public ConditionException(String name) {
        super(name);
        code= "500";//统一的异常状态码
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
