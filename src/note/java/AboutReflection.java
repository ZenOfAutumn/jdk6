import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by liuda on 2016/10/22.
 */
class Animal{
    public String name = "cat";
    private int age = 6;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
public class AboutReflection {
    public static void main(String[] args){
        //访问和修改private成员：
        Animal animal = new  Animal();
        try {
            //抛异常
            /*
            Field field= animal.getClass().getField("age");
            System.out.println(field.get(animal));
            */
            //
            /*

             */
            Field field = animal.getClass().getDeclaredField("age");//根据属性名获取field，不管是私有的还是公有的。
            field.setAccessible(true); //将该属性对象 访问权设置为 accessible=true相当于public
            System.out.println(field.get(animal));
            field.set(animal, 10);    //修改属性对象的值。
            System.out.println(animal.getAge()); //对象animal的私有域 值被修改了。
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
