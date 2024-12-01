package org.eclipse.ecf.internal.ssl;

import java.lang.reflect.*;
//import java.net.Socket;
import java.security.NoSuchAlgorithmException;
//import java.security.cert.X509Certificate;
import java.util.concurrent.Flow.Subscriber;
import javax.net.ssl.SSLContext;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

public class ECFKeyStoreManager implements BundleActivator {
	private static ECFKeyStoreManager INSTANCE = null;
	private static volatile BundleContext context;
	private SSLContext sslContext;
	private volatile ServiceTracker keyManagerTracker = null;
	private ServiceRegistration keyStoreFactoryRegistration;

	public static ECFKeyStoreManager getInstance() {
		try {
			if (INSTANCE == null) {
				INSTANCE = new ECFKeyStoreManager();
				System.out.println("ECFKeyStoreManager new INSTANCE");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return INSTANCE;
	}

	public void setSSLContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	public SSLContext getSSLContext() {
		System.out.println("ECFKeyStoreManager START getSSLContext");
		// getPW();

		// setSubscriber();
		if (sslContext == null) {
			// setSubscriber();
			getSecurityOp();
			try {

				Class pkiClass = Class.forName("org.eclipse.core.pki.auth.KeystoreSetup"); //$NON-NLS-1$

				Constructor[] constructors = pkiClass.getDeclaredConstructors();
				Constructor<?> privateConstructor = constructors[0];
				privateConstructor.setAccessible(true);
				for (Constructor c : constructors) {
					System.out.println("ECFKeyStoreManager CONST:" + c.getName());
				}

				// Object pkiManager =
				// pkiClass.getDeclaredConstructor().newInstance();
				Object pkiManager = privateConstructor.newInstance();
				Method myInstance = pkiClass.getDeclaredMethod("getInstance", null);

				Object myObject = myInstance.invoke(pkiManager);

				Method method = pkiManager.getClass().getMethod("isKeyStoreLoaded");
				boolean result = (Boolean) method.invoke(pkiManager, null);
				if (result) {
					Method sslMethod = pkiManager.getClass().getDeclaredMethod("getSSLContext");
					sslContext = (SSLContext) sslMethod.invoke(pkiManager, null);

					if (sslContext == null) {
						System.out.println("ECFKeyStoreManager  SSLContext is NULL");
						sslContext = SSLContext.getDefault(); // available as
																// default too
					}
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sslContext;
	}

	public void getSecurityOp() {
		try {
			Class secClass = Class.forName("org.eclipse.core.pki.pkiselection.SecurityOpRequest");
			Method[] methods = secClass.getDeclaredMethods();
			Field[] fields = secClass.getDeclaredFields();
			Object[] constants = secClass.getEnumConstants();
			// for (Object constant : constants) {
			// System.out.println("ECFKeyStoreManager - Constant " + constant);
			// }

			Method getMethod = secClass.getDeclaredMethod("getConnected");
			boolean b = (Boolean) getMethod.invoke(constants[0]);
			System.out.println("ECFKeyStoreManager PKI INSTALLED:" + b);
			if (b) {
				SSLContext.getDefault();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("ECFKeyStoreManager  No PKI FOUND");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getPW() {
		try {
			Class pubClass = Class.forName("org.eclipse.ecf.internal.ssl.ECFpwSubscriber"); //$NON-NLS-1$
			Constructor constructor = pubClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object obj = constructor.newInstance();
			try {
				Subscriber ecfSubscriber = (Subscriber) obj;
				// up.subscribe(ecfSubscriber);
			} catch (Exception e) {

				System.out.println("ECFKeyStoreManager - ECF Object Failed"); //$NON-NLS-1$
			}
			System.out.println("ECFKeyStoreManager - Loaded ECF SUBSCRIBER."); //$NON-NLS-1$
		} catch (Exception e) {

			System.out.println("ECFKeyStoreManager - Cant get ECF:"); //$NON-NLS-1$
		}
	}

	public boolean setSubscriber() {
		try {
			System.out.println("ECFKeyStoreManager - setSubscriber");
			if (!(ECFSubscriberProperty.INSTANCE.isSubscribed())) {
				ECFSubscriberProperty.INSTANCE.setSubscribed(true);
				ECFpwSubscriber subscriber1 = new ECFpwSubscriber("ECF");
				Class pubClass = Class.forName("org.eclipse.core.pki.auth.PublishPasswordUpdate"); //$NON-NLS-1$
				Constructor constructor = pubClass.getDeclaredConstructor();
				constructor.setAccessible(true);
				Object obj = constructor.newInstance();

				Method myInstance = pubClass.getDeclaredMethod("getInstance", null);
				Object myObject = myInstance.invoke(obj);

				Method mo = myObject.getClass().getDeclaredMethod("subscribe", Subscriber.class);
				mo.invoke(myObject, subscriber1);
				// ECFSubscriberProperty.INSTANCE.setSubscribed(true);
				// System.out.println("ECFKeyStoreManager
				// SUBSCRIBED-------method-----Called:");
				ECFpwSubscriber subscriber2 = new ECFpwSubscriber("FILEXfer");
				mo.invoke(myObject, subscriber2);
				System.out.println("ECFKeyStoreManager - setSubscriber DONE");
				return true;
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("ECFKeyStoreManager  No PKI FOUND");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	// @Override
	public void start(BundleContext contextIn) throws Exception {
		// TODO Auto-generated method stub
		ECFKeyStoreManager.context = contextIn;
		System.out.println("ECFKeyStoreManager start bundle");
		// ECFKeyStoreManager man = ECFKeyStoreManager.getInstance();
		// man.setSubscriber();
		// keyStoreFactoryRegistration =
		// contextIn.registerService(SSLSocketFactory.class.getName(),
		// new ECFSSLSocketFactory(), null);
		// The following locks up eclipse
		ECFKeyStoreManager man = ECFKeyStoreManager.getInstance();
		// man.setSubscriber(); // locks eclipse up
		System.out.println("ECFKeyStoreManager bundle done loading");
	}

	// @Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		if (keyStoreFactoryRegistration != null) {
			keyStoreFactoryRegistration.unregister();
			keyStoreFactoryRegistration = null;
		}
		if (keyManagerTracker != null) {
			keyManagerTracker.close();
			keyManagerTracker = null;
		}
		ECFKeyStoreManager.context = null;
	}
}
