package com.firstlinecode.sand.protocols.keepalive;

import com.firstlinecode.basalt.oxm.convention.annotations.ProtocolObject;
import com.firstlinecode.basalt.oxm.convention.validation.annotations.NotNull;
import com.firstlinecode.basalt.protocol.core.Protocol;

public class Keepalive {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    @ProtocolObject(namespace="urn:xmpp:keepalive", localName="negotiation")
    public static class KeepaliveNegotiation{
        public static final Protocol PROTOCOL = new Protocol("urn:xmpp:keepalive", "negotiation");
    }

    @ProtocolObject(namespace="urn:xmpp:keepalive", localName="keepalive-support")
    public static class KeepaliveSupport{
        public static final Protocol PROTOCOL = new Protocol("urn:xmpp:keepalive", "keepalive-support");
        @NotNull
        private String supported;

        private Long keepaliveTimeInterval;

        public String getSupported() {
            return supported;
        }

        public void setSupported(String supported) {
            this.supported = supported;
        }

        public Long getKeepaliveTimeInterval() {
            return keepaliveTimeInterval;
        }

        public void setKeepaliveTimeInterval(Long keepaliveTimeInterval) {
            this.keepaliveTimeInterval = keepaliveTimeInterval;
        }
    }
}
