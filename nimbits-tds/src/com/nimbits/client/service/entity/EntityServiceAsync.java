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

package com.nimbits.client.service.entity;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;

import java.util.*;

@SuppressWarnings("unused")
public interface EntityServiceAsync {
    void getEntities(AsyncCallback<List<Entity>> async);

    void addUpdateEntity(final Entity entity, AsyncCallback<Entity> async);

    void getEntityByKey(String uuid, final EntityType type, AsyncCallback<List<Entity> > async);

    void copyEntity(Entity originalEntity, EntityName newName, AsyncCallback<Entity> async);

    void getEntityNameMap(EntityType type, AsyncCallback<Map<EntityName, Entity>> async);

    void getEntityMap(EntityType type, final int limit, AsyncCallback<Map<String, Entity>> async);

    void getChildren(Entity parentEntity, EntityType type, AsyncCallback<List<Entity>> async);

    void addUpdateEntity(User user, Entity aConnection, AsyncCallback<Entity> async);

    void getEntityByKey(User u, String entityId, EntityType type, AsyncCallback<List<Entity> > async);

    void getEntityByName(User u, EntityName name, EntityType type, AsyncCallback<List<Entity> > async);

    void deleteEntity(User u, Entity entity, AsyncCallback<List<Entity>> async);

    void deleteEntity(Entity entity, AsyncCallback<List<Entity>> asyncCallback);

    void getEntityChildren(User u, Entity c, EntityType point, AsyncCallback<List<Entity>> async);

    void getEntityMap(User user, EntityType type, final int limit, AsyncCallback<Map<String, Entity>> async);

    void getIdleEntities(AsyncCallback<List<Entity>> async);

    void getEntityByTrigger(User user, Entity entity, EntityType type, AsyncCallback<List<Entity>> async);

    void getSubscriptionsToEntity(User user, Entity subscribedEntity, AsyncCallback<List<Entity>> async);

    void getSystemWideEntityMap(EntityType type, AsyncCallback<Map<String, Entity>> async);

    void findEntityByKey(final String key, AsyncCallback<List<Entity>> async) throws NimbitsException;

    void findEntityByKey(User user, String param, AsyncCallback< List<Entity>> async) throws NimbitsException;
}
