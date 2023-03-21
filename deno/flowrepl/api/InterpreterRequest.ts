export class InterpreterRequest {
    id = -1;
    code: string;
    language = "javascript";
    history = false;

    constructor({
        id = -1,
        code,
        language = "javascript",
        history = false,
    }: {
        id?: number;
        code: string;
        language?: string;
        history?: boolean;
    }) {
        this.id = id;
        this.code = code;
        this.language = language;
        this.history = history;
    }
}