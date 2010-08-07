package net.sourcewalker.picrename;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExifWorker {

    private static ExecutorService worker;

    public static void enqueue(final FileEntry entry, final AppData data) {
        if (worker == null) {
            worker = Executors.newFixedThreadPool(3, new ThreadFactory() {

                private int number = 0;

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "ExifWorker-" + (number++));
                    t.setDaemon(true);
                    t.setPriority(Thread.MIN_PRIORITY);
                    return t;
                }
            });
        }
        worker.submit(new Runnable() {

            @Override
            public void run() {
                ExifReader exif = new ExifReader(entry.getSource());
                if (exif.isSuccessful()) {
                    entry.setDateTaken(exif.getDateTaken());
                    data.fireDataChanged();
                }
            }
        });
    }
}
