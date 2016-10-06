package thinkInJava;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuda on 2016/10/5.
 */
public class Chapter15_SimpleHolder<T> {
    private Object obj;
    private  T obj2;
    public void set(Object obj){ this.obj = obj;}
    public Object get(){return obj;}
    public T get2(){return obj2;}
    public static void main(String[] args){
        Chapter15_SimpleHolder holder = new Chapter15_SimpleHolder();
        holder.set("Item");
        String s = (String) holder.get();  //   18:  checkcast       #8; //class java/lang/String

        Chapter15_SimpleHolder<String> holder2 = new Chapter15_SimpleHolder<String>();
        holder2.set("Item");
        String s2 =holder2.get2();  //   18:  checkcast       #8; //class java/lang/String f
    }
}
//  编译器将确保类型标签可以匹配泛型参数。
