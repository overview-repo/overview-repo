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
package com.github.overview_repo.repo;

import com.github.overview_repo.domain.Identifiable;
import com.github.overview_repo.mapper.EntityMapper;

import javax.sql.DataSource;

/**
 * Full implementation of SQL repository using {@link EntityMapper}.
 * @author Radek Beran
 */
public class SqlRepository<T extends Identifiable<K>, K, F> extends AbstractRepository<T, K, F> {

    private final DataSource dataSource;

    private final EntityMapper<T, F> entityMapper;


    public SqlRepository(DataSource dataSource, EntityMapper<T, F> entityMapper) {
        this.dataSource = dataSource;
        this.entityMapper = entityMapper;
    }


    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public EntityMapper<T, F> getEntityMapper() {
        return entityMapper;
    }
}
