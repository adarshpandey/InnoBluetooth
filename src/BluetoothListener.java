import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.*;


public class BluetoothListener implements DiscoveryListener
{
	Window parent;
	DiscoveryAgent discover;
	ArrayList<String> devicesFound = new ArrayList<String>();
	final static Object lock = new Object();
	int devicePoint=-1;
	
	public void deviceDiscovered(RemoteDevice newDevice, DeviceClass arg1) 
	{
		Device d = new Device(newDevice);
		d.name = newDevice.toString();
		try
		{
			parent.btDevice.add(d);
			String s =  newDevice.getFriendlyName(true);
			d.frndName = s;
		}
		catch(IOException e)
		{
			parent.btDevice.add(d);
			System.out.println("Problem getting name!!!");
			return;
		}
	}

	public void inquiryCompleted(int arg0) 
	{
		for(int i=0;i<parent.btDevice.size();i++)
		{
			Device rd = parent.btDevice.get(i);
			try
			{
				System.out.println(i+1 + ". " + rd.name + " : " + rd.frndName);
			}
			catch(Exception e)
			{
				System.out.println("Error fetching friendly name.");
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
            parent.btDevice.get(devicePoint).connUrl.add(url);
            DataElement serviceName = services[i].getAttributeValue(0x0100);
            if (serviceName != null) 
            {
                System.out.println("service " + serviceName.getValue() + " found " + url);
            } 
            else 
            {
                System.out.println("service found " + url);
            }
        }
		
	}
}
