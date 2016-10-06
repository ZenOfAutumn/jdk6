package compareJdk;

/**
 * Created by liuda on 2016/10/4.
 */
import static  java.lang.System.out;
public class VarArgs {
    public static void main(String[] args){
        varArgs("aa","bb","cc");
        String strArray[] = {"dd","ee","ff"};
        varArgs(strArray);
    }
    public static void varArgs(String... args){
        for(String s :args){
            out.println(s);
        }
    }
}
