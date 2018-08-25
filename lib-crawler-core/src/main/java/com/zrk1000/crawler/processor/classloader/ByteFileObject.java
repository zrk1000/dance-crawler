package com.zrk1000.crawler.processor.classloader;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ByteFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream bos ;
    private String className;

    public ByteFileObject(String name, Kind kind) {
        super(toURI(name), kind);
        this.bos = new ByteArrayOutputStream();
        this.className = name;
    }

    public byte[] getBytes() {
        byte[] bytes = this.bos.toByteArray();
        if(bos != null){
            try {
                bos.close();
            } catch (IOException e) {
            }
            bos = null;
        }
        return bytes;
    }

    public OutputStream openOutputStream() throws IOException {
        return this.bos;
    }

    static URI toURI(String name) {
        final StringBuilder newUri = new StringBuilder();
        newUri.append("mfm:///");
        newUri.append(name.replace('.', '/'));
        if (name.endsWith(Kind.CLASS.extension)) {
            newUri.replace(newUri.length() - Kind.CLASS.extension.length(), newUri.length(), Kind.CLASS.extension);
        }
        URI uri = URI.create(newUri.toString());
        return uri;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}