declare module 'nprogress' {
    interface NProgress {
      start(): void;
      done(): void;
      set(n: number): void;
      inc(): void;
      configure(options: { showSpinner?: boolean }): void;
    }
  
    const nprogress: NProgress;
    export = nprogress;
  }
  