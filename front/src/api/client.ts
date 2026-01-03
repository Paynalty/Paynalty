import { z } from 'zod';
import { ENV } from '../config/env';

interface ApiOptions extends RequestInit {
  schema?: z.ZodTypeAny;
}

export async function apiFetch<T>(path: string, options: ApiOptions = {}): Promise<T> {
  const response = await fetch(`${ENV.API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
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
    try {
      return options.schema.parse(data) as T;
    } catch (error) {
      if (error instanceof z.ZodError) {
        console.error(`[Zod Validation Error] ${path}:`, error.issues);
      }
      throw error;
    }
  }

  return data as T;
}
