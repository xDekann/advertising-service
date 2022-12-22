package com.aservice.util.modifiers;

import org.springframework.stereotype.Component;

import com.aservice.util.OfferUtil.OfferConst;
import com.aservice.util.UserUtil.UserConst;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserListModifier extends Modifier{
	
	// rows to change
	private final int limit = UserConst.USER_ROWS_PER_PAGE.getValue();
}
