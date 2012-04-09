package com.nimbits.client.model.entity;

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:50 AM
 */
public interface Entity  extends Serializable {
    EntityName getName() throws NimbitsException;

    String getUUID();

    void setUUID(String uuid);


    void setName(EntityName name) throws NimbitsException;

    String getDescription();

    void setDescription(String description);

    EntityType getEntityType();

    void setEntityType(EntityType entityType);

    String getKey();

    String getParent();

    void setParent(String parent);

    ProtectionLevel getProtectionLevel();

    void setProtectionLevel(ProtectionLevel protectionLevel);

    String getOwner();

    void setOwner(String owner);

    AlertType getAlertType();

    void setAlertType(AlertType alertType);

    boolean isReadOnly();

    void setReadOnly(boolean readOnly);

    String getBlobKey();

    void setBlobKey(String blobKey);

    void setPoints(List<Point> points);

    void update(Entity update) throws NimbitsException;



}
