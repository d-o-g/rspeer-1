package org.rspeer.commons;

import java.io.*;

public class Util {

    public static void deleteRandomDat() {
        try {
            File f = new File(System.getProperty("user.home"), "random.dat");
            if (f.exists()) {
                f.setReadOnly();
            }
            if (f.exists()) {
                f.setWritable(true);
                RandomAccessFile raf = new RandomAccessFile(f, "rw");
                raf.seek(0);
                raf.write(new byte[24]);
                raf.seek(0);
                raf.write(0xff);
                raf.getChannel().force(true);
                raf.close();
                f.setReadOnly();
            } else {
                f.createNewFile();
            }
        } catch (Exception e) {

        }
    }
}
