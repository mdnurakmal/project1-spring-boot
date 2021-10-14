//package com.mdnurakmal.studentattendance.model;
//
//import javax.persistence.*;
//import java.io.Serializable;
//
//@Entity
//public class Student implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(nullable = false,updatable = false)
//    private Long id;
//    private String name;
//    private String email;
//    private String course;
//    private String phone;
//    private String imageUrl;
//    @Column(nullable = false,updatable = false)
//    private String studentCode;
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public Student() {
//    }
//
//    public Student(Long id, String name, String email, String course, String phone, String imageUrl, String studentCode) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.course = course;
//        this.phone = phone;
//        this.imageUrl = imageUrl;
//        this.studentCode = studentCode;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getCourse() {
//        return course;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public String getStudentCode() {
//        return studentCode;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public void setCourse(String course) {
//        this.course = course;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public void setStudentCode(String studentCode) {
//        this.studentCode = studentCode;
//    }
//
//    @Override
//    public String toString() {
//        return "Student{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", course='" + course + '\'' +
//                ", phone='" + phone + '\'' +
//                ", imageUrl='" + imageUrl + '\'' +
//                ", studentCode='" + studentCode + '\'' +
//                '}';
//    }
//}
