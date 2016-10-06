package thinkInJava;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by liuda on 2016/10/5.
 */
public class Chapter10_ControllerFramework {
    public static void main(String[] args){
        GreenhouseController.test();
    }
}

/*
内部类 & 控制框架
 */
//事件
abstract  class  Event{
    private long eventTime;
    protected  final long delayTime;
    //运行Event并随后调用start(),start是一个独立方法，未包含在构造器内，故可以在事件运行后重新启动计时器，即可重复使用Event对象，例：想要重复一个事件，只需要简单在action()中调用start()
    public Event(long delayTime){
        this.delayTime = delayTime;
        start();
    }
    public void start(){
        eventTime = System.nanoTime() + delayTime;
    }
    public boolean ready(){     //告诉何时可以运行acton方法，当然ready()可以被覆盖，使得Event能够基于时间以下的其他因素而触发。
        return System.nanoTime()>=eventTime;
    }
    public abstract  void action();              //抽象方法
}
//用来管理并触发事件的实际控制框架
class Controller{
    private List<Event> eventList = new ArrayList<Event>();          //多态 List-ArrayList
    public void addEvent(Event c){ eventList.add(c);}
    public void run(){
        while(eventList.size()>0){
            for(Event e : new ArrayList<Event>(eventList)){          //需要使用eventList, 使用实际类型
                if(e.ready()){
                    out.println(e);
                    e.action();
                    eventList.remove(e);
                }
            }
        }
    }
}
/* 在目前设计，并不知道Event了什么，这正是设计的关键所在， “使变化 的事物与不变的事物分离“
  即： “变化向量” 是各种不同的Event对象所具有的不同行为，而你通过创建不同的Event子类来表现不同的行为。
  这正是内部需要做的， 内部类允许：
     1. 控制框架的完整实现是由单个的类创建的，从而使得实现的细节被封装起来，内部类用来表示 解决问题所需要的各种不同的action()
     2. 内部类能够很容易地访问外围类的任意成员 ，所以可以避免这种实现变得笨拙。
 */
// exp: 控制框架的一个特定实现 ， 控制温室的动作：控制灯光，水，温度调节器开关，响铃，重启系统
// 控制框架的设计使这些不同代码变得非常容易， 使用内部类，可以在单一的类里面产生对同一个基类Event的多种导出版本，对于温室系统的每一种行为，都继承一个新的Event内部类，并在实现的action中编写控制代码
class GreenhouseControls extends  Controller{
    private  boolean light = false;
    public class LightOn extends Event{
        public LightOn(long delayTime){super(delayTime);}
        public void action(){light = true;}
        public String toString(){return "Light is on";}
    }
    public class LightOff extends Event{
        public LightOff(long delayTime){super(delayTime);}
        public void action(){light = false;}
        public String toString(){return "Light is off";}
    }
    private boolean water = false;
    public class WaterOn extends Event{
        public WaterOn(long delayTime){super(delayTime);}
        public void action(){water = true;}
        public String toString(){return "Water is on";}
    }
    public class WaterOff extends Event{
        public WaterOff(long delayTime){super(delayTime);}
        public void action(){water = false;}
        public String toString(){return "Water is off";}
    }
    private boolean bell = false;
    public class Bell extends Event{
        public Bell(long delayTime){super(delayTime);}
        public void action(){addEvent(new Bell(delayTime));}
        public String toString(){return "Bing";}
    }
    public class Restart extends  Event{                        //内部类Bell,Restart有Event的所有方法，并且似乎也拥有外围类GreenhouseContronls的所有方法
        private  Event[] eventsList;
        public Restart(long delayTime, Event[] eventsList){            //参数： 命令列表
            super(delayTime);
            this.eventsList = eventsList;
            for(Event e :eventsList){
                addEvent(e);                                          //添加命令
            }
        }
        public void action(){
            for(Event e: eventsList){
                e.start();
                addEvent(e);
            }
            start();
            addEvent(this);  //Restart也是一个Event对象，所以同样可以将Restart对象添加到action()中，以使系统有规律地重新启动自己
        }
        public String toString(){
            return "Restarting system";
        }
    }
    public static class Terminate extends Event{
        public Terminate(long delayTime){super(delayTime);}
        public void action(){System.exit(0);}
        public String toString(){
            return "Terminating";
        }
    }



}
class GreenhouseController{                  //例：添加不同的Event对象来配置该系统，命令设计模式
    public static void test(){
        GreenhouseControls gc = new GreenhouseControls();
        gc.addEvent(gc.new Bell(900) );
        Event[] eventList = {
                gc.new LightOn(200),  //灯照
                gc.new LightOff(400),
                gc.new WaterOn(600), //浇水
                gc.new WaterOff(800)
        };
        gc.addEvent(gc.new Restart(2000,eventList));
        //注： gc.new  此时，新建内部类对象，其外围类对象是gc,故在Restart构造方法中，参数不仅初始化了restart对象，
        // eventList通过for循环调用addEvent(),也被加入到了gc的eventList尾部
        gc.addEvent(new GreenhouseControls.Terminate(new Integer(10000)));
        gc.run();
        /*
        如果 不加入 Terminater事件，程序将循环，不会自动终止，原因在于：
            控制器的run方法：遍历控制器中的eventList执行命令后并删除命令
            而命令Restart在每次执行时会将自已持有ventList 和 自己令 加载到控制器中。
         */
    }
}