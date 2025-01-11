package jrmr.vds.model.features;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Anonymous extends BaseFeature {	
	public boolean calculate(String editor) {
		@SuppressWarnings("unused")
		InetAddress ipAddr; 
		try {
			ipAddr = InetAddress.getByName(editor);
		} catch (UnknownHostException e) {
			return false;
		}		
		return true;	
	}
}
