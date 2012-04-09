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

package com.nimbits.client.model.intelligence;

import com.nimbits.client.exception.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/29/11
 * Time: 3:20 PM
 */
public class IntelligenceFactory {

    public static Intelligence createIntelligence(Intelligence intelligence) throws NimbitsException {
        return new IntelligenceModel(intelligence);

    }

    public static List<Intelligence> createIntelligences(Collection<Intelligence> results) throws NimbitsException {
        final List<Intelligence> retObj = new ArrayList<Intelligence>(results.size());
        for (final Intelligence i : results) {
            retObj.add(createIntelligence(i));
        }
        return retObj;
    }
}
