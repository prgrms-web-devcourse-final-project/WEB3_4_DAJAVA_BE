'use client';

import { useEffect } from 'react';
import NProgress from 'nprogress';
import './progressBar.css';

function ProgressBar() {
  useEffect(() => {
    NProgress.configure({ showSpinner: false });
    const handleAnchorClick = (event: Event) => {
        const targetUrl = (event.currentTarget as HTMLAnchorElement).href;
        const currentUrl = window.location.href;
      
        if (targetUrl !== currentUrl) {
          NProgress.start();
        }
      };
      
      document.querySelectorAll('a[href]').forEach(anchor => {
        anchor.addEventListener('click', handleAnchorClick as EventListener);
      });
      
    const handleMutation = () => {
      const anchorElements = document.querySelectorAll('a[href]');

      anchorElements.forEach((anchor) => {
        anchor.addEventListener('click', handleAnchorClick);
      });
    };

    const mutationObserver = new MutationObserver(handleMutation);

    mutationObserver.observe(document, { childList: true, subtree: true });

    window.history.pushState = new Proxy(window.history.pushState, {
        apply: (target, thisArg, argArray: [any, string, string | URL | null | undefined]) => {
          NProgress.done();
          return target.apply(thisArg, argArray);
        },
      });
    return () => {
      mutationObserver.disconnect();
    };
  }, []);

  return <div></div>;
}
export default ProgressBar;