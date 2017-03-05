package net.sf.ardengine.io;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Util {

	private Util() {
	}

	/**
	 * 
	 * @param buffer - source buffer
	 * @param newCapacity - size of new buffer
	 * @return new buffer with copied data
	 */
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer resizedBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		resizedBuffer.put(buffer);
		return resizedBuffer;
	}

	/**
	 * Automatically selects between fileToByteBuffer() and streamToByteBuffer()
	 * @param source path to resource
	 * @param defaultSize potential starting size of buffer from streamToByteBuffer
	 * @return ByteBuffer with data
	 * @throws IOException
     */
	public static ByteBuffer sourceToByteBuffer(String source, int defaultSize) throws IOException{
		if ( Files.isReadable(Paths.get(source)) ) {
			return fileToByteBuffer(new File(source));
		}
		return streamToByteBuffer(Util.class.getClassLoader().getResourceAsStream(source), defaultSize);
	}

	/**
	 * @param source the file to read
	 *
	 * @return buffer filled from given source
	 */
	public static ByteBuffer fileToByteBuffer(File source) throws IOException{
		ByteBuffer buffer = BufferUtils.createByteBuffer((int)source.length() + 1);

		FileInputStream fis = new FileInputStream(source);
		FileChannel fc = fis.getChannel();

		while( fc.read(buffer) != -1 ){};
			
		fis.close();
		fc.close();
		
		buffer.flip();
		return buffer;
	}
	
	/**
	 * 
	 * @param is  the input stream to read
	 * @param size  size of new buffer
	 * @return buffer filled from given source
	 */
	public static ByteBuffer streamToByteBuffer(InputStream is, int size) throws IOException{
		ByteBuffer buffer = BufferUtils.createByteBuffer(size);
		
		ReadableByteChannel rbc = null;
		try {
			rbc = Channels.newChannel(is);

			while ( true ) {
				int bytes = rbc.read(buffer);
				if ( bytes == -1 )
					break;
				if ( buffer.remaining() == 0 )
					buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}

		} finally {
			if(rbc != null){
				rbc.close();
			}
			is.close();
		}
		
		buffer.flip();
		return buffer;
	}

}
