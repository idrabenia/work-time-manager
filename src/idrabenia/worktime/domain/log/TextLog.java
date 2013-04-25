package idrabenia.worktime.domain.log;

import android.util.Log;

import java.io.*;
import java.util.Date;

/**
 * @author Ilya Drabenia
 * @since 23.04.13
 */
public class TextLog implements Closeable {
    private BufferedWriter writer;

    public TextLog(String fileName) {
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
        try {
            writer.append(new Date().toString()).append(" ").append(message).append("\n");
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException ex) {
            Log.e("WorkTime", "Error on close writer in TextLog", ex);
        }
    }

}