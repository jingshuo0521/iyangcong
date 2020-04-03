package org.geometerplus.fbreader.formats.oeb.function.bean;

/**
 * 
 */
public class BookSegment {

    private String id;
    private String text;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    public int getSegmentLength(){
        return text.length();
    }

}
