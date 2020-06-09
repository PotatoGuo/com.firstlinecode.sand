package com.firstlinecode.sand.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	public void startRegisterActivity(View view) {
		startActivity(new Intent(this, RegisterActivity.class));
	}

	public void startConfigureStreamActivity(View view) {
		startActivity(new Intent(this, ConfigureStreamActivity.class));
	}
}