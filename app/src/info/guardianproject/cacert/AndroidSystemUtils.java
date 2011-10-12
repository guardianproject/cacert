package info.guardianproject.cacert;

import java.io.DataOutputStream;
import java.io.IOException;

public class AndroidSystemUtils {


    
    private final static String CMD_REMOUNT_RW = "grep \" /system \" /proc/mounts | awk '{system(\"mount -o rw,remount -t \"$3\" \"$1\" \"$2)}'";
    private final static String CMD_REMOUNT_RO = "grep \" /system \" /proc/mounts | awk '{system(\"mount -o ro,remount -t \"$3\" \"$1\" \"$2)}'";
    
    private final static String CMD_CHANGE_PERMS = "chmod 777";
    private final static String CMD_CHANGE_PERMS_ALL_READ = "chmod a+r";
    
	public static boolean remountRWandCopy (String srcPath, String targetPath) throws IOException
    {
    	
    	Process p = Runtime.getRuntime().exec("su");
    	DataOutputStream os = new DataOutputStream(p.getOutputStream());
    	
    	//remote system partition read write
    	os.writeBytes(CMD_REMOUNT_RW + '\n'); 
    	
    	//change perms on the target file for all to write
		os.writeBytes(CMD_CHANGE_PERMS + ' ' + targetPath + '\n');
		
		//copy the source file over the target
    	os.writeBytes("cp " + srcPath + ' ' + targetPath + '\n');
    	
    	//change perms on the target file to all read only
    	os.writeBytes(CMD_CHANGE_PERMS_ALL_READ + ' ' + targetPath + '\n');
    	
    	//remount file system read only
    	os.writeBytes(CMD_REMOUNT_RO + '\n'); 
    	
    	os.writeBytes("exit\n");
    	os.flush();
    	
    	
    	return true;
    	
    }
    
    public static void remountSystemRO () throws IOException
    {
    	Process p = Runtime.getRuntime().exec("su");
    	DataOutputStream os = new DataOutputStream(p.getOutputStream());
    	os.writeBytes(CMD_REMOUNT_RO + "\n");          
    	os.writeBytes("exit\n");  
    	os.flush();
    	
    }
}
