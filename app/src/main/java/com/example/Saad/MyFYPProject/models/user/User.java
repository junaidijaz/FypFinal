package com.example.Saad.MyFYPProject.models.user;

public class User {
    private float id;
    private String name;
    private String email;
    private String created_at;
    private String updated_at;
    private String votes_allowed;
    private String items_requests_allowed;
    private String last_vote_update;


    // Getter Methods

    public float getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getVotes_allowed() {
        return votes_allowed;
    }

    public String getItems_requests_allowed() {
        return items_requests_allowed;
    }

    public String getLast_vote_update() {
        return last_vote_update;
    }

    // Setter Methods

    public void setId(float id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setVotes_allowed(String votes_allowed) {
        this.votes_allowed = votes_allowed;
    }

    public void setItems_requests_allowed(String items_requests_allowed) {
        this.items_requests_allowed = items_requests_allowed;
    }

    public void setLast_vote_update(String last_vote_update) {
        this.last_vote_update = last_vote_update;
    }
}