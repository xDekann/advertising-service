package com.aservice.util;

import com.aservice.util.OfferUtil.OfferConst;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserListModifier extends ListModifier{
	
	private final int limit = OfferConst.ROWS_PER_PAGE.getValue();
}
