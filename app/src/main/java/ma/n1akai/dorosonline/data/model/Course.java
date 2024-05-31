package ma.n1akai.dorosonline.data.model;

import java.io.Serializable;

public class Course implements Serializable {
    private String id, title, about, youtubeUrl, imgUrl, date, pdfUrl, pdfFileName;

    public Course() {};

    public Course(String id, String title, String about, String youtubeUrl, String imgUrl, String date, String pdfUrl, String pdfFileName) {
        this.id = id;
        this.title = title;
        this.about = about;
        this.youtubeUrl = youtubeUrl;
        this.imgUrl = imgUrl;
        this.date = date;
        this.pdfUrl = pdfUrl;
        this.pdfFileName = pdfFileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
}
