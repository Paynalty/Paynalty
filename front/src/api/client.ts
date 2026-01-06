import { z } from 'zod';
import { ENV } from '../config/env';

import { Storage } from '@apps-in-toss/framework';

import { logout } from '../stores/authStore';

interface ApiOptions extends RequestInit {
  schema?: z.ZodTypeAny;
}

export async function apiFetch<T>(path: string, options: ApiOptions = {}): Promise<T> {
  const token = await Storage.getItem('accessToken');
  const url = `${ENV.API_BASE_URL}${path}`;

  console.log(`🚀 [API Request] ${options.method || 'GET'} ${url}`);

  try {
    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.headers ?? {}),
      },
      ...options,
    });

    console.log(`✅ [API Response Status] ${response.status} ${url}`);

    if (response.status === 401) {
      console.error(`🔒 [Unauthorized] JWT Expired or Invalid. Logging out... (${url})`);
      await logout();
      throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
    }

    if (!response.ok) {
      const errorBody = await response.text();
      console.error(`❌ [API Error Response] ${url}\nBody: ${errorBody.substring(0, 200)}`);
      throw new Error(errorBody || 'API 요청 실패');
    }

    if (response.status === 204) {
      return {} as T;
    }

    const text = await response.text();
    if (!text || text.trim() === '') {
      return {} as T;
    }

    try {
      const data = JSON.parse(text);

      // Zod 스키마가 제공된 경우 검증 수행
      if (options.schema) {
        const result = options.schema.safeParse(data);
        if (!result.success) {
          console.group(`🔴 [Zod Validation Error] ${path}`);
          console.error('Issues:', result.error.format());
          console.error('Received Data:', data);
          console.groupEnd();
          throw result.error;
        }
        return result.data as T;
      }

      return data as T;
    } catch (e) {
      console.error(`🔥 [JSON Parse Error] ${url}\nBody Start: ${text.substring(0, 100)}`);
      if (text.startsWith('<!DOCTYPE') || text.startsWith('<html')) {
        throw new Error(
          `API가 JSON 대신 HTML을 반환했습니다. 엔드포인트가 잘못되었거나 서버 에러일 수 있습니다. (Path: ${path})`
        );
      }
      throw new Error(`JSON 파싱 실패: ${text.substring(0, 100)}...`);
    }
  } catch (error) {
    if (!(error instanceof Error && (error.message.includes('JSON') || error.message.includes('인증')))) {
      console.error(`⚠️ [Network/Fetch Error] ${url}\n`, error);
    }
    throw error;
  }
}
