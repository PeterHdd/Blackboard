
package com.seniortest.activity.seniorproject;

/**
 * Created by peter on 11/5/17.
 */

public class ClassStudent {
    private String name,image,studentid,classid; //capital as in database
    public ClassStudent() {
    }
    public ClassStudent(String name,String image,String studentid,String classid) {
        this.name=name;
        this.image=image;
        this.studentid=studentid;
        this.classid=classid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }
}


