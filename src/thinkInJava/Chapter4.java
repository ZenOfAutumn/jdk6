package thinkInJava;

import java.math.BigInteger;
import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/4.
 */
public class Chapter4 {
    public static void main(String[] args){
        continueMark();

    }
    /*
    标签 存在的理由： 循环嵌套存在，想从中break或continue;
     */
    public static void continueMark(){
        outer:
        for(int i=0;i<6;i++){
            inner:
            for(int j=0;j<10;j++){
                out.println(i+","+j);
                if(i==2){
                    out.println("continue");
                    continue ; //continue使执行点移回内部迭代的起始处
                }
                if(i==3){
                    out.println("continue inner");
                    continue inner;
                }
                if(i==4){
                    out.println("continue outer");
                    continue outer;
                }
                if(i==5){
                    out.println("break outer");
                    break outer;
                }
            }
        }
    }
    //高精度数字： BigInteger，（支持任意精度的整数）， BigDecimal(支持任意精度的定点数）
    public static void bigInteger(){
        byte[] bigInts1 = {0x20,0x10};     //大端模式  (16^3)*2+16^1 = 8208
        BigInteger bigInteger1 = new BigInteger(bigInts1);
        out.println(bigInteger1.toString());
        byte[] bigInts2 = {0x20,0x10,0x30};
        BigInteger bigInteger2 = new BigInteger(bigInts1);
        bigInteger2=bigInteger2.add(bigInteger1);
        out.println(bigInteger2.toString());

    }
    //for-each循环遍历，适用：数组、容器
    public static void forEach(){
        String names[]={"Zhang","Chen","Li"};
        for(String name : names){
            out.println(name);
        }
        for(char c : "hello".toCharArray()){
            out.print(c);
        }
    }

}
