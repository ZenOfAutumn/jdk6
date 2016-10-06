package thinkInJava;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/5.
 */
class Pet {

}

class Cat extends Pet {
    public Cat(){}
    @Override
    public String toString(){
        return "cat";
    }
}

class Dog extends Pet {

}

public class Chapter14 {
    //类字面常量
    public static final List<Class<? extends Pet>> allTypes = Collections.unmodifiableList(Arrays.asList(Pet.class, Dog.class, Cat.class));
    private static final List<Class<? extends Pet>> types = allTypes.subList(allTypes.indexOf(Dog.class), allTypes.size());
    public static void classInfo(){
        System.out.println(types);
        System.out.println(String.class);

        Class c = null;
        try {
            c = Class.forName("thinkInJava.Cat");  //产生Cat对应的Class对象
        }catch (ClassNotFoundException  e){
            e.printStackTrace();
        }
    }
    public static void reflectionByClass(){
        Class<?> c = null;
        try {
            c = Class.forName("thinkInJava.Cat");  //产生Cat对应的Class对象
            Method[] methods = c.getMethods();       //Method对象
            Constructor[] ctors = c.getConstructors(); //Constructor对象
            for(Method method :methods){
                out.println(method.toString());
            }
            for(Constructor ctor : ctors){
                out.println(ctor.toString());
            }
        }catch (ClassNotFoundException  e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

    }
}
