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
package com.github.overview_repo;

import com.github.overview_repo.domain.Voucher;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Voucher data for use in tests.
 * @author Radek Beran
 */
public class VoucherTestData {

	public Voucher newVoucher(String code) {
		Voucher voucher = new Voucher();
		voucher.setCode(code);
		voucher.setCreationTime(Instant.now());
		voucher.setDiscountPrice(BigDecimal.valueOf(100000, 2)); // database stores decimal with two decimal digits
		voucher.setInvoiceNote("January invoice");
		return voucher;
	}
}
