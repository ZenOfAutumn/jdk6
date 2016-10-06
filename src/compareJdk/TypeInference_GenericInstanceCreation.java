package compareJdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuda on 2016/10/3.
 */
public class TypeInference_GenericInstanceCreation {
    public static void main(){
        Map<String,List<String>> myMap = new HashMap<String,List<String>>();

        //Map<String,List<String>> myMap1 = new HashMap<>();//java6±àÒë±¨´í,java7Í¨¹ı

    }
}
