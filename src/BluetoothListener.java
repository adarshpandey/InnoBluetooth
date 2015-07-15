import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
<<<<<<< HEAD
import javax.bluetooth.*;
=======
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e


public class BluetoothListener implements DiscoveryListener
{
<<<<<<< HEAD
	Window parent;
	DiscoveryAgent discover;
	ArrayList<String> devicesFound = new ArrayList<String>();
	final static Object lock = new Object();
	int devicePoint=-1;
	
=======
	MainWin parent;
	DiscoveryAgent discover;
	ArrayList<String> devicesFound = new ArrayList<String>();
	final static Object lock = new Object();

>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
	public void deviceDiscovered(RemoteDevice newDevice, DeviceClass arg1) 
	{
		Device d = new Device(newDevice);
		d.name = newDevice.toString();
		try
		{
<<<<<<< HEAD
			parent.btDevice.add(d);
=======
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
			String s =  newDevice.getFriendlyName(true);
			d.frndName = s;
		}
		catch(IOException e)
		{
<<<<<<< HEAD
			parent.btDevice.add(d);
			System.out.println("Problem getting name!!!");
			return;
=======
			System.out.print("Problem getting name!!!");
		}
		finally
		{
			parent.btDevice.add(d);
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
		}
	}

	public void inquiryCompleted(int arg0) 
	{
<<<<<<< HEAD
=======
		System.out.print("Search Completed.\n" + parent.btDevice.size() + " device(s) found -\n");
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
		for(int i=0;i<parent.btDevice.size();i++)
		{
			Device rd = parent.btDevice.get(i);
			try
			{
				System.out.println(i+1 + ". " + rd.name + " : " + rd.frndName);
			}
			catch(Exception e)
			{
<<<<<<< HEAD
				System.out.println("Error fetching friendly name.");
=======
				System.out.println("Error in friendly name.");
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
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
<<<<<<< HEAD
            parent.btDevice.get(devicePoint).connUrl.add(url);
            DataElement serviceName = services[i].getAttributeValue(0x0100);
            if (serviceName != null) 
            {
                System.out.println("service " + serviceName.getValue() + " found " + url);
            } 
            else 
            {
=======
            parent.btDevice.get(i).connUrl=url;
            DataElement serviceName = services[i].getAttributeValue(0x0100);
            if (serviceName != null) {
                System.out.println("service " + serviceName.getValue() + " found " + url);
            } else {
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
                System.out.println("service found " + url);
            }
        }
		
	}
}
