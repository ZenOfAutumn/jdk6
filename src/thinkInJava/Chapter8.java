package thinkInJava;
import static java.lang.System.out;
/**
 * Created by liuda on 2016/10/5.
 */

/*
任何事物发生之前 ，将分配给对象的存储空间初始化成二进制的零
调用基类构造器，此时调用覆盖后的draw()方法。
按声明的顺序调用成员的初始化方法
调用导出类的构造器主体
 */
class Glyph{
    void draw(){
        out.println("GlyPh.draw()");
    }
    public Glyph(){
        out.println("Glyph() before draw()");
        draw();
        out.println("Glyph() after draw()");
    }
}
class RoundGlyph extends Glyph{
    private int radius = 1;
    public RoundGlyph(int r){
        radius =r;
        out.println("RoundGlyph.RoundGlyph(),radius = "+ radius);
    }
    void draw(){
        out.println("RoundGlyph.draw(), radius = "+ radius);
    }
}

public class Chapter8 {
    public static void main(String[] args){
        new RoundGlyph(5);
    }
}
