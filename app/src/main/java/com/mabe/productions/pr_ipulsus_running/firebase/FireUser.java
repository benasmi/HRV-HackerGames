package com.mabe.productions.pr_ipulsus_running.firebase;

public class FireUser {

    private String id;
    private String identifier;
    private String email;
    private boolean doneInitial;
    private float base_duration;
    private String birthday;
    private int gender;
    private float height;
    private float kmi;
    private float maxDuration;
    private float weight;
    private String workout_days;
    private int activity_streak;
    private int activity_index;

    private String walking_pulse_zones;
    private String running_pulse_zones;
    private String workout_intervals;
    private int pulse_zone;
    private float workout_duration;

    private String first_weekly_Date;

    public FireUser(String id, String identifier, String email, boolean doneInitial) {
        this.id = id;
        this.identifier = identifier;
        this.email = email;
        this.doneInitial = doneInitial;
    }

    public FireUser(String first_weekly_Date, String id, String identifier, String email, boolean doneInitial, float base_duration, String birthday, int gender, float height, float kmi, float maxDuration, float weight, String workout_days, int activity_streak, int activity_index, String walking_pulse_zones, String running_pulse_zones, String workout_intervals, int pulse_zone, float workout_duration) {
        this.id = id;
        this.identifier = identifier;
        this.email = email;
        this.doneInitial = doneInitial;
        this.base_duration = base_duration;
        this.birthday = birthday;
        this.gender = gender;
        this.height = height;
        this.kmi = kmi;
        this.maxDuration = maxDuration;
        this.weight = weight;
        this.workout_days = workout_days;
        this.activity_streak = activity_streak;
        this.activity_index = activity_index;
        this.walking_pulse_zones = walking_pulse_zones;
        this.running_pulse_zones = running_pulse_zones;
        this.workout_intervals = workout_intervals;
        this.pulse_zone = pulse_zone;
        this.workout_duration = workout_duration;
        this.first_weekly_Date = first_weekly_Date;
    }

    public FireUser() {

    }

    public String getWalking_pulse_zones() {
        return walking_pulse_zones;
    }

    public String getRunning_pulse_zones() {
        return running_pulse_zones;
    }

    public String getWorkout_intervals() {
        return workout_intervals;
    }

    public int getPulse_zone() {
        return pulse_zone;
    }

    public float getWorkout_duration() {
        return workout_duration;
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

    public boolean isDoneInitial() {
        return doneInitial;
    }

    public float getBase_duration() {
        return base_duration;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getGender() {
        return gender;
    }

    public float getHeight() {
        return height;
    }

    public float getKmi() {
        return kmi;
    }

    public float getMaxDuration() {
        return maxDuration;
    }

    public float getWeight() {
        return weight;
    }

    public String getWorkout_days() {
        return workout_days;
    }

    public int getActivity_streak() {
        return activity_streak;
    }

    public int getActivity_index() {
        return activity_index;
    }

    public String getFirst_weekly_Date() {
        return first_weekly_Date;
    }
}
