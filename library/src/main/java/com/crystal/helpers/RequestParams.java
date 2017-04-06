package com.crystal.helpers;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams implements Serializable {

    public final static String APPLICATION_OCTET_STREAM =
            "application/octet-stream";


    protected final static String LOG_TAG = "RequestParams";
    protected final ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, StreamWrapper> streamParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, List<FileWrapper>> fileArrayParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Object> urlParamsWithObjects = new ConcurrentHashMap<>();
    protected boolean forceMultipartEntity = false;
    protected boolean autoCloseInputStreams;

    public RequestParams() {
        this((Map<String, String>) null);
    }

    public RequestParams(Map<String, String> source) {
        if (source != null) {
            for (Map.Entry<String, String> entry : source.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public RequestParams(final String key, final String value) {
        this(new HashMap<String, String>() {{
            put(key, value);
        }});
    }

    public RequestParams(Object... keysAndValues) {
        int len = keysAndValues.length;
        if (len % 2 != 0)
            throw new IllegalArgumentException("Supplied arguments must be even");
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    public void setForceMultipartEntityContentType(boolean force) {
        this.forceMultipartEntity = force;
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public void put(String key, File files[]) throws FileNotFoundException {
        put(key, files, null, null);
    }

    public void put(String key, File files[], String contentType, String customFileName) throws FileNotFoundException {

        if (key != null) {
            List<FileWrapper> fileWrappers = new ArrayList<FileWrapper>();
            for (File file : files) {
                if (file == null || !file.exists()) {
                    throw new FileNotFoundException();
                }
                fileWrappers.add(new FileWrapper(file, contentType, customFileName));
            }
            fileArrayParams.put(key, fileWrappers);
        }
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, file, null, null);
    }

    public void put(String key, String customFileName, File file) throws FileNotFoundException {
        put(key, file, null, customFileName);
    }

    public void put(String key, File file, String contentType) throws FileNotFoundException {
        put(key, file, contentType, null);
    }

    public void put(String key, File file, String contentType, String customFileName) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException();
        }
        if (key != null) {
            fileParams.put(key, new FileWrapper(file, contentType, customFileName));
        }
    }

    public void put(String key, InputStream stream) {
        put(key, stream, null);
    }

    public void put(String key, InputStream stream, String name) {
        put(key, stream, name, null);
    }

    public void put(String key, InputStream stream, String name, String contentType) {
        put(key, stream, name, contentType, autoCloseInputStreams);
    }

    public void put(String key, InputStream stream, String name, String contentType, boolean autoClose) {
        if (key != null && stream != null) {
            streamParams.put(key, StreamWrapper.newInstance(stream, name, contentType, autoClose));
        }
    }

    public void put(String key, Object value) {
        if (key != null && value != null) {
            urlParamsWithObjects.put(key, value);
        }
    }

    public void put(String key, int value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
    }

    public void put(String key, long value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
    }

    public void add(String key, String value) {
        if (key != null && value != null) {
            Object params = urlParamsWithObjects.get(key);
            if (params == null) {
                // Backward compatible, which will result in "k=v1&k=v2&k=v3"
                params = new HashSet<String>();
                this.put(key, params);
            }
            if (params instanceof List) {
                ((List<Object>) params).add(value);
            } else if (params instanceof Set) {
                ((Set<Object>) params).add(value);
            }
        }
    }

    public void remove(String key) {
        urlParams.remove(key);
        streamParams.remove(key);
        fileParams.remove(key);
        urlParamsWithObjects.remove(key);
        fileArrayParams.remove(key);
    }

    public boolean has(String key) {
        return urlParams.get(key) != null ||
                streamParams.get(key) != null ||
                fileParams.get(key) != null ||
                urlParamsWithObjects.get(key) != null ||
                fileArrayParams.get(key) != null;
    }

    public static class FileWrapper implements Serializable {
        public final File file;
        public final String contentType;
        public final String customFileName;

        public FileWrapper(File file, String contentType, String customFileName) {
            this.file = file;
            this.contentType = contentType;
            this.customFileName = customFileName;
        }
    }

    public static class StreamWrapper {
        public final InputStream inputStream;
        public final String name;
        public final String contentType;
        public final boolean autoClose;

        public StreamWrapper(InputStream inputStream, String name, String contentType, boolean autoClose) {
            this.inputStream = inputStream;
            this.name = name;
            this.contentType = contentType;
            this.autoClose = autoClose;
        }

        static StreamWrapper newInstance(InputStream inputStream, String name, String contentType, boolean autoClose) {
            return new StreamWrapper(
                    inputStream,
                    name,
                    contentType == null ? APPLICATION_OCTET_STREAM : contentType,
                    autoClose);
        }
    }
}
