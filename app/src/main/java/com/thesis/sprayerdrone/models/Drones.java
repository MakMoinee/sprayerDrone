package com.thesis.sprayerdrone.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Drones {
    private int id;
    private int userID;
    private String deviceName;
    private String deviceIP;
    private String status;
    private String registeredDate;

    public Drones(DroneBuilder builder) {
        this.id = builder.id;
        this.userID = builder.userID;
        this.deviceName = builder.deviceName;
        this.deviceIP = builder.deviceIP;
        this.status = builder.status;
        this.registeredDate = builder.registeredDate;
    }

    public static class DroneBuilder {
        private int id;
        private int userID;
        private String deviceName;
        private String deviceIP;
        private String status;
        private String registeredDate;

        public DroneBuilder setRegisteredDate(String registeredDate) {
            this.registeredDate = registeredDate;
            return this;
        }

        public DroneBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public DroneBuilder setUserID(int userID) {
            this.userID = userID;
            return this;
        }

        public DroneBuilder setDeviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public DroneBuilder setDeviceIP(String deviceIP) {
            this.deviceIP = deviceIP;
            return this;
        }

        public DroneBuilder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Drones build() {
            return new Drones(this);
        }
    }
}
