package com.seemann.ben.salisburyzoo;


/**
 * Created by Ben on 12/21/2016.
 */

public class Animal {
    private String name;
    private String description;
    private String added;
    private String habitat;
    private String sn;
    private String status;
    private String image;
    private Boolean discovered;
    private long rowid;
    private static final String TAG = "RF";

    public Animal(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public boolean getDiscovered(){
        return discovered;
    }

    public void setDiscovered(Boolean d){
        this.discovered = d;
    }

    public long getRowid() { return rowid; }

    public void setRowid(long rowid) { this.rowid = rowid; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getDetailsString(){
        String result = "";
        result = result + "<big>"+ name +"</big>" +
                    "<br>(<i>" + sn + "</i>)<br><br><b>Habitat</b> - " +
                    habitat + "<br><br><b>Description</b> - " +
                    description + "<br><br><b>Status</b> - " + status;
        return result;
    }

    public String getTTSString(){
        String result = name + ". " + sn + ". " + ". Habitat: " + habitat + ". Description: " + description +". Status: " + status;
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
