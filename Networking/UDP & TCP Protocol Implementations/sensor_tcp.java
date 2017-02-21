import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * Mani Japra
 *
 * CS 3251
 * TCP Client
 *
 */

public class sensor_tcp
{
    private static String server;
    private static int port;
    private static boolean debug = false;
    private static boolean error = false;
    private static boolean authenticated = false;
    private static String challengeVal = "";

    private static String username;
    private static String password;

    private static double value;

    public static void main(String[] args) throws IOException
    {
        if (args.length > 0) //Parse through arguments
        {
            for (int i = 0; i < args.length - 1; i ++)
            {
                if (args[i].equalsIgnoreCase("-d"))
                {
                    debug = true;
                }
                else if (args[i].equalsIgnoreCase("-s"))
                {
                    server = args[i + 1];
                }
                else if (args[i].equalsIgnoreCase("-p"))
                {
                    port = Integer.parseInt(args[i + 1]);
                }
                else if (args[i].equalsIgnoreCase("-u"))
                {
                    username = args[i + 1];
                }
                else if (args[i].equalsIgnoreCase("-c"))
                {
                    password = args[i + 1];
                }
                else if (args[i].equalsIgnoreCase("-r"))
                {
                    value = Double.parseDouble(args[i + 1]);
                }
            }

            if (port == 0) //Port not set check
            {
                System.out.println("Port not set.");
                return;
            }

            String sendStr = "";

            //Client Setup
            try
            {
                Socket s = new Socket(server, port);
                InputStream input = s.getInputStream();
                OutputStream output = s.getOutputStream();

                //Send authentication request
                sendStr = "-a_req";
                if (debug) System.out.println("\nSending Authentication Request...");

                byte[] byteBuf = sendStr.getBytes();

                output.write(byteBuf);

                byte[] rec = new byte[1024];

                input.read(rec);
                if (debug) System.out.println("Received Challenge Value...");

                String recStr = new String(rec);

                challengeVal = recStr.trim();

                //Calculate MD5 hash value
                if (debug) System.out.println("Calculating MD5 Hash...");
                String concatCheck = username + password + challengeVal;
                MessageDigest md = MessageDigest.getInstance("MD5");

                md.update(concatCheck.getBytes(), 0, concatCheck.length());

                String hashToString = new BigInteger(1, md.digest()).toString(16);
                sendStr = "-h;" + username + ";" + hashToString;
                if (debug) System.out.println("Sending MD5 Hash...");

                //Send Hash value with username
                byteBuf = sendStr.getBytes();

                output.write(byteBuf);

                rec = new byte[1024];

                input.read(rec);

                if (debug) System.out.println("Received Authentication Response...");
                recStr = new String(rec);
                recStr = recStr.trim();
                String[] authCheck = recStr.split(" ");

                if (authCheck[0].equalsIgnoreCase("-auth"))
                {
                    if (authCheck[1].equalsIgnoreCase("true")) authenticated = true;
                }

                if (authenticated)
                {
                    if (debug) System.out.println("Authentication Successful!\n");
                    sendStr = "";

                    for (int j = 0; j < args.length; j++)
                    {
                        sendStr += args[j] + ";";
                    }

                    //Send Sensor information and value
                    if (debug) System.out.println("Sending Sensor Data...");
                    byteBuf = sendStr.getBytes();

                    output.write(byteBuf);

                    rec = new byte[1024];

                    input.read(rec);

                    recStr = new String(rec);

                    if (recStr.substring(0,1).equals("!")) System.out.println(recStr.substring(1));

                    System.out.println(recStr);
                }
                else
                {
                    System.out.println("Authentication Failed.");
                }

                rec = new byte[1024];

                s.close();
            }
            catch (Exception e)
            {
                if (!error) System.out.println("\nServer refused connection. Try again.");
            }
        }
        else
        {
            System.out.println("\nInsufficient arguments to start server. Please try again.");
        }
    }
}
