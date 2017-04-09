package net.sf.ardengine.sound;

import net.sf.ardengine.renderer.Renderer;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecIBXM;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple class responsible for initializing Paulscode soundsystem.
 */
public class SoundManager {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /** http://www.paulscode.com/tutorials/SoundSystem/SoundSystem.pdf */
    private static SoundSystem system;

    /**
     * Initializes SoundSystem, called automatically.
     * @param preferredLib Renderer.JAVAFX(JavaSound) / other(OpenAL)
     */
    public static void init(Renderer preferredLib){
        try
        {
            //Plugins
            SoundSystemConfig.setCodec( "wav", CodecWav.class );
            SoundSystemConfig.setCodec( "ogg", CodecJOgg.class );
            SoundSystemConfig.setCodec( "s3m", CodecIBXM.class );
            SoundSystemConfig.setCodec( "mod", CodecIBXM.class );
            SoundSystemConfig.setCodec( "xm", CodecIBXM.class );

            system = new SoundSystem((preferredLib==Renderer.JAVAFX)?LibraryJavaSound.class:LibraryLWJGLOpenAL.class);

            //SoundSystemConfig.setSoundFilesPackage("net/sf/ardengine/res/");
            //system.backgroundMusic( "boss", "boss.ogg", true);
            //system.play("boss");
        }
        catch( SoundSystemException e )
        {
            LOGGER.log(Level.SEVERE, "Failed to load sound system!", e);
        }
    }

    /**
     * @return Paulscode awesome sound system
     */
    public static SoundSystem getSystem() {
        return system;
    }

    /**
     * Frees SoundSystem resources, called automatically.
     */
    public static void cleanUp(){
        system.cleanup();
    }
}