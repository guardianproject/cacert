/* Copyright (c) 2011, Nathan Freitas,/ The Guardian Project - https://guardianproject.info */
/* See LICENSE for licensing information */

package info.guardianproject.cacert;


import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CACertManagerActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	
	public static final String TAG = "CACert";
	
	private final static String CACERT_SYSTEM_PATH = "/system/etc/security/cacerts.bks";
	private final static String CACERT_BACKUP_PATH = "mycacerts.bks";
	private final static String CACERT_TMP_PATH = "tmpcacerts.bks";
	
	private final static String DEFAULT_PASS = "changeit";
	
	private CACertManager mCertMan;
	
    private ListView mListCerts;

    private  ArrayList<X509Certificate> alCerts;
    private X509Certificate mSelectedCert;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.main);

        String version = "";
        
        try
        {
        	version = " v" + getPackageManager().getPackageInfo(this.getPackageName(), 0 ).versionName;
        }
        catch (Exception e){}
        
        this.setTitle(getString(R.string.app_name) + version);
        
        mListCerts = (ListView)findViewById(R.id.listCerts);

	    mListCerts.setOnItemClickListener(this);
	    mListCerts.setOnItemLongClickListener(this);
        
        try
        {
        	mCertMan = new CACertManager ();
        	
          
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }
    
    
    
    private void loadList (String keyword) throws Exception
    {
    	
    	  Enumeration<String> aliases = mCertMan.getCertificateAliases();
          
          alCerts = new ArrayList<X509Certificate>();
          
          while (aliases.hasMoreElements())
          {
        	  X509Certificate cert = (X509Certificate)mCertMan.getCertificate(aliases.nextElement());
        	  
        	  if (keyword == null || keyword.length() == 0)
        		  alCerts.add(cert);
        	  else
        	  {
        		  if (cert.getIssuerDN().toString().indexOf(keyword)!=-1
        			  && cert.getSubjectDN().toString().indexOf(keyword)!=-1)
        			  alCerts.add(cert);
        	  }
          }
          
          if (alCerts.size() == 0)
          {
        	  Toast.makeText(this, getString(R.string.no_certificates_matched_the_search), Toast.LENGTH_SHORT).show();
          }
         
          String[] names = new String[alCerts.size()];
          int i = 0;
          
          for (X509Certificate cert : alCerts)
          {
          	names[i++] = processCert(cert);
          			
          }
          
          mListCerts.setAdapter(new ArrayAdapter<String>(this,
       				android.R.layout.simple_list_item_1, names));
          
       		
    }
    
    private String processCert(X509Certificate cert)
    {
    	StringBuffer buff = new StringBuffer();
    	
    	String name = cert.getSubjectDN().getName();
    	
    	name=name.substring(name.indexOf("CN=")+3);
    	
    	if (name.indexOf(";")!=-1)
    		name=name.substring(0,name.indexOf(";"));
    	
    	buff.append(name);
    	
    	return buff.toString();
    }
    
   
    private void deleteCertificate (Certificate cert)
    {
    	try
    	{
            mCertMan.delete(cert);
    		showAlert(getString(R.string.success_remove));
    		
    		loadList(null);
    	}
    	catch (Exception e)
    	{
    		showAlert(getString(R.string.error_deleting_cert) + e.getMessage());
    	}
    }
    
    private void doSearch ()
    {
    	 LayoutInflater factory = LayoutInflater.from(this);
         final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
         new AlertDialog.Builder(this)
             .setTitle(getString(R.string.app_name))
             .setView(textEntryView)
             .setMessage(getString(R.string.enter_keyword_to_search_for))
             .setPositiveButton(getString(R.string.search), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                 	EditText eText = ((android.widget.EditText)textEntryView.findViewById(R.id.dialog_edit));
                 	String keyword = eText.getText().toString();
                 	
                 	try
                 	{
                 		loadList(keyword);
                 	}
                 	catch (Exception e){}
                 	
                 }
             })
             .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                 }
             })
             .create().show();
    }
    
   
    private void saveKeystore ()
    {

    	try
    	{
    		
    		mCertMan.remountRW(CACERT_SYSTEM_PATH);
    		mCertMan.save(CACERT_SYSTEM_PATH, DEFAULT_PASS);
    		
    		showAlert(getString(R.string.success_cacert_keystore_saved_to_system) + ' ' + CACERT_SYSTEM_PATH);
    		
    		
    	}
    	catch (Exception e)
    	{
    		showAlert(getString(R.string.failure_to_save) + e.getMessage());
    		Log.e(TAG,"error saving",e);
    	}
    }
    
    private void backupKeystore ()
    {

    	try
    	{
    		File fileBak = new File(getFilesDir(),CACERT_BACKUP_PATH);
    		
    		fileBak.mkdirs();
    		mCertMan.save(fileBak.getAbsolutePath(), DEFAULT_PASS);

    		showAlert(getString(R.string.success_system_cacert_keystore_backed_up_to) + fileBak.getAbsolutePath());
    	}
    	catch (Exception e)
    	{
    		showAlert(getString(R.string.failure_to_save) + e.getMessage());
    	}
    }
    
    private void restoreKeystore ()
    {

    	try
    	{
    		String bakPath = new File(getFilesDir(),CACERT_BACKUP_PATH).getAbsolutePath();

    		mCertMan.remountRW(CACERT_SYSTEM_PATH);
    		
    		mCertMan.load(bakPath, DEFAULT_PASS);
    		mCertMan.save(CACERT_SYSTEM_PATH, DEFAULT_PASS);
    		
    		showAlert(getString(R.string.success_system_cacert_restored_from) + bakPath);
    	}
    	catch (Exception e)
    	{
    		showAlert(getString(R.string.failure_to_save) + e.getMessage());
    	}
    }


	@Override
	protected void onResume() {
		super.onResume();
		
		try 
		{
			showAlert(getString(R.string.loading_cacert_keystore_from_system));

			mCertMan.load(CACERT_SYSTEM_PATH, DEFAULT_PASS);
			loadList(null);
			
		}
		catch (Exception e)
		{
			showAlert(getString(R.string.error_loading_certs) + e.getMessage());
			Log.e(TAG,"error loading",e);
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
	    		
	    	case R.id.menu_restore:
	    		restoreKeystore ();
	    		return true;
	    		
	    	case R.id.menu_search:
	    		doSearch();
	    		return true;
	    		
	    	case R.id.menu_about:
	    		showDialog(getString(R.string.about));
	    		return true;
	    	
	    	case R.id.menu_help:
	    		showDialog(getString(R.string.help));
	    		return true;
	    	}
	    	return super.onOptionsItemSelected(item);
	    }



		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			mSelectedCert = alCerts.get(arg2);
			
			 new AlertDialog.Builder(this)
             .setTitle(getString(R.string.app_name))
             .setMessage(getString(R.string.are_you_sure_delete) + processCert(mSelectedCert))
             .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                 	deleteCertificate(mSelectedCert);
                 	mSelectedCert = null;
                 	
                 }
             })
             .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                 }
             })
             .create().show();
			
			return true;
		}



		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mSelectedCert = alCerts.get(arg2);
			
			StringBuffer message = new StringBuffer();
			
			message.append(mSelectedCert.getSubjectDN().toString());
			message.append("\n");
			
			message.append(mSelectedCert.getIssuerDN().toString());
			message.append("\n");
			
			message.append(getString(R.string.serial));
			message.append(' ');
			message.append(mSelectedCert.getSerialNumber());
			message.append("\n");
			
			message.append(getString(R.string.expires));
			message.append(' ');
			message.append(mSelectedCert.getNotAfter().toGMTString());
			message.append("\n");
			
			
			 new AlertDialog.Builder(this)
             .setTitle(getString(R.string.app_name))
             .setMessage(message.toString())
             .create().show();
		}
		
		private void showDialog (String msg)
		{
			 new AlertDialog.Builder(this)
             .setTitle(getString(R.string.app_name))
             .setMessage(msg)
             .create().show();
		}


		@Override
		protected void onDestroy() {
			
			super.onDestroy();
			
			try { mCertMan.remountSystemRO(); }
			catch (Exception e){}
			
		}
    
	  
}
