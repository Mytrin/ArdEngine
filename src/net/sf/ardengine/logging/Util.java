package net.sf.ardengine.logging;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
	 *
	 * @throws IOException thrown if file on given path does not exists or is not readable
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
	 *
	 * @throws IOException thrown if file does not exists or is not readable
	 */
	public static ByteBuffer fileToByteBuffer(File source) throws IOException{
		ByteBuffer buffer = BufferUtils.createByteBuffer((int)source.length());
		FileInputStream fis = new FileInputStream(source);
		FileChannel fc = fis.getChannel();

        //read() returns number of read bytes or -1
		while( fc.read(buffer) < 1){};
			
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
	 *
	 * @throws IOException thrown if given InputStream is corrupted or given size wrong
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

    /**
     * Writes buffer content to file. Existing file content is deleted.
     * @param target target file
     * @param data buffer with data
     * @return true, if successfully written data to file
     */
	public static boolean writeBufferToFile(File target, ByteBuffer data){
        try{
            if(target.exists()) target.delete();
            target.createNewFile();

            byte[] toWrite = data.hasArray()?data.array():createBufferArray(data);

            Files.write(target.toPath(),toWrite,
                    StandardOpenOption.WRITE);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    private static byte[] createBufferArray(ByteBuffer data){
        byte[] array = new byte[data.limit()];

        for(int i=0; i < data.limit(); i++){
            array[i] = data.get(i);
        }

        return array;
    }

}
