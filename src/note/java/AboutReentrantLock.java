import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuda on 2016/11/6.
 */
public class AboutReentrantLock implements Lock,java.io.Serializable{
    private static final long serialVersionUID = 7373984872572414699L;
    private final Sync sync;
    static abstract  class Sync extends AboutAbstractQueuedSynchronizer{
        private static final long serialVersionUID = -5179523762034025860L;
        //实现Lock.lock，主要原因是子类可允许快速使用非公平版本。
        abstract  void lock();
        //实现非公平 tryLock.  tryAcquire将在子类中实现，但在非公平的tryLock()中try也是需要的。
        final boolean nonfairTryAcquire(int acquires){
            final Thread current = Thread.currentThread();
            int c = getState();
            if(c == 0){
                if(compareAndSetState(0,acquires)){
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if(current == getExclusiveOwnerThread()){
                int nextc = c + acquires;
                if(nextc<0) //溢出
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
        protected  final boolean tryRelease(int releases){
            int c = getState() - releases;
            if(Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if(c == 0){
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
        protected final boolean isHeldExclusively(){
            return getExclusiveOwnerThread() == Thread.currentThread();
        }
        final  Condition newCondition{return new ConditionObject();}
        final Thread getOwner(){return getState() == 0 ? null : getExclusiveOwnerThread();}
        final int getHoldCount(){ return isHeldExclusively()? getState():0;}
        final boolean isLocked(){
            return getState()!=0;
        }
        private void readObject(java.io.ObjectInputStream s)throws java.io.IOException,ClassNotFoundException{
            s.defaultReadObject();
            setState(0);
        }
    }
    //非公平锁的Sync对象
    final static class NonfairSync extends Sync{
        private static final long serialVersionUID = 7316153563782823691L;
        final void lock(){
            if(compareAndSetState(0,1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }
        protected final boolean tryAcquire(int acquires){return nonfairTryAcquire(acquires);}
    }
    final static class FairSync extends Sync{
        private static final long serialVersionUID = -3000897897090466540L;
        final void lock(){acquire(1);}
        protected final boolean tryAcquire(int acquires){
            final Thread current = Thread.currentThread();
            int c = getState();
            if(c == 0){
                if(isFirst(current) && compareAndSetState(0,acquires)){
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if(current == getExclusiveOwnerThread()){
                int nextc = c + acquires;
                if(nextc<0)
                    throw new Error("Maximun lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }
    public AboutReentrantLock(){sync = new NonfairSync();}

    public AboutReentrantLock(boolean fair){ sync = (fair)? new FairSync(): new NonfairSync();}

    /*
    获取锁，并设置锁持有计数器置1，若已持有锁，则设置锁持有计数器加1
     */
    public void lock() {
        sync.lock();
    }
    /*
    同上，不同点：可中断
     */
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }
    // 当当前线程是锁的持有者，则使锁持有计数器减1， 若锁持有计数器已为0，则释放锁。
    public void unlock() {
        sync.release(1);
    }
    public int getHoldCount(){return sync.getHoldCount();}
    public boolean isHeldByCurrentThread(){return sync.isHeldExclusively();}
    public boolean isLocked(){ return sync.isLocked();}
    public final boolean isFair(){return sync instanceof  FairSync;}
    protected Thread getOwner(){ return sync.getOwner();}
    //查询是否有线程正在等待当前锁，由于线程随时有可能取消，故返回结果并不保证其他 线程会获取到当前锁，这个方法用于监视系统状态。
    public final boolean hasQueuedThreads(){return sync.hasQueuedThreads();}
    public final boolean hasQueueThread(Thread thread){ return sync.isQueued(thread);}
    public final int getQueueLength(){return sync.getQueueLength();}
    protected Collection<Thread> getQueueThreads(){return sync.getQueuedThreads();}
    public boolean hasWaiters(Condition condition){
        if(condition == null)
            throw new NullPointerException();
        if(!(condition instanceof  AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject) condition);
    }
    public int getWaitQueueLength(Condition condition){
        if(condition == null)
            throw new NullPointerException();
        if(!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject) condition);
    }
    protected  Collection<Thread> getWaitingThreads(Condition condition){
        if(condition == null)
            throw new NullPointerException();
        if(!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

}
