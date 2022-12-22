package com.aservice.util.modifiers;

import com.aservice.util.UserUtil.UserConst;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserContactsModifier extends Modifier {
	
	// rows to change
	private final int limit = UserConst.USER_CONTACTS_PER_PAGE.getValue();
}
