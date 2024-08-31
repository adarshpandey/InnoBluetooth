/*
	Project name : Bluetooth
	Date : 2015-June-11
	Description :   device discovery = tested OK 
					service discovery = tested OK
					communication = tetsed OK
 */
//test


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;
import javax.bluetooth.*;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
// main class
public class MainWin
{
	BluetoothListener listen;
	private int URL_ATTRIBUTE = 0X0100;
	private UUID HANDS_FREE = new UUID(0x111E);
	static final UUID OBEX_OBJECT_PUSH = new UUID(0x1105);
	ArrayList< Device > btDevice = new ArrayList< Device >();
	int[] attrIDs = new int[]{URL_ATTRIBUTE};
	UUID[] searchUuidSet = new UUID[]{HANDS_FREE};
	
	final static int RUNNING=1,STOPPED=2;
	static volatile int searchingDevices=STOPPED,searchingServices=STOPPED;
	
	//  constructor : tested OK
	MainWin()
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
		             search.wait(15000);
		             searchingDevices=STOPPED;
		         }
			 }
		 } 
		 catch (Exception e) 
		 { 
			 System.out.println("search exception : "+e.toString());
		 }
	}
	
	//Service Inquiry Initialisation :tested OK
	void searchAvailServices()
	{
		
		final Object serviceSearch = new Object();
		
		try
		{
			services(serviceSearch);
		}
		catch(Exception e)
		{
			System.out.println("Service search exception!!!"+e.toString());
		}
		finally
		{
			searchingServices=STOPPED;
		}
	}
	
	//  Service Inquiry : tetsed OK 
	void services(Object search) throws Exception
	{
		DiscoveryAgent da = LocalDevice.getLocalDevice().getDiscoveryAgent();
		for(int i=0;i<btDevice.size();i++)
		{
			RemoteDevice rd = btDevice.get(i).device;
			synchronized (search) 
			{
	            da.searchServices(attrIDs, searchUuidSet, rd, listen);
	            search.wait(20000);
	        }
		}
	}
	
	//   Sending data to Remote Device : testing
	void sendData(int i)
	{
		try
		{
			Device rd = btDevice.get(i-1);
			UUID[] uuidSet = new UUID[1];
			int[] attrIDs = new int[]{URL_ATTRIBUTE};
			uuidSet[0]=new UUID("1101",false);
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			DiscoveryAgent agent = localDevice.getDiscoveryAgent();
			agent.searchServices(attrIDs,uuidSet,rd.device,listen);
			StreamConnection streamConnection=null;
			StreamConnectionNotifier scn=null;
			
			if(rd.connUrl==null) 
				System.out.println("Service Not Available.");
			else
			{
				sendStreamData(streamConnection,rd,scn);
				//receiveStreamData(streamConnection,rd,scn);
				System.out.println("Pairing Done");
				Thread.sleep(3000);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error in communication : " + e.toString());
		}
	}
	
	void sendStreamData(StreamConnection streamConnection,Device d,StreamConnectionNotifier scn) throws IOException
	{
		System.out.println("Sending data at : " + d.connUrl);
		streamConnection=(StreamConnection)Connector.open(d.connUrl);
		OutputStream outStream=streamConnection.openOutputStream();
		PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
		pWriter.write("Test String from SPP Client\n");
		pWriter.flush();
	}
	
	void receiveStreamData(StreamConnection streamConnection,Device d,StreamConnectionNotifier scn) throws IOException
	{
		System.out.println("Receiving data...");
		streamConnection=(StreamConnection)Connector.open(d.connUrl);
		//InputStream inStream=streamConnection.openInputStream();
		//BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
		//String lineRead=bReader2.readLine();
		//System.out.println("Received Message :"+lineRead);
	}
	
	public static void main(String[] args)
	{
		final MainWin test = new MainWin();
		searchingDevices=RUNNING;
		searchingServices=RUNNING;
		Thread t1,t2;
		try
		{
			t1 = deviceInquiryThread(test);
			t2 = serviceInquiryThread(test);
			t1.start();
			t2.start();
		}
		catch(Exception e)
		{
			System.out.println("Error in threading:"+e.toString());
		}
	}
	
	static Thread deviceInquiryThread(final MainWin mainThread)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				mainThread.searchDevices();
				while(true)
				{
					if(searchingDevices==RUNNING)
						continue;
					else
					{
						mainThread.searchAvailServices();
						break;
					}
				}
			}

		};
		return t;
	}
	
	static Thread serviceInquiryThread(final MainWin mainThread) throws Exception
	{
		Thread t = new Thread()
		{
			public void run()
			{
				while(true)
				{
					if(searchingServices==RUNNING)
						continue;
					else
					{
							input(mainThread);
							break;
					}
				}
			}
		};
		return t;
	}
	
	static void input(MainWin mainThread)
	{
		try
		{
			System.out.println("Enter desired device number : ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int innoDeviceNumber = Integer.parseInt(br.readLine());
			br.close();
			//mainThread.sendData(innoDeviceNumber);
			Device d = mainThread.btDevice.get(innoDeviceNumber-1);
			mainThread.sendFile(d);
		}
		catch(Exception e)
		{
			System.out.println("Error in input"+e.toString());
		}
	}
	
	void sendFile(Device d)
	{
		
		String url2="btgoep://"+d.name+ ":1;master=false;authenticate=false;encrypt=false";
		try
		{
			ClientSession cs =  (ClientSession) Connector.open(url2);
			
			//HeaderSet hs = cs.connect(cs.createHeaderSet());
			//System.out.println("Headerset created");
			//if(hs.getResponseCode()==ResponseCodes.OBEX_HTTP_OK)
				//{System.out.println("Obex service not available!!");return;}
			
			
			System.out.println("waiting for headerset");
			HeaderSet hsOp = cs.createHeaderSet();
			hsOp.setHeader(HeaderSet.NAME, "test.txt");
			hsOp.setHeader(HeaderSet.TYPE, "text");
			System.out.println("HeaderSet created");
			
			System.out.println("Creating operation object...");
			//////  ERROR here 
			/// cannot create operation object
			///IO exception session not connected
			Operation op = cs.put(hsOp);
			System.out.println("operation object created");
			
			byte[] data = "Hello World".getBytes();
			OutputStream os = op.openOutputStream();
			os.write(data);
			
			os.close();
			op.close();
			cs.disconnect(hsOp);
			cs.close();
			
			System.out.println("Connection terminated normally.");
			
		}
		catch(Exception e)
		{
		System.out.println("connection:"+e);
		}
		
	}

	
	
}
