package org.acme.resource;

import org.acme.model.Birth;
import org.acme.service.PremiumCalculationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response; // 💡 この行を追加


@ApplicationScoped
@Path("/api")
public class PremiumCalculationResource {

    @Inject 
    PremiumCalculationService premiumCalculationService;

    @POST // 💡 新しいデータを作成・送信するためのPOSTメソッド
    @Path("/calculate")
    @Consumes(MediaType.APPLICATION_JSON)     // 💡 クライアントが JSON 形式のデータ (MediaType.APPLICATION_JSON) を送信することを指定
    @Produces(MediaType.APPLICATION_JSON)     // 💡 応答形式も JSON (MediaType.APPLICATION_JSON) であることを指定
  public Response calculateEstimate(@Valid Birth request) {
    return premiumCalculationService.premiumCulclate(request);
  }
}