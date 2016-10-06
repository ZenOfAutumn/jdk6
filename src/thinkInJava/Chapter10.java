package thinkInJava;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/5.
 */
//内部类
class OuterClass{
    public void f(){}
    public class InnerClass{
        private String key;
        private String value;
        //private static String other; 报错：内部类不能有static字段和方法
        public void h(){
            f();                   //内部类无论被嵌套多少层，均可访问外部类所有成员
        }
    }
}
//嵌套类
class OuterClass2{
    public void f(){}
    public static void e(){}
    public static class NestClass{
        private String key;
        private String value;
        private static String other;  //正确，嵌套类可以拥有static字段和方法
        public void h(){
           // f();                   //嵌套类独立于外部类，无法访问外部非静态成员。
            e();
        }
    }
}
public class Chapter10 {
    public  static void main(String[] args){
        Callbacks.test();
    }
    public static void InnerClass_and_NestClass(){
        OuterClass outer  = new OuterClass();
        OuterClass.InnerClass  inner = outer.new InnerClass();       //内部类对象的创建方式
        // OuterClass.InnerClass  inner2 = new OuterClass.InnerClass();

        OuterClass2 outer2 = new OuterClass2();
        OuterClass2.NestClass nest = new OuterClass2.NestClass();    //嵌套类对象的创建方式
        // OuterClass2.NestClass nest2 = outer2.new NestClass();
    }
}
//闭包与回调
interface  Incrementable {
    void increment();
}
class MyIncrement{
    public void increment(){ out.println("Other operation");}
    static void f(MyIncrement mi){ mi.increment();}
}
class Callee2 extends  MyIncrement{
    private  int i = 0;
    public void increment(){
        super.increment();
        i++;
        out.println(i);
    }
    private  class  Closure implements  Incrementable{
        public  void  increment(){
            Callee2.this.increment();                  //通过.this访问外围对象,  钩子
        }
    }
    Incrementable getCallbackReference(){              //由于内部类Closure为private， 故需由外围类创建内部类对象。
        return new Closure();
    }
}
class Caller{
    private  Incrementable callbackReference ;
    Caller(Incrementable cbn){callbackReference = cbn;} //Caller的构造器需要一个Incremetable的引用作为参数， 然后在以后的某个时刻，Caller对象可以使用此引用回调Caller类。
    void go(){callbackReference.increment();}
}
class Callbacks{
    public static void test(){
        Callee2 c2 =  new Callee2();
        MyIncrement.f(c2);             //将导出类对象传入基类方法 -回调方式一
        Caller caller1 = new Caller(c2.getCallbackReference());
        caller1.go();
        caller1.go();

    }
}
/*
由于Callee2继承了MyIncrement,后者有increment方法的实现，并且与Incremetable接口期望的increment()完全不相关。
故Callee2继承了MyIncrement,就不能为了Incrementable的用途而覆盖increment()方法，于是只能合适内部类独立地实现Incrementable。
注： 创建一个内部类时，并没有在外围类的接口添加东西，也没有修改外围类的接口。
这里展示 了： interface是如何允许接口与接口的实现完全独立的。
回调价值： 可以在运行时动态地决定 需要调用什么方法。
 */
