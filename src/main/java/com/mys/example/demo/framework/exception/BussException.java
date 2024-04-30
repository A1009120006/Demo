package com.mys.example.demo.framework.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class BussException extends RuntimeException{


	private String message;

	private Integer code;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
