package com.example.Saad.MyFYPProject;

/**
 * Created by wajid on 3/15/2018.
 */

public class ItemNeedClass {
    private String Id;
    private String Name;
    private String Vote;
    private String Description;
    private String ImagePath;

    ItemNeedClass(String id, String name, String Vote, String description, String imagePath) {
        this.Id = id;
        this.Name = name;
        this.Vote = Vote;
        this.Description = description;
        this.ImagePath = imagePath;
    }



    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getVote() {
        return Vote;
    }

    public String getDescription() {
        return Description;
    }

    public String getImagePath() {
        return ImagePath;
    }
}
