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

package com.nimbits.server.diagram;

import com.nimbits.client.model.user.*;
import com.nimbits.server.dao.diagram.*;
import com.nimbits.server.memcache.diagram.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 4:14 PM
 *
 */
public class DiagramTransactionFactory {

    protected DiagramTransactionFactory() {
    }

    public static DiagramTransaction getInstance(User user) {
        return new DiagramMemCacheImpl(user);
    }

    public static DiagramTransaction getDaoInstance(User user) {
        return new DiagramDaoImpl(user);
    }
}