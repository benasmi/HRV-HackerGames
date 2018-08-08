package com.mabe.productions.hrv_madison.firebaseDatase;

/**
 * Created by Benas on 8/7/2018.
 */

public class FireUser {

    private String id;
    private String identifier;
    private String email;
    private String password;
    private boolean doneInitial;
    private String base_duration;
    private String birthday;
    private String gender;
    private String height;
    private String kmi;
    private String maxDuration;
    private String weight;
    private String workout_days;
    private int activity_streak;
    private int activity_index;

    public FireUser(String id, String identifier, String email, String password, boolean doneInitial) {
        this.id = id;
        this.identifier = identifier;
        this.email = email;
        this.password = password;
        this.doneInitial = doneInitial;
    }

    public FireUser(String id, String identifier, String email, String password, boolean doneInitial, String base_duration, String birthday, String gender, String height, String kmi, String maxDuration, String weight, String workout_days, int activity_streak, int activity_index) {
        this.id = id;
        this.activity_index = activity_index;
        this.activity_streak = activity_streak;
        this.identifier = identifier;
        this.email = email;
        this.password = password;
        this.doneInitial = doneInitial;
        this.base_duration = base_duration;
        this.birthday = birthday;
        this.gender = gender;
        this.height = height;
        this.kmi = kmi;
        this.maxDuration = maxDuration;
        this.weight = weight;
        this.workout_days = workout_days;
    }

    public FireUser(){

    }

    public int getActivity_index() {
        return activity_index;
    }

    public int getActivity_streak() {
        return activity_streak;
    }

    public String getBase_duration() {
        return base_duration;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getHeight() {
        return height;
    }

    public String getKmi() {
        return kmi;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public String getWeight() {
        return weight;
    }

    public String getWorkout_days() {
        return workout_days;
    }

    public String getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDoneInitial() {
        return doneInitial;
    }
}
