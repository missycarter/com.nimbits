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

package com.nimbits.client.model.connection;

import com.extjs.gxt.ui.client.data.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;

import java.io.*;
import java.util.*;


public class ConnectionRequestGxtModel extends BaseModelData implements Serializable, ConnectionRequest {
    private Long key;
    private String requestorID;
    private EmailAddress requestorEmail;
    private EmailAddress targetEmail;
    private Date requestDate;
    private Date approvedDate;
    private Boolean approved;
    private Boolean rejected;
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    protected ConnectionRequestGxtModel() {
    }

    public ConnectionRequestGxtModel(ConnectionRequest c) throws NimbitsException {
        this.requestorID = c.getRequestorID();
        this.targetEmail = c.getTargetEmail();
        this.requestorEmail = c.getRequestorEmail();
        this.requestDate = c.getRequestDate();
        this.approved = c.getApproved();
        this.rejected = c.getRejected();

        this.key = c.getKey();
    }

    @Override
    public String getRequestorID() {
        return requestorID;
    }

    @Override
    public void setRequestorID(final String requestorID) {
        this.requestorID = requestorID;
    }

    @Override
    public EmailAddress getTargetEmail() {
        return targetEmail;
    }

    @Override
    public void setTargetEmail(EmailAddress targetEmail) {
        this.targetEmail = targetEmail;
    }

    @Override
    public Date getRequestDate() {

        return new Date(this.requestDate.getTime());
    }

    @Override
    public void setRequestDate(Date requestDate) {
        this.requestDate = new Date(requestDate.getTime());
    }

    @Override
    public Date getApprovedDate() {

        return new Date(this.approvedDate.getTime());
    }

    @Override
    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = new Date(approvedDate.getTime());
    }


    @Override
    public Boolean getApproved() {
        return approved;
    }

    @Override
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public Long getKey() {
        return key;
    }

    @Override
    public void setRequestorEmail(EmailAddress requestorEmail) {
        this.requestorEmail = requestorEmail;
    }

    @Override
    public EmailAddress getRequestorEmail() {
        return requestorEmail;
    }

    @Override
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    @Override
    public Boolean getRejected() {
        if (rejected == null) {
            rejected = false;
        }
        return rejected;
    }


}
