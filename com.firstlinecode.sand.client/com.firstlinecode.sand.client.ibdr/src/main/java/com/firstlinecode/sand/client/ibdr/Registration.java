package com.firstlinecode.sand.client.ibdr;

import java.util.ArrayList;
import java.util.List;

import com.firstlinecode.basalt.protocol.core.IError;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.Stanza;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAuthorized;
import com.firstlinecode.basalt.protocol.core.stanza.error.RemoteServerTimeout;
import com.firstlinecode.chalk.AuthFailureException;
import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.ISyncTask;
import com.firstlinecode.chalk.IUnidirectionalStream;
import com.firstlinecode.chalk.core.ErrorException;
import com.firstlinecode.chalk.core.stream.INegotiationListener;
import com.firstlinecode.chalk.core.stream.IStream;
import com.firstlinecode.chalk.core.stream.IStreamNegotiant;
import com.firstlinecode.chalk.core.stream.NegotiationException;
import com.firstlinecode.chalk.core.stream.StandardStreamConfig;
import com.firstlinecode.chalk.network.ConnectionException;
import com.firstlinecode.chalk.network.IConnectionListener;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.protocols.ibdr.DeviceRegister;

public class Registration implements IRegistration, IConnectionListener, INegotiationListener {
	private StandardStreamConfig streamConfig;
	private List<IConnectionListener> connectionListeners = new ArrayList<>();
	private List<INegotiationListener> negotiationListeners = new ArrayList<>();
	
	private boolean dontThrowConnectionException = false;
	
	@Override
	public DeviceIdentity register(String deviceId) throws RegistrationException {
		IChatClient chatClient = new IbdrChatClient(streamConfig);
		chatClient.register(InternalIbdrPlugin.class);
		
		chatClient.addConnectionListener(this);
		chatClient.addNegotiationListener(this);
		
		try {
			chatClient.connect(null);
		} catch (ConnectionException e) {
			if (!chatClient.isClosed())
				chatClient.close();
			
			throw new RegistrationException(IbdrError.CONNECTION_ERROR, e);
		} catch (AuthFailureException e) {
			// it's impossible
		}
		
		try {
			return chatClient.getChatServices().getTaskService().execute(new RegisterTask(deviceId));
		} catch (ErrorException e) {
			IError error = e.getError();
			if (error.getDefinedCondition().equals(RemoteServerTimeout.DEFINED_CONDITION)) {
				throw new RegistrationException(IbdrError.TIMEOUT);
			} else if (error.getDefinedCondition().equals(NotAcceptable.DEFINED_CONDITION)) {
				throw new RegistrationException(IbdrError.NOT_ACCEPTABLE);
			} else if (error.getDefinedCondition().equals(NotAuthorized.DEFINED_CONDITION)) {
				throw new RegistrationException(IbdrError.NOT_AUTHORIZED);
			} else if (error.getDefinedCondition().equals(Conflict.DEFINED_CONDITION)) {
				throw new RegistrationException(IbdrError.CONFLICT);
			} else {
				throw new RegistrationException(IbdrError.UNKNOWN, e);
			}
		} finally {
			if (!chatClient.isClosed()) {
				// To avoid throw ConnectionException.CONNECTION_CLOSED.
				dontThrowConnectionException = true;
				chatClient.close();
			}
		}
	}
	
	private class RegisterTask implements ISyncTask<Iq, DeviceIdentity> {
		private DeviceRegister deviceRegister;
		
		public RegisterTask(String deviceId) {
			deviceRegister = new DeviceRegister();
			deviceRegister.setRegister(deviceId);
		}

		@Override
		public void trigger(IUnidirectionalStream<Iq> stream) {
			Iq iq = new Iq(deviceRegister, Iq.Type.SET, Stanza.generateId("ibdr"));
			iq.setObject(deviceRegister);
			
			stream.send(iq);
		}

		@Override
		public DeviceIdentity processResult(Iq iq) {
			DeviceRegister register = iq.getObject();
			return (DeviceIdentity)register.getRegister();
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Feature not implemented.");
	}

	@Override
	public void addConnectionListener(IConnectionListener listener) {
		connectionListeners.add(listener);
	}

	@Override
	public void removeConnectionListener(IConnectionListener listener) {
		connectionListeners.remove(listener);
	}

	@Override
	public void addNegotiationListener(INegotiationListener listener) {
		negotiationListeners.add(listener);
	}

	@Override
	public void removeNegotiationListener(INegotiationListener listener) {
		negotiationListeners.remove(listener);
	}

	@Override
	public void before(IStreamNegotiant source) {
		for (INegotiationListener negotiationListener : negotiationListeners) {
			negotiationListener.before(source);
		}
	}

	@Override
	public void after(IStreamNegotiant source) {
		for (INegotiationListener negotiationListener : negotiationListeners) {
			negotiationListener.after(source);
		}
	}

	@Override
	public void occurred(NegotiationException exception) {
		for (INegotiationListener negotiationListener : negotiationListeners) {
			negotiationListener.occurred(exception);
		}
	}

	@Override
	public void done(IStream stream) {
		for (INegotiationListener negotiationListener : negotiationListeners) {
			negotiationListener.done(stream);
		}
	}

	@Override
	public void occurred(ConnectionException exception) {
		if (dontThrowConnectionException)
			return;
		
		for (IConnectionListener connectionListener : connectionListeners) {
			connectionListener.occurred(exception);
		}
	}

	@Override
	public void received(String message) {
		for (IConnectionListener connectionListener : connectionListeners) {
			connectionListener.received(message);
		}
	}

	@Override
	public void sent(String message) {
		for (IConnectionListener connectionListener : connectionListeners) {
			connectionListener.sent(message);
		}
	}

}
