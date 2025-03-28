interface Window {
    dajava?: {
      UserEventRecorder: new () => {
        startRecording: () => void;
        stopRecording: () => void;
      };
    };
  }