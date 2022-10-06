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

        // TODO copy the folder from local to remote machine
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(main.username, main.IPadress);
            session.setPassword(main.password);
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

//            File directory = new File("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR");
//            File[] fList = directory.listFiles();
//
//            for (File file : fList){
//                if (file.isFile()){
//                    String filename=file.getAbsolutePath();
//                    channelSftp.put(filename, sftpDirectory, ChannelSftp.OVERWRITE);
//                    System.out.println(filename + " transferred to " + sftpDirectory );
//                }
//            }

//            listf("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR", channelSftp, sftpDirectory, sftpDirectory);

            ZipFile zipFile = new ZipFile("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR.zip");
            ArrayList<File> filesToAdd = new ArrayList<File>();
            File folder = new File("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR");
            File[] listOfFiles = folder.listFiles();
            // Add files to be archived into zip file
            for (File file : listOfFiles) {
                filesToAdd.add(file);
                System.out.println(file.getName());
            }

            channelSftp.put(new FileInputStream("C:\\Users\\janru\\PycharmProjects\\TestCodeForCodeBubblesAR.zip"), "/home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip", ChannelSftp.OVERWRITE);

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
//            channelExec.setCommand("unzip /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//
//            channelExec.setCommand("rm /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");

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
    }

//    public static List<File> listf(String directoryName, ChannelSftp channelSftp, String sftpDirectory, String NewDir) throws JSchException, SftpException {
//        File directory = new File(directoryName);
//        List<File> resultList = new ArrayList<File>();
//        File[] fList = directory.listFiles();
//        resultList.addAll(Arrays.asList(fList));
//        for (File file : fList) {
//            if (file.isFile()) {
//                String filename=file.getAbsolutePath();
//                channelSftp.put(filename, NewDir, ChannelSftp.OVERWRITE);
//                System.out.println(filename + " transferred to " + sftpDirectory );
//            } else if (file.isDirectory()) {
//                System.out.println(file.getAbsolutePath());
//                NewDir = sftpDirectory+file.getName();
//                channelSftp.mkdir(NewDir);
//                System.out.println(NewDir + " Folder created ");
//                resultList.addAll(listf(file.getAbsolutePath(), channelSftp, sftpDirectory, NewDir));
//            }
//        }
//        System.out.println(fList);
//        return resultList;
//    }


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


    private void connectToSSHServer() throws JSchException, InterruptedException
    {
        System.out.println("Start ssh now:");

        Session session = new JSch().getSession(this.username, this.IPadress);
        session.setPassword(this.password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand("cd Desktop");
//        System.out.println("unzip the file");
//        channelExec.setCommand("unzip /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//        channelExec.setCommand("unzip /Desktop/TestCodeForCodeBubblesAR.zip");
//        System.out.println("remove the file");
//        channelExec.setCommand("rm /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//        System.out.println("start pytho script");
//        channelExec.setCommand("python /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR/classMain.py");
        ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
        channelExec.setOutputStream(responseOutputStream);
        channelExec.connect();

        while (channelExec.isConnected())
        {
            Thread.sleep(100);
        }

        String response = new String(responseOutputStream.toByteArray());
        System.out.println(response);

        channelExec.disconnect();

//        channelExec.setCommand("cd Desktop");
        System.out.println("unzip the file");
        channelExec.setCommand("unzip /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//        channelExec.setCommand("unzip /Desktop/TestCodeForCodeBubblesAR.zip");
//        System.out.println("remove the file");
//        channelExec.setCommand("rm /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//        System.out.println("start pytho script");
//        channelExec.setCommand("python /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR/classMain.py");
        channelExec.setOutputStream(responseOutputStream);
        channelExec.connect();

        while (channelExec.isConnected())
        {
            Thread.sleep(100);
        }

        response = new String(responseOutputStream.toByteArray());
        System.out.println(response);

//        channelExec.setCommand("cd Desktop");
//        System.out.println("unzip the file");
//        channelExec.setCommand("unzip /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//        channelExec.setCommand("unzip /Desktop/TestCodeForCodeBubblesAR.zip");
        System.out.println("remove the file");
        channelExec.setCommand("rm /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//        System.out.println("start pytho script");
//        channelExec.setCommand("python /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR/classMain.py");
        channelExec.setOutputStream(responseOutputStream);
        channelExec.connect();

        while (channelExec.isConnected())
        {
            Thread.sleep(100);
        }

        response = new String(responseOutputStream.toByteArray());
        System.out.println(response);


//        channelExec.setCommand("cd Desktop");
//        System.out.println("unzip the file");
//        channelExec.setCommand("unzip /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
//        channelExec.setCommand("unzip /Desktop/TestCodeForCodeBubblesAR.zip");
//        System.out.println("remove the file");
//        channelExec.setCommand("rm /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR.zip");
        System.out.println("start pytho script");
        channelExec.setCommand("python /home/jnruppenthal/Desktop/TestCodeForCodeBubblesAR/classMain.py");
        channelExec.setOutputStream(responseOutputStream);
        channelExec.connect();

        while (channelExec.isConnected())
        {
            Thread.sleep(100);
        }

        response = new String(responseOutputStream.toByteArray());
        System.out.println(response);

        session.disconnect();
        channelExec.disconnect();


        System.out.println("End of ssh connection");
    }

}
