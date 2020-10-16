package com.ten951.design.promise;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.omg.SendingContext.RunTime;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 王永天
 * @date 2020-10-15 16:50
 */
public class FTPClientUtil {
    private final FTPClient ftp = new FTPClient();

    private volatile static ThreadPoolExecutor threadPoolExecutor;
    private final Map<String, Boolean> dirCreateMap = new HashMap<>();

    private FTPClientUtil() {
    }

    static {
        threadPoolExecutor = new ThreadPoolExecutor(
                1, Runtime.getRuntime().availableProcessors() * 2,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setDaemon(true);
                        return t;
                    }
                }, new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static Future<FTPClientUtil> newInstance(final String ftpServer, final String userName, final String password) {
        Callable<FTPClientUtil> callable = () -> {
            FTPClientUtil ftpClientUtil = new FTPClientUtil();
            ftpClientUtil.init(ftpServer, userName, password);
            return ftpClientUtil;
        };
        final FutureTask<FTPClientUtil> task = new FutureTask<>(callable);
        threadPoolExecutor.execute(task);
        return task;

    }

    private void init(String ftpServer, String userName, String password) throws Exception {
        FTPClientConfig config = new FTPClientConfig();
        ftp.configure(config);
        int reply;
        ftp.connect(ftpServer);
        System.out.println(ftp.getReplyString());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new RuntimeException("FTP server refused connection.");
        }

        boolean isOk = ftp.login(userName, password);
        if (isOk) {
            System.out.println(ftp.getReplyString());
        } else {
            throw new RuntimeException("Failed to login." + ftp.getReplyString());
        }

        reply = ftp.cwd("~/subspsync");
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new RuntimeException("Failed to change working directory.reply:" + reply);
        } else {
            System.out.println(ftp.getReplyString());
        }
        ftp.setFileType(FTP.ASCII_FILE_TYPE);
    }

    public void upload(File file) throws Exception {
        InputStream dataIn = new BufferedInputStream(new FileInputStream(file), 1024 * 8);
        boolean isOK;
        String dirName = file.getParentFile().getName();
        String fileName = dirName + "/" + file.getName();
        ByteArrayInputStream checkFileInputStream = new ByteArrayInputStream("".getBytes());
        try {
            if (!dirCreateMap.containsKey(dirName)) {
                ftp.makeDirectory(dirName);
                dirCreateMap.put(dirName, null);
            }
            try {
                isOK = ftp.storeFile(fileName, dataIn);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload" + file, e);
            }
            if (isOK) {
                ftp.storeFile(fileName + ".c", checkFileInputStream);
            } else {
                throw new RuntimeException("Failed to upload" + file + ",reply:" + "," + ftp.getReplyString());
            }
        } finally {
            dataIn.close();
        }
    }

    public void disconnect() {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ignore) {

            }
        }
    }
}
