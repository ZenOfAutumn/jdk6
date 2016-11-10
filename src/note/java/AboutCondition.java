import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuda on 2016/11/5.
 */
public class AboutCondition {

}

class BoundBuffer{
    final Lock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();
    final Object[] items = new Object[100];
    int putptr,takeptr,count;
    public void put(Object x)throws InterruptedException{
        lock.lock();
        try{
            while(count == items.length){  //注： 这里不用if,而是while
                notFull.await();
            }
            items[putptr] = x;
            if(++putptr == items.length) putptr=0;
            ++count;
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
    }
    public Object take() throws  InterruptedException{
        lock.lock();;
        try{
            while(count == 0){
                notEmpty.await();
            }
            Object x = items[takeptr];
            if(++takeptr == items.length) takeptr=0;
            --count;
            notFull.signal();
            return x;
        }finally {
            lock.unlock();
        }

    }
}
