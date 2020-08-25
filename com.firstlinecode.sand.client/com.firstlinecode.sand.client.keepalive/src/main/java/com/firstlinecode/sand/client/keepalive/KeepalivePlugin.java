package com.firstlinecode.sand.client.keepalive;

import com.firstlinecode.basalt.oxm.convention.NamingConventionParserFactory;
import com.firstlinecode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.firstlinecode.basalt.protocol.core.ProtocolChain;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.chalk.IChatSystem;
import com.firstlinecode.chalk.IPlugin;
import com.firstlinecode.sand.client.things.IKeepaliver;
import com.firstlinecode.sand.protocols.keepalive.Keepalive;

import java.util.Properties;

public class KeepalivePlugin implements IPlugin {
    @Override
    public void init(IChatSystem chatSystem, Properties properties) {
        chatSystem.registerTranslator(
                Keepalive.KeepaliveNegotiation.class,
                new NamingConventionTranslatorFactory<>(
                        Keepalive.KeepaliveNegotiation.class
                )
        );
        chatSystem.registerParser(
                ProtocolChain.first(Iq.PROTOCOL).next(Keepalive.KeepaliveSupport.PROTOCOL),
                new NamingConventionParserFactory<>(
                        Keepalive.KeepaliveSupport.class
                )
        );

        chatSystem.registerApi(IKeepaliver.class, Keepaliver.class, properties);
    }

    @Override
    public void destroy(IChatSystem chatSystem) {

    }
}
