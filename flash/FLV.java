/**
 * FLV Helper - Copyright (C) 2012 Pablo Schaffner
 * Created to be used along SwfParser Coldfusion Component
 * Helps reading and writing FLV video files.
 * 
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class FLV {

	DataOutputStream os;

    public FLV() {
       
    }

    public FLV(OutputStream os, boolean withaudio) throws IOException
	{
		this.os = new DataOutputStream(os);
		writeHeader(withaudio);
	}

	private void writeHeader(boolean withaudio) throws IOException
	{
		// signature
		os.writeBytes("FLV");
		// version
		os.write8((byte) 1);		// FLV version 1
		// flags
		if (withaudio) {
			os.write8((byte) 5);	// video and audio
		} else {
			os.write8((byte) 1);	// video only
		}
		// offset: header size: always 9
		os.writeInt((byte) 9);
		// flvstream: always 0
		os.writeInt((byte) 0);
	}
	
	public void writeVideoFrame(int dataSize_24, int timestamp, byte[] content, int frametype, int codec) throws IOException
	{
		os.writeByte((byte) 9);
		os.write24(dataSize_24);
		os.write24(timestamp & 0x00ffffff);
		os.write8((byte) (timestamp >> 24 & 0xff));
		os.write24(0);
		// first the packet content HEADER (frame type and codec)
		if (codec==1) {
			// JPEG : not supported on FLV specs (not currently used by swf either).
		} else if (codec==2) {
			// Sorenson H.263
			os.writeByte((byte) hex2decimal(frametype+"2"));	// UB4: keyframe:1 + UB4: codec:2 = 12 hex = 18 decimal.
		} else if (codec==3) {
			// Screen video
			os.writeByte((byte) hex2decimal(frametype+"3"));
		} else if (codec==4) {
			// On2 VP6
			os.writeByte((byte) hex2decimal(frametype+"4"));
		} else if (codec==5) {
			// On2 VP6 with alpha channel
			os.writeByte((byte) hex2decimal(frametype+"5"));
		} else if (codec==6) {
			// Screen video version 2
			os.writeByte((byte) hex2decimal(frametype+"6"));
		}
		// then goes the packet content
		os.write(content);
		// then the dataSize_24+11
		os.writeInt(dataSize_24 + 11);
	}	
	
	public void writeAudioFrame(int dataSize_24, int timestamp, byte[] content, int soundformat, int soundrate, int soundsize, int soundtype) throws IOException
	{
		// audio header = 11 bytes
		os.writeByte((byte) 8);
		os.write24(dataSize_24-3);
		os.write24(timestamp & 0x00ffffff);
		os.write8((byte) (timestamp >> 24 & 0xff));	
		os.write24(0);
		// first the new flv packet HEADER tag (soundformat:UB4 , soundrate:UB2, soundsize:UB1, soundtype:UB1) = total 8 bits = 1 byte.
		BitOutputStream tmp = new BitOutputStream(os);		// 1 byte
		tmp.write(4, soundformat);		// number of bits, int value
		tmp.write(2, soundrate);
		tmp.write(1, soundsize);
		tmp.write(1, soundtype);
		tmp.flush();
		// then goes the packet content
		//os.write(content, 1, content.length-1);
		os.write(content, 4, content.length-4);		// content without swf packet header tag (4 bytes)
		// then the dataSize_24-3+11 = contentSize-4 bytes (ex-packetheader)+1 byte (new packetheader)-11 bytes (frame header)
		os.writeInt(dataSize_24-3 + 11);
	}
	//
    
    public byte[] getBytes(ByteBuffer buf) {
        return getBytes(buf,0,buf.capacity());
    }
    
    public byte[] getBytes(ByteBuffer buf,int from,int len) {       
        byte[] bytes = new byte[len];
        buf.get(bytes, 0, bytes.length);
        return bytes;
    }
    
    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

	// used to read uint32, uint24, etc.
    public static int getInt(byte[] data) {
        
        int number = 0;            
        for (int i = 0; i < data.length; ++i) {
          byte b = data[(data.length-1)-i];
          int bitsToShift = i << 3;
          int add = ( b & 0xff) << bitsToShift;
          number = number | add;
        }
        return number;
    }

    public static long arr2long (byte[] arr, int start) {
        int i = 0;
        int len = 4;
        int cnt = 0;
        byte[] tmp = new byte[len];
        for (i = start; i < (start + len); i++) {
            tmp[cnt] = arr[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
            accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
            i++;
        }
        return accum;
    }
    
    public byte[] getBytes(int number) {
      byte[] data = new byte[4]; 
      for (int i = 0; i < 4; ++i) {
        int shift = i << 3; // i * 8
        data[3-i] = (byte)((number & (0xff << shift)) >>> shift);
      }
      return data;
    } 
    
    public void reverse(byte[] b) {
        int left  = 0;          // index of leftmost element
        int right = b.length-1; // index of rightmost element
       
        while (left < right) {
           // exchange the left and right elements
           byte temp = b[left]; 
           b[left]  = b[right]; 
           b[right] = temp;
          
           // move the bounds toward the center
           left++;
           right--;
        }
     }//endmethod reverse
                      
}
