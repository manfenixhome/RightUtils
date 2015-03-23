package com.rightutils.rightutils.net;

import android.annotation.TargetApi;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.socket.LayeredConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.BrowserCompatHostnameVerifier;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

/**
 * Created by Anton Maniskevich on 8/27/14.
 */
public class TlsSniSocketFactory implements LayeredConnectionSocketFactory {
	private static final String TAG = TlsSniSocketFactory.class.getSimpleName();

	public final static TlsSniSocketFactory INSTANCE = new TlsSniSocketFactory();

	private final static SSLCertificateSocketFactory sslSocketFactory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(0);
	private final static HostnameVerifier hostnameVerifier = new BrowserCompatHostnameVerifier();


	@Override
	public Socket createSocket(HttpContext context) throws IOException {
		return sslSocketFactory.createSocket();
	}

	@Override
	public Socket connectSocket(int timeout, Socket plain, HttpHost host, InetSocketAddress remoteAddr, InetSocketAddress localAddr, HttpContext context) throws IOException {
		Log.d(TAG, "Preparing direct SSL connection (without proxy) to " + host);

		// we'll rather use an SSLSocket directly
		plain.close();

		// create a plain SSL socket, but don't do hostname/certificate verification yet
		SSLSocket ssl = (SSLSocket) sslSocketFactory.createSocket(remoteAddr.getAddress(), host.getPort());

		// connect, set SNI, shake hands, verify, print connection info
		connectWithSNI(ssl, host.getHostName());

		return ssl;
	}

	@Override
	public Socket createLayeredSocket(Socket plain, String host, int port, HttpContext context) throws IOException, UnknownHostException {
		Log.d(TAG, "Preparing layered SSL connection (over proxy) to " + host);

		// create a layered SSL socket, but don't do hostname/certificate verification yet
		SSLSocket ssl = (SSLSocket) sslSocketFactory.createSocket(plain, host, port, true);

		// already connected, but verify host name again and print some connection info
		Log.w(TAG, "Setting SNI/TLSv1.2 will silently fail because the handshake is already done");
		connectWithSNI(ssl, host);

		return ssl;
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void connectWithSNI(SSLSocket ssl, String host) throws SSLPeerUnverifiedException {
		// set reasonable SSL/TLS settings before the handshake:
		// - enable all supported protocols (enables TLSv1.1 and TLSv1.2 on Android <4.4.3, if available)
		ssl.setEnabledProtocols(ssl.getSupportedProtocols());

		// - set SNI host name
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Log.d(TAG, "Using documented SNI with host name " + host);
			sslSocketFactory.setHostname(ssl, host);
		} else {
			Log.d(TAG, "No documented SNI support on Android <4.2, trying with reflection");
			try {
				java.lang.reflect.Method setHostnameMethod = ssl.getClass().getMethod("setHostname", String.class);
				setHostnameMethod.invoke(ssl, host);
			} catch (Exception e) {
				Log.w(TAG, "SNI not useable", e);
			}
		}

		// verify hostname and certificate
		SSLSession session = ssl.getSession();
		if (!hostnameVerifier.verify(host, session))
			throw new SSLPeerUnverifiedException("Cannot verify hostname: " + host);

		Log.i(TAG, "Established " + session.getProtocol() + " connection with " + session.getPeerHost() +
				" using " + session.getCipherSuite());
	}

}
