import { z } from 'zod';

export interface ApiOptions extends RequestInit {
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

export interface ApiClientConfig {
  baseUrl: string;
  getToken: () => Promise<string | null>;
  onUnauthorized: () => Promise<void> | void;
}

export class ApiClient {
  constructor(private config: ApiClientConfig) {}

  async fetch<T>(path: string, options: ApiOptions = {}): Promise<T> {
    const token = await this.config.getToken();
    const url = `${this.config.baseUrl}${path}`;

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
        console.error(`🔒 [Unauthorized] JWT Expired or Invalid. (${url})`);
        await this.config.onUnauthorized();
        return new Promise(() => {}) as unknown as T;
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

      // '인증' 관련 에러 메시지는 onUnauthorized 처리 후 발생하는 경우가 많으므로 로깅 제외 고려 가능
      // 하지만 여기서는 안전하게 로깅 유지
      if (!(error instanceof Error && error.message.includes('인증') && error.message.includes('필요'))) {
         console.error(`⚠️ [Network/Fetch Error] ${url}\n`, error);
      }
      throw error;
    }
  }
}
