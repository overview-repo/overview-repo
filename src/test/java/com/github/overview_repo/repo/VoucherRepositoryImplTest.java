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


import com.github.overview_repo.VoucherTestData;
import com.github.overview_repo.VoucherTestDb;
import com.github.overview_repo.common.Pair;
import com.github.overview_repo.domain.Voucher;
import com.github.overview_repo.mapper.Attribute;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests for {@link VoucherRepositoryImpl}.
 * @author Radek Beran
 */
public class VoucherRepositoryImplTest {
	
	private final DataSource dataSource;
	private final VoucherTestData testData;
	private final VoucherRepository repo;
	
	public VoucherRepositoryImplTest() {
		VoucherTestDb testDb = new VoucherTestDb();
		this.dataSource = testDb.createDataSource();
		this.testData = new VoucherTestData();
		this.repo = new VoucherRepositoryImpl(dataSource);
	}

	@Test
	public void createFindDeleteVoucher() {
		Voucher voucher = testData.newVoucher("ABCD");

		Voucher voucherCreated = repo.create(voucher, false);
		assertTrue("Created voucher equals voucher to store", EqualsBuilder.reflectionEquals(voucher, voucherCreated));
		
		assertEquals(voucher.getCode(), voucher.getId());
		Optional<Voucher> foundVoucherOpt = repo.findById(voucher.getId());
		assertTrue("Found voucher " + foundVoucherOpt.get() + " equals voucher to store " + voucher, EqualsBuilder.reflectionEquals(voucher, foundVoucherOpt.get()));
		
		repo.delete(voucher.getId());
		Optional<Voucher> foundVoucherAfterDeleteOpt = repo.findById(voucher.getId());
		assertFalse("Voucher should not be present in database after deletion", foundVoucherAfterDeleteOpt.isPresent());
	}
	
	@Test
	public void updateVoucher() {
		Voucher voucher = testData.newVoucher("QWERTY");

		Voucher voucherCreated = repo.create(voucher, false);
		
		Instant redemptionTime = Instant.now().plus(Duration.ofDays(2));
		Voucher voucherToUpdate = voucherCreated;
		voucherToUpdate.setRedeemedBy("customerXY");
		voucherToUpdate.setSoldBy("sellerXY");
		voucherToUpdate.setRedemptionTime(redemptionTime);
		Optional<Voucher> voucherUpdatedOpt = repo.update(voucherToUpdate);
		assertTrue("Updated voucher " + voucherUpdatedOpt.get() + " equals voucher to update " + voucherToUpdate, EqualsBuilder.reflectionEquals(voucherUpdatedOpt.get(), voucherToUpdate));
		
		Optional<Voucher> foundVoucherOpt = repo.findById(voucher.getId());
		assertTrue("Found voucher " + foundVoucherOpt.get() + " is equal to voucher to update " + voucherToUpdate, EqualsBuilder.reflectionEquals(foundVoucherOpt.get(), voucherToUpdate));
	}

	@Test
	public void updateSelectedAttributes() {
		Voucher voucher = testData.newVoucher("HGTDFKL");
		VoucherMapper mapper = VoucherMapper.getInstance();
		BigDecimal newDiscountPrice = BigDecimal.valueOf(200000, 2);
		String newInvoiceNote = "Updated invoice note";

		repo.create(voucher, false);
		int updatedCnt = repo.update(voucher.getId(), Arrays.asList(new Pair[] {
			new Pair<>(mapper.discount_price, newDiscountPrice),
			new Pair<>(mapper.invoice_note, newInvoiceNote)
		}));

		assertEquals(1, updatedCnt);
		Voucher updatedVoucher = repo.findById(voucher.getId()).get();
		assertEquals(newDiscountPrice, updatedVoucher.getDiscountPrice());
		assertEquals(newInvoiceNote, updatedVoucher.getInvoiceNote());
	}

}
