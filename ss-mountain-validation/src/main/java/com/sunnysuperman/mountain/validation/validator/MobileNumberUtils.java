package com.sunnysuperman.mountain.validation.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunnysuperman.mountain.lang.utils.Str;

public class MobileNumberUtils {

	private static final String CN_COUNTRY_CODE = "86";
	@SuppressWarnings("squid:S5869")
	private static final Pattern CN_NO_REGEX = Pattern.compile("^1[3,4,5,6,7,8,9]\\d{9}$");

	private MobileNumberUtils() {
	}

	public static class PhoneComponent {
		String countryCode;
		String num;

		public PhoneComponent(String countryCode, String num) {
			super();
			this.countryCode = countryCode;
			this.num = num;
		}

		public boolean isEmpty() {
			return num == null;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public String getNum() {
			return num;
		}

	}

	/** 获取手机号区号及号码 **/
	public static PhoneComponent getNumberComponents(String s) {
		if (s == null) {
			return null;
		}
		int len = s.length();
		if (len == 0 || s.length() > 15) {
			return null;
		}
		int regionIndex = s.indexOf('-');
		String countryCode = regionIndex <= 0 ? CN_COUNTRY_CODE : s.substring(0, regionIndex);
		String number = regionIndex <= 0 ? s : s.substring(regionIndex + 1);
		// 检查地区码和号码是否数字
		if (!Str.isNumeric(countryCode) || !Str.isNumeric(number)) {
			return null;
		}
		// 中国地区号码严格校验
		if (countryCode.equals(CN_COUNTRY_CODE) && !isValidChinaPhoneNumber(number)) {
			return null;
		}
		return new PhoneComponent(countryCode, number);
	}

	/** 格式化手机号(转成E164格式：如+8613612345678) **/
	public static String formatE164(String s) {
		PhoneComponent components = getNumberComponents(s);
		if (components == null) {
			return null;
		}
		int len = components.getCountryCode().length() + components.getNum().length() + 1;
		return new StringBuilder(len).append('+').append(components.getCountryCode()).append(components.getNum())
				.toString();
	}

	/** 格式化手机号(转成格式：国家码-号码，如86-13612345678) **/
	public static String format(String s, boolean omitChinaCode) {
		PhoneComponent components = getNumberComponents(s);
		if (components == null) {
			return null;
		}
		if (omitChinaCode && components.getCountryCode().equals(CN_COUNTRY_CODE)) {
			return components.getNum();
		}
		int len = components.getCountryCode().length() + components.getNum().length() + 1;
		return new StringBuilder(len).append(components.getCountryCode()).append('-').append(components.getNum())
				.toString();
	}

	/** 格式化手机号(转成格式：国家码-号码，中国地区省略国家编码，如1-13612345678，13612345678) **/
	public static String format(String s) {
		return format(s, true);
	}

	/** 是否有效的中国地区手机号 **/
	public static boolean isValidChinaPhoneNumber(String s) {
		if (s == null) {
			return false;
		}
		if (s.length() != 11) {
			return false;
		}
		Matcher matcher = CN_NO_REGEX.matcher(s);
		return matcher.matches();
	}

	/** 隐藏手机号(打星) **/
	public static String mask(String s) {
		if (s == null || s.isEmpty()) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		int codeIndex = s.indexOf('-');
		if (codeIndex > 0) {
			buf.append(s.substring(0, codeIndex + 1));
			s = s.substring(codeIndex + 1);
		}
		if (s.length() == 11) {
			buf.append(s.substring(0, 3)).append("****").append(s.substring(7));
		} else {
			buf.append(Str.mask(s));
		}
		return buf.toString();
	}
}
