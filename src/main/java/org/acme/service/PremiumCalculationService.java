package org.acme.service;

import java.util.List;

import org.acme.model.Birth;
import org.acme.model.CalculateResponse;
import org.acme.repository.PremiumCalculationRepository;
import org.acme.validation.CalcValidation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PremiumCalculationService {

    @Inject
    PremiumCalculationRepository premiumCalculationRepository;

    @Inject
    CalcValidation calcValidation;

    private static final int MIN_AGE = 20;
    private static final int MAX_AGE_EXCLUSIVE = 70;

    @Transactional // DBの読み取り操作のため、トランザクションを宣言
    public Response premiumCulclate(Birth request) {
        try {
            // 1. バリデーションと年齢計算をValidation層に移譲 (エラーは InvalidDateInputExceptionMapperで処理される)
            int age = calcValidation.validateAndCalculateAge(request);

            // 現在のテーブルは20歳未満、70歳以上に対応していません。
            if (age < MIN_AGE || age >= MAX_AGE_EXCLUSIVE) {
                throw new IllegalArgumentException("AgeOutOfRange: The provided age " + age + " is outside the acceptable range (20-69).");
            }
        
            // 2. 保険料計算(Repositoryへ移譲)
            int estimate = premiumCalculationRepository.getPremium(age, request.getGender());
    
            //正常応答: HTTP 200 と JSON(premium) を返す
            return Response.ok(new CalculateResponse(estimate)).build();

            // 💡 Repositoryからのエラー（年齢範囲外 20-69歳, 性別無効）のみを捕捉
        } catch (IllegalArgumentException e) {

            // 発生したエラー詳細をリストとしてラップ
            List<String> errorDetails = List.of(e.getMessage());

            // 異常応答: HTTP 400 Bad Request と JSON を返す
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new CalculateResponse(errorDetails))
                    .build();
        }
    }
}
