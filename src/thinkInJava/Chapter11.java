package thinkInJava;

import java.util.*;
import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/5.
 */
public class Chapter11 {
    public static void main(String[] args){
        printCollection();
    }
    /* 将多个数据转为List
        new ArrayLIst<Integer>(Arrays.adList((2,3,4,5))
     */
    public static void collectionsAddAll(){
        Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6));
        Integer[] moreInts = {7,8,9,10};
        collection.addAll(Arrays.asList(moreInts));
        Collections.addAll(collection, 11, 12, 13, 14, 15);
    }
    /*
将数组、或多个数据转为List: Arrays.asList(T... a)
new ArrayLIst<Integer>(Arrays.asList((2,3,4,5))
new ArrayLIst<Integer>(Arrays.asList(Integer[])
添加一组元素
Collections.addAll(Collection<? super T> c, T... elements)//工具类Collections的静态方法
Collection.addAll(Collection<? extends E> c); //Collection实例方法

两种方式比较：
    1.Collection.addAll为首选，可构建一个不包含元素的Collection，再用该方法添加元素。
     2.       使用Array.asList（）的输出作为List,exp: List<Integer> list1 = Arrays.adList(15,5,6,7);
            不利之处在于，其底层表示为数组，无法调整尺寸，试图用add,delete对列表增加元素时，就有可能会引发改变数组的尺寸。


 */
    public static void printCollection(){
        Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6));
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("11","q1");
        map.put("12","e1");
        out.println(collection);
        out.println(map);
    }
    public static void queue(){
        Queue<Integer> queue = new LinkedList<Integer>();
    }
    public static void prioryQueue(){
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<Integer>();
    }
}
