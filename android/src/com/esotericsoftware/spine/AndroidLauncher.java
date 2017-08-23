package com.esotericsoftware.spine;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	private MyGdxGame myGdxGame;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(myGdxGame = new MyGdxGame(), config);
	}

	@Override
	public void onBackPressed() {
		myGdxGame.changeCharacter();
	}
}
