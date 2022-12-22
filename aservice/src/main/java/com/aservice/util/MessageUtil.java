package com.aservice.util;

public class MessageUtil {
	public enum MessageConst{
		ROWS_PER_PAGE(4);

		private final int value;
		
		private MessageConst(int value) {
			this.value=value;
		}
		
		public int getValue() {
			return value;
		}
	}
}
