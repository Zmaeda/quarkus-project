package org.acme.model;

import java.util.List;

public class CalculateResponse {
    public int status;
    public Integer estimate;
    public List<String> errors; // 💡 Stringではなく、List<String>に型を修正（推奨）
    
    // 正常応答用のコンストラクタ
    public CalculateResponse(Integer estimate) {
        this.status = 200;
        this.estimate = estimate;
        this.errors = null;
    }

    // 💡 400/500 エラー用のコンストラクタを追加
    public CalculateResponse(int status, List<String> errors) {
        this.status = status;
        this.estimate = null;
        this.errors = errors;
    }
    // 既存の 400 用コンストラクタもこの形式に修正
    public CalculateResponse(List<String> errors) {
        this(400, errors); // 400を指定して上のコンストラクタを呼び出す
    }
}