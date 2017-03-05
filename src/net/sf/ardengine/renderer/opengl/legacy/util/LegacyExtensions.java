package net.sf.ardengine.renderer.opengl.legacy.util;


import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_EXTENSIONS;
import static org.lwjgl.opengl.GL11.glGetString;

public class LegacyExtensions {

    private static boolean needsInit = true;
    private static Set<String> extensions = new HashSet<>();

    private LegacyExtensions(){};

    private static void init(){
        String[] extensionArray = glGetString(GL_EXTENSIONS).split(" ");

        for(String extension : extensionArray){
            extensions.add(extension.toUpperCase());
        }

        needsInit = false;
    }

    public static boolean isExtensionAvailable(String extensionName){
        if(needsInit) init();

        return extensions.contains(extensionName.toUpperCase());
    }
}
