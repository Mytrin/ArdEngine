package net.sf.ardengine.renderer.opengl.lib.text;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;

/**
 * HERE BE DRAGONS
 *
 * This is calculation of potential size of text rendered by STB Truetype library.
 * It's quite complicated, because it has to count with automatic wrapping,
 * line breaks, spaces and worst of all - kerning
 *
 * STEPS:
 * 1) Break String into words
 * 2) Determine length of each word and their coordinates
 *      - if line is too long, \n is inserted before currently tested word
 * 3) Split too large words by inserting \n
 * 4) Create new text String, sum spaces between words
 *
 * TODO space sensitive split, clean mess this up
 */
public class WrappedText {

    private String text;
    private float width = -1;
    private float height = -1;
    private float yOffset=-1;

    private final STBFont font;
    private final float wrappingWidth;

    private final List<Word> splitWords = new ArrayList<>();
    private final Stack<Word> partStack = new Stack<>();

    private boolean success = false;

    public WrappedText(String text, STBFont font, float wrappingWidth){
        this.text = text;
        this.font = font;
        this.wrappingWidth = wrappingWidth;
    }

    public void calculate(){
        if(font.getCharData() == null) return;

        try ( MemoryStack stack = MemoryStack.stackPush() ) {
            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);

            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);

            String[] wordArray = text.replaceAll("\n", " \n ").split(" ");

            List<Word> words = new ArrayList<>();

            boolean start= true;

            //getting length of words
            for(int i=0; i < wordArray.length; i++){
                float x0=0;
                float x1=0;

                if(wordArray[i].length() == 0) continue;

                font.activateChar(x,y,quad, wordArray[i].charAt(0));
                x0=quad.x0();

                if(start){
                    //STB counts coords from different corner
                    yOffset=-quad.y0() +1; //magic number for filtering to prevent texture cut
                    start = false;
                }

                for(int c=1; c < wordArray[i].length()-1; c++){
                    char ch = wordArray[i].charAt(c);

                    if(ch == '\n'){
                        y.put(0, y.get(0) + font.getFontHeight());
                        x.put(0, 0.0f);
                        continue;
                    }

                    font.activateChar(x,y,quad, ch);
                }

                font.activateChar(x,y,quad, wordArray[i].charAt(wordArray[i].length()-1));
                x1=quad.x1();

                words.add(new Word(wordArray[i], x0, x1));

                //STB has to know about spaces between words
                font.activateChar(x,y,quad, ' ');
            }

            //splitting large words
            for(int i=0; i < words.size(); i++){
                checkLength(words.get(i));
            }

            //reconstructing text
            StringBuilder wrappedText = new StringBuilder();
            float lineLength = 0;
            float largestLine = 0;
            float lineCount = 1;

            boolean firstWord = true;
            Word previousWord = null;
            Word actWord;
            float spaceLength;

            for(int i=0; i < splitWords.size(); i++){

                actWord = splitWords.get(i);

                if(actWord.getContent().equals("\n")){
                    wrappedText.append('\n');
                    largestLine = Math.max(largestLine, lineLength);
                    lineLength = 0;
                    firstWord = true;
                    previousWord = null;
                    lineCount++;
                }else{
                    //space length is variable
                    if(previousWord != null){
                        spaceLength = actWord.getStartX() - previousWord.getEndX();
                    }else{
                        spaceLength = 0;
                    }
                    if(wrappingWidth == -1){ //no wrapping width - no check
                        if(!firstWord){
                            wrappedText.append(" ");
                            lineLength+=spaceLength;
                        }else{
                            firstWord = false;
                        }
                        wrappedText.append(actWord.getContent());
                        lineLength += actWord.getLength();
                        previousWord = actWord;
                    }else if(lineLength + spaceLength + actWord.getLength() <= wrappingWidth){
                        if(!firstWord){
                            wrappedText.append(" ");
                            lineLength+=spaceLength;
                        }else{
                            firstWord = false;
                       }
                        wrappedText.append(actWord.getContent());
                        lineLength += actWord.getLength();
                        previousWord = actWord;
                    }else{
                        wrappedText.append('\n');
                        wrappedText.append(actWord.getContent());
                        largestLine = Math.max(largestLine, lineLength);
                        lineLength = actWord.getLength();
                     //   firstWord=true;
                        previousWord = null;
                        lineCount++;
                    }
                }
            }

            largestLine = Math.max(largestLine, lineLength);

            text = wrappedText.toString();
            height = lineCount*font.getFontHeight();
            width = largestLine;
            success = true;
        }
    }

    private void checkLength(Word largeWord){
        if(wrappingWidth == -1){
            splitWords.add(largeWord);
            return;
        }

        while(largeWord.getLength() > wrappingWidth){
            if(largeWord.getCharCount() > 1){
                partStack.push(largeWord.splitWord());
            }else{
                //Single character larger than wrapping width...
                break;
            }
        }
        splitWords.add(largeWord);

        while(!partStack.isEmpty()){
            checkLength(partStack.pop());
        }
    }

    private class Word{

        private String content;
        private float length;
        private float startX;
        private float endX;

        public Word(String content, float x0 ,float x1){
            this.content = content;
            this.startX = x0;
            this.endX  = x1;
            this.length = endX-startX;

        }

        public String getContent() {
            return content;
        }

        public float getLength() {
            return length;
        }

        public int getCharCount(){
            return content.length();
        }

        public float getStartX() {
            return startX;
        }

        public float getEndX() {
            return endX;
        }

        public Word splitWord(){
            String nextWord = content.substring(content.length()/2);
            content = content.substring(0, content.length()/2);
            length /=1.5f; //THIS is quite destructive, however it is far better than starting whole cycle again
            endX /=2f;

            return new Word(nextWord, endX, endX+length);
        }

        @Override
        public String toString() {
            return "Word{content: "+content+", startX: "+startX+", endX: "+endX+" }";
        }
    }


    public String getText() {
        return text;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getYOffset() {
        return yOffset;
    }

    public boolean isSuccess() {
        return success;
    }
}