package compareJdk;

/**
 * Created by liuda on 2016/10/4.
 */
import static java.lang.System.out;
public class UseEnumTypeSafe {
    public static void main(String[] args){
        for(EnumTypeSafe e : EnumTypeSafe.values()){
            out.printf("this is %s , and its degress is %d, name is %s%n",e,e.degree(),e.season());
        }
    }
}

// System.out.printf(）可格式化输出语句，代替 + 符号打印，可读性更好。