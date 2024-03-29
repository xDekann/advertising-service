package com.aservice.util.modifiers;

import com.aservice.util.MessageUtil.MessageConst;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MessagesModifier extends Modifier {

	// rows to change
	private final int limit = MessageConst.ROWS_PER_PAGE.getValue();
	private int receiverId;
	
}
