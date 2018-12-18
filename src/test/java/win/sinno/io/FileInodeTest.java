package win.sinno.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.Test;

/**
 * win.sinno.io.FileInodeTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/6/1
 */
public class FileInodeTest {


  @Test
  public void testGetFileInode() throws IOException {

    /**
     *
     * 重命名文件：
     * (dev=1000005,ino=3023463)
     * 3023463
     * sun.nio.fs.UnixFileAttributes$UnixAsBasicFileAttributes@ea30797
     *
     * 复制得到的文件：
     * (dev=1000005,ino=8597252516)
     * 8597252516
     * sun.nio.fs.UnixFileAttributes$UnixAsBasicFileAttributes@ea30797
     */
    String filePath = "/Users/chenlizhong/Downloads/bug.xlsx";
    File file = new File(filePath);
    System.out.println(file.getAbsolutePath());

    Path path = Paths.get(filePath);
    BasicFileAttributes bfa = Files.readAttributes(path, BasicFileAttributes.class);

    System.out.println("Creation Time      : " + bfa.creationTime());
    System.out.println("Last Access Time   : " + bfa.lastAccessTime());
    System.out.println("Last Modified Time : " + bfa.lastModifiedTime());
    System.out.println("Is Directory       : " + bfa.isDirectory());
    System.out.println("Is Other           : " + bfa.isOther());
    System.out.println("Is Regular File    : " + bfa.isRegularFile());
    System.out.println("Is Symbolic Link   : " + bfa.isSymbolicLink());
    System.out.println("Size               : " + bfa.size());
    Object objectKey = bfa.fileKey();
    System.out.println("Object Key               : " + bfa.fileKey());

    String s = bfa.fileKey().toString();
    System.out.println(s);
    String inode = s.substring(s.indexOf("ino=") + 4, s.indexOf(")"));
    System.out.println(inode);

    System.out.println(bfa);
  }
}
