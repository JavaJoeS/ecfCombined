package org.eclipse.ecf.internal.ssl;

public enum ECFSubscriberProperty {
	INSTANCE;

	public boolean isSubscribed = false;

	public boolean isSubscribed() {
		return isSubscribed;
	}

	public void setSubscribed(boolean isSubscribed) {
		this.isSubscribed = isSubscribed;
	}
}
