package thinkInJava;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuda on 2016/10/5.
 */
public class Chapter15 {
    public static void main(String[] args){
        GenericMethods gm = new GenericMethods();
        gm.f("");
        gm.f(1);
        GenericVarargs.test();
        ClassAsFactory.test();
    }
}
//泛型类
class Stack<T>{
    private static class Node<U>{
        U item;
        Node<U> next;
        Node(){ item = null ; next = null;}
        Node(U item, Node<U> next){
            this.item = item;
            this.next = next;
        }
        boolean end(){return item == null && next == null;}
    }
    private Node<T> top = new Node<T>();

}
//泛型方法
class GenericMethods{
    public <T> void f(T x){
        System.out.println(x.getClass().getName());
    }
}
//可变参数与泛型方法
class GenericVarargs{
    public static <T> List<T> makeList(T... args){
        List<T> result = new ArrayList<T>();
        for(T item: args){
            result.add(item);
        }
        return result;
    }
    public static void test(){
        List<String> ls = makeList("A");
        System.out.println(ls);
        ls = makeList("A","B","C");
        System.out.println(ls);
        ls = makeList("ABCDEFGLI".split(""));
        System.out.println(ls);
    }
}
//创建泛型实例
class ClassAsFactory<T>{
    T x;
    /*
    public void createInstace(){
        x = new T();
    }
    */
    public ClassAsFactory(Class<T> kind){
        try{
            x = kind.newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public static void test(){
        ClassAsFactory<Cat> cat = new ClassAsFactory<Cat>(Cat.class);
        System.out.println("ClassAsFactory<Cat>  succeeded");
        try {
            ClassAsFactory<Integer> fi = new ClassAsFactory<Integer>(Integer.class); //可以编译，但创建失败，因为Integer没有默认的构造器。 解决：显式工厂
        }catch (Exception e){
            System.out.println(" ClassAsFactory<Integer> faield");
        }
    }
}