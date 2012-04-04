/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.orm;

import com.google.appengine.api.datastore.KeyFactory;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class PointEntity implements Point {

    @PrimaryKey
    @Persistent
    private com.google.appengine.api.datastore.Key key;

    @Persistent
    private Double highAlarm = 0.0;

    @Persistent
    private int expire = 90;

    @Persistent
    private String unit;

    @Persistent
    private Double filterValue = 0.1;

    @Persistent
    private Integer filterType = 0;

    @Persistent
    private Double lowAlarm = 0.0;

    @Persistent
    private Boolean highAlarmOn;

    @Persistent
    private Boolean lowAlarmOn;

    @Persistent
    public Boolean idleAlarmOn;

    @Persistent
    private Integer idleSeconds = 0;

    //reset on any data write
    @Persistent
    private Boolean idleAlarmSent;

    @Override
    public boolean isIdleAlarmOn() {
        return (idleAlarmOn == null) ? false : idleAlarmOn;
    }

    @Override
    public void setIdleAlarmOn(final boolean idleAlarmOn) {
        this.idleAlarmOn = idleAlarmOn;
    }

    @Override
    public int getIdleSeconds() {
        return (idleSeconds == null) ? 0 : idleSeconds;
    }

    @Override
    public void setIdleSeconds(final int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }

    @Override
    public boolean getIdleAlarmSent() {
        return (idleAlarmSent == null) ? false : idleAlarmSent;
    }

    @Override
    public void setIdleAlarmSent(final boolean idleAlarmSent) {
        this.idleAlarmSent = idleAlarmSent;
    }

    // Constructors
    protected PointEntity() {
    }

    public PointEntity(final Entity key) {
       this.key =  KeyFactory.createKey(PointEntity.class.getSimpleName(), key.getKey());
    }


    public PointEntity(final Entity key, final Point point) {

        this.highAlarm = point.getHighAlarm();
        this.expire = point.getExpire();
        this.unit = point.getUnit();
        this.key = KeyFactory.createKey(PointEntity.class.getSimpleName(), key.getKey());
        this.lowAlarm = point.getLowAlarm();
        this.highAlarmOn = point.isHighAlarmOn();
        this.lowAlarmOn = point.isLowAlarmOn();
        this.idleAlarmOn = point.isIdleAlarmOn();
        this.idleSeconds = point.getIdleSeconds();
        this.idleAlarmSent = point.getIdleAlarmSent();

        this.values = point.getValues();
        this.value = point.getValue();
        this.filterType = point.getFilterType().getCode();
        this.filterValue = point.getFilterValue();

    }


    @Persistent
    private Double target;

    @NotPersistent
    private List<Value> values;
    @NotPersistent
    private Value value;


     @Override
    public int getExpire() {
        return expire;
    }


    @Override
    public double getHighAlarm() {
        return (highAlarm == null) ? 0.0 : highAlarm;

    }

    @Override
    public double getLowAlarm() {
        return lowAlarm;
    }


    @Override
    public String getUnit() {
        return unit;
    }

    @NotPersistent
    public Value getValue() {
        return value;
    }

    @NotPersistent
    public List<Value> getValues() {
        return values;
    }

    @Override
    public boolean isHighAlarmOn() {
        return (highAlarmOn == null) ? false : highAlarmOn;

    }

    @Override
    public boolean isLowAlarmOn() {
        return (lowAlarmOn == null) ? false : lowAlarmOn;
    }
    @Override
    public void setExpire(final int expire) {
        this.expire = expire;
    }

//    @Override
//    public void setFormula(final String formula) {
//        this.formula = formula;
//    }

    @Override
    public void setHighAlarm(final double highAlarm) {
        this.highAlarm = highAlarm;
    }

    @Override
    public void setHighAlarmOn(final boolean highAlarmOn) {
        this.highAlarmOn = highAlarmOn;
    }

    @Override
    public void setLowAlarm(final double lowAlarm) {
        this.lowAlarm = lowAlarm;
    }

    @Override
    public void setLowAlarmOn(final boolean lowAlarmOn) {
        this.lowAlarmOn = lowAlarmOn;
    }

    @Override
    public void setUnit(final String unit) {
        this.unit = unit;
    }

    @Override
    public void setValue(final Value value) {
        this.value = value;
    }

    @Override
    public void setValues(final List<com.nimbits.client.model.value.Value> values) {
        this.values = values;
    }

    @Override
    public FilterType getFilterType() {
        return filterType == null ? FilterType.fixedHysteresis : FilterType.get(filterType);
    }

    @Override
    public void setFilterType(final FilterType filterType) {
        this.filterType = filterType.getCode();
    }

    @Override
    public double getFilterValue() {
        return filterValue == null ? 0.0 : filterValue;
    }

    @Override
    public void setFilterValue(final double value) {
        this.filterValue = value;
    }

    @Override
    public String getKey() {
        return key.getName();
    }
}