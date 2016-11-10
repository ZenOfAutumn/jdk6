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

    //����һ��ͬ����ʵ������ʼ״̬Ϊ0
    protected AboutAbstractQueuedSynchronizer() {
    }

    //�ȴ����н����
    /*
    �ȴ����� ��CLH�ı��壬CLH��ͨ���������������˴���������ͬ���ߣ���ʹ�û�������س���һЩ����һ���̵߳�ǰ�̽ڵ������Ϣ
    ÿ���ڵ��еġ�status"��ɼ�¼���ٵ�һ���߳��Ƿ�Ӧ��������
    ��ǰһ���ڵ��ͷ�ʱ���ýڵ�Żᱻ���ѡ�
    �����е�ÿ���ڵ���Ϊ�ض�֪ͨ�����������е����ȴ��߳�
    ״̬"status"�򲢲������߳��Ƿ񱻸�������
    ���̴߳��ڶ��е�һ���ڵ�ʱ���п��ܻ�ȡ�������Ǵ��ڵ�һ���ڵ㲢����֤��ȡ�ɹ���ֻ��ӵ�о�����Ȩ����
    ���Ե�ǰ���ͷŵľ����߿�����Ҫ���µȴ���

     * <pre>
     *      +------+  prev +-----+       +-----+
     * head |      | <---- |     | <---- |     |  tail
     *      +------+       +-----+       +-----+
     * </pre>
    ���ʱ����Ҫ�Զ���Ϊβ�ڵ���룬����ʱ����Ҫ����head��
    ���뵽CLH���� ��Ҫ��tail�ڵ�������ԭ�Ӳ��������á�
     */
    static final class Node {
        //�ȴ�״ֵ̬ ָʾ �߳���ȡ�� ��������ͬ�������еȴ����̵߳ȴ���ʱ���жϣ���Ҫ��ͬ��������ȡ���ȴ����ڵ�����״̬������仯 ��
        static final int CANCELLED = 1;
        //�ȴ�״ֵ̬ ָʾ����߳���Ҫunparking:������̴߳��ڵȴ�״̬������ǰ�ڵ��߳����ͷ�ͬ��״̬��ȡ��������֪ͨ��̽ڵ� ��ʹ��̽ڵ���̵߳������С�
        static final int SIGNAL = -1;
        //�ȴ�״ֵ̬ ָʾ�߳����ڵȴ�����Condition���������̶߳�Condition����signal()�󣬸ýڵ㽫�ӵȴ�������ת�Ƶ�ͬ�¶����У��ӵ�ͬ��״̬�Ļ�ȡ�С�
        static final int CONDITION = -2;
        //�ȴ�״ֵ̬ ָʾ��һ�ι���ʽͬ��״̬��ȡ���������ر�������ȥ
        static final int PROPAGATE = -3;

        //ָʾһ���ڵ������Թ���ģʽ�ȴ�
        static final Node SHARED = new Node();
        //һ���ڵ�����������ģʽ�ȴ�
        static final Node EXCLUSIVE = null;

        volatile int waitStatus; //�ȴ�״ֵ̬��ȡֵΪ CANCELLED,SIGNAL,CONDTION, PROPAGATE,INITAL=0

        //ǰ���ڵ� ����ǰ�ڵ����ͬ������ ʱ�����ã�β����ӣ�
        volatile Node prev;

        //��̽ڵ�
        volatile Node next;

        //��Ӹýڵ���̣߳��ڹ������г�ʼ����ʹ�ú���null
        volatile Thread thread;
        //�ȴ������еĺ�̽ڵ㡣�����ǰ�ڵ��ǹ���ģ���ô����ֶν�ָʾһ��SHARED�������� �ڵ����ͣ���ռ�͹����͵ȴ������еĺ�̽ڵ㹲��һ���ֶ�

        /*
        ��Ϊ���������н����Զ�ռģʽʱ����Ҫһ���򵥵����Ӷ��У�ȥ���нڵ㡣��Ϊ����ֻ���������ģ�������һ����������ָʾ����ģʽ
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
        }       //���ڽ��� ��ʼ��ͷ�ڵ� �� ����maker

        Node(Thread thread, Node mode) {      //��addWaiter()ʹ��
            this.nextWaiter = mode;
            this.thread = thread;
        }

        //��COnditionʹ��
        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    //�ȴ����е�ͷ�ڵ㣬����ʼ
        /*
        ���ǳ�ʼ������ͨ��setHeand()�����޸�
        ���ͷ�ڵ���ڣ����ĵȴ�״̬��֤���ᱻȡ����
         */
    private transient volatile Node head;
    /*
    �ȴ����е�β�ڵ㣬����ʼ�������������µĵȴ��ڵ�ʱ�޸�
     */
    private transient volatile Node tail;

    //ͬ��״̬
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

    //�Ŷӹ���
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

    //���ݸ������̺߳�ģʽ����������ӽ��
    //Node.EXCLUSIVE Ϊ��ռģʽ��Node.SHAREDΪ����ģʽ
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
    statusΪ��������������Ի���
    ���ʧ�ܣ�����status���ȴ��е��߳��޸ģ�Ҳ�ǿ��Եġ�
     */
    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0) {
            compareAndSetWaitStatus(node, ws, 0);
        }
        Node s = node.next;
        if (s == null || s.waitStatus > 0) { //����ǰ����޺�̽ڵ㣬���̽ڵ�ȴ�״̬>0(��ȡ���������β�ڵ���ǰ�鵽��ֱ���Ӻ���ǰ�����һ��״̬<=�Ľڵ�
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev) {
                if (t.waitStatus <= 0)
                    s = t;
            }
        }
        if (s != null)
            LockSupport.unpark(s.thread); //���Ѹ��߳�
    }

    /*
    ����ģʽ���ͷ� - ���Ѻ�̽ڵ㲢�����������
     */
    private void doReleaseShared() {
        for (; ; ) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue;  //ѭ�����ٴμ��
                    unparkSuccessor(h);
                } else if (ws == 0 && !compareAndSetWaitStatus(h, 0, PROPAGATE))
                    continue; // CAS����ʧ�ܣ����������CAS����
            }
            if (h == head)
                break;
        }
    }

    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head;
        setHead(node);
            /*
            ���Ի�����һ�������еĽ�� if:
              ������ָʾ������propagate
              ��ǰһ������(���waitStatus��ΪPROPAGATE״̬�����л���SIGNAL)��¼��waitStatus)��
              ��һ���ڵ��Թ���ģʽ�ȴ�
              ��Щ�����ܻᵼ�·Ǳ�Ҫ�Ļ��� ������ֻ�е��ж������������ȡ���ͷš�
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
            node.next = node; //����GC
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
                    p.next = null; //����GC
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
    //�����Զ�ռģʽ��ȡ �����������Ӧ�ü�飬������״̬�����Զ�ռģʽ����ȡ�����ܻ�ȡ ��
        /*
        ���������������쳣��acquire()��ʹ�߳���ӣ���δ�������£���ֱ���������̵߳�release��������
        ����ʹ��Lock.tryLock()ʵ��
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
        Head��next�� ���ܻ�δ���ã�������setHead��δ���ã�
        ���� ���β����Ƿ�Ϊ��һ���ڵ㣬
        ����ǣ������ ����ȫ��β��㵽ͷ���������ҵ�ÿһ���ڵ㣬����֤��������ֹ��
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
        /** First node of condition queue. �������� �е�һ���ڵ�  */
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

