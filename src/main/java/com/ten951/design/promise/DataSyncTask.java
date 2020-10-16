package com.ten951.design.promise;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author 王永天
 * @date 2020-10-16 10:57
 */
public class DataSyncTask implements Runnable {
    private final Map<String, String> taskParameters;

    public DataSyncTask(Map<String, String> taskParameters) {
        this.taskParameters = taskParameters;
    }

    @Override
    public void run() {
        String server = taskParameters.get("server");
        String userName = taskParameters.get("userName");
        String password = taskParameters.get("password");
        Future<FTPClientUtil> ftpClientUtilFuture = FTPClientUtil.newInstance(server, userName, password);
        generateFilesFromDB();
    }

    private void generateFilesFromDB() {

    }

    private void uploadFile(FTPClientUtil ftpClientUtil) {
        Set<File> files = retrieveGenerateFiles();
        for (File file : files) {
            try {
                ftpClientUtil.upload(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Set<File> retrieveGenerateFiles() {
        Set<File> files = new HashSet<>();
        return files;
    }
}
