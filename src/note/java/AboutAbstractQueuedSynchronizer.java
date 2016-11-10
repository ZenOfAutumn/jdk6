import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by liuda on 2016/11/6.
 */
public abstract class AboutAbstractQueuedSynchronizer extends AbstractOwnableSynchronizer
        implements java.io.Serializable {

    private static final long serialVersionUID = 7373984972572414691L;

    //创建一个同步器实例，初始状态为0
    protected AboutAbstractQueuedSynchronizer() {
    }

    //等待队列结点类
    /*
    等待队列 是CLH的变体，CLH锁通常用于自旋锁。此处用于阻塞同步者，但使用基本有序地持有一些关于一个线程的前继节点控制信息
    每个节点中的“status"域可记录跟踪到一个线程是否应该阻塞。
    当前一个节点释放时，该节点才会被唤醒。
    队列中的每个节点作为特定通知风格监视器持有单个等待线程
    状态"status"域并不控制线程是否被给予锁。
    当线程处于队列第一个节点时才有可能获取锁，但是处于第一个节点并不保证获取成功，只是拥有竞争的权力，
    所以当前已释放的竞争者可能需要重新等待。

     * <pre>
     *      +------+  prev +-----+       +-----+
     * head |      | <---- |     | <---- |     |  tail
     *      +------+       +-----+       +-----+
     * </pre>
    入队时，需要自动作为尾节点插入，出队时，需要设置head域。
    插入到CLH队列 需要在tail节点作单个原子操作来设置。
     */
    static final class Node {
        //等待状态值 指示 线程已取消 （由于在同步队列中等待的线程等待超时或被中断，需要从同步队列中取消等待，节点进入该状态将不会变化 ）
        static final int CANCELLED = 1;
        //等待状态值 指示后继线程需要unparking:即后继线程处于等待状态，而当前节点线程若释放同步状态或被取消，将会通知后继节点 ，使后继节点的线程得以运行。
        static final int SIGNAL = -1;
        //等待状态值 指示线程正在等待条件Condition，当其他线程对Condition调用signal()后，该节点将从等待队列中转移到同下队列中，加到同步状态的获取中。
        static final int CONDITION = -2;
        //等待状态值 指示下一次共享式同步状态获取将会条件地被传播下去
        static final int PROPAGATE = -3;

        //指示一个节点正在以共享模式等待
        static final Node SHARED = new Node();
        //一个节点正在以排他模式等待
        static final Node EXCLUSIVE = null;

        volatile int waitStatus; //等待状态值，取值为 CANCELLED,SIGNAL,CONDTION, PROPAGATE,INITAL=0

        //前驱节点 ，当前节点加入同步队列 时被设置（尾部添加）
        volatile Node prev;

        //后继节点
        volatile Node next;

        //入队该节点的线程，在构造器中初始化，使用后置null
        volatile Thread thread;
        //等待队列中的后继节点。如果当前节点是共享的，那么这个字段将指示一个SHARED常量，即 节点类型（独占和共享）和等待队列中的后继节点共用一个字段

        /*
        因为当条件队列仅当以独占模式时，需要一个简单的连接队列，去持有节点。因为条件只能是排他的，故设置一个特殊域来指示共享模式
         */
        Node nextWaiter;

        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {
        }       //用于建立 初始化头节点 或 共享maker

        Node(Thread thread, Node mode) {      //由addWaiter()使用
            this.nextWaiter = mode;
            this.thread = thread;
        }

        //由COndition使用
        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    //等待队列的头节点，懒初始
        /*
        除非初始化，仅通过setHeand()才能修改
        如果头节点存在，它的等待状态保证不会被取消。
         */
    private transient volatile Node head;
    /*
    等待队列的尾节点，懒初始，仅在入队添加新的等待节点时修改
     */
    private transient volatile Node tail;

    //同步状态
    private volatile int state;

    protected final int getState() {
        return state;
    }

    protected final void setState(int newState) {
        state = newState;
    }

    protected final boolean compareAndSetState(int except, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, except, update);
    }

    //排队工具
    static final long spinForTimeoutThreshold = 1000L;

    private Node enq(final Node node) {
        for (; ; ) {
            Node t = tail;
            if (t == null) {
                Node h = new Node();
                h.next = node;
                node.prev = h;
                if (compareAndSetHead(h)) {
                    tail = node;
                    return h;
                }
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    //根据给定的线程和模式，创建和入队结点
    //Node.EXCLUSIVE 为独占模式，Node.SHARED为共享模式
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }

    /*
    status为负，尝试清除，以唤醒
    如果失败，或者status被等待中的线程修改，也是可以的。
     */
    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0) {
            compareAndSetWaitStatus(node, ws, 0);
        }
        Node s = node.next;
        if (s == null || s.waitStatus > 0) { //若当前结点无后继节点，或后继节点等待状态>0(已取消），则从尾节点向前查到，直到从后向前的最后一个状态<=的节点
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev) {
                if (t.waitStatus <= 0)
                    s = t;
            }
        }
        if (s != null)
            LockSupport.unpark(s.thread); //唤醒该线程
    }

    /*
    共享模式的释放 - 唤醒后继节点并传递这个动作
     */
    private void doReleaseShared() {
        for (; ; ) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue;  //循环以再次检查
                    unparkSuccessor(h);
                } else if (ws == 0 && !compareAndSetWaitStatus(h, 0, PROPAGATE))
                    continue; // CAS操作失败，则继续进行CAS操作
            }
            if (h == head)
                break;
        }
    }

    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head;
        setHead(node);
            /*
            尝试唤醒下一个队列中的结点 if:
              调用者指示传递性propagate
              或被前一个操作(检查waitStatus因为PROPAGATE状态可以切换到SIGNAL)记录（waitStatus)。
              下一个节点以共享模式等待
              这些检查可能会导致非必要的唤醒 ，但是只有当有多个竞争的锁获取和释放。
             */
        if (propagate > 0 || h == null || h.waitStatus < 0) {
            Node s = node.next;
            if (s == null || s.isShared()) {
                doReleaseShared();
            }
        }
    }

    private void cancelAcquire(Node node) {
        if (node == null)
            return;
        node.thread = null;
        Node pred = node.prev;
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;
        Node preNext = pred.next;
        node.waitStatus = Node.CANCELLED;
        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNext(pred, preNext, null);
        } else {
            int ws;
            if (pred != head &&
                    ((ws = pred.waitStatus) == Node.SIGNAL || (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL)))
                    && pred.thread != null) {
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)
                    compareAndSetNext(pred, predNext, next);
            } else {
                unparkSuccessor(node);
            }
            node.next = node; //帮助GC
        }
    }

    private static boolean shoudParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            return true;
        if (ws > 0) {
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    private static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }

    final boolean acquireQueue(final Node node, int arg) {
        try {
            boolean interrupted = false;
            for (; ; ) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; //帮助GC
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
                    interrupted = true;
                }
            }
        } catch (RuntimeException ex) {
            cancelAcquire(node);
            throw ex;
        }
    }

    /*
    private void doAcquireInterruptibly(int arg)
    private boolean doAcquireNanos(int arg,long nanosTimeout)
     */
    private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        long lastTime = System.nanoTime();
        final Node node = addWaiter(Node.EXCLUSIVE);
        try {
            for (; ; ) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null;
                    return true;
                }
                if (nanosTimeout <= 0) {
                    cancelAcquire(node);
                    return false
                }
                if (nanosTimeout > spinForTimeoutThreshold && shoudParkAfterFailedAcquire(p, node))
                    LockSupport.parkNanos(this, nanosTimeout);
                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
                if (Thread.interrupted())
                    break;
            }
        } catch (RuntimeException ex) {
            cancelAcquire(node);
            throw ex;
        }
        cancelAcquire(node);
        throw new InterruptedException();
    }

    /*
    private void doAcquireShared(int arg)
     */
    //尝试以独占模式获取 锁，这个方法应该检查，当对象状态允许以独占模式被获取，才能获取 。
        /*
        这个方法如果报告异常，acquire()会使线程入队（在未入队情况下），直到被其他线程的release操作唤醒
        可以使用Lock.tryLock()实现
         */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }

    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (!tryAcquire(arg))
            doAcquireInterruptibly(arg);
    }

    public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquire(arg) ||
                doAcquireNanos(arg, nanosTimeout);
    }

    public final boolean hasQueuedThreads() {
        return head != tail;
    }
    public final boolean hasContended() {
        return head != null;
    }
    public final Thread getFirstQueuedThread() {
        // handle only fast path, else relay
        return (head == tail)? null : fullGetFirstQueuedThread();
    }
    private Thread fullGetFirstQueuedThread() {
        Node h, s;
        Thread st;
        if (((h = head) != null && (s = h.next) != null &&
                s.prev == head && (st = s.thread) != null) ||
                ((h = head) != null && (s = h.next) != null &&
                        s.prev == head && (st = s.thread) != null))
            return st;
        Node t = tail;
        Thread firstThread = null;
        /*
        Head的next域 可能还未设置，可能在setHead后还未设置，
        必须 检查尾结点是否为第一个节点，
        如果是，则继续 ，安全从尾结点到头结点遍历以找到每一个节点，并保证操作会终止。
         */
        while (t != null && t != head) {
            Thread tt = t.thread;
            if (tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread;
    }
    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.thread == thread)
                return true;
        return false;
    }
    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return ((h = head) != null && (s = h.next) != null &&
                s.nextWaiter != Node.SHARED);
    }
    final boolean isFirst(Thread current) {
        Node h, s;
        return ((h = head) == null ||
                ((s = h.next) != null && s.thread == current) ||
                fullIsFirst(current));
    }
    final boolean fullIsFirst(Thread current) {
        // same idea as fullGetFirstQueuedThread
        Node h, s;
        Thread firstThread = null;
        if (((h = head) != null && (s = h.next) != null &&
                s.prev == head && (firstThread = s.thread) != null))
            return firstThread == current;
        Node t = tail;
        while (t != null && t != head) {
            Thread tt = t.thread;
            if (tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread == current || firstThread == null;
    }
    public final int getQueueLength() {
        int n = 0;
        for (Node p = tail; p != null; p = p.prev) {
            if (p.thread != null)
                ++n;
        }
        return n;
    }
    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.thread;
            if (t != null)
                list.add(t);
        }
        return list;
    }
    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }
    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }
    final boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == Node.CONDITION || node.prev == null)
            return false;
        if (node.next != null) // If has successor, it must be on queue
            return true;
        /*
         * node.prev can be non-null, but not yet on queue because
         * the CAS to place it on queue can fail. So we have to
         * traverse from tail to make sure it actually made it.  It
         * will always be near the tail in calls to this method, and
         * unless the CAS failed (which is unlikely), it will be
         * there, so we hardly ever traverse much.
         */
        return findNodeFromTail(node);
    }
    private boolean findNodeFromTail(Node node) {
        Node t = tail;
        for (;;) {
            if (t == node)
                return true;
            if (t == null)
                return false;
            t = t.prev;
        }
    }
    final boolean transferForSignal(Node node) {
        /*
         * If cannot change waitStatus, the node has been cancelled.
         */
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        /*
         * Splice onto queue and try to set waitStatus of predecessor to
         * indicate that thread is (probably) waiting. If cancelled or
         * attempt to set waitStatus fails, wake up to resync (in which
         * case the waitStatus can be transiently and harmlessly wrong).
         */
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }
    final boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            enq(node);
            return true;
        }
        /*
         * If we lost out to a signal(), then we can't proceed
         * until it finishes its enq().  Cancelling during an
         * incomplete transfer is both rare and transient, so just
         * spin.
         */
        while (!isOnSyncQueue(node))
            Thread.yield();
        return false;
    }
    final int fullyRelease(Node node) {
        try {
            int savedState = getState();
            if (release(savedState))
                return savedState;
        } catch (RuntimeException ex) {
            node.waitStatus = Node.CANCELLED;
            throw ex;
        }
        // reach here if release fails
        node.waitStatus = Node.CANCELLED;
        throw new IllegalMonitorStateException();
    }
    public final boolean owns(ConditionObject condition) {
        if (condition == null)
            throw new NullPointerException();
        return condition.isOwnedBy(this);
    }
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }
    public class ConditionObject implements  Condition, java.io.Serializable{
        private static final long serialVersionUID = 1173984872572414699L;
        /** First node of condition queue. 条件队列 中第一个节点  */
        private transient Node firstWaiter;
        /** Last node of condition queue. */
        private transient Node lastWaiter;
        private Node addConditionWaiter() {
            Node t = lastWaiter;
            // If lastWaiter is cancelled, clean out.
            if (t != null && t.waitStatus != Node.CONDITION) {
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
            if (t == null)
                firstWaiter = node;
            else
                t.nextWaiter = node;
            lastWaiter = node;
            return node;
        }
        private void doSignal(Node first){
            do{

            }
        }
    }

}

