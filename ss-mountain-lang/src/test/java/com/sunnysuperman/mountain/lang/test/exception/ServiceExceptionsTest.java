package com.sunnysuperman.mountain.lang.test.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.exception.service.GenericServiceError;
import com.sunnysuperman.mountain.lang.exception.service.ServiceExceptions;
import com.sunnysuperman.mountain.lang.exception.service.SessionServiceException;

class ServiceExceptionsTest {

	@Test
	void testExtract() {
		{
			Exception e = new Exception();
			assertNull(ServiceExceptions.extract(e));
		}
		{
			Exception e = new Exception(new RuntimeException());
			assertNull(ServiceExceptions.extract(e));
		}
		{
			Exception e = new Exception(new SessionServiceException());
			assertEquals(GenericServiceError.SESSION_EXPIRES.code(), ServiceExceptions.extract(e).getErrorCode());
		}
		{
			Exception e = new Exception(new RuntimeException(new SessionServiceException()));
			assertNull(ServiceExceptions.extract(e, 1));
			assertNull(ServiceExceptions.extract(e, 2));
			assertEquals(GenericServiceError.SESSION_EXPIRES.code(), ServiceExceptions.extract(e, -1).getErrorCode());
			assertEquals(GenericServiceError.SESSION_EXPIRES.code(), ServiceExceptions.extract(e, 0).getErrorCode());
			assertEquals(GenericServiceError.SESSION_EXPIRES.code(), ServiceExceptions.extract(e, 3).getErrorCode());
			assertEquals(GenericServiceError.SESSION_EXPIRES.code(), ServiceExceptions.extract(e, 4).getErrorCode());
			assertEquals(GenericServiceError.SESSION_EXPIRES.code(), ServiceExceptions.extract(e).getErrorCode());
		}
	}

}
