package win.sinno.io.async.dl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/5 10:09
 */
public class OutputProcessor implements Callable<String> {

    private static final Logger LOG = LoggerFactory.getLogger(OutputProcessor.class);

    private InputStream is;

    public OutputProcessor(InputStream is) {
        this.is = is;
    }


    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public String call() throws Exception {
        StringBuilder sb = new StringBuilder();

        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return sb.toString();
    }
}
