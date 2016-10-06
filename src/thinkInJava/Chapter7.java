package thinkInJava;

/**
 * Created by liuda on 2016/10/5.
 */
class Person{
        public String name;
        }
public class Chapter7 {
    public static void main(String[] args){
        Girl girl = new Girl();
        System.out.println(girl.getFirstName());
    }
    public static void finalParam(final Person person1, Person person2){
        person1.name = "fdf ";
       // person1 = new Person();
        person2 = new Person();
    }
}
/*
继承与初始化：
   1.基类的私有属性，子类无法访问到，但是子类继承了基类所有的属性，和方法。 （debug调试可知）
   2.在生成子类对象时，并没有生成基类对象，而是调用基类的构造方法（继承得到）
   3 初始化顺序： 基类静态域、导出类静态域， 基类实例变量，基类构造方法，导出类实例变量，导出类构造方法。
  4 一般基类的域是private的，只有基类的构造器才具有恰当的知识和权限来对自己的元素进行初始化，因此必须令所有构造器都得到调用。
 */
class Father{
    private String first_name;
    public Father(){
        this.first_name = "Liu";
    }
    public String getFirstName(){
        return first_name;
    }

}
class Girl extends Father {

}

