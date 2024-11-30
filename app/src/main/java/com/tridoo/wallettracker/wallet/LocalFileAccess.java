package com.tridoo.wallettracker.wallet;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LocalFileAccess {
    private final Context ctx;

    public LocalFileAccess(Context ctx) {
        this.ctx = ctx;
    }

    public List<String> readSavedData(String fileName) {
        List<String> result = new ArrayList<>();
        try {
            if (!ctx.getFileStreamPath(fileName).exists()) {
                Log.i(LocalFileAccess.class.getName(), "brak pliku");
                return result;
            }
            FileInputStream fileInput = ctx.openFileInput(fileName);
            InputStreamReader inputStream = new InputStreamReader(fileInput);
            BufferedReader reader = new BufferedReader(inputStream);

            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }

            inputStream.close();
        } catch (IOException e) {
            Log.e(LocalFileAccess.class.getName(), "IOException", e);
        }
        return result;
    }

    public void writeData(String data, String fileName, int mode) {
        try {
            FileOutputStream fOut = ctx.openFileOutput(fileName, mode);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data + System.lineSeparator());
            osw.flush();
            osw.close();
        } catch (Exception e) {
            Log.e(LocalFileAccess.class.getName(), "writeData", e);
        }
        Log.i(LocalFileAccess.class.getName(), "sukces " + fileName);
    }

    public void deleteFile(String fileName) {
        File file = new File(ctx.getFilesDir(), fileName);
        if (!file.exists()) {
            Log.w(LocalFileAccess.class.getName(), "brak pliku: " + fileName);
            return;
        }

        if (file.delete()) {
            Log.i(LocalFileAccess.class.getName(), "plik usunięty: " + fileName);
        } else {
            Log.w(LocalFileAccess.class.getName(), "plik nieusunięty: " + fileName);
        }
    }


}
