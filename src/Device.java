<<<<<<< HEAD
import java.util.ArrayList;

=======
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
import javax.bluetooth.RemoteDevice;




<<<<<<< HEAD

=======
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
public class Device 
{
	RemoteDevice device;
	String name,frndName;
<<<<<<< HEAD
	ArrayList< String > connUrl = new ArrayList< String >();
=======
	String connUrl;
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
	
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

<<<<<<< HEAD
=======
	public String getConnUrl() {
		return connUrl;
	}

	public void setConnUrl(String connUrl) {
		this.connUrl = connUrl;
	}
>>>>>>> b300d574014d9d1719fcaca696a9966f6eecb15e
	
}
