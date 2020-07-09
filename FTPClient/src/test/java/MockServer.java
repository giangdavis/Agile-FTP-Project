import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;

public class MockServer {
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void createAndStartServer() {
        FakeFtpServer server = new FakeFtpServer();
        server.addUserAccount(new UserAccount("test", "test", "c:\\data"));
        WindowsFakeFileSystem fs = new WindowsFakeFileSystem();
        fs.add(new DirectoryEntry("c:\\data"));
        fs.add(new FileEntry("c:\\data\\file1.txt", "abcdef"));
        fs.add(new FileEntry("c:\\data\\run.exe"));
        server.setFileSystem(fs);
        setPort(22);
        server.setServerControlPort(getPort());
        server.start();
    }
}
