import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;


public class BluetoothListener implements DiscoveryListener
{
	MainWin parent;
	DiscoveryAgent discover;
	ArrayList<String> devicesFound = new ArrayList<String>();
	final static Object lock = new Object();

	public void deviceDiscovered(RemoteDevice newDevice, DeviceClass arg1) 
	{
		Device d = new Device(newDevice);
		d.name = newDevice.toString();
		try
		{
			String s =  newDevice.getFriendlyName(true);
			d.frndName = s;
		}
		catch(IOException e)
		{
			System.out.print("Problem getting name!!!");
		}
		finally
		{
			parent.btDevice.add(d);
		}
	}

	public void inquiryCompleted(int arg0) 
	{
		System.out.print("Search Completed.\n" + parent.btDevice.size() + " device(s) found -\n");
		for(int i=0;i<parent.btDevice.size();i++)
		{
			Device rd = parent.btDevice.get(i);
			try
			{
				System.out.println(i+1 + ". " + rd.name + " : " + rd.frndName);
			}
			catch(Exception e)
			{
				System.out.println("Error in friendly name.");
			}
		}
		
	}

	public void serviceSearchCompleted(int arg0, int arg1) 
	{
		System.out.println("Service search completed");
	}

	public void servicesDiscovered(int transID, ServiceRecord[] services) 
	{
		for (int i = 0; i < services.length; i++) 
		{
            String url = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url == null) 
            {
                continue;
            }
            parent.btDevice.get(i).connUrl=url;
            DataElement serviceName = services[i].getAttributeValue(0x0100);
            if (serviceName != null) {
                System.out.println("service " + serviceName.getValue() + " found " + url);
            } else {
                System.out.println("service found " + url);
            }
        }
		
	}
}
