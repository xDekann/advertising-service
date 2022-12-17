package com.aservice.util.modifiers;

import org.springframework.stereotype.Component;

import com.aservice.util.OfferUtil.OfferConst;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class OfferReportsModifier extends Modifier {

	// rows to change
	private final int limit = OfferConst.ROWS_PER_PAGE.getValue();
}
