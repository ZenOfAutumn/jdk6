package RTTI_Reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/5.
 */
interface Interface {                          //接口
    void doSomething();
    void doSomethingElse(String args);
}
class RealObject implements Interface {        //实现类
    public void doSomething() {
        out.println("doSomething");
    }

    public void doSomethingElse(String arg) {
        out.println("somethingElse "+arg);
    }
}
public class DynamicProxyTest {
    public static void consumer(Interface iface){
        iface.doSomething();
        iface.doSomethingElse("bonobl");
    }
    public static void main(String[] args){
        //类加载器，接口， InvocationHandler
        RealObject real = new RealObject();
        Interface proxy = (Interface) Proxy.newProxyInstance(Interface.class.getClassLoader(),new Class[]{Interface.class},new DynamicProxyHandler(real));
        consumer(proxy);
    }
}

class DynamicProxyHandler implements InvocationHandler {      //代理
    private Object proxied;
    public DynamicProxyHandler(Object proxied){
        this.proxied = proxied;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        out.println("**** proxy:"+ proxy.getClass()+"  ,  mehod"+ method+",  args:"+args);
        return method.invoke(proxied,args);
    }
}