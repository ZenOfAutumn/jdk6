package thinkInJava;

import java.util.Arrays;
import java.util.Comparator;

import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/6.
 */
public class Chapter16 {
    public static void main(String[] args){
       // test2();
        ComType.test2();
    }
    //数组的复制 System.arraycopy() 两个数组必须有相同的确切类型
    public static void test1(){
        int[] i = new int[7];
        Arrays.fill(i, 47);
        out.println(Arrays.toString(i));
        int[] j = new int [10];
        Arrays.fill(j,99);
        System.arraycopy(i,0,j,0,i.length);
        out.println(Arrays.toString(j));
    }
    //数组的比较 Arrays.equals  两个数组相等的条件：个数，及对应元素也相等。
    public static void test2(){
        int[] a1 = new int[10];
        int[] a2 = new int[10];
        Arrays.fill(a1,10);
        Arrays.fill(a2,10);
        out.println(Arrays.equals(a1, a2));
        a2[3]=11;
        out.println(Arrays.equals(a1,a2));
    }
}
//数组元素的比较
class ComType implements Comparable<ComType>{
    int i;
    int j;
    public static int count =1;
    public ComType(int n1,int n2){
        this.i = n1;
        this.j = n2;
    }
    @Override
    public String toString(){
        return "("+i+","+j+")";
    }
    @Override
    public int compareTo(ComType rv) {
        return (i<rv.i? -1 : (i==rv.i ? 0 :1));
    }
    public static void test(){
        ComType type1 = new ComType(2,5);
        ComType type2 = new ComType(1,8);
        ComType type3 = new ComType(2,8);
        out.println(type1.compareTo(type2));
        out.println(type1.compareTo(type3));

    }
    public static void test2(){
        ComType[] types = {new ComType(2,5),new ComType(1,8),new ComType(2,8),new ComType(0,3)};
        Arrays.sort(types);
        out.println(Arrays.toString(types));
    }
}