export type InterpreterRequest = {
  id: number;
  code: string;
  language: string;
  framework: string;
  history: boolean;
};

export type Message = {
  id: number;
  resultValue: string;
  className: string;
  msgType: string;
  content: FrontendBundleContent | any;
};

export type FrontendBundleContent = {
  scripts: string[];
};
