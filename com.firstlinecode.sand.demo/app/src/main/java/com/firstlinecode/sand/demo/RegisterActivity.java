package com.firstlinecode.sand.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.firstlinecode.basalt.protocol.core.IError;
import com.firstlinecode.basalt.xeps.ibr.IqRegister;
import com.firstlinecode.basalt.xeps.ibr.RegistrationField;
import com.firstlinecode.basalt.xeps.ibr.RegistrationForm;
import com.firstlinecode.chalk.IChatClient;
import com.firstlinecode.chalk.android.StandardChatClient;
import com.firstlinecode.chalk.core.stream.NegotiationException;
import com.firstlinecode.chalk.core.stream.StandardStreamConfig;
import com.firstlinecode.chalk.xeps.ibr.IRegistration;
import com.firstlinecode.chalk.xeps.ibr.IRegistrationCallback;
import com.firstlinecode.chalk.xeps.ibr.IbrError;
import com.firstlinecode.chalk.xeps.ibr.IbrPlugin;
import com.firstlinecode.chalk.xeps.ibr.RegistrationException;

public class RegisterActivity extends AppCompatActivity {

	public static final int INTERNET_PERMISSION_REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	public void register(View view) {
		EditText etUserName = findViewById(R.id.userName);
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(this, getString(R.string.user_name_cant_be_null), Toast.LENGTH_LONG).show();
			etUserName.requestFocus();

			return;
		}

		EditText etPassword = findViewById(R.id.password);
		if (TextUtils.isEmpty(etPassword.getText().toString())) {
			Toast.makeText(this, getString(R.string.password_cant_be_null), Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		EditText etConfirmPassword = findViewById(R.id.confirmPassword);
		if (TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
			Toast.makeText(this, getString(R.string.confirm_password_cant_be_null), Toast.LENGTH_LONG).show();
			etConfirmPassword.requestFocus();

			return;
		}

		if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
			Toast.makeText(this, getString(R.string.password_not_equal_to_confirm_password), Toast.LENGTH_LONG).show();
			etPassword.requestFocus();

			return;
		}

		register(this, etUserName, etPassword);
	}

	private void register(Context context, final EditText userName, final EditText password) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
			try {
				register(userName.getText().toString(), password.getText().toString());
				Toast.makeText(context, context.getString(R.string.text_user_has_registered), Toast.LENGTH_LONG).show();
				finish();
			} catch (RegistrationException e) {
				IbrError error = e.getError();
				if (error == IbrError.CONFLICT) {
					Toast.makeText(context, context.getString(R.string.text_user_name_has_existed), Toast.LENGTH_LONG).show();
					userName.selectAll();
					userName.requestFocus();
				} else if (error == IbrError.NOT_ACCEPTABLE) {
					Toast.makeText(context, context.getString(R.string.text_invalid_user_name), Toast.LENGTH_LONG).show();
					userName.selectAll();
					userName.requestFocus();
				} else if (error == IbrError.CONNECTION_ERROR || error == IbrError.TIMEOUT) {
					Toast.makeText(context, context.getString(R.string.text_network_error), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, context.getString(R.string.text_unknown_error,
							 e.getCause() == null ? "No cause found" :  e.getCause().getClass().getName()),
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				NegotiationException ne = findNegotiationException(e);
				if (ne != null && ne.getAdditionalErrorInfo() instanceof IError) {
					IError error = (IError)ne.getAdditionalErrorInfo();
					Toast.makeText(context, context.getString(R.string.text_unknown_error,
							getErrorInfo(error)), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, context.getString(R.string.text_unknown_error, e.getClass().getName()), Toast.LENGTH_LONG).show();
				}
			}
		} else {
			requestPermissions(new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
		}

	}

	private String getErrorInfo(IError error) {
		if (error.getText() == null)
			return error.getDefinedCondition();
		else
			return String.format("%s - %s", error.getDefinedCondition(), error.getText());
	}

	private NegotiationException findNegotiationException(Exception e) {
		Throwable t = e;
		while (t.getCause() != null) {
			if (t.getCause() instanceof NegotiationException)
				return (NegotiationException)t.getCause();

			t = t.getCause();
		}

		return null;
	}

	private void register(final String userName, final String password) throws RegistrationException {
		IChatClient chatClient = createChatClient();
		IRegistration registration = chatClient.createApi(IRegistration.class);

		registration.register(new IRegistrationCallback() {
			@Override
			public Object fillOut(IqRegister iqRegister) {
				if (iqRegister.getRegister() instanceof RegistrationForm) {
					RegistrationForm form = new RegistrationForm();
					form.getFields().add(new RegistrationField("username", userName));
					form.getFields().add(new RegistrationField("password", password));

					return form;
				} else {
					throw new RuntimeException("Can't get registration form.");
				}
			}
		});
	}

	private IChatClient createChatClient() {
		StandardStreamConfig streamConfig = PreferencesUtils.getStreamConfig(this);
		IChatClient chatClient = new StandardChatClient(streamConfig);
		chatClient.register(IbrPlugin.class);

		return chatClient;
	}
}