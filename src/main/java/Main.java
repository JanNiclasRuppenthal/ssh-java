import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipFile;

public class Main
{
    private String username;
    private String password;
    private String IPadress;

    public static void main(String[] args)
    {
        Main main = new Main();

        main.extractProperties();

        try
        {
            main.sftpToRemoteMachine();
        }
        catch (JSchException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Transfer Process Completed...");
        }

        try
        {
            main.connectToSSHServer();
        } catch (JSchException e)
        {
            throw new RuntimeException(e);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        // Thread does not end here?
        System.exit(0);
    }

    private void extractProperties()
    {
        PropertiesFileLoader propertiesLoader = PropertiesFileLoader.createPropertiesFileLoader();

        Properties properties;
        try
        {
            properties = propertiesLoader.loadProperties("ssh.properties");
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.IPadress = properties.getProperty("IPadress");
    }

    private void sftpToRemoteMachine() throws JSchException, FileNotFoundException, SftpException
    {
        JSch jsch = new JSch();
        Session session = jsch.getSession(this.username, this.IPadress);
        session.setPassword(this.password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications",
                "publickey,keyboard-interactive,password");

        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        System.out.println("sftp channel opened and connected.");
        ChannelSftp channelSftp = (ChannelSftp) channel;

        String sftpDirectory = "/home/jnruppenthal/Desktop/";

//            ZipFile zipFile = new ZipFile("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR.zip");
        ArrayList<File> filesToAdd = new ArrayList<File>();
        File folder = new File("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR");
        File[] listOfFiles = folder.listFiles();
        // Add files to be archived into zip file
        for (File file : listOfFiles) {
            filesToAdd.add(file);
//                System.out.println(file.getName());
        }

        channelSftp.put(new FileInputStream("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR.zip"), "/home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip", ChannelSftp.OVERWRITE);

    }

    private void connectToSSHServer() throws JSchException, InterruptedException
    {
        System.out.println("Start ssh now:");

        Session session = new JSch().getSession(this.username, this.IPadress);
        session.setPassword(this.password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        StringBuilder command = new StringBuilder()
                .append("cd Desktop")
                .append(" ; ")
                .append("unzip -u /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip")
                .append(" ; ")
                .append("rm /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip")
                .append(" ; ")
                .append("python /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR/classMain.py");

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command.toString());
        ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
        channelExec.setOutputStream(responseOutputStream);
        channelExec.connect();

        while (channelExec.isConnected())
        {
            Thread.sleep(100);
        }

        String response = new String(responseOutputStream.toByteArray());
        System.out.println(response);

        session.disconnect();
        channelExec.disconnect();


        System.out.println("End of ssh connection");
    }

}
