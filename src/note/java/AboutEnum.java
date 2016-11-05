/**
 * Created by liuda on 2016/10/22.
 */
public class AboutEnum {

    public static void main(String[] args) {
        Fruit fruit = Fruit.valueOf(Fruit.Apple.getName());
    }
}

/*
枚举类型
        使用方法
        将enum类定义，而非class类定义
        例举枚举对象
        枚举对象的成员
        构造方法（枚举对象的成员)
        外部访问方法。
        }
        }
*/
enum Fruit{
    Apple("苹果",1),
    Oragne("橘子",2);
    private String name;
    private int index;
    private Fruit(String name, int index){ //写成public会报错。因为枚举是不可以在外部用new 生成新对象
        this.name = name;
        this.index = index;
    }
    public static String getName(int index){ //提供static外部访问方法
        for(Fruit c : Fruit.values()){
            if(c.getIndex() == index) return c.getName();
        }
        return null;
    }
    public int getIndex(){
        return index;
    }
    public String getName(){
        return name;
    }
}