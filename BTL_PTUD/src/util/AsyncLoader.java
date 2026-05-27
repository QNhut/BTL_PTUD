package util;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Tiện ích tải dữ liệu bất đồng bộ.
 * Mọi công việc nặng (DB, I/O) chạy trong background thread;
 * cập nhật giao diện luôn chạy trên EDT.
 */
public final class AsyncLoader {

    private AsyncLoader() {}

    /**
     * Tải dữ liệu trong background rồi cập nhật UI trên EDT.
     *
     * @param bgWork   công việc DB / nặng — chạy trên background thread
     * @param uiUpdate cập nhật Swing — chạy trên EDT sau khi bgWork xong
     * @param <T>      kiểu dữ liệu trả về
     */
    public static <T> void run(Callable<T> bgWork, Consumer<T> uiUpdate) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return bgWork.call();
            }

            @Override
            protected void done() {
                try {
                    uiUpdate.accept(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Overload không có kiểu trả về (chỉ cần chạy xong rồi cập nhật UI).
     *
     * @param bgWork   công việc DB / nặng — chạy trên background thread
     * @param uiUpdate cập nhật Swing — chạy trên EDT sau khi bgWork xong
     */
    public static void run(Runnable bgWork, Runnable uiUpdate) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                bgWork.run();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // propagate exceptions
                    SwingUtilities.invokeLater(uiUpdate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
