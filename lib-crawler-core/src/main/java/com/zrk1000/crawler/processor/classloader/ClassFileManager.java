package com.zrk1000.crawler.processor.classloader;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;

public class ClassFileManager extends ForwardingJavaFileManager {
    private ByteFileObject currObject;

    @SuppressWarnings("unchecked")
    public ClassFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        this.currObject = new ByteFileObject(className, kind);
        return this.currObject;
    }

    /**
     * 返回缓存的Object
     *
     * @return
     */
    public ByteFileObject getCachedObject() {
        return this.currObject;
    }
}