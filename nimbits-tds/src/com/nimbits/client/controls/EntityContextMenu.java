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

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.panels.*;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.shared.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/17/12
 * Time: 9:44 AM
 */
public class EntityContextMenu extends Menu {

    private EntityTree<ModelData> tree;
    private GxtModel currentModel;


    private MenuItem deleteContext;
    private MenuItem subscribeContext;
    private MenuItem reportContext;
    private MenuItem propertyContext;
    private MenuItem copyContext;
    private MenuItem calcContext;
    private Map<String, String> settings;
    private List<EntityModifiedListener> entityModifiedListeners;

    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    void notifyEntityModifiedListener(final GxtModel model, final Action action)  {
        for (EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }

    public interface EntityModifiedListener {
        void onEntityModified(final GxtModel model, final Action action) ;

    }


    public EntityContextMenu(EntityTree<ModelData> tree, final Map<String, String> settings) {
        super();
        entityModifiedListeners = new ArrayList<EntityModifiedListener>();
        this.tree = tree;
        this.settings = settings;
        deleteContext = deleteContext();
        subscribeContext = subscribeContext();
        reportContext = reportContext();
        propertyContext = propertyContext();
        copyContext = copyContext();
        calcContext = calcContext();
        add(propertyContext);
        add(subscribeContext);
        add(calcContext);
        add(reportContext);
        add(copyContext);
        add(deleteContext);
    }

    @Override
    public void showAt(int x, int y) {
        super.showAt(x, y);
        ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
        currentModel = (GxtModel)selectedModel;
        deleteContext.setEnabled(!currentModel.getEntityType().equals(EntityType.user) || ! currentModel.isReadOnly());
        subscribeContext.setEnabled(
                currentModel.getEntityType().equals(EntityType.point) ||
                        currentModel.getEntityType().equals(EntityType.category));
        reportContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) ||
                currentModel.getEntityType().equals(EntityType.category));
        copyContext.setEnabled(currentModel.getEntityType().equals(EntityType.point));
        calcContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.calculation));
        propertyContext().setEnabled(!currentModel.isReadOnly());


    }

    private MenuItem deleteContext() {
        MenuItem retObj = new MenuItem();


        retObj.setText("Delete");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.delete()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (GxtModel)selectedModel;
                if (! currentModel.isReadOnly()) {
                    MessageBox.confirm("Confirm", "Are you sure you want delete this? Doing so will permanently delete it including all of it's children (points, documents data etc)"
                            , deleteEntityListener);
                }

            }
        });
        return retObj;


    }

    private MenuItem calcContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText("Calculation");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.formula()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                GxtModel selectedModel = (GxtModel) tree.getSelectionModel().getSelectedItem();
                Entity entity = selectedModel.getBaseEntity();
                showCalcPanel(entity);
            }

        });
        return retObj;
    }


    private MenuItem propertyContext() {
        MenuItem retObj = new MenuItem();

        retObj.setText("Edit");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.edit()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                GxtModel selectedModel = (GxtModel) tree.getSelectionModel().getSelectedItem();
                Entity entity = selectedModel.getBaseEntity();
                switch (selectedModel.getEntityType()) {
                    case category:  {


                        CategoryPropertyPanel dp = new CategoryPropertyPanel(entity);
                        final Window w = new Window();
                        w.setWidth(500);
                        w.setHeight(400);
                        w.setHeading(entity.getName().getValue() + " " + Const.WORD_PROPERTIES);
                        w.add(dp);
                        w.show();
                        break;

                    }
                    case point: {

                        createPointPropertyWindow(entity);

                        break;


                    }

                    case subscription: {
                        showSubscriptionPanel(entity);
                        break;
                    }
                    case calculation: {
                        showCalcPanel(entity);
                        break;
                    }
                    case file: {
                        FilePropertyPanel dp = new FilePropertyPanel(entity);
                        final Window w = new Window();
                        w.setWidth(500);
                        w.setHeight(400);
                        w.setHeading(entity.getName().getValue() + " " + Const.WORD_PROPERTIES);
                        w.add(dp);
                        w.show();
                        break;
                    }
                }
            }
        });
        return retObj;
    }

    private MenuItem subscribeContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Subscribe");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.plugin()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (GxtModel) selectedModel;
                Entity entity =  currentModel.getBaseEntity();
                //TODO for now only subscribe to points
                if (entity.getEntityType().equals(EntityType.subscription)  ||
                        entity.getEntityType().equals(EntityType.point)) {
                    showSubscriptionPanel(entity);
                }

            }
        });
        return retObj;
    }


    private MenuItem copyContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Copy");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.album()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (GxtModel) selectedModel;

                final MessageBox box;
                if (currentModel.getEntityType().equals(EntityType.point) && ! currentModel.isReadOnly()) {

                    box= MessageBox.prompt(
                            Const.MESSAGE_NEW_POINT,
                            Const.MESSAGE_NEW_POINT_PROMPT);
                    box.addCallback(copyPointListener);
                }
                else {
                    box = MessageBox.alert("Not supported", "Sorry, for the moment you can only copy your data points", null);

                }
                box.show();
            }
        });
        return retObj;
    }



    private MenuItem reportContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Report");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.form()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                GxtModel model = (GxtModel) selectedModel;
                if (model.getEntityType().equals(EntityType.point) || model.getEntityType().equals(EntityType.category)) {
                    Entity p =  model.getBaseEntity();
                    openUrl(p.getEntity(), p.getName().getValue());
                }



            }

            private void openUrl(String uuid, String title) {
                String u = com.google.gwt.user.client.Window.Location.getHref()
                        + "?uuid=" + uuid
                        + "&count=10";
                com.google.gwt.user.client.Window.open(u, title, Const.PARAM_DEFAULT_WINDOW_OPTIONS);
            }
        });

        return retObj;
    }

    final Listener<MessageBoxEvent> deleteEntityListener = new Listener<MessageBoxEvent>() {
        public void handleEvent(MessageBoxEvent ce) {
            com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();
            final EntityServiceAsync service = GWT.create(EntityService.class);

            if (btn.getText().equals(Const.WORD_YES)) {
                final Entity entityToDelete = currentModel.getBaseEntity();
                service.deleteEntity(entityToDelete, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Error Deleting Point", caught);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        notifyEntityModifiedListener(currentModel, Action.delete);

                    }
                });

            }
        }
    };

    private final Listener<MessageBoxEvent> copyPointListener  = new Listener<MessageBoxEvent>() {
        private String newEntityName;


        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                box.show();
                EntityServiceAsync service = GWT.create(EntityService.class);
                EntityName name = CommonFactoryLocator.getInstance().createName(newEntityName);
                Entity entity =  currentModel.getBaseEntity();

                service.copyEntity(entity, name,new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        box.close();
                    }

                    @Override
                    public void onSuccess(Entity entity) {
                        box.close();
                        GxtModel model = new GxtModel(entity);
                        notifyEntityModifiedListener(model, Action.create);
                        //  addUpdateTreeModel(entity, false);
                    }
                });

            }
        }
    };
    private void createPointPropertyWindow(Entity entity) {
        final Window window = new Window();


        final PointPanel panel = new PointPanel(entity);

        panel.addPointUpdatedListeners(new PointPanel.PointUpdatedListener() {
            @Override
            public void onPointUpdated(Entity result) {
                notifyEntityModifiedListener(new GxtModel(result), Action.create);
            }
        });



        window.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connect()));
        window.setSize(466, 520);
        window.setPlain(false);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setHeading(entity.getName().getValue() + " Properties");
        window.setHeaderVisible(true);
        window.setBodyBorder(true);

        window.add(panel);
        window.show();
    }

    public void showSubscriptionPanel(final Entity entity) {
        SubscriptionPanel dp = new SubscriptionPanel(entity, settings);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(500);
        w.setHeight(500);
        w.setHeading("Subscribe");
        w.add(dp);
        dp.addSubscriptionAddedListener(new NavigationEventProvider.EntityAddedListener() {
            @Override
            public void onEntityAdded(Entity entity) {
                w.hide();
                Cookies.removeCookie(Action.subscribe.name());
                notifyEntityModifiedListener(new GxtModel(entity), Action.create);

            }
        });

        w.show();
    }

    public void showCalcPanel(final Entity entity) {
        CalculationPanel dp = new CalculationPanel(entity, settings);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(600);
        w.setHeight(600);
        if (entity.getEntityType().equals(EntityType.point)) {
        w.setHeading("Calculations triggered when data is recorded to " + entity.getName().getValue());
        }
        else {
            w.setHeading("Edit Calculation");

        }
        w.add(dp);
        dp.addSubscriptionAddedListener(new NavigationEventProvider.EntityAddedListener() {
            @Override
            public void onEntityAdded(Entity entity) {
                w.hide();
                notifyEntityModifiedListener(new GxtModel(entity), Action.create);

            }
        });

        w.show();
    }
}