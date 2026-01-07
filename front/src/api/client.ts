import { z } from 'zod';
import { ENV } from '../config/env';

import { Storage } from '@apps-in-toss/framework';

import { logout } from '../stores/authStore';

interface ApiOptions extends RequestInit {
  schema?: z.ZodTypeAny;
}

export class ApiError extends Error {
  status: number;
  data: any;

  constructor(status: number, data: any) {
    super(typeof data === 'object' ? data.message || JSON.stringify(data) : data || 'API 요청 실패');
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

export async function apiFetch<T>(path: string, options: ApiOptions = {}): Promise<T> {
  const token = await Storage.getItem('accessToken');
  const url = `${ENV.API_BASE_URL}${path}`;

  console.log(`🚀 [API Request] ${options.method || 'GET'} ${url}`);

  try {
    const headers: Record<string, string> = {
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers as Record<string, string>),
    };

    if (!(options.body instanceof FormData)) {
      headers['Content-Type'] = 'application/json';
    }

    const response = await fetch(url, {
      ...options,
      headers,
    });

    console.log(`✅ [API Response Status] ${response.status} ${url}`);

    if (response.status === 401) {
      console.error(`🔒 [Unauthorized] JWT Expired or Invalid. Logging out... (${url})`);
      await logout();
      // throw new ApiError(401, 'AUTH_REQUIRED');
    }

    if (!response.ok) {
      const errorText = await response.text();
      let errorBody;
      try {
        errorBody = JSON.parse(errorText);
      } catch {
        errorBody = errorText;
      }

      if (response.status === 404) {
        console.log(`ℹ️ [API 404 Not Found] ${url}`);
      } else {
        console.error(`❌ [API Error Response] ${url} (Status: ${response.status})\nBody:`, errorBody);
      }
      throw new ApiError(response.status, errorBody);
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
      if (e instanceof z.ZodError) throw e;

      console.error(`🔥 [JSON Parse Error] ${url}\nBody Start: ${text.substring(0, 100)}`);
      if (text.startsWith('<!DOCTYPE') || text.startsWith('<html')) {
        throw new Error(
          `API가 JSON 대신 HTML을 반환했습니다. 엔드포인트가 잘못되었거나 서버 에러일 수 있습니다. (Path: ${path})`
        );
      }
      throw new Error(`JSON 파싱 실패: ${text.substring(0, 100)}...`);
    }
  } catch (error) {
    if (error instanceof ApiError || error instanceof z.ZodError) {
      throw error;
    }

    if (!(error instanceof Error && error.message.includes('인증'))) {
      console.error(`⚠️ [Network/Fetch Error] ${url}\n`, error);
    }
    throw error;
  }
}
