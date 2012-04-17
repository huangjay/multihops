package discover.activity;

import java.io.File;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class File_discoveryActivity extends Activity {
	
	public boolean isFileExist; //the file is in the phone
	       
	boolean isInExternal, //the file is in the external storage of the phone (SD card)
	        isInInternal; //the file is in the internal storage of the phone 
	
	String myFile = "music.txt";
	String state = Environment.getExternalStorageState();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        checkInternal(myFile);
        if(checkSD()){
        	checkExternal(myFile);  
        }
    }
    
    //check if the file is in the internal storage of the phone
    void checkInternal(String fileName){
    	File file = new File(this.getFileStreamPath(fileName).toString());
 
    	if (file.exists()) {
    		isInInternal = true;
    	}
    	else{
    		isInInternal = false;
    	}
    }
    
    //check if the file is in the SD card
    void checkExternal(String fileName) {
    	String sdDir = getExternalFilesDir(null).getPath();
    	File file = new File(sdDir + File.separator + fileName);
   
    	if (file.exists()) {
    		isInExternal=true;
    	}
    	else{
    		isInExternal=false;
    	}
    }
    
    //check if SD card is available
    boolean checkSD(){
    	boolean isSDavailable;
    	if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    		isSDavailable = true;
    	} else {
    		isSDavailable = false;
    	}
    	return isSDavailable;
    } 
    
    boolean checkFileExist(){
    	if (isInInternal || isInExternal){
    		isFileExist = true;
    	}
    	else {
    		isFileExist = false;
    	}
    	return isFileExist;
    }
}

