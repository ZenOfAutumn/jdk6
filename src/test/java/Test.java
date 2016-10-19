import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liudan19 on 2016/10/19.
 */
public class Test {
    public static void main(String[] args){
        HashMap<Object, Object> map = new HashMap<Object,Object>();
        for(Map.Entry<Object,Object> entry : map.entrySet()){
            map.remove(entry.getKey());
        }

    }
}
