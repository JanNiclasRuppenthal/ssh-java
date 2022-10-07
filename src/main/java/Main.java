import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Main
{
    private String username;
    private String password;
    private String IPadress;

    private final String ABSOLUTPATHTOPYCHARMPROJECTS = "C:\\Users\\janru\\PycharmProjects";
    private final String ZIPFILENAME = "TestCodeForCodeBubblesAR.zip";
    private final String PROJECTNAME = "TestCodeForCodeBubblesAR";
    private final String DESTINATION = "/home/jnruppenthal/";

    public static void main(String[] args)
    {
        Main main = new Main();

        main.extractProperties();

        try
        {
            main.zipTheFile();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            System.out.println("Zipping Process Completed...");
        }

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
        finally
        {
            System.out.println("Running Python Script Completed...");
        }
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

    private void zipTheFile() throws IOException
    {
        StringBuilder commandBuilder = new StringBuilder()
                .append("cd " + this.ABSOLUTPATHTOPYCHARMPROJECTS)
                .append(" ; ")
                .append("zip -r " + this.ZIPFILENAME + " " + this.PROJECTNAME);
        String[] command = {"powershell", "/C", commandBuilder.toString()};
        ProcessBuilder probuilder = new ProcessBuilder( command );

        Process process = probuilder.start();

        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        System.out.printf("Output of running %s is:\n",
                Arrays.toString(command));
        while ((line = br.readLine()) != null)
        {
            System.out.println(line);
        }

        //Wait to get exit value
        try
        {
            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        ArrayList<File> filesToAdd = new ArrayList<File>();
        File folder = new File(this.ABSOLUTPATHTOPYCHARMPROJECTS + "\\" + this.PROJECTNAME);
        File[] listOfFiles = folder.listFiles();
        // Add files to be archived into zip file
        for (File file : listOfFiles) {
            filesToAdd.add(file);
        }

        channelSftp.put(new FileInputStream(this.ABSOLUTPATHTOPYCHARMPROJECTS + "\\" + this.ZIPFILENAME), this.DESTINATION + this.ZIPFILENAME, ChannelSftp.OVERWRITE);

        session.disconnect();
        channelSftp.disconnect();

    }

    private void connectToSSHServer() throws JSchException, InterruptedException
    {
        System.out.println("Start ssh now:");

        Session session = new JSch().getSession(this.username, this.IPadress);
        session.setPassword(this.password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        StringBuilder command = new StringBuilder()
                .append("unzip -u " + this.DESTINATION + this.ZIPFILENAME)
                .append(" ; ")
                .append("rm " + this.DESTINATION + this.ZIPFILENAME)
                .append(" ; ")
                .append("python " + this.DESTINATION + this.PROJECTNAME + "/classMain.py");

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
