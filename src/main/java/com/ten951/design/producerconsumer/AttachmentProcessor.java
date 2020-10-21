package com.ten951.design.producerconsumer;

import com.ten951.design.twophase.AbstractTerminatableThread;

import java.io.*;
import java.text.Normalizer;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author 王永天
 * @date 2020-10-21 10:54
 */
public class AttachmentProcessor {
    private final String ATTACHMENT_STORE_BASE_DIR = "/home/viscent/tmp/attachments";
    private final Channel<File> channel = new BlockingQueueChannel<>(new ArrayBlockingQueue<>(200));
    private final AbstractTerminatableThread indexingThread = new AbstractTerminatableThread() {
        @Override
        protected void doRun() throws Exception {
            File file = channel.take();
            indexFile(file);
            terminationToken.reservations.decrementAndGet();
        }

        private void indexFile(File file) throws Exception {
            Random rnd = new Random();
            Thread.sleep(rnd.nextInt(100));
        }
    };


    public void init() {
        indexingThread.start();
    }

    public void shutdown() {
        indexingThread.terminate();
    }

    public void saveAttachment(InputStream in, String documentId, String originalFileName) throws IOException {
        File file = saveAsFile(in, documentId, originalFileName);
        try {
            channel.put(file);
        } catch (InterruptedException ignored) {

        }
        indexingThread.terminationToken.reservations.incrementAndGet();
    }

    private File saveAsFile(InputStream in, String documentId, String originalFileName) throws IOException {
        String dirName = ATTACHMENT_STORE_BASE_DIR + documentId;
        File dir = new File(dirName);
        dir.mkdirs();
        File file = new File(dirName + "/" + Normalizer.normalize(originalFileName, Normalizer.Form.NFC));
        if (!dirName.equals(file.getCanonicalFile().getParent())) {
            throw new SecurityException("Invalid originalFileName:" + originalFileName);
        }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = new BufferedInputStream(in);
        byte[] buf = new byte[2048];
        int len = -1;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            while ((len = bis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } finally {
            try {
                bis.close();
            } catch (IOException ignored) {
            }
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException ignored) {
            }
        }
        return file;
    }
}
