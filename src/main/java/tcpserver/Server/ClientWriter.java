package tcpserver.Server;

import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.GT06;

import java.io.*; 

public class ClientWriter {
    private ClientHandler client;
    private BufferedOutputStream output;

    // Constructor for initializing ClientWriter
    public ClientWriter(ClientHandler client, BufferedOutputStream bos) {
        this.client = client;
        this.output = bos;
    }

    // Method to send a standard response to the client
    public void respondStandard(String prot, String isn) throws IOException {
        String respond = "";

        String protNum = prot;
        String serialNum = isn;
        int packLenInt = (protNum.length() + serialNum.length()) / 2 + 2; // Calculate packet length
        String packLenStr = String.format("%02X", packLenInt); // Convert packet length to hexadecimal
        
        respond = packLenStr + protNum + serialNum;
        String crc = GT06.crcCalc(respond); // Calculate CRC for the response
        respond += crc;

        respond = GT06.addStartEnd(respond);
        System.out.println("Respond: " + respond);
        System.out.println();

        byte[] bArr = Helpers.hexStrToByteArr(respond);

        output.write(bArr);
        output.flush();
    }

    // Method to send a command to the client
    public void sendCommand(String str) throws IOException {
        String respond = "";

        String protNum = "80"; // Protocol number for command
        String serverFlags = "00000001";
        String command = getCommand(str);

        String language = "0002";

        int isnInt = Integer.parseInt(client.isn, 16);
        String serNum = String.format("%04X", isnInt + 1); // Increment and convert serial number to hexadecimal

        int commandLen = (serverFlags.length() + command.length()) / 2; // Calculate command length
        String comLenStr = String.format("%02X", commandLen); // Convert command length to hexadecimal

        respond = protNum + comLenStr + serverFlags + command + language + serNum;

        int packLenInt = respond.length() / 2 + 2; // Calculate packet length
        String packLenStr = String.format("%02X", packLenInt);

        respond = packLenStr + respond;
        String crc = GT06.crcCalc(respond); // Calculate CRC for the response
        respond += crc;

        respond = GT06.addStartEnd(respond);

        Helpers.sendMessage(respond, output);
    }

    // Method to convert command string to hexadecimal
    public String getCommand(String str) {
        String hexStr = "";
        for (char c : str.toCharArray()) {
            hexStr += String.format("%H", c); // Convert each character to hexadecimal
        }

        hexStr.replace(" ", ""); // Remove any white spaces from the hexadecimal string

        System.out.println("Hex String: " + hexStr);
        System.out.println();
        return hexStr;
    }
}
