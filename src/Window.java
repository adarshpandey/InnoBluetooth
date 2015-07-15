/*
	Project name : Bluetooth
	Under : Innosium Pvt. Ltd.
	Date : 2015-June-11
	Description :   device discovery = tested OK 
					service discovery = tested OK
					communication = still testing
 */


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.intel.bluetooth.RemoteDeviceHelper;
// main class
public class Window
{
	BluetoothListener listen;
	ArrayList< Device > btDevice = new ArrayList< Device >();
	
	private int URL_ATTRIBUTE = 0X0100;
	private UUID HANDS_FREE = new UUID(0x1105);         
	
	int[] attrIDs = new int[]{URL_ATTRIBUTE};
	UUID[] searchUuidSet = new UUID[]{HANDS_FREE};
	volatile boolean receiving=false;
	
	String receivePath = "E:\received\\";
	//  constructor : tested OK
	Window()
	{
		listen = new BluetoothListener();
		listen.parent = this;
		btDevice.clear();
	}
	
	//        Device discovery : tested OK
	void searchDevices()
	{
		try
		{ 
		 LocalDevice localDevice = LocalDevice.getLocalDevice();
		 String name=localDevice.getFriendlyName();
		 System.out.println("Host name:"+name);
		 search(); 
		} 
		catch (Exception e) 
		{ 
			System.out.println("start() exception : "+e.toString());
		} 
	 
	}
	
	void search()    // module used for Device Discovery : tested OK
	{
		final Object search =new Object();
		try 
		 { 
			 boolean started;
			 synchronized(search) 
			 {
		         started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listen);
		         if (started) 
		         {
		        	 System.out.println("Searching for devices list...");
		             search.wait(30000);
		         }
			 }
		 } 
		 catch (Exception e) 
		 { 
			 System.out.println("search exception : "+e.toString());
		 }
	}
	
	
	///                   Main class                     /////
	public static void main(String[] args)
	{
		final Window test = new Window();
		try
		{
			
			System.out.println("Welcome to Innosium Bluetooth Project\nChoose an option");
			System.out.println("1. Send \n2. Receive ");
			System.out.println("Enter your choice : ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int choice = Integer.parseInt(br.readLine());
			
			if(choice==1)
			{
				test.searchDevices();
				if(test.btDevice.size()==0)
				{
					System.out.println("No device found");
					return;
				}
				System.out.println(test.btDevice.size() + " device(s) found.");
				int n = test.input();
				test.sendFile(n);
			}
			else if(choice==2)
			{
				test.receiveFile();
			}
			else
			{
				System.out.println("Invalid Entry!!");
			}
			
			/*
			new Thread(){
				public void run()
				{
					test.searchDevices();
					int n = test.input();
					test.sendFile(n);
				}
			}.start();
			
			new Thread(){
				public void run()
				{
					test.receiveFile();
				}
			}.start();*/
		}
		catch(Exception e)
		{
			System.out.println("Error in threading:"+e.toString());
		}
	}
	
	int input()
	{
		try
		{
			System.out.println("Enter desired device number : ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int innoDeviceNumber = Integer.parseInt(br.readLine());
			br.close();
			return innoDeviceNumber-1;
		}
		catch(Exception e)
		{
			System.out.println("Error in input"+e.toString());
			return -1;
		}
	}
	
	void sendFile(int nDev)
	{
		URL_ATTRIBUTE = 0X0100;
		HANDS_FREE = new UUID(0x1105);         
		
		attrIDs = new int[]{URL_ATTRIBUTE};
		searchUuidSet = new UUID[]{HANDS_FREE};
		
		Device dev;
		Object search;
		DiscoveryAgent da;
		RemoteDevice rd;
		ClientSession cs;
		File file;
		JFileChooser fc;
		InputStream is;
		HeaderSet check,hs;
		Operation op;
		OutputStream os;
		
		try
		{
			
			dev = btDevice.get(nDev);
			search = new Object();
			da = LocalDevice.getLocalDevice().getDiscoveryAgent();
			
			System.out.println("Searching for services...");
			listen.devicePoint=nDev;
			rd = dev.getDevice();
			synchronized (search) 
			{
				da.searchServices(attrIDs, searchUuidSet, rd, listen);
		        search.wait(20000);
			}
			
			System.out.println("Service Found.\nInitialising connection...");
			
			cs = (ClientSession)Connector.open(dev.connUrl.get(0));
			
			if(dev.getDevice().isAuthenticated())
				;
			else
				RemoteDeviceHelper.authenticate(dev.getDevice(), "1234");
			
			file = new File("/home/adarsh/bane.jpg");
			fc = new JFileChooser();
			fc.showOpenDialog(fc);
			file = fc.getSelectedFile();
			
			is = new FileInputStream(file);
			byte filebytes[] = new byte[is.available()];
			is.read(filebytes);
			is.close();
			
			check = cs.connect(null);
			if(check.getResponseCode()!=ResponseCodes.OBEX_HTTP_OK)
			{
				System.out.println("Failed Obex connection.");
				return;
			}
			
			hs = cs.createHeaderSet();
			hs.setHeader(HeaderSet.NAME, file.getName());
			hs.setHeader(HeaderSet.TYPE, file.toString());
			hs.setHeader(HeaderSet.COUNT, new Long(filebytes.length));
			
			System.out.println("Sending data...");
			
			op = cs.put(hs);
			os = op.openOutputStream();
			os.write(filebytes);
			
			os.close();
			op.close();
			cs.disconnect(hs);
			cs.close();
			JOptionPane.showMessageDialog(null, "Sent","Message",1);
			
			System.out.println("Connection terminated normally.");
			
		}
		catch(Exception e)
		{
			System.out.println("connection:"+e);
		}
		
	}
	
	void receiveFile()
	{
		
		final String serverUUID = "11111111111111111111111111111123";
		UUID uuid = new UUID(0x1105);
		try
		{
			LocalDevice thisDevice = LocalDevice.getLocalDevice();
			thisDevice.setDiscoverable(DiscoveryAgent.GIAC);
			SessionNotifier serverConnection = (SessionNotifier) Connector.open("btgoep://localhost:"+ /*serverUUID*/uuid + ";name=ObexExample");
			System.out.println("Server waiting for connection...");
			RequestHandler handler = new RequestHandler();
			handler.parent = this;
            receiving = true;
			serverConnection.acceptAndOpen(handler);
            while(receiving)
            	;
            System.out.println("File saved in "+receivePath);
            
		}
		catch(Exception e)
		{
			System.out.println("server exception:"+e.toString());
		}
	}
	
	class RequestHandler extends ServerRequestHandler 
	{
		Window parent;
		public int onPut(Operation op) 
        {
            try 
            {
            	
                HeaderSet hs = op.getReceivedHeaders();
                String name = (String) hs.getHeader(HeaderSet.NAME);
                if (name != null) 
                {
                    System.out.println("Receiving : " + name);
                }

                InputStream is = op.openInputStream();
                File saveAs = new File(receivePath+name);
                OutputStream os = new FileOutputStream(saveAs);
                byte[] buff = new byte[8*1024];
                int readBytes;
                while((readBytes=is.read(buff))!=-1)
                	os.write(buff,0,readBytes);
             
                os.close();
                parent.receiving = false;
                return ResponseCodes.OBEX_HTTP_OK;
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
                return ResponseCodes.OBEX_HTTP_UNAVAILABLE;
            }
        }
    }

	
	
}
