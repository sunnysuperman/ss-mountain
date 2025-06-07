package com.sunnysuperman.mountain.lang.test.exception;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.exception.service.ArgumentServiceException;
import com.sunnysuperman.mountain.lang.exception.service.DataNotFoundServiceException;
import com.sunnysuperman.mountain.lang.exception.service.DetailedServiceException;
import com.sunnysuperman.mountain.lang.exception.service.FrequencyServiceException;
import com.sunnysuperman.mountain.lang.exception.service.GenericServiceError;
import com.sunnysuperman.mountain.lang.exception.service.NeedUpgradeServiceException;
import com.sunnysuperman.mountain.lang.exception.service.PermissionDeniedServiceException;
import com.sunnysuperman.mountain.lang.exception.service.RuntimeServiceException;
import com.sunnysuperman.mountain.lang.exception.service.ServiceErrorCode;
import com.sunnysuperman.mountain.lang.exception.service.ServiceException;
import com.sunnysuperman.mountain.lang.exception.service.SessionServiceException;

class ServiceExceptionTest {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceExceptionTest.class);

	@Test
	void testRuntimeServiceException() {
		try {
			throw new RuntimeServiceException();
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.UNKNOWN_ERROR.code()));
			LOG.error(null, e);
		}
		try {
			throw new RuntimeServiceException("未知的服务异常");
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.UNKNOWN_ERROR.code()));
			assertTrue(e.getCause() instanceof RuntimeException);
			LOG.error(null, e);
		}
		try {
			throw new RuntimeServiceException(new UnexpectedException("未知的服务异常"));
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.UNKNOWN_ERROR.code()));
			assertTrue(e.getCause() instanceof UnexpectedException);
			LOG.error(null, e);
		}
	}

	@Test
	void testArgumentServiceException() {
		try {
			throw new ArgumentServiceException("参数123");
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.ILLEGAL_ARGUMENT.code()));
			assertTrue(e.getMessage().contains("参数123"));
			LOG.error(null, e);
		}
		try {
			throw new ArgumentServiceException("abc", "123");
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.ILLEGAL_ARGUMENT.code()));
			assertTrue(e.getMessage().contains("abc"));
			assertTrue(e.getMessage().contains("123"));
			LOG.error(null, e);
		}
	}

	@Test
	void testPermissionDeniedServiceException() {
		try {
			throw new PermissionDeniedServiceException();
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.PERMISSION_DENIED.code()));
			LOG.error(null, e);
		}
		try {
			throw new PermissionDeniedServiceException("权限不足");
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.PERMISSION_DENIED.code()));
			assertTrue(e.getMessage().contains("权限不足"));
			LOG.error(null, e);
		}
	}

	@Test
	void testDetailedServiceException() {
		try {
			throw new DetailedServiceException("错误的操作");
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.DETAILED_MESSAGE.code()));
			assertTrue(e.getMessage().contains("错误的操作"));
			LOG.error(null, e);
		}
	}

	@Test
	void testSessionServiceException() {
		try {
			throw new SessionServiceException();
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.SESSION_EXPIRES.code()));
			LOG.error(null, e);
		}
	}

	@Test
	void testFrequencyServiceException() {
		try {
			throw new FrequencyServiceException();
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.OPERATION_TOO_FREQUENT.code()));
			LOG.error(null, e);
		}
	}

	@Test
	void testDataNotFoundServiceException() {
		try {
			throw new DataNotFoundServiceException();
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.DATA_NOT_FOUND.code()));
			LOG.error(null, e);
		}
		try {
			throw new DataNotFoundServiceException("用户不存在");
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.DATA_NOT_FOUND.code()));
			assertTrue(e.getMessage().contains("用户不存在"));
			LOG.error(null, e);
		}
	}

	@Test
	void testNeedUpgradeServiceException() {
		try {
			throw new NeedUpgradeServiceException();
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:" + GenericServiceError.NEED_TO_UPGRADE.code()));
			LOG.error(null, e);
		}
	}

	public enum MyServiceErrorCode implements ServiceErrorCode {

		CODE_1001(1001),

		;

		private int code;

		private MyServiceErrorCode(int code) {
			this.code = code;
		}

		@Override
		public int code() {
			return code;
		}

	}

	@Test
	void testCustomServiceException() {
		try {
			throw new ServiceException(1000);
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:1000"));
			LOG.error(null, e);
		}

		try {
			throw new ServiceException(1000, "123", "456");
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:1000"));
			assertTrue(e.getMessage().contains("error-params:"));
			assertTrue(e.getMessage().contains("123") && e.getMessage().contains("456"));
			LOG.error(null, e);
		}

		try {
			throw new ServiceException(Map.of("abc", "123"), 1000);
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:1000"));
			assertTrue(e.getMessage().contains("error-data:"));
			assertTrue(e.getMessage().contains("abc") && e.getMessage().contains("123"));
			assertFalse(e.getMessage().contains("error-params:"));
			LOG.error(null, e);
		}

		try {
			throw new ServiceException(Map.of("abc", "123"), MyServiceErrorCode.CODE_1001);
		} catch (ServiceException e) {
			assertTrue(e.getMessage().startsWith("error-code:1001"));
			assertTrue(e.getMessage().contains("error-data:"));
			assertTrue(e.getMessage().contains("abc") && e.getMessage().contains("123"));
			assertFalse(e.getMessage().contains("error-params:"));
			LOG.error(null, e);
		}
	}
}
