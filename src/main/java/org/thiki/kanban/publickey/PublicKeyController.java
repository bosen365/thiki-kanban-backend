package org.thiki.kanban.publickey;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thiki.kanban.foundation.common.Response;

import javax.annotation.Resource;

/**
 * Created by xubt on 7/5/16.
 */
@RestController
public class PublicKeyController {
    @Resource
    private PublicKeyService publicKeyService;
    @Resource
    private PublicKeyResource publicKeyResource;

    @RequestMapping(value = "/publicKey", method = RequestMethod.GET)
    public HttpEntity identify() throws Exception {
        PublicKey publicPublicKey = publicKeyService.authenticate();

        return Response.build(publicKeyResource.toResource(publicPublicKey));
    }
}
