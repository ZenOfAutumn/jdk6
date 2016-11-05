import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;

/**
 * Created by liuda on 2016/10/22.
 */
/*
 ����һ���¼��������ƣ�
 ���ƿ���ʱ���ƴ򿪣�������ʱ���ƹرա�
 �ж���š�

 */
public class AboutEventListener {
    public static void main(String[] args){
        DoorManager doorManager = new DoorManager(); //ң����
        //������1
        doorManager.addDoorListener(new DoorListener1());
        //������2
        doorManager.addDoorListener(new DoorListener2());
        //����
        doorManager.openDoor();
        //������
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
    //���������¼�
    public void openDoor(){
        if(listeners==null) return;
        DoorEvent doorEvent = new DoorEvent(this,"open");
        notifyDoorListeners(doorEvent);
    }
    //���������¼�
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
        super(source); //���������һ�䣬�����EventObject��Ĭ�Ϲ���������EventObjectû��Ĭ�Ϲ��������ᱨ��
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
