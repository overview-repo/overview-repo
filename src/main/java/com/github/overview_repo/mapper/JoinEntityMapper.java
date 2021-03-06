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
package com.github.overview_repo.mapper;

import com.github.overview_repo.common.Pair;
import com.github.overview_repo.funs.CollectionFuns;
import com.github.overview_repo.repo.Condition;
import com.github.overview_repo.repo.join.JoinType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Entity mapper that is result of join operation between the first and second joined mapper.
 * It implements an {@link EntityMapper} capable to fetch joined entities.
 * @author Radek Beran
 */
public class JoinEntityMapper<T, F, U, G, V, H> implements EntityMapper<V, H> {

    private final EntityMapper<T, F> firstMapper;
    private final EntityMapper<U, G> secondMapper;
    private final List<Condition> onConditions;
    private final BiFunction<T, U, V> composeEntity;
    private final Function<H, Pair<F, G>> decomposeFilter;
    private final JoinType joinType;

    public JoinEntityMapper(EntityMapper<T, F> firstMapper, EntityMapper<U, G> secondMapper, List<Condition> onConditions, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter, JoinType joinType) {
        this.firstMapper = firstMapper;
        this.secondMapper = secondMapper;
        this.onConditions = onConditions;
        this.composeEntity = composeEntity;
        this.decomposeFilter = decomposeFilter;
        this.joinType = joinType;
    }

    public EntityMapper<T, F> getFirstMapper() {
        return firstMapper;
    }

    public EntityMapper<U, G> getSecondMapper() {
        return secondMapper;
    }

    @Override
    public V createEntity() {
        return composeEntity.apply(firstMapper.createEntity(), secondMapper.createEntity());
    }

    @Override
    public V buildEntity(AttributeSource attributeSource, String ignored) {
        T firstEntity = firstMapper.buildEntity(attributeSource, firstMapper.getAliasPrefix());
        U secondEntity = secondMapper.buildEntity(attributeSource, secondMapper.getAliasPrefix());
        return composeEntity.apply(firstEntity, secondEntity);
    }

    @Override
    public String getDataSet() {
        StringBuilder sqlBuilder = new StringBuilder(firstMapper.getDataSet() + " " + joinType.name() + " JOIN " + secondMapper.getDataSet());
        if (onConditions != null && !onConditions.isEmpty()) {
            List<String> onClause = onConditions.stream().map(c -> c.getConditionWithPlaceholders()).collect(Collectors.toList());
            List<Object> parameters = onConditions.stream().flatMap(c -> c.getValues().stream()).collect(Collectors.toList());
            if (parameters != null && !parameters.isEmpty()) {
                throw new IllegalArgumentException("Placeholders in JOIN ON CLAUSE are not supported, please use concrete values that do not come from user input");
            }
            sqlBuilder.append(" ON (").append(CollectionFuns.join(onClause, " AND ")).append(")");
        }
        return sqlBuilder.toString();
    }

    @Override
    public List<Condition> composeFilterConditions(H filter) {
        List<Condition> conditions = new ArrayList<>();
        Pair<F, G> filters = decomposeFilter.apply(filter);
        if (filters.getFirst() != null) {
            conditions.addAll(firstMapper.composeFilterConditions(filters.getFirst()));
        }
        if (filters.getSecond() != null) {
            conditions.addAll(secondMapper.composeFilterConditions(filters.getSecond()));
        }
        return conditions;
    }

    @Override
    public List<String> getAttributeNames() {
        // Full names with aliases will be automatically used in queries selection
        return getAttributeNamesFullAliased();
    }

    @Override
    public List<String> getAttributeNamesFullAliased() {
        List<String> names = new ArrayList<>();
        names.addAll(firstMapper.getAttributeNamesFullAliased());
        names.addAll(secondMapper.getAttributeNamesFullAliased());
        return names;
    }

    @Override
    public String getAliasPrefix() {
        return firstMapper.getDataSet() + "_" + secondMapper.getDataSet() + "_";
    }

    @Override
    public List<Attribute<V, ?>> getAttributes() {
        return new ArrayList<>(); // join mapper has not its own attributes, it composes attributes of first and second joined mapper
    }

    @Override
    public List<String> getPrimaryAttributeNames() {
        throw new UnsupportedOperationException("Unsupported operation in joined mapper");
    }

    @Override
    public List<Object> getPrimaryAttributeValues(V entity) {
        throw new UnsupportedOperationException("Unsupported operation in joined mapper");
    }

    @Override
    public List<Object> getAttributeValues(V instance) {
        throw new UnsupportedOperationException("Unsupported operation in joined mapper");
    }
}
