package z.houbin.robot;

import java.io.IOException;
import java.io.InputStream;

public class Streams {
    public static String stream2String(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            try {
                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes)) != -1) {
                    builder.append(new String(bytes, 0, len));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
