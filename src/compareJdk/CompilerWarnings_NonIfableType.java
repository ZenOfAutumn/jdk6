package compareJdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuda on 2016/10/4.
 */
public class CompilerWarnings_NonIfableType {
    public static void main(String[] args){
        List l = new ArrayList<Number>();
        List<String> ls = l;       // unchecked warning
        l.add(0, new Integer(42)); // another unchecked warning
        String s = ls.get(0);      // ClassCastException is thrown
        //由于类型擦除，以上语句在编译期均不会报错，因为List<Number>, List<String>均被探险为List类型
        /*
        String s = ls.get(0);  在运行时会报错，因为类型信息在被擦除前，已经生成相应的二进制文件。
         */

    }

}
