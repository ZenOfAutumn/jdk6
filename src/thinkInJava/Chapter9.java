package thinkInJava;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/5.
 */

//  需要生成对外部类对象的引用：.this
class DotThis{
    DotThis(){}
    public void f(){out.println("DotThis.f()");}
    public class Inner{
        Inner(){}
        public DotThis outer(){
            return DotThis.this;
        }
    }
    public Inner inner(){return new Inner();}
}
//告知某些对象，去创建其某个内部类的对象,必须 在new静态式中提供对其外部类的引用：.new
public class Chapter9 {
    public static void main(String[] args){
        anonymouseClass();

    }
    public static void innerClass(){
        DotThis dt = new DotThis();
        DotThis.Inner dti = dt.inner();
        DotThis.Inner dti2 = dt.new Inner();
        dti.outer().f();
        dti2.outer().f();
    }
    public static void anonymouseClass(){
        Base base = AnonymousConstructor.getBase(47);
        base.f();
    }
}

abstract  class Base{
    public Base(int i){
        out.println("Base contructor,i = " + i);
    }
    public abstract void f();
}
class AnonymousConstructor{
    public static Base getBase(int i){
        return new Base(i){
            {out.println("Inside instance initizlizer");} //构造器，注：匿名类不可能命名构造器，因为根本没有名字
            public  void f(){
                out.println("In anonymous f()");
            }
        };
    }
}