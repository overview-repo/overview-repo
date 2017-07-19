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
package com.github.overview_repo.repo.inmemory;

import com.github.overview_repo.domain.Identifiable;
import com.github.overview_repo.repo.Repository;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Basic abstract in-memory implementation of {@link Repository} with Long key.
 * @author Radek Beran
 */
public abstract class InMemoryLongKeyRepository<T extends Identifiable<Long>, F> extends InMemoryRepository<T, Long, F> {

	private AtomicLong idSequence = new AtomicLong(1L);
	
	@Override
	public Long generateId() {
		return idSequence.getAndIncrement();
	}
}
