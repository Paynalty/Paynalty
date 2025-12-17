// 유효성 검사 유틸리티 함수

/**
 * 이메일 유효성 검사
 */
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * 비밀번호 유효성 검사 (최소 8자, 영문+숫자 조합)
 */
export const isValidPassword = (password: string): boolean => {
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
  return passwordRegex.test(password);
};

/**
 * 휴대폰 번호 유효성 검사
 */
export const isValidPhoneNumber = (phone: string): boolean => {
  const phoneRegex = /^01[0-9]-?\d{4}-?\d{4}$/;
  return phoneRegex.test(phone);
};

/**
 * 금액 유효성 검사 (0보다 큰 양수)
 */
export const isValidAmount = (amount: number): boolean => {
  return amount > 0 && Number.isFinite(amount);
};