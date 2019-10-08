package com.example.testappfornatlex;

class Data {

    private String name;
    private String temp;
    private String time;
    private String image;

    String getName(){return name;}
    String getTemp(){return temp;}
    String getTime(){return time;}
    String getImage(){return image;}

    void setName(String name){this.name = name;}
    void setTemp(String temp){this.temp = temp;}
    void setTime(String time){this.time = time;}
    void setImage(String image){this.image = image;}

    Data(String name, String temp, String time, String image){
        this.name = name;
        this.temp = temp;
        this.time = time;
        this.image = image;
    }
}
