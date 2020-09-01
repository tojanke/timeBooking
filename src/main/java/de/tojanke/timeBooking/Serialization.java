package de.tojanke.timeBooking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialization {
	
	public static void out( Object o, String fileName ) {
		try (FileOutputStream fos = new FileOutputStream (fileName);
			ObjectOutputStream oos = new ObjectOutputStream (fos)) {
			oos.writeObject(o);
			oos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static Object in(String fileName) {
		try (FileInputStream f_in = new FileInputStream (fileName);
				 ObjectInputStream obj_in = new ObjectInputStream (f_in)) {
				return obj_in.readObject();
			}			
			catch (Exception e) {				
				return null;
			}
	}
	
}
