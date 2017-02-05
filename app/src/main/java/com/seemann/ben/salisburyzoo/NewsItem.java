package com.seemann.ben.salisburyzoo;

/**
 * Created by Ben on 1/23/2017.
 */

public class NewsItem implements Comparable<NewsItem>{
    private String added;
    private String date;
    private String image;
    private String info;
    private String name;

    public NewsItem(){};

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(NewsItem newsItem) {
        return newsItem.getAdded().compareTo(this.added);
    }

    public String getDetailsString(){
        String result = "";
        result = result + "<big>"+ name +"</big>";
        if(date != null){
            result = result + "<br><br>" + date;
        }
        result = result + "<br><br>" + info;

        return result;
    }
}
