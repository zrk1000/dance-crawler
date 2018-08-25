package com.zrk1000.crawler.processor.classloader;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;
import java.nio.CharBuffer;

public class StringFileObject extends SimpleJavaFileObject {
    private String code;


    public StringFileObject(String className, String code) {
        super(toURI(className), Kind.SOURCE);
        this.code = code;
    }
    public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
        return CharBuffer.wrap(code);
    }

    static URI toURI(String name) {
        final StringBuilder newUri = new StringBuilder();
        newUri.append("mfm:///");
        newUri.append(name.replace('.', '/'));
        if (name.endsWith(Kind.SOURCE.extension)) {
            newUri.replace(newUri.length() - Kind.SOURCE.extension.length(), newUri.length(), Kind.SOURCE.extension);
        }
        URI uri = URI.create(newUri.toString());
        return uri;
    }
}
