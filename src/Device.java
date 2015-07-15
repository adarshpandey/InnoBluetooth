import java.util.ArrayList;

import javax.bluetooth.RemoteDevice;





public class Device 
{
	RemoteDevice device;
	String name,frndName;
	ArrayList< String > connUrl = new ArrayList< String >();
	
	Device(RemoteDevice r)
	{
		device = r;
	}

	public RemoteDevice getDevice() {
		return device;
	}

	public void setDevice(RemoteDevice device) {
		this.device = device;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFrndName() {
		return frndName;
	}

	public void setFrndName(String frndName) {
		this.frndName = frndName;
	}

	
}