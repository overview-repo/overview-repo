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

import com.github.overview_repo.Overview;
import com.github.overview_repo.VoucherCustomerTestData;
import com.github.overview_repo.VoucherTestDb;
import com.github.overview_repo.domain.Voucher;
import com.github.overview_repo.domain.VoucherCustomer;
import com.github.overview_repo.domain.VoucherCustomerFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests for {@link VoucherCustomerRepositoryImpl}.
 * @author Radek Beran
 */
public class VoucherCustomerRepositoryImplTest {

	private final DataSource dataSource;
	private final VoucherCustomerTestData testData;
	
	public VoucherCustomerRepositoryImplTest() {
		VoucherTestDb testDb = new VoucherTestDb();
		this.dataSource = testDb.createDataSource();
		this.testData = new VoucherCustomerTestData();
	}

	@Test
	public void createFindDeleteCustomer() {
		VoucherCustomerRepository repo = createVoucherCustomerRepository();
		VoucherCustomer customer = testData.newVoucherCustomer("jan.novak@gmail.com", "Jan", "Novak");
		customer.setSupplyPoints(null); // Supply points from test data are not loaded by findById
		customer.setVoucher(null); // Voucher from test data are now not loaded by findById

		VoucherCustomer customerCreated = repo.create(customer, true);
		assertTrue("Created customer has id assigned", customerCreated.getId() != null);
		customer.setId(customerCreated.getId()); // so the entities are now equal
		assertTrue("Created customer " + customerCreated + " equals customer to store " + customer, EqualsBuilder.reflectionEquals(customer, customerCreated));

		Optional<VoucherCustomer> foundCustomerOpt = repo.findById(customer.getId());
		assertTrue("Found customer " + foundCustomerOpt.get() + " was not equal to customer to store " + customer, EqualsBuilder.reflectionEquals(customer, foundCustomerOpt.get()));
		
		repo.delete(customer.getId());
		Optional<VoucherCustomer> foundCustomerAfterDeleteOpt = repo.findById(customer.getId());
		assertFalse("Customer should not be present in database after deletion", foundCustomerAfterDeleteOpt.isPresent());
	}

	@Test
	public void findCustomerLeftJoinVoucher() {
		VoucherCustomerRepositoryImpl customerRepo = createVoucherCustomerRepository();
		VoucherRepositoryImpl voucherRepo = createVoucherRepository();

		// Customer has at most one generated voucher
		VoucherCustomer jan = testData.newVoucherCustomer("jan.novak@gmail.com", "Jan", "Novak");
		jan = customerRepo.create(jan, true);

		Voucher janVoucher = testData.createVoucher("XCVB", "" + jan.getId());
		janVoucher = voucherRepo.create(janVoucher, false);
		jan.setVoucher(janVoucher);
		jan.setSupplyPoints(new ArrayList<>());

		VoucherCustomer martina = testData.newVoucherCustomer("martina.vesela@gmail.com", "Martina", "Vesela");
		martina = customerRepo.create(martina, true);
		martina.setSupplyPoints(new ArrayList<>());
		// without voucher

		List<VoucherCustomer> customers = customerRepo.findByOverview(new Overview<>(null, null, null));
		// Customers are sorted by their id (default ordering)
		assertEquals(2, customers.size());
		VoucherCustomer janLoaded = customers.get(0);
		assertEquals(jan.getEmail(), janLoaded.getEmail());
		VoucherCustomer martinaLoaded = customers.get(1);
		assertEquals(martina.getEmail(), martinaLoaded.getEmail());

		assertTrue("Jan has one voucher", janLoaded.getVoucher() != null && janLoaded.getVoucher().getCode() != null);
		assertTrue(janLoaded + " equals " + jan, EqualsBuilder.reflectionEquals(janLoaded, jan));
		assertTrue(janVoucher + " equals " + janLoaded.getVoucher(), EqualsBuilder.reflectionEquals(janVoucher, janLoaded.getVoucher()));

		assertTrue("Martina has no voucher", martinaLoaded.getVoucher() == null);
		assertTrue(martinaLoaded + " equals " + martina, EqualsBuilder.reflectionEquals(martinaLoaded, martina));
	}

	protected VoucherCustomerRepositoryImpl createVoucherCustomerRepository() {
		return new VoucherCustomerRepositoryImpl(dataSource, new SupplyPointRepositoryImpl(dataSource));
	}

	protected VoucherRepositoryImpl createVoucherRepository() {
		return new VoucherRepositoryImpl(dataSource);
	}

}
