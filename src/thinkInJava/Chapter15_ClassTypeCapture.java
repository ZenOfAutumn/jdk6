package thinkInJava;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuda on 2016/10/6.
 */
public class Chapter15_ClassTypeCapture {
    public static void main(String[] args){
        ClassTypeCapture.test();
        NonCovariantGenerics.test2();
    }
}
//擦除的补偿
class ClassTypeCapture<T>{
    Class<T> kind;
    public ClassTypeCapture(Class<T> kind){
        this.kind = kind;
    }
    public boolean f(Object arg){
        return kind.isInstance(arg);
    }
    public static void test(){
        ClassTypeCapture<Cat> ctt1 = new ClassTypeCapture<Cat>(Cat.class);
        System.out.println(ctt1.f(new Cat()));
        System.out.println(ctt1.f(new Dog()));
    }
}

//通配符
class Fruit{
}
class Apple extends  Fruit{

}
class NonCovariantGenerics{
   // List<Fruit> flist = new ArrayList<Apple>();
   // 编译错误，这根本不是向上转型。 apple的List不是Fruit的List， Apple的List持有任何Apple和Apple的子类型，而Fruit的List持有任何类型的Fruit,f真正问题在于：容器的类型，而不是容器持有的类型

    //解决方法： 通配符在两个类型之间建立某种类型的向上转型关系
    List<? extends  Fruit> flist = new ArrayList<Apple>();
    /*现在，flist的类型是List<? extends Fruit> 即“具有任何从Fruit继承来的类型的列表”，这并不意味着这个List将持有任何类型的Fruit。
       通配符引用的是明确的类型，因此mean"某种flist引用没有指定的具体类型“
     */
    public void test(){
        //flist.add(new Apple());//无法增加任何类型的对象
        flist.add(null);
        Fruit f = flist.get(0);
    }
    /*<? extend Fruit> 声明的类型，可以get,不可以set，因为它可以是任何事物，但编译器无法验证任何事物的安全性
    思考 ：public ArrayList(Collection<? extends E> c) 为何这样设计
    */


    //超类型边界放松了可以向方法传递的参数上所作的限制
    List<? super Fruit> flist2  = new ArrayList<Fruit>();

    static List<Apple> apples = new ArrayList<Apple>();
    static List<Fruit> fruit = new ArrayList<Fruit>();
    static <T> void writeWithWildCard(List<? super T> list,T item){
        list.add(item);

    }
    static <T> void writeWithWildCard2(List<T> list,T item){
        list.add(item);

    }
     static <T> T readExact(List<T> list){
         return list.get(0);
     }
    public static void test2(){
        writeWithWildCard(apples,new Apple());
        writeWithWildCard(fruit, new Apple());

        writeWithWildCard2(apples, new Apple());
        writeWithWildCard2(fruit, new Apple());

        //apples.add(new Fruit());
        Fruit fruit = readExact(apples);
    }
/*
List<T> 无通配符，有精确类型： 可以写入和读取这个类型
List<? extend T>:有通配符， 只能读取，不可写入。
 */
    /*
    使用<? extend T>的一个场景：
    List<Apple>类型 可以 放入Fruit对象。
     */
}
class Holder<T>{
    T item;
    public void set(T item ){this.item = item;}
    public T get(){return this.item;}
}
//无界通配符
class Wildcards{
    static void rawArgs(Holder holder, Object arg){
        holder.set(arg); //由于它是原生类型，编译器知道向set()传递一个Object是不安全的，t可以将任何类型的对象传递给set,而这个对象将向上转型为Objec
        Object obj =holder.get();
    }
    static void rawArg2s(Holder<?> holder, Object arg){
        // holder.set(arg);  报错， Holder<?>将具有某种具体类型的同构集合，因此不能只是向其中传递Object
        Object obj =holder.get();

    }
    static <T> T exact(Holder<T> holder){
        T t = holder.get();
        return t;
    }
    static <T> T exact2(Holder<T> holder, T arg){
        holder.set(arg);
        T t = holder.get();
        return t;
    }
    /*
     //放松，持有扩展自T对象的Holder, mean: 若T为Fruit, holder可以是Holder<Apple>
     为了防止将Orange放置到Holder<Apple>中，对set（）的调用是不允许的。  但至少知道，来自Holder<? extend Fruit>的对象至少是Fruit，因此get是允许的
     */
    static <T> T wildSubtype(Holder<? extends  T> holder, T arg){
       // holder.set(arg); error    set(capture of ? extends T ) cann't be applied to (T)
        return holder.get();

    }
    //与前者相反的行为
    /*
    holder可以是持有任何T的基类型的容器，因此，set()可以接受T,因为任何可工作于基类的对象都可以多态地作用于导出类，但是
    调用get()是没有用的，因此由Holder持有的精英可以是任何超类型， 因此唯一安全的类型就是Object
     */
    static <T> void wildSupertype(Holder<? super T> holder, T arg){
        holder.set(arg); //error
      // T t = holder.get(); 类型不兼容, 返回的是Object类型，而t是T类型

    }
}

//参数 协变
class Basea{
}
class Drived extends Basea{

}
interface  OrdinaryGetter{
    Basea get();
}
interface DerivedGetter extends  OrdinaryGetter{
    Drived get();
}
/*
OrdinaryGetter的导出类DerviedGetter，覆盖方法get(),返回类型是Baseda的导出类
 */
// 自限定泛型: 将产生确切的导出类作为其反回值
interface  GenericGetter<T extends  GenericGetter<T>>{
    T get();
}
interface  Getter extends  GenericGetter<Getter>{

}
class GenericsAndReturnTypes{
    void test(Getter g, DerivedGetter d){
        Getter result =  g.get();
        GenericGetter gg = g.get();
        Drived drived  = d.get();
    }
}