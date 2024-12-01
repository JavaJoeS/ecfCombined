package org.eclipse.ecf.internal.ssl;

import java.lang.reflect.*;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import javax.net.ssl.SSLContext;

public class ECFpwSubscriber implements Subscriber<String> {
	private Subscription subscription;
	private String name;
	private SSLContext sslContext;

	public ECFpwSubscriber() {
	}

	public ECFpwSubscriber(String name) {
		this.name = name;
		System.out.println("ECFpwSubscriber CONSTRUCTOR NAME:" + name);
	}

	public void onSubscribe(Subscription subscription) {
		// TODO Auto-generated method stub
		this.subscription = subscription;
		System.out.println("ECFpwSubscriber onSubscribe");
	}

	public void onNext(String item) {
		// TODO Auto-generated method stub
		System.out.println("ECFpwSubscriber onNext NAME:" + name);
		try {
			Class secClass = Class.forName("org.eclipse.core.pki.pkiselection.SecurityOpRequest");
			Method[] methods = secClass.getDeclaredMethods();
			Field[] fields = secClass.getDeclaredFields();
			Object[] constants = secClass.getEnumConstants();
			// for (Object constant : constants) {
			// System.out.println("ECFpwSubscriber - Constant " + constant);
			// }

			Method getMethod = secClass.getDeclaredMethod("getConnected");
			boolean result = (Boolean) getMethod.invoke(constants[0]);
			System.out.println("ECFpwSubscriber PKI Connected isSECURE:" + result);

			// ECFKeyStoreManager.getInstance().getSSLContext();
			try {

				Class pkiClass = Class.forName("org.eclipse.core.pki.auth.KeystoreSetup"); //$NON-NLS-1$
				Constructor pkiConstructor = pkiClass.getDeclaredConstructor();
				pkiConstructor.setAccessible(true);

				System.out.println("ECFpwSubscriber KeystoreSetup new instance");
				Object pkiManager = pkiClass.getDeclaredConstructor().newInstance();
				Method method = pkiManager.getClass().getMethod("isKeyStoreLoaded");
				boolean result1 = (Boolean) method.invoke(pkiManager, null);
				System.out.println("ECFpwSubscriber isKeyStoreLoaded method called");
				if (result1) {
					Method sslMethod = pkiManager.getClass().getMethod("getSSLContext");
					sslContext = (SSLContext) sslMethod.invoke(pkiManager, null);
					SSLContext.setDefault(sslContext);
					// System.out.println("ECFKeyStoreManager PKI is enabled
					// context
					// set");
					if (sslContext == null) {
						System.out.println("ECFpwSubscriber  SSLContext is NULL");
						sslContext = SSLContext.getDefault(); // available as
																// default too
					} else {
						System.out.println("ECFpwSubscriber  SSLContext is NOW set");
						ECFKeyStoreManager.getInstance().setSSLContext(sslContext);
					}
				} else {
					System.out.println("ECFpwSubscriber  Result of getSSLContext is FALSE");
				}
				System.out.println("ECFpwSubscriber  Completed PKI CHECK");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("ECFKeyStoreManager  No KEystore FOUND");
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
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			try {
//				IURLConnectionModifier urlConnectionModifier = null;
//				Class urlConnectionModifierClass = Class
//						.forName("org.eclipse.ecf.internal.provider.filetransfer.ssl.ECFURLConnectionModifier"); //$NON-NLS-1$
//				urlConnectionModifier = (IURLConnectionModifier) urlConnectionModifierClass.newInstance();
//
//				urlConnectionModifier.init(Activator.getDefault().getBundleContext());
//			} catch (ClassNotFoundException e) {
//				// will occur if fragment is not installed or not on proper
//				// execution environment
//				System.out.println("ECFpwSubscriber ECFURLConnectionModifier Class NOT found");
//			} catch (Throwable t) {
//				System.out.println("ECFpwSubscriber PKI failed");
//			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onError(Throwable throwable) {
		// TODO Auto-generated method stub
		System.out.println("ECFpwSubscriber onError");

	}

	public void onComplete() {
		// TODO Auto-generated method stub
		System.out.println("ECFpwSubscriber onComplete");
	}

}
