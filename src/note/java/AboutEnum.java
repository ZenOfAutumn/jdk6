/**
 * Created by liuda on 2016/10/22.
 */
public class AboutEnum {

    public static void main(String[] args) {
        Fruit fruit = Fruit.valueOf(Fruit.Apple.getName());
    }
}

/*
ö������
        ʹ�÷���
        ��enum�ඨ�壬����class�ඨ��
        ����ö�ٶ���
        ö�ٶ���ĳ�Ա
        ���췽����ö�ٶ���ĳ�Ա)
        �ⲿ���ʷ�����
        }
        }
*/
enum Fruit{
    Apple("ƻ��",1),
    Oragne("����",2);
    private String name;
    private int index;
    private Fruit(String name, int index){ //д��public�ᱨ����Ϊö���ǲ��������ⲿ��new �����¶���
        this.name = name;
        this.index = index;
    }
    public static String getName(int index){ //�ṩstatic�ⲿ���ʷ���
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