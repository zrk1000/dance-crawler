package com.zrk1000.crawler;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.junit.Test;

/**
 * @author zhouRongKang
 * @date 2017/11/9 14:53
 */
public class TikaTest {

    @Test
    public void tika() throws MimeTypeException {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType jpeg = allTypes.forName("image/gif");
        String ext = jpeg.getExtension();
        System.out.println("ext:"+ext);
    }
}
