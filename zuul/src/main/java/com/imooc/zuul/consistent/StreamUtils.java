package com.imooc.zuul.consistent;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamUtils {
    public static byte[] getByteByStream(InputStream is) throws Exception {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.flush();
        return bos.toByteArray();
    }
}