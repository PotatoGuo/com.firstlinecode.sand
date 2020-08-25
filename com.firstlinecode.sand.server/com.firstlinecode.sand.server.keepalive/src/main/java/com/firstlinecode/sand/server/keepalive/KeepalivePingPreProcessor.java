package com.firstlinecode.sand.server.keepalive;

import com.firstlinecode.basalt.protocol.core.Keepalive;
import com.firstlinecode.granite.framework.core.integration.IMessage;
import com.firstlinecode.granite.framework.core.integration.IMessageChannel;
import com.firstlinecode.granite.framework.core.integration.SimpleMessage;
import com.firstlinecode.granite.framework.core.routing.MessageChannelAware;
import com.firstlinecode.granite.framework.core.session.ISession;
import com.firstlinecode.granite.framework.parsing.IPipePreprocessor;

import java.util.HashMap;
import java.util.Map;

public class KeepalivePingPreProcessor implements IPipePreprocessor, MessageChannelAware {
    public static final Object KEY_SESSION_ALIVE_TIME = "granite.session.alive.time";
    private IMessageChannel messageChannel;

    @Override
    public String beforeParsing(String message, ISession session) {
        if (Keepalive.MESSAGE.equals(message)) {
            Map<Object, Object> header = new HashMap<>(1);
            header.put(IMessage.KEY_SESSION_JID, session.getJid());
            session.setAttribute(KEY_SESSION_ALIVE_TIME, System.currentTimeMillis());
            messageChannel.send(new SimpleMessage(header, Keepalive.MESSAGE));
            return null;
        }

        return message;
    }

    @Override
    public Object afterParsing(Object object) {
        return object;
    }

    @Override
    public void setMessageChannel(IMessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    @Override
    public String getMessageChannelId() {
        return null;
    }
}
