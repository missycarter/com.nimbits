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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.value.impl;


import com.nimbits.client.enums.AlertType;
import com.nimbits.client.constants.Const;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueData;

import java.io.Serializable;
import java.util.Date;


public class ValueModel implements Serializable, Comparable<Value>, Value {

    private static final int INT = 64;
    /**
     *
     */


    double lt;
    double lg;
    double d;
    long t;
    String n;
    String dx;
    int st;

    @SuppressWarnings("unused")
    protected ValueModel() {

    }

    @Override
    public ValueData getData() {
        return ValueFactory.createValueData(dx);
    }


    public ValueModel(final Value v) {

        this.lt = v.getLatitude();
        this.lg = v.getLongitude();
        this.d = v.getDoubleValue();
        this.t = v.getTimestamp().getTime();
        this.n = v.getNote();
        this.dx = v.getData().getContent();
        this.st = v.getAlertState().getCode();

    }

    public ValueModel(final Value v, final String dataOverride) {

        this.lt = v.getLatitude();
        this.lg = v.getLongitude();
        this.d = v.getDoubleValue();
        this.t = v.getTimestamp().getTime();
        this.n = v.getNote();
        this.dx = dataOverride;
        this.st = v.getAlertState().getCode();

    }

    public ValueModel(final double lat,
                      final double lng,
                      final double d,
                      final Date timestamp,
                      final String note,
                      final ValueData data,
                      final AlertType alert) {

        this.lt = lat;
        this.lg = lng;
        this.d = d;
        this.st = alert.getCode();
        this.t = timestamp.getTime();
        this.n = note;
        this.dx = data.getContent();
    }




    @Override
    public String getNote() {
        return n == null ? "" : n;
     }


    @Override
    public double getLatitude() {
        return lt;
    }


    @Override
    public double getLongitude() {
        return lg;
    }


    @Override
    public double getDoubleValue() {
        return this.d;

    }

    @Override
    public String getValueWithNote() {
        StringBuilder sb = new StringBuilder(INT);
        if ( this.d != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(this.d);
        }
        if (this.n != null && !this.n.isEmpty()) {
            sb.append(' ');
            sb.append(this.n);
        }
        return sb.toString().trim();

    }

    @Override
    public Date getTimestamp() {
        return new Date(this.t);
    }

    @Override
    public AlertType getAlertState() {
        return AlertType.get(this.st);
    }

    @Override
    public int compareTo(Value that) {
        return this.t < that.getTimestamp().getTime()
                ? 1
                : this.t > that.getTimestamp().getTime()
                ? -1
                : 0;



    }

    @SuppressWarnings({"InstanceofInterfaces", "CastToConcreteClass", "NonFinalFieldReferenceInEquals"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueModel)) return false;

        ValueModel that = (ValueModel) o;

        if (Double.compare(that.d, d) != 0) return false;
        if (Double.compare(that.lg, lg) != 0) return false;
        if (Double.compare(that.lt, lt) != 0) return false;
        if (st != that.st) return false;
        if (t != that.t) return false;
        if (dx != null ? !dx.equals(that.dx) : that.dx != null) return false;
        if (n != null ? !n.equals(that.n) : that.n != null) return false;

        return true;
    }


}
