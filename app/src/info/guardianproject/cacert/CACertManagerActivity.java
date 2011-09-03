package info.guardianproject.cacert;


import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CACertManagerActivity extends Activity {
	
	public static final String TAG = "CACert";
	
	private final static String CACERT_SYSTEM_PATH = "/system/etc/security/cacerts.bks";
	private final static String CACERT_BACKUP_PATH = "mycacerts.bks";
	private final static String CACERT_TMP_PATH = "tmpcacerts.bks";
	
	private final static String DEFAULT_PASS = "changeit";
	
	private CACertManager certMan;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.main);
        
        try
        {
        	certMan = new CACertManager ();
	        certMan.load(CACERT_SYSTEM_PATH, DEFAULT_PASS);
	       
	       
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }
    
    public void listCertificates () throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException
    {
    
    	TextView txtCa = (TextView)this.findViewById(R.id.cabox);
    	txtCa.setText("");
    	
        Enumeration<String> e = certMan.getCertificateAliases();
        while (e.hasMoreElements()) {
        	
        	StringBuffer out = new StringBuffer();
        	
            String alias = e.nextElement();
            out.append("Alias: ").append(alias).append('\n');
            
            X509Certificate cert = (X509Certificate) certMan.getCertificate(alias);
            
            out.append("Serial: ").append(cert.getSerialNumber()).append('\n');
            out.append("IssuerDN: ").append(cert.getIssuerDN().toString()).append('\n');
            out.append("SubjectDN: ").append(cert.getSubjectDN().toString()).append('\n');
            out.append("Expires: ").append(cert.getNotAfter().toGMTString()).append('\n');

            
            X500Principal subject = cert.getSubjectX500Principal();
            X500Principal issuer = cert.getIssuerX500Principal();
            
            txtCa.append(out.toString());
            txtCa.append("\n--------------------------------\n");
            
           
        }
    }
    
    private void deleteAlias (String alias)
    {
    	try
    	{
            X509Certificate cert = (X509Certificate) certMan.getCertificate(alias);
    		certMan.delete(alias);
    		showAlert("Success! Cert '" + cert.getSerialNumber() + "' has been eradicated!");
    	}
    	catch (Exception e)
    	{
    		showAlert("error deleting cert: " + e.getMessage());
    	}
    }
    private void doDelete ()
    {
    	 LayoutInflater factory = LayoutInflater.from(this);
         final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
         new AlertDialog.Builder(this)
             .setTitle(getString(R.string.app_name))
             .setView(textEntryView)
             .setMessage("Enter a cert alias to delete")
             .setPositiveButton(("Delete"), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                 	EditText eText = ((android.widget.EditText)textEntryView.findViewById(R.id.password_edit));
                 	String alias = eText.getText().toString();
                 	
                 	deleteAlias(alias);
                 	
                 }
             })
             .setNegativeButton(("Cancel"), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                 }
             })
             .create().show();
    }
    
    private void saveKeystore ()
    {

    	try
    	{
    		File tmpFile = new File(getFilesDir(),CACERT_TMP_PATH);
    		String tmppath = tmpFile.getAbsolutePath();
    		certMan.save(tmppath, DEFAULT_PASS);
    		
    		certMan.remountAndCopy(tmppath, CACERT_SYSTEM_PATH);
    		
    		showAlert("Success! CACert keystore saved to /system");
    		
    		tmpFile.delete();
    		
    	}
    	catch (Exception e)
    	{
    		showAlert("Failure to save: " + e.getMessage());
    	}
    }
    
    private void backupKeystore ()
    {

    	try
    	{
    		String path = new File(getFilesDir(),CACERT_BACKUP_PATH).getAbsolutePath();
    		certMan.save(path, DEFAULT_PASS);
    		
    		showAlert("Success! CACert keystore saved to: " + path);
    	}
    	catch (Exception e)
    	{
    		showAlert("Failure to save: " + e.getMessage());
    	}
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		try 
		{
			showAlert("Loading CACert keystore from /system");
			listCertificates();
		}
		catch (Exception e)
		{
			showAlert("Error loading certs: " + e.getMessage());
		}
	}
	
	private void showAlert (String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		
	}
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.main, menu);
	        
	        
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {

	        	
	    	case R.id.menu_save:
	    		saveKeystore ();
	    		return true;
	    		
	    	case R.id.menu_backup:
	    		backupKeystore ();
	    		return true;
	    		
	    	case R.id.menu_delete:
	    		doDelete();
	    		return true;
	    		
	    	}
	    	return super.onOptionsItemSelected(item);
	    }
    
}
