import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

/**
 * Mani Japra
 *
 * CS 3251
 * UDP Client
 *
 */

public class sensor_udp
{
    private static final int TIMEOUT = 2000;
    private static final int MAXTRIES = 3;
    private static boolean cont = true;

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
                InetAddress serverAddress = InetAddress.getByName(server);
                DatagramSocket s = new DatagramSocket();
                s.setSoTimeout(TIMEOUT);

                //Send authentication request
                sendStr = "-a_req";
                if (debug) System.out.println("\nSending Authentication Request...");

                byte[] byteBuf = sendStr.getBytes();

                DatagramPacket sendP = new DatagramPacket(byteBuf, byteBuf.length, serverAddress, port);

                byte[] rec = new byte[1024];

                DatagramPacket recP = new DatagramPacket(rec, rec.length);

                int tries = 0;
                boolean receivedResponse = false;
                do
                {
                    s.send(sendP);
                    try
                    {
                        s.receive(recP);
                        if (!recP.getAddress().toString().equals("/" + server)) throw new IOException("Received packet from an unknown source");
					    receivedResponse = true;
                    }
                    catch (InterruptedIOException e)
                    {
                        tries += 1;
                        System.out.println("Server hasn't responded... \nTrying again...");
                    }
                } while ((!receivedResponse) && (tries < MAXTRIES));

                if (!(receivedResponse))
                {
                    System.out.println("Server Not Responding.\n");
                    cont = false;
                }

                if (cont)
                {
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
                    rec = new byte[1024];

                    sendP = new DatagramPacket(byteBuf, byteBuf.length, serverAddress, port);

                    rec = new byte[1024];

                    recP = new DatagramPacket(rec, rec.length);

                    tries = 0;
                    receivedResponse = false;
                    do
                    {
                        s.send(sendP);
                        try
                        {
                            s.receive(recP);
                            if (!recP.getAddress().toString().equals("/" + server)) throw new IOException("Received packet from an unknown source");
    					    receivedResponse = true;
                        }
                        catch (InterruptedIOException e)
                        {
                            tries += 1;
                            System.out.println("Server hasn't responded... \nTrying again...");
                        }
                    } while ((!receivedResponse) && (tries < MAXTRIES));

                    if (!(receivedResponse))
                    {
                        System.out.println("Server Not Responding.\n");
                        cont = false;
                    }

                    if (cont)
                    {
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
                            byteBuf = sendStr.getBytes();

                            if (debug) System.out.println("Sending Sensor Data...");

                            sendP = new DatagramPacket(byteBuf, byteBuf.length, serverAddress, port);

                            rec = new byte[1024];

                            recP = new DatagramPacket(rec, rec.length);

                            tries = 0;
                            receivedResponse = false;
                            do
                            {
                                s.send(sendP);
                                try
                                {
                                    s.receive(recP);
                                    if (!recP.getAddress().toString().equals("/" + server)) throw new IOException("Received packet from an unknown source");
            					    receivedResponse = true;
                                }
                                catch (InterruptedIOException e)
                                {
                                    tries += 1;
                                    System.out.println("Server hasn't responded... \nTrying again...");
                                }
                            } while ((!receivedResponse) && (tries < MAXTRIES));

                            if (!(receivedResponse))
                            {
                                System.out.println("Server Not Responding.\n");
                                cont = false;
                            }

                            if (cont)
                            {
                                recStr = new String(rec);

                                if (recStr.substring(0,1).equals("!")) System.out.println(recStr.substring(1));

                                System.out.println(recStr);
                            }
                        }
                        else
                        {
                            System.out.println("Authentication Failed.");
                        }
                    }
                }

                rec = new byte[1024];

                s.close();
            }
            catch (Exception e)
            {
                if (!error) System.out.println("\nServer refused connection. Try again.");
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("\nInsufficient arguments to start server. Please try again.");
        }
    }
}
