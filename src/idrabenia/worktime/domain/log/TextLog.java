package idrabenia.worktime.domain.log;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.util.Log;

/**
 * @author Ilya Drabenia
 * @since 23.04.13
 */
public class TextLog implements Closeable {
	private static final boolean DISABLED = true;
    private BufferedWriter writer;

    public TextLog(String fileName) {
    	if (DISABLED) {
    		return;
    	}
    	
        try {
            File logFile = new File("sdcard/WorkTime/" + fileName);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void log(String message) {
    	if (DISABLED) {
    		return;
    	}
    	
        try {
            writer.append(new Date().toString()).append(" ").append(message).append("\n");
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
    	if (DISABLED) {
    		return;
    	}
    	
        try {
            writer.close();
        } catch (IOException ex) {
            Log.e("WorkTime", "Error on close writer in TextLog", ex);
        }
    }

}
