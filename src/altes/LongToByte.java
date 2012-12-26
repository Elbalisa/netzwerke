package altes;

public class LongToByte {

	public static void toByteArry(long value, byte[] array){
	    for(int i = 0; i < 8; i++){  
	       array[7 - i] = (byte)(value >>> (i * 8));  
	    }  
	}
	
	public static long toLong(byte[] array){
		long value = 0;
	    for(int i =0; i < 8; i++){      
	        value <<= 8;  
	        value |= (long)array[i] & 0xFFl;      
	     }  
		return value;
	}
	
}
