package com.toshi.aerke.model;

public class UserState {

        private String time;
        private String state;
        private String date;

        public UserState() {
        }

    public UserState(String time, String state, String date) {
        this.time = time;
        this.state = state;
        this.date = date;
    }

    public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

}
