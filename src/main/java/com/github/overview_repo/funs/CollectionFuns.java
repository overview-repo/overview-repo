/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.overview_repo.funs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Radek Beran
 */
public class CollectionFuns {

    private CollectionFuns() {
    }

    /**
     * Returns optional first element of collection.
     * @param coll
     * @param <T>
     * @return
     */
    public static <T> Optional<T> headOpt(Collection<T> coll) {
        if (coll == null) return Optional.empty();
        return coll.isEmpty() ? Optional.empty() : Optional.ofNullable(coll.iterator().next());
    }

    public static <T> List<Object> toObjectList(Collection<T> coll) {
        List<Object> objects = new ArrayList<>();
        if (coll != null) {
            for (T c : coll) {
                objects.add(c);
            }
        }
        return objects;
    }

    public static String join(Collection<String> coll, String delimiter) {
        StringBuilder s = new StringBuilder();
        if (coll != null) {
            int i = 0;
            for (String c : coll) {
                if (i != 0) {
                    s.append(delimiter);
                }
                s.append(c);
                i++;
            }
        }
        return s.toString();
    }

    public static <T> String mkString(Collection<T> coll, Function<T, String> stringify, String delimiter) {
        int i = 0;
        StringBuilder s = new StringBuilder();
        if (coll != null) {
            for (T c : coll) {
                if (i != 0) {
                    s.append(delimiter);
                }
                s.append(stringify.apply(c));
                i++;
            }
        }
        return s.toString();
    }
}
