package win.sinno.io;

import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * random access file
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-06-01 15:06.
 */
public class RandomAccessFileTest {

    String lineSeparator = System.getProperty("line.separator");

    /**
     * 测试随机访问
     */
    @Test
    public void testRandom() throws IOException {

        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\data\\ddy_market_xls\\test.csv", "rw");

        long fileLength = randomAccessFile.length();

        System.out.println(fileLength / 1024 + "KB");

        System.out.println(fileLength);

        randomAccessFile.seek(0);

        String header = randomAccessFile.readLine();

        System.out.println(new String(header.getBytes("ISO-8859-1"), "UTF-8"));

        randomAccessFile.seek(0);

        randomAccessFile.write("序1号,消3息,时4间".getBytes("UTF-8"));

        randomAccessFile.writeBytes(lineSeparator);

        randomAccessFile.close();

    }

}
