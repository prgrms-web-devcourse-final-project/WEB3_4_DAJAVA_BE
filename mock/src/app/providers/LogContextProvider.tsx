'use client';

import React, { useEffect, useState, createContext, ReactNode, useContext } from 'react';
import { LogContextProviderProps } from '../types/provider';

interface LogContextType {
  isLoaded: boolean;
}

const LogContext = createContext<LogContextType | null>(null);


const LogContextProvider = ({ children }: LogContextProviderProps) => {
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    const script = document.createElement('script');
    script.src = 'https://sdk.dajava.link/event-recorder.js';
    script.async = true;
    script.onload = async () => {
      setIsLoaded(true);
    };
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  useEffect(() => {
    if (!isLoaded || !window.dajava) {
      return;
    }

    const userEventRecorder = new window.dajava.UserEventRecorder();
    userEventRecorder.startRecording();

    return () => userEventRecorder.stopRecording();
  }, [isLoaded]);

  return (
    <LogContext.Provider value={{ isLoaded }}>
      {children}
    </LogContext.Provider>
  );
};

export const useDajava = () => {
  const context = useContext(LogContext);
  if (!context) {
    throw new Error('useDajava must be used within a DajavaProvider');
  }
  return context;
};

export default LogContextProvider;
