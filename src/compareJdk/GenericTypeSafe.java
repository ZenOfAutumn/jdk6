package compareJdk;

import java.util.*;
import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/2.
 */
/*
 泛型
   泛型通过类型擦除实现。类型信息只在编译期有，之后被擦除。
   优点： 泛型与原型类型可共同使用；
   缺点：运行期不能获得类型信息，无法保证类型安全。 示例test1();
   解决方法： test2
   java.util.Conllections类通过包装类，可以满足运行期类型安全。在debugging时这些“受检查的集合包装器”非常有用。例如，
一个字符串集合，但被插入一个integer类型。
   如果要保证类型安全可以将：
     Set<String> s = new HashSet<String>();
   替换为
     Set<String> s = Collections.checkSet(new HashSet<String>(),String.class);
   当试图插入一个integer类型时，该集合会抛出一个ClassCastException
 */
public class GenericTypeSafe {


        public static void main(String args[]){
            Set<String> ss = new HashSet<String>();
            ss.add("am");
            //ss.add(22);                    //报错，因为类型检查存在。  ss是具体类型。
            test1(ss);
            Set<String> sx = Collections.checkedSet(new HashSet<String>(),String.class);
            sx.add("bm");
            test2(sx);

            Set sy = new HashSet<String>();
            sy.add("bm");
            sy.add(22);                       //不报错，因为sy声明时即编译期类型被擦除。sy是泛化类型。
        }
        public static void test1(Set ss){
            ss.add(11);                    //不报错，因为传参时，类型信息被擦除。ss是泛化类型。
            for(Object ob : ss){
                out.println(ob);
            }
        }
        public static void test2(Set sx){
            sx.add(11);
            for(Object ob : sx){
                out.println(ob);
            }
        }


}
