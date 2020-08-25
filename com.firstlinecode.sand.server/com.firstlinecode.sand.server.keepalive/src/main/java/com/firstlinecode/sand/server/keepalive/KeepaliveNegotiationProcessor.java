package com.firstlinecode.sand.server.keepalive;

import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.granite.framework.processing.IProcessingContext;
import com.firstlinecode.granite.framework.processing.IXepProcessor;
import com.firstlinecode.sand.protocols.keepalive.Keepalive;

public class KeepaliveNegotiationProcessor implements IXepProcessor<Iq, Keepalive.KeepaliveNegotiation> {

    private static final long timeInterval = 10 * 1000L;

    @Override
    public void process(IProcessingContext context, Iq stanza, Keepalive.KeepaliveNegotiation xep) {
        Iq result = new Iq(Iq.Type.RESULT);
        result.setId(stanza.getId());
        Keepalive.KeepaliveSupport keepaliveSupport = new Keepalive.KeepaliveSupport();
        // TODO 加入配置验证
        keepaliveSupport.setSupported(Keepalive.TRUE);
        keepaliveSupport.setKeepaliveTimeInterval(timeInterval);
        result.setObject(keepaliveSupport);

        context.write(result);
    }
}
