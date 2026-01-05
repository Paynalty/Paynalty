import { z } from 'zod';
import { ENV } from '../config/env';

import { Storage } from '@apps-in-toss/framework';

interface ApiOptions extends RequestInit {
  schema?: z.ZodTypeAny;
}

export async function apiFetch<T>(path: string, options: ApiOptions = {}): Promise<T> {
  const token = await Storage.getItem('accessToken');

  const response = await fetch(`${ENV.API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers ?? {}),
    },
    ...options,
  });

  if (!response.ok) {
    const errorBody = await response.text();
    throw new Error(errorBody || 'API 요청 실패');
  }

  if (response.status === 204) {
    return {} as T;
  }

  const text = await response.text();
  if (!text) {
    return {} as T;
  }

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
}
