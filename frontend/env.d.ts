/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent, SlotsType } from 'vue'

  const component: DefineComponent<
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    any,
    SlotsType<{ default: () => unknown }>
  >
  export default component
}
