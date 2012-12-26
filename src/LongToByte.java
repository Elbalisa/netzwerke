public class LongToByte {

	public static byte[] toByteArry(long value, byte[] array, int offset){
	    for(int i = 0; i < 8; i++){  
	       array[7 - i + offset] = (byte)(value >>> (i * 8));  
	    }  
	    return array;
	}

	public static long toLong(byte[] array, int offset){
		long value = 0;
	    for(int i = offset; i < 9; i++){      
	        value <<= 8;  
	        value |= (long)array[i] & 0xFFl;      
	     }  
		return value;
	}

}
