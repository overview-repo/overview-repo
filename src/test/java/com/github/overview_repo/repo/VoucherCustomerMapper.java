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

import com.github.overview_repo.domain.DiscountEmailType;
import com.github.overview_repo.domain.SendingState;
import com.github.overview_repo.domain.VoucherCustomer;
import com.github.overview_repo.domain.VoucherCustomerFilter;
import com.github.overview_repo.funs.CollectionFuns;
import com.github.overview_repo.mapper.Attr;
import com.github.overview_repo.mapper.Attribute;
import com.github.overview_repo.mapper.DynamicEntityMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping of voucher customer attributes to database fields.
 * @author Radek Beran
 */
public class VoucherCustomerMapper extends DynamicEntityMapper<VoucherCustomer, VoucherCustomerFilter> {

	/** Mapped entity class. */
	private static final Class<VoucherCustomer> cls = VoucherCustomer.class;
	private static final String DB_TABLE_NAME = "voucher_customer";
	private static final VoucherCustomerMapper INSTANCE = new VoucherCustomerMapper();

	public final Attribute<VoucherCustomer, Integer> id;
	public final Attribute<VoucherCustomer, Instant> creation_time;
	public final Attribute<VoucherCustomer, String> email;
	public final Attribute<VoucherCustomer, String> first_name;
	public final Attribute<VoucherCustomer, String> last_name;
	public final Attribute<VoucherCustomer, String> salutation;
	public final Attribute<VoucherCustomer, String> business_partner_code;
	public final Attribute<VoucherCustomer, String> discount_email_type;
	public final Attribute<VoucherCustomer, Instant> email_sent_time;
	public final Attribute<VoucherCustomer, String> email_sending_state;
	public final Attribute<VoucherCustomer, String> email_text;
	public final Attribute<VoucherCustomer, String> import_file_name;

	private VoucherCustomerMapper() {
		id = add(Attr.ofInteger(cls, "id").primary().get(e -> e.getId()).set((e, a) -> e.setId(a)));
		creation_time = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()).set((e, a) -> e.setCreationTime(a)));
		email = add(Attr.ofString(cls, "email").get(e -> e.getEmail()).set((e, a) -> e.setEmail(a)));
		first_name = add(Attr.ofString(cls, "first_name").get(e -> e.getFirstName()).set((e, a) -> e.setFirstName(a)));
		last_name = add(Attr.ofString(cls, "last_name").get(e -> e.getLastName()).set((e, a) -> e.setLastName(a)));
		salutation = add(Attr.ofString(cls, "salutation").get(e -> e.getSalutation()).set((e, a) -> e.setSalutation(a)));
		business_partner_code = add(Attr.ofString(cls, "business_partner_code").get(e -> e.getBusinessPartnerCode()).set((e, a) -> e.setBusinessPartnerCode(a)));
		discount_email_type = add(Attr.ofString(cls, "discount_email_type")
			.get(e -> e.getDiscountEmailType() == null ? null : e.getDiscountEmailType().name())
			.set((e, a) -> {
				DiscountEmailType emailType = null;
				if (a != null && !a.isEmpty()) {
					emailType = DiscountEmailType.valueOf(a);
				}
				e.setDiscountEmailType(emailType);
			}));
		email_sent_time = add(Attr.ofInstant(cls, "email_sent_time").get(e -> e.getEmailSentTime()).set((e, a) -> e.setEmailSentTime(a)));
		email_sending_state = add(Attr.ofString(cls, "email_sending_state")
			.get(e -> e.getEmailSendingState() == null ? null : e.getEmailSendingState().name())
			.set((e, a) -> {
				SendingState emailSendingState = null;
				if (a != null && !a.isEmpty()) {
					emailSendingState = SendingState.valueOf(a);
				}
				e.setEmailSendingState(emailSendingState);
			}));
		email_text = add(Attr.ofString(cls, "email_text").get(e -> e.getEmailText()).set((e, a) -> e.setEmailText(a)));
		import_file_name = add(Attr.ofString(cls, "import_file_name").get(e -> e.getImportFileName()).set((e, a) -> e.setImportFileName(a)));
	}

	public static VoucherCustomerMapper getInstance() {
		return INSTANCE;
	}

	@Override
	public String getDataSet() {
		return DB_TABLE_NAME;
	}

	@Override
	public List<Condition> composeFilterConditions(VoucherCustomerFilter filter) {
		List<Condition> conditions = new ArrayList<>();
		if (filter.getId() != null) {
			conditions.add(Condition.eq(id, filter.getId()));
		}
		if (filter.getImportFileName() != null) {
			conditions.add(Condition.eq(import_file_name, filter.getImportFileName()));
		}
		if (filter.getCustomerIds() != null) {
			conditions.add(Condition.in(id, CollectionFuns.toObjectList(filter.getCustomerIds())));
		}
		return conditions;
	}

	@Override
	public VoucherCustomer createEntity() {
		return new VoucherCustomer();
	}

}
