package compareJdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuda on 2016/10/4.
 */
public class TypeInference {
    public static void main(String[] args){
       // List<String> stringList = new ArrayList<>();
        //stringList.add("A");
        //stringList.addAll(Arrays.asList()); jdk6,jdk7报错，原因：stringList为具体类型，Arrays.asList()类型被擦除. jdk8支持：原因类型推断增强
       // stringList.addAll(Arrays.<String>.asList()); jdk7的使用方式

    }
}
