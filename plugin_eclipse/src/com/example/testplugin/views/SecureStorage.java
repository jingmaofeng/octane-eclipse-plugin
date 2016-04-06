package com.example.testplugin.views;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import com.hpe.nga.ide.restclient.CredentialsStore;

public class SecureStorage implements CredentialsStore {

	private static SecureStorage instance;
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_LOCATION = "location";
	private static final String KEY_USER = "user";
	private static final String KEY_STATE = "keyState";
	private static final String DEFAULT_VALUE = "";

	private SecureStorage() {
		super();
	}

	public static synchronized SecureStorage getInstance() {
		if (instance == null) {
			instance = new SecureStorage();
		}
		return instance;
	}

	@Override
	public String getPassword() {
		String password = DEFAULT_VALUE;
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		try {
			password = node.get(KEY_PASSWORD, DEFAULT_VALUE);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return password;
	}

	@Override
	public String getUserName() {
		String userName = DEFAULT_VALUE;
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		try {
			userName = node.get(KEY_USER, DEFAULT_VALUE);
		} catch (StorageException e) {
			e.printStackTrace();
		}
		return userName;
	}

	public String getState() {
		String state = DEFAULT_VALUE;
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		try {
			state = node.get(KEY_STATE, DEFAULT_VALUE);
		} catch (StorageException e) {
			e.printStackTrace();
		}
		return state;
	}

	public String getLocation() {
		String location = DEFAULT_VALUE;
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		try {
			location = node.get(KEY_LOCATION, DEFAULT_VALUE);
		} catch (StorageException e) {
			e.printStackTrace();
		}
		return location;
	}

	public void setUserName(String userName) {
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		try {
			node.put(KEY_USER, userName, true);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}

	public void setLocation(String location) {
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		try {
			node.put(KEY_LOCATION, location, true);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}

	public void setPassword(String password) {
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		try {
			node.put(KEY_PASSWORD, password, true);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}

	public void setState(Boolean state) {
		ISecurePreferences node = SecurePreferencesFactory.getDefault().node("cvs/eclipse.org");
		if (state) {
			try {
				node.put(KEY_STATE, "1", true);
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				node.put(KEY_STATE, "0", true);
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
