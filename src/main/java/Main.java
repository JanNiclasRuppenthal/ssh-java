import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.Properties;

public class Main
{
    private String username;
    private String password;
    private String IPadress;

    public static void main(String[] args)
    {
        Main main = new Main();

        main.extractProperties();

        Session session = null;
        ChannelExec channelExec = null;

        try
        {
            main.connectToSSHServer(session, channelExec);
        } catch (JSchException e)
        {
            throw new RuntimeException(e);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }


        // TODO copy the folder from local to remote machine
//        Process p = null;
//        try
//        {
////            p = Runtime.getRuntime().exec("scp -r .\\..\\..\\PycharmProjects\\TestCodeForCodeBubblesAR jnruppenthal@169.254.224.166:/home/jnruppenthal/Desktop/");
////            bw.flush();
//
////            String line;
////            p = Runtime.getRuntime().exec( new String[]{"ls"});
//
//            ProcessBuilder builder = new ProcessBuilder(
//                    "powershell.exe", "/c", "scp -r .\\..\\..\\PycharmProjects\\TestCodeForCodeBubblesAR jnruppenthal@169.254.224.166:/home/jnruppenthal/Desktop/");
//            builder.redirectErrorStream(true);
//            p = builder.start();
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
//            bw.write(main.password + "\n");
//            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while (true) {
//                line = r.readLine();
//                if (line == null) { break; }
//                System.out.println(line);
//            }
//
//        } catch (IOException e)
//        {
//            throw new RuntimeException(e);
//        }


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


    private void connectToSSHServer(Session session, ChannelExec channelExec) throws JSchException, InterruptedException
    {
        System.out.println("Start ssh now:");

        session = new JSch().getSession(this.username, this.IPadress);
        session.setPassword(this.password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand("ls");
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
