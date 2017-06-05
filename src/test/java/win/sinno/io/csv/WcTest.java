package win.sinno.io.csv;

import org.junit.Test;
import win.sinno.io.async.dl.OutputProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/2 18:02
 */
public class WcTest {

    private ExecutorService es = Executors.newCachedThreadPool();

    @Test
    public void testWc() throws InterruptedException {
        String filePath = "/Users/clz/logs/584776652148047873.csv";
        String[] command = new String[]{"/bin/sh", "-c", "cat " + filePath + " | wc -l"};
//        String command = "java";

        try {
            Process process = Runtime.getRuntime().exec(command);

            OutputProcessor out = new OutputProcessor(process.getInputStream());
            OutputProcessor errOut = new OutputProcessor(process.getErrorStream());

            Future<String> future = es.submit(out);
            Future<String> errFuture = es.submit(errOut);

            String successMsg = future.get();
            String errMsg = errFuture.get();

            System.out.println("successMsg:" + successMsg);
            System.out.println("errMsg:" + errMsg);

            int exitVal = process.waitFor();
            System.out.println("process exit value:" + exitVal);

            process.destroy();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
