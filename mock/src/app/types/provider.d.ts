interface CodeContextType {
  code: Record<string, any>;
}

interface CodeContextProviderProps {
  children: ReactNode;
}

export {CodeContextType, CodeContextProviderProps};