/* Copyright (c) 2011, Nathan Freitas,/ The Guardian Project - https://guardianproject.info */
/* See LICENSE for licensing information */

package info.guardianproject.cacert;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.scheme.HostNameResolver;


import android.content.Context;
import android.util.Log;

public class CustomTrust
{

	SSLContext ssl_ctx;
    SSLSocketFactory socketFactory;
    
    private static final String RANDOM_ALGORITHM = "SHA1PRNG";

	public CustomTrust (Context context, int rawResource, String password) throws IOException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException, CertificateException
	{
		
        // Setup the SSL context to use the truststore
        ssl_ctx = SSLContext.getInstance("TLS");
        
		 // Setup truststore
        KeyStore ksCACert = KeyStore.getInstance("BKS");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        InputStream trustStoreStream = context.getResources().openRawResource(rawResource);
        ksCACert.load(trustStoreStream, password.toCharArray());
        
        //init factory with custom cacert
        trustManagerFactory.init(ksCACert);
        Log.d("SSL", "CACerts " + ksCACert.size());
        Log.d("SSL", "trustManagerFactory " + trustManagerFactory.getTrustManagers().length);

        // Setup client keystore
        /*
        KeyStore keyStore = KeyStore.getInstance("BKS");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        InputStream keyStoreStream = context.getResources().openRawResource(R.raw.clientkeystore);
        keyStore.load(keyStoreStream, "testtest".toCharArray());
        keyManagerFactory.init(keyStore, "testtest".toCharArray());
        Log.d("SSL", "Key " + keyStore.size());
        
        Log.d("SSL", "keyManagerFactory " + keyManagerFactory.getKeyManagers().length);
        */

        //nothing implemented yet
        SecureRandom secRand = SecureRandom.getInstance(RANDOM_ALGORITHM);
        
        ssl_ctx.init(null, trustManagerFactory.getTrustManagers(), secRand);

        socketFactory = (SSLSocketFactory) ssl_ctx.getSocketFactory();
		
	}
	
	public SSLContext getSSLContext ()
	{
		return ssl_ctx;
	}
	

	public Socket createSocket() throws IOException {
		return socketFactory.createSocket();
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return socketFactory.createSocket(host, port);
	}
	
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return socketFactory.createSocket(socket, host, port, true);
	}

	public boolean isSecure(Socket sock) throws IllegalArgumentException {
		return (sock instanceof SSLSocket);
	}
}
