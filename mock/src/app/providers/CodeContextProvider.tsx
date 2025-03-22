'use client';

import React, { useEffect, useMemo, useState } from 'react';
import { CodeData } from '../services/CodeData';
import { CodeContextType } from '../types/provider';

export const CodeContext = React.createContext<CodeContextType | null>(null);

const CodeContextProvider = ({ children }: { children: React.ReactNode }) => {
  const [code, setCode] = useState<Record<string, any>>({});

  useEffect(() => {
    CodeData().then((res) => {
      setCode(res);
    });
  }, []);

  const codeData = useMemo(() => {
    return { code };
  }, [code]);

  return <CodeContext.Provider value={codeData}>{children}</CodeContext.Provider>;
};

export default CodeContextProvider;
