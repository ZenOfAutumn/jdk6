import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;

/**
 * Created by liuda on 2016/10/22.
 */
/*
 制作一个事件监听机制：
 人推开门时，灯打开，关上门时，灯关闭。
 有多道门。

 */
public class AboutEventListener {
    public static void main(String[] args){
        DoorManager doorManager = new DoorManager(); //遥控器
        //监听门1
        doorManager.addDoorListener(new DoorListener1());
        //监听门2
        doorManager.addDoorListener(new DoorListener2());
        //打开门
        doorManager.openDoor();
        //关上门
        doorManager.closeDoor();

    }
}
class DoorManager{
    private Collection<DoorListener> listeners;
    public void addDoorListener(DoorListener doorListener){
        if(listeners == null){
            listeners = new HashSet();
        }
        listeners.add(doorListener);
    }
    public void removeDoorListener(DoorListener doorListener){
        if(listeners==null) return;
        listeners.remove(doorListener);
    }
    //触发开门事件
    public void openDoor(){
        if(listeners==null) return;
        DoorEvent doorEvent = new DoorEvent(this,"open");
        notifyDoorListeners(doorEvent);
    }
    //触发关门事件
    public void closeDoor(){
        if(listeners==null) return;
        DoorEvent doorEvent = new DoorEvent(this,"close");
        notifyDoorListeners(doorEvent);
    }
    public void notifyDoorListeners(DoorEvent doorEvent){
        for(DoorListener doorListener: listeners){
            doorListener.onEvent(doorEvent);
        }
    }
}
interface DoorListener extends EventListener{
    public void onEvent(DoorEvent doorEvent);

}
class DoorListener1 implements DoorListener{

    public void onEvent(DoorEvent doorEvent) {
        if(doorEvent.getState().equals("open")){
            System.out.println("door is opened, opend the lamb");
        }
    }
}
class DoorListener2 implements DoorListener{

    public void onEvent(DoorEvent doorEvent) {
        if(doorEvent.getState().equals("close")){
            System.out.println("door is closed, close the lamb");
        }
    }
}
class DoorEvent extends EventObject{
    private String state = "";
    public DoorEvent(Object source,String state){
        super(source); //如果不加这一句，会调用EventObject的默认构造器，而EventObject没有默认构造器，会报错。
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
