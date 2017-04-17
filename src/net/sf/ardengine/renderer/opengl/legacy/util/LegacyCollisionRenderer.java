package net.sf.ardengine.renderer.opengl.legacy.util;

import javafx.scene.paint.Color;
import net.sf.ardengine.Core;

import static org.lwjgl.opengl.GL11.*;

/**
 * Simple singleton for rendering lines of collision shapes
 */
public class LegacyCollisionRenderer {

    private LegacyCollisionRenderer(){};

    public static void drawLines(float[] coords, Color color){
        glDisable(GL_TEXTURE_2D);

        glColor4f((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 1);
        glPushMatrix();
        glLineWidth(1.3f);
        glBegin(GL_LINE_STRIP);
        for( int i=0; i <coords.length; i+=2){
            glVertex2f(coords[i], coords[i+1]);
        }
        //line strip
        glVertex2f(coords[0], coords[1]);
        glEnd();
        glPopMatrix();

        glEnable(GL_TEXTURE_2D);
    }

}
