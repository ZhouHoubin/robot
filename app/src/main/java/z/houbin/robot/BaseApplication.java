package z.houbin.robot;

import android.app.Application;

public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        StringBuilder builder = new StringBuilder();
        builder.append("Exception in thread ");
        builder.append("\"");
        builder.append(t.getName());
        builder.append("\"");
        builder.append("\t");
        builder.append(e.getClass().getName());
        builder.append("\t");
        builder.append(e.getMessage());
        builder.append("\r\n");

        builder.append(getTraceDetail(e.getStackTrace()));

        Throwable throwable = e.getCause();
        while (throwable != null) {
            builder.append("Caused by: ");
            builder.append(throwable.getClass().getName());
            String message = throwable.getMessage();
            if (message != null) {
                builder.append(" ");
                builder.append(throwable.getMessage());
            }
            builder.append("\r\n");
            builder.append(getTraceDetail(throwable.getStackTrace()));
            throwable = throwable.getCause();
        }
        System.err.println(builder.toString());
    }

    private String getTraceDetail(StackTraceElement[] elements, String... filters) {
        StringBuilder builder = new StringBuilder();
        elements:
        for (StackTraceElement element : elements) {
            String className = element.getClassName();

            for (String filter : filters) {
                if (className.contains(filter)) {
                    continue elements;
                }
            }

            builder.append("\t");
            builder.append("at");
            builder.append(" ");
            builder.append(element.getClassName());
            builder.append(".");
            builder.append(element.getMethodName());
            builder.append("(");
            builder.append(element.getFileName());
            builder.append(":");
            builder.append(element.getLineNumber());
            builder.append(")");
            builder.append("\r\n");
        }
        return builder.toString();
    }
}
