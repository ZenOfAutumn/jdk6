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
        //���ʺ��޸�private��Ա��
        Animal animal = new  Animal();
        try {
            //���쳣
            /*
            Field field= animal.getClass().getField("age");
            System.out.println(field.get(animal));
            */
            //
            /*

             */
            Field field = animal.getClass().getDeclaredField("age");//������������ȡfield��������˽�еĻ��ǹ��еġ�
            field.setAccessible(true); //�������Զ��� ����Ȩ����Ϊ accessible=true�൱��public
            System.out.println(field.get(animal));
            field.set(animal, 10);    //�޸����Զ����ֵ��
            System.out.println(animal.getAge()); //����animal��˽���� ֵ���޸��ˡ�
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
