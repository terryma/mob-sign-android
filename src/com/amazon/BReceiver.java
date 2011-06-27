package com.amazon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BReceiver extends BroadcastReceiver {

	public Test2 test2;

	public BReceiver(Test2 test2) {
		this.test2 = test2;
	}

	@Override
	public void onReceive(Context arg0, Intent i) {
		test2.updateForIntent(i);
	}

}
