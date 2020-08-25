package com.firstlinecode.sand.client.keepalive;

import com.firstlinecode.basalt.protocol.core.Keepalive;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.chalk.IChatServices;
import com.firstlinecode.chalk.IParsingListener;
import com.firstlinecode.chalk.ITask;
import com.firstlinecode.chalk.IUnidirectionalStream;
import com.firstlinecode.chalk.network.ConnectionException;
import com.firstlinecode.sand.client.things.IKeepaliver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class Keepaliver implements IKeepaliver {

    private static final Logger logger = LoggerFactory.getLogger(Keepaliver.class);


    private static final long DEFAULT_TIMEOUT = 10 * 1000L;

    private IChatServices chatServices;

    private long timeout = DEFAULT_TIMEOUT;
    // TODO 使用统一的线程池管理，目前的timer内部使用了单个线程，如果发生频繁的start和stop会频繁启动线程
    private Timer timer;
    private volatile long lastMessageReceivedTimeMills;
    private volatile PingTask pingTask;
    private int threshhold;
    private boolean aliveSupported;

    public Keepaliver() {
        threshhold = 3;
    }

    @Override
    public void start() {
        chatServices.getTaskService().execute(new KeepaliveNegotiationTask());
    }

    @Override
    public void stop() {
        timer.cancel();
        this.timer = null;
        this.pingTask = null;
    }

    @Override
    public boolean isAlive() {
        return (System.currentTimeMillis() - lastMessageReceivedTimeMills) < threshhold * timeout;
    }

    private void startPing() {
        // 客户端应该不需要加锁
        timer = new Timer();
        startPingTask();
    }

    private void startPingTask() {
        pingTask = new PingTask(timer, timeout);
        pingTask.schedule();
    }

    @Override
    public String beforeParsing(String message) {
        return message;
    }

    @Override
    public Object afterParsing(Object parseObject) {
        long now = System.currentTimeMillis();
        this.lastMessageReceivedTimeMills = now;
        if (parseObject instanceof Keepalive) {
            // 处理ping逻辑，如果有需要的话
            // 直接返回，不需要后续的stream处理
            return null;
        } else if(this.pingTask != null) {
            this.pingTask.setLastMessageReceivedTimeMills(now);
        }
        return parseObject;
    }

    @Override
    public IParsingListener next() {
        return null;
    }

    @Override
    public IParsingListener prev() {
        return null;
    }

    @Override
    public void occurred(ConnectionException exception) {
        // 发生异常，stop?
        stop();
    }

    @Override
    public void received(String message) {

    }

    @Override
    public void sent(String message) {

    }

    private class KeepaliveNegotiationTask implements ITask<Iq> {

        @Override
        public void trigger(IUnidirectionalStream<Iq> stream) {
            Iq iq = new Iq(new com.firstlinecode.sand.protocols.keepalive.Keepalive.KeepaliveNegotiation(), Iq.Type.SET);
            stream.send(iq, (int) DEFAULT_TIMEOUT);
        }

        @Override
        public void processResponse(IUnidirectionalStream<Iq> stream, Iq iq) {
            if (iq.getType() != Iq.Type.RESULT || iq.getObject() == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Server returns an bad response. Result is %s.", iq));
                }

                return;
            }

            com.firstlinecode.sand.protocols.keepalive.Keepalive.KeepaliveSupport keepaliveSupport = iq.getObject();
            if (com.firstlinecode.sand.protocols.keepalive.Keepalive.TRUE.equals(keepaliveSupport.getSupported())) {
                if (keepaliveSupport.getKeepaliveTimeInterval() != null && keepaliveSupport.getKeepaliveTimeInterval() > 0) {
                    timeout = keepaliveSupport.getKeepaliveTimeInterval();
                }
                aliveSupported = true;
                startPing();
            } else {
                aliveSupported = false;
            }
        }

        @Override
        public boolean processError(IUnidirectionalStream<Iq> stream, StanzaError error) {
            return false;
        }

        @Override
        public boolean processTimeout(IUnidirectionalStream<Iq> stream, Iq stanza) {
            aliveSupported = false;
            return true;
        }

        @Override
        public void interrupted() {

        }
    }

    private class PingTask extends TimerTask {
        private Timer timer;
        private volatile long lastMessageReceivedTimeMills;
        private long period;
        private volatile boolean pingActive;

        public PingTask(Timer timer, long period) {
            this.timer = timer;
            pingActive = false;
            this.period = period;
        }

        public void setLastMessageReceivedTimeMills(long lastMessageReceivedTimeMills) {
            this.lastMessageReceivedTimeMills = lastMessageReceivedTimeMills;
        }

        public void schedule() {
            if (!pingActive) {
                timer.schedule(this, period, period);
                pingActive = true;
            }
        }

        private void ping() {
            chatServices.getStream().getConnection().write(Keepalive.MESSAGE);
        }

        @Override
        public void run() {
            if(pingActive && System.currentTimeMillis() - lastMessageReceivedTimeMills > Keepaliver.this.timeout) {
                ping();
            }
        }
    }
}
