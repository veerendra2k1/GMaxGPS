/*
 * Copyright 2013 - 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gmaxgps.model;

import com.google.android.gms.maps.model.LatLng;

public class LocationUpdate {

    private long deviceId;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    private LatLng location;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng type) {
        this.location = type;
    }

    private boolean location_update;

    public boolean getlocation_update() {
        return location_update;
    }

    public void setlocation_update(boolean update) {
        this.location_update = update;
    }

}
