package info.guardianproject.cacert;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SSLTestActivity extends Activity {
	
	public static final String TAG = "CACert";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        try {
			CustomTrust cTrust = new CustomTrust(this, R.raw.cacerts, "changeit");
			
			Socket s = cTrust.createSocket("google.com", 443);
			
		} catch (KeyManagementException e) {
			Log.e(TAG, e.getMessage(), e);
			
		} catch (KeyStoreException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (CertificateException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
    }
    
    
}