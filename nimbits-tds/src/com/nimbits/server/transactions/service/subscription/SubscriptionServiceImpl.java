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

package com.nimbits.server.transactions.service.subscription;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.xmpp.*;
import com.nimbits.client.service.subscription.*;
import com.nimbits.server.communication.email.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.external.facebook.*;
import com.nimbits.server.transactions.service.feed.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.external.twitter.*;
import com.nimbits.server.transactions.service.user.*;
import com.nimbits.server.transactions.service.value.*;
import com.nimbits.server.communication.xmpp.*;

import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 3:51 PM
 */
public class SubscriptionServiceImpl extends RemoteServiceServlet implements
        SubscriptionService {
    private static final Logger log = Logger.getLogger(SubscriptionServiceImpl.class.getName());
    private static final int SECONDS = 60;
    private static final int INT = 120;
    private static final int INT1 = 512;

    @Override
    public void processSubscriptions(final User user, final Point point, final Value v) throws NimbitsException {


        final List<Entity> subscriptions= EntityServiceFactory.getInstance().getSubscriptionsToEntity(user, point);
        log.info("processing " + subscriptions.size() + " subscriptions");

        for (final Entity entity : subscriptions) {

            Subscription subscription = (Subscription) entity;
            if  (subscription.getLastSent().getTime() + subscription.getMaxRepeat() * SECONDS * 1000 < new Date().getTime()) {


                log.info("Processing Subscription " + subscription.getKey());
                subscription.setLastSent(new Date());
                EntityServiceFactory.getInstance().addUpdateEntity(user, subscription);

                final List<Entity> subscriptionEntity = EntityServiceFactory.getInstance().getEntityByKey(user, subscription.getKey(),EntityType.subscription);

                if (subscriptionEntity.isEmpty()) {


                } else {

                   // final List<Entity> result = EntityServiceFactory.getInstance().getEntityByKey(UserServiceFactory.getServerInstance().getAdmin(), point.getKey(), ).get(0);

                    final User subscriber = UserServiceFactory.getInstance().getUserByKey(subscriptionEntity.get(0).getOwner(), AuthLevel.readWriteAll);
                    final AlertType alert = v.getAlertState();

                    switch (subscription.getSubscriptionType()) {


                        case none:
                            break;
                        case anyAlert:
                            if (!alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn())) {
                                sendNotification(subscriber, point, subscription, point, v);
                            }
                            break;
                        case high:
                            if (alert.equals(AlertType.HighAlert) && point.isHighAlarmOn()) {
                                sendNotification(subscriber, point, subscription, point, v);
                            }
                            break;
                        case low:
                            if (alert.equals(AlertType.LowAlert) && point.isLowAlarmOn()) {
                                sendNotification(subscriber, point, subscription, point, v);
                            }
                            break;
                        case idle:
                            if (alert.equals(AlertType.IdleAlert) && point.isIdleAlarmOn()) {
                                sendNotification(subscriber, point, subscription, point, v);
                            }
                            break;
                        case newValue:
                            sendNotification(subscriber, point, subscription, point, v);
                            break;
                        case changed:
                            break;
                    }

                }

            }




        }

    }


    private static void sendNotification(final User user, final Entity entity, final Subscription subscription, final Point point, final Value value) throws NimbitsException {
        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                EmailServiceFactory.getInstance().sendAlert(entity, point, user.getEmail(), value);
                break;
            case facebook:
                postToFB(point,entity, user, value);
                break;
            case twitter:
                sendTweet(user, entity, value);
                break;
            case instantMessage:
                doXMPP(user, subscription, entity, point, value);
                break;
            case feed:
                FeedServiceFactory.getInstance().postToFeed(user, entity, point, value, FeedType.data);
                break;
        }
    }

    private static void doXMPP(final User u, final Subscription subscription, final Entity entity, final Point point, final Value v) throws NimbitsException {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getDoubleValue();
        }

        final List<XmppResource> resources =  XmppServiceFactory.getInstance().getPointXmppResources(u, point);
        if (resources.isEmpty()) {
            XmppServiceFactory.getInstance().sendMessage(message, u.getEmail());
        } else {
            log.info("Sending XMPP with resources count: " + resources.size());
            XmppServiceFactory.getInstance().sendMessage(resources, message, u.getEmail());
        }

    }

    private static void sendTweet(final User u, final Entity entity, final Value v) throws NimbitsException {
        final StringBuilder message = new StringBuilder(INT);
        message.append('#').append(entity.getName().getValue()).append(' ');
        message.append("Value=").append(v.getDoubleValue());
        if (!Utils.isEmptyString(v.getNote())) {
            message.append(' ').append(v.getNote());
        }
        message.append(" via #Nimbits");
        TwitterServiceFactory.getInstance().sendTweet(u, message.toString());
    }

    private static void postToFB(final Entity p, final Entity entity, final User u, final Value v) throws NimbitsException {

        String m = entity.getName().getValue() + " = " + v.getDoubleValue();
        if (v.getNote() != null) {
            m += ' ' + v.getNote();
        }

        final StringBuilder picture = new StringBuilder(INT1);



        if (entity.getProtectionLevel().equals(ProtectionLevel.everyone)) {

            final List<Value> values = ValueServiceFactory.getInstance().getTopDataSeries(p, 10);
            if (values.isEmpty()) {
                picture.append("http://app.nimbits.com/resources/images/logo.png");
            } else {

                picture.append("http://chart.apis.google.com/chart?chd=t:");
                for (int x = values.size(); x >= 0; x--) {
                    Value vx = values.get(x);
                    picture.append(vx.getDoubleValue()).append(',');
                }

                picture.deleteCharAt(picture.length() - 1);
                picture.append("&chs=100x100&cht=ls&chco=3072F3&chds=0,105&chdlp=b&chls=2,4,1&chma=5,5,5,25&chds=a");
            }



        } else {
            picture.append("http://app.nimbits.com/resources/images/logo.png");
        }

        final String link = "http://app.nimbits.com?uuid=" + p.getUUID() + "&email=" + p.getOwner();

        final String d = Utils.isEmptyString(entity.getDescription()) ? "" : entity.getDescription();
        FacebookFactory.getInstance().updateStatus(u.getFacebookToken(), m, picture.toString(), link, "Subscribe to this data feed.",
                "nimbits.com", d);


    }
}
