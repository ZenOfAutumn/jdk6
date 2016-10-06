package compareJdk;

/**
 * Created by liuda on 2016/10/4.
 */
interface Car{
    public double getHeght();
    public double getWidth();
    /*  java8 新特性： 在接口中设置默认方法，所有实现类均有该方法，实现类不必再去继承其他类。
    default String getCompayName(){
        return "Foter";
    }


    static public String getCompany() {
        return "Foster";
    }
        */
}

class MiniCar implements Car {

    @Override
    public double getHeght() {
        return 0;
    }

    @Override
    public double getWidth() {
        return 0;
    }

}

public class Inteface_Default_Method {

}
