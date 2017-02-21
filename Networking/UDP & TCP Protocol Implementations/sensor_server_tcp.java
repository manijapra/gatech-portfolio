import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * Mani Japra
 * 902958199
 *
 * CS 3251
 * TCP Server
 *
 */

public class sensor_server_tcp
{
    private static String returnStr = "";
    private static Date time;
    private static String challengeVal = "";

    //Nested Class to allow for multiple clients to be connected at the same time.
    static class MultiClientHelper implements Runnable
    {
        private Socket clientSocket = null;

        public MultiClientHelper(Socket socket)
        {
            clientSocket = socket;
        }

        public void run()
        {
            try
            {
                byte[] byteBuf = new byte[1024];

                if (debug)
                {
                    System.out.println("\nClient Connected!");
                    System.out.println("Host Address: " + clientSocket.getInetAddress().getHostAddress());
                    System.out.println("Port: " + clientSocket.getPort());
                }

                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                //-a_req
                input.read(byteBuf);

                String rec = new String(byteBuf);

                parseReceivedData(rec);

                if (returnStr != "")
                {
                    if (debug) System.out.println(returnStr);
                }
                else
                {
                    returnStr = "Did not receive valid information from Client.";
                }

                byte[] returnBuf = returnStr.getBytes();

                returnStr = "";

                output.write(returnBuf);

                byteBuf = new byte[1024];

                //-h
                input.read(byteBuf);

                rec = new String(byteBuf);

                parseReceivedData(rec);

                if (returnStr != "")
                {
                    if (debug) System.out.println(returnStr);
                }
                else
                {
                    returnStr = "Did not receive valid information from Client.";
                }

                returnBuf = returnStr.getBytes();

                returnStr = "";

                output.write(returnBuf);

                byteBuf = new byte[1024];

                //-sensor data
                input.read(byteBuf);

                rec = new String(byteBuf);

                parseReceivedData(rec);

                if (returnStr != "")
                {
                    System.out.println(returnStr);
                }
                else
                {
                    returnStr = "Did not receive valid information from Client.";
                }

                returnBuf = returnStr.getBytes();

                returnStr = "";

                output.write(returnBuf);

                byteBuf = new byte[1024];

                clientSocket.close();
            }
            catch (Exception e)
            {
                System.out.println("\nProblem connecting with Client.");
            }
        }
    }

    //Sensor class to hold all of the information for each sensor
    static class Sensor
    {
        private String username;

        public double minimum;
        public double maximum;
        public double average;
        public double avgT;
        private boolean noData = true;

        public ArrayList<Double> sensorData;

        public Sensor(String user)
        {
            username = user;
            sensorData = new ArrayList<Double>();
            minimum = 0;
            maximum = 0;
            average = 0;
            avgT = 0;
            noData = true;
        }

        public boolean addValue(double val)
        {
            sensorData.add(val);
            if (val > maximum || noData) maximum = val;
            if (val < minimum || noData) minimum = val;
            avgT += val;
            average = (double) (avgT)/(sensorData.size());
            time = new Date();
            returnStr = "\nSuccessfully added new sensor value!\nSensor: " + username
                            + "\nRecorded: " + val
                            + "\nTime: " + time.toString()
                            + "\nMaximum: " + maximum
                            + "\nMinimum: " + minimum
                            + "\nAverage: " + average;
            noData = false;
            return true;
        }
    }

    //Sensor Hashmap where the username is matched with a Sensor class object
    private static HashMap<String, Sensor> sensors = new HashMap<String, Sensor>();
    //Username & Password hashmap
    private static HashMap<String, String> sensorValidation = new HashMap<String, String>();

    private static int port;
    private static String pwList;

    private static boolean debug = false;

    private static double allSensorAverage = 0;
    private static double allSensorAvgT = 0;
    private static double allSensorAvgNum = 0;


    public static void main(String[] args) throws IOException
    {
        if (args.length > 0) //If arguments are available
        {
            for (int i = 0; i < args.length - 1; i ++)
            {
                if (args[i].equalsIgnoreCase("-d")) //Debugging
                {
                    debug = true;
                }
                else if (args[i].equalsIgnoreCase("-p")) //Port definition
                {
                    int tempPort = Integer.parseInt(args[i + 1]);
                    if (!(tempPort < 0) || !(tempPort > 49151))
                    {
                        port = tempPort;
                        if (debug) System.out.println("Port Number set to: " + port);
                    }
                    else
                    {
                        if (debug) System.out.println("Error setting Port Number.");
                    }
                }
                else if (args[i].equalsIgnoreCase("-f")) //Password file
                {
                    String tempPWList = args[i + 1];
                    if (tempPWList != null)
                    {
                        String fileFormatCheck = tempPWList.substring(tempPWList.indexOf("."), tempPWList.length());
                        if (fileFormatCheck.equalsIgnoreCase(".csv"))
                        {
                            pwList = args[i + 1];
                            if (debug) System.out.println("Password List CSV file set to: " + "\"" + pwList + "\"");
                            setupSensors();
                        }
                        else
                        {
                            if (debug) System.out.println("Incorrect File Type.");
                        }
                    }
                    else
                    {
                        if (debug)System.out.println("Filename not specified.");
                    }
                }

            }

            if (port == 0)
            {
                System.out.println("Port not set.");
                return;
            }

            try
            {
                //Server Setup
                ServerSocket serverSocket = new ServerSocket(port);

                byte[] byteBuf = new byte[1024];

                while (true)
                {
                    Socket clientSocket = serverSocket.accept();

                    MultiClientHelper miniServer = new MultiClientHelper(clientSocket);
                    miniServer.run(); //Where the server & client authenticate and parse sensor data
                }
            }
            catch (Exception e)
            {
                System.out.println("\nProblem connecting with Client.");
            }
        }
        else
        {
            System.out.println("\nInsufficient arguments to start server. Please try again.");
        }
    }

    /** Sensors are set up based on .csv file provided by user.
     *  HashMaps are also updated
     */
    private static void setupSensors()
    {
        BufferedReader br = null;
        String curLine = "";
        String comma = ",";
        try
        {
           br = new BufferedReader(new FileReader(pwList));
           while ((curLine = br.readLine()) != null) {
               String[] sensor  = curLine.split(comma);
               String tempU = sensor[0].substring(1, sensor[0].length() - 1);
               String tempP = sensor[1].substring(1, sensor[1].length() - 1);
               sensors.put(tempU, new Sensor(tempU));
               sensorValidation.put(tempU, tempP);
           }
       } catch (FileNotFoundException e)
       {
           e.printStackTrace();
       } catch (IOException e)
       {
           e.printStackTrace();
       } finally
       {
           if (br != null) {
               try {
                   br.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
    }

    /** Helper method to parse through received packets of information from client.
     */
    private static void parseReceivedData(String data)
    {
        if (data.trim().equalsIgnoreCase("-a_req")) //Authentication Request is received.
        {
            challengeVal = "";
            if (debug) System.out.println("\nGenerating Challenge Value...");
            Random rand = new Random();
            for (int i = 0; i < 64; i++)
            {
                int randNum = rand.nextInt(78) + 48;
                challengeVal += Character.toString((char) randNum); //Challenge Value is calculated
            }
            returnStr = challengeVal;
        }
        else
        {
            String[] rData = data.split(";");
            String tempUser = "";
            String tempPass = "";
            double tempVal = 0;

            boolean hashCheck = false;
            String hash = "";

            for (int i = 0; i < rData.length - 1; i ++) //Received packet of information is decrypted to see what client wants
            {
                if (rData[i].equalsIgnoreCase("-u"))
                {
                    tempUser = rData[i + 1]; //Username stored
                }
                else if (rData[i].equalsIgnoreCase("-c"))
                {
                    tempPass = rData[i + 1]; //Password stored
                }
                else if (rData[i].equalsIgnoreCase("-r"))
                {
                    try
                    {
                        tempVal = Double.parseDouble(rData[i + 1]); //Sensor value stored
                    }
                    catch (Exception e)
                    {
                        System.out.println("Bad value received from: " + tempUser);
                    }
                }
                else if (rData[i].equalsIgnoreCase("-h")) //Received a hash value, authentication is still in progress
                {
                    hashCheck = true;
                    tempUser = rData[i + 1];
                    hash = rData[i + 2];
                }
            }

            if (hashCheck) //Completes authentication check
            {
                if (sensorValidation.containsKey(tempUser))
                {
                    tempPass = sensorValidation.get(tempUser);
                    String concatCheck = tempUser + tempPass + challengeVal;
                    try
                    {
                        MessageDigest md = MessageDigest.getInstance("MD5");

                        md.update(concatCheck.getBytes(), 0, concatCheck.length());

                        String ver = new BigInteger(1, md.digest()).toString(16);
                        if (ver.trim().equals(hash.trim())) returnStr = "-auth true"; //Authentication successful.
                        else
                        {
                            System.out.println("Incorrect password for username " + tempUser + ", User Authentication Failed!");
                            returnStr = "-auth false"; //Authentication failed.
                        }
                    }
                    catch (Exception e)
                    {

                    }
                }
                else
                {
                    System.out.println("Unknown username " + tempUser + ", User Authentication Failed!");
                    returnStr = "-auth false"; //Authentication failed.
                }

                hashCheck = false; //No longer needed to check hash.
            }
            else
            {
                Sensor s = sensors.get(tempUser); //Sensor is updated with new value.
                if (s != null)
                {
                    s.addValue(tempVal);
                    allSensorAvgT += tempVal;
                    allSensorAvgNum++;
                    allSensorAverage = allSensorAvgT/allSensorAvgNum;
                    returnStr += "\nAll Sensor Average: " + allSensorAverage + "\n";
                }
            }
        }
    }
}
