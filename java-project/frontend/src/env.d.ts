/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_API_BASE_URL: string
  readonly VITE_UPLOAD_SIZE_LIMIT: string
  readonly VITE_TOKEN_KEY: string
  readonly VITE_USER_INFO_KEY: string
  readonly VITE_USE_MOCK: string
  readonly VITE_USE_PROXY: string
  readonly VITE_PROXY_TARGET: string
  readonly VITE_API_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
} 